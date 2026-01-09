package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CaveVines;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.DiveJumpingGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.EscapeSunlightGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.PowderSnowJumpGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class FoxEntity extends AnimalEntity {
   private static final TrackedData VARIANT;
   private static final TrackedData FOX_FLAGS;
   private static final int SITTING_FLAG = 1;
   public static final int CROUCHING_FLAG = 4;
   public static final int ROLLING_HEAD_FLAG = 8;
   public static final int CHASING_FLAG = 16;
   private static final int SLEEPING_FLAG = 32;
   private static final int WALKING_FLAG = 64;
   private static final int AGGRESSIVE_FLAG = 128;
   private static final TrackedData OWNER;
   private static final TrackedData OTHER_TRUSTED;
   static final Predicate PICKABLE_DROP_FILTER;
   private static final Predicate JUST_ATTACKED_SOMETHING_FILTER;
   static final Predicate CHICKEN_AND_RABBIT_FILTER;
   private static final Predicate NOTICEABLE_PLAYER_FILTER;
   private static final int EATING_DURATION = 600;
   private static final EntityDimensions BABY_BASE_DIMENSIONS;
   private static final Codec TRUSTED_ENTITIES_CODEC;
   private static final boolean DEFAULT_SLEEPING = false;
   private static final boolean DEFAULT_SITTING = false;
   private static final boolean DEFAULT_CROUCHING = false;
   private Goal followChickenAndRabbitGoal;
   private Goal followBabyTurtleGoal;
   private Goal followFishGoal;
   private float headRollProgress;
   private float lastHeadRollProgress;
   float extraRollingHeight;
   float lastExtraRollingHeight;
   private int eatingTime;

   public FoxEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.lookControl = new FoxLookControl();
      this.moveControl = new FoxMoveControl();
      this.setPathfindingPenalty(PathNodeType.DANGER_OTHER, 0.0F);
      this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 0.0F);
      this.setCanPickUpLoot(true);
      this.getNavigation().setMaxFollowRange(32.0F);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(OWNER, Optional.empty());
      builder.add(OTHER_TRUSTED, Optional.empty());
      builder.add(VARIANT, FoxEntity.Variant.DEFAULT.getIndex());
      builder.add(FOX_FLAGS, (byte)0);
   }

   protected void initGoals() {
      this.followChickenAndRabbitGoal = new ActiveTargetGoal(this, AnimalEntity.class, 10, false, false, (entity, world) -> {
         return entity instanceof ChickenEntity || entity instanceof RabbitEntity;
      });
      this.followBabyTurtleGoal = new ActiveTargetGoal(this, TurtleEntity.class, 10, false, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER);
      this.followFishGoal = new ActiveTargetGoal(this, FishEntity.class, 20, false, false, (entity, world) -> {
         return entity instanceof SchoolingFishEntity;
      });
      this.goalSelector.add(0, new FoxSwimGoal());
      this.goalSelector.add(0, new PowderSnowJumpGoal(this, this.getWorld()));
      this.goalSelector.add(1, new StopWanderingGoal());
      this.goalSelector.add(2, new EscapeWhenNotAggressiveGoal(2.2));
      this.goalSelector.add(3, new MateGoal(this, 1.0));
      this.goalSelector.add(4, new FleeEntityGoal(this, PlayerEntity.class, 16.0F, 1.6, 1.4, (entity) -> {
         return NOTICEABLE_PLAYER_FILTER.test(entity) && !this.canTrust(entity) && !this.isAggressive();
      }));
      this.goalSelector.add(4, new FleeEntityGoal(this, WolfEntity.class, 8.0F, 1.6, 1.4, (entity) -> {
         return !((WolfEntity)entity).isTamed() && !this.isAggressive();
      }));
      this.goalSelector.add(4, new FleeEntityGoal(this, PolarBearEntity.class, 8.0F, 1.6, 1.4, (entity) -> {
         return !this.isAggressive();
      }));
      this.goalSelector.add(5, new MoveToHuntGoal());
      this.goalSelector.add(6, new JumpChasingGoal());
      this.goalSelector.add(6, new AvoidDaylightGoal(1.25));
      this.goalSelector.add(7, new AttackGoal(1.2000000476837158, true));
      this.goalSelector.add(7, new DelayedCalmDownGoal());
      this.goalSelector.add(8, new FollowParentGoal(this, 1.25));
      this.goalSelector.add(9, new GoToVillageGoal(32, 200));
      this.goalSelector.add(10, new EatBerriesGoal(1.2000000476837158, 12, 1));
      this.goalSelector.add(10, new PounceAtTargetGoal(this, 0.4F));
      this.goalSelector.add(11, new WanderAroundFarGoal(this, 1.0));
      this.goalSelector.add(11, new PickupItemGoal());
      this.goalSelector.add(12, new LookAtEntityGoal(this, PlayerEntity.class, 24.0F));
      this.goalSelector.add(13, new SitDownAndLookAroundGoal());
      this.targetSelector.add(3, new DefendFriendGoal(LivingEntity.class, false, false, (entity, world) -> {
         return JUST_ATTACKED_SOMETHING_FILTER.test(entity) && !this.canTrust(entity);
      }));
   }

   public void tickMovement() {
      if (!this.getWorld().isClient && this.isAlive() && this.canActVoluntarily()) {
         ++this.eatingTime;
         ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
         if (this.canEat(itemStack)) {
            if (this.eatingTime > 600) {
               ItemStack itemStack2 = itemStack.finishUsing(this.getWorld(), this);
               if (!itemStack2.isEmpty()) {
                  this.equipStack(EquipmentSlot.MAINHAND, itemStack2);
               }

               this.eatingTime = 0;
            } else if (this.eatingTime > 560 && this.random.nextFloat() < 0.1F) {
               this.playEatSound();
               this.getWorld().sendEntityStatus(this, (byte)45);
            }
         }

         LivingEntity livingEntity = this.getTarget();
         if (livingEntity == null || !livingEntity.isAlive()) {
            this.setCrouching(false);
            this.setRollingHead(false);
         }
      }

      if (this.isSleeping() || this.isImmobile()) {
         this.jumping = false;
         this.sidewaysSpeed = 0.0F;
         this.forwardSpeed = 0.0F;
      }

      super.tickMovement();
      if (this.isAggressive() && this.random.nextFloat() < 0.05F) {
         this.playSound(SoundEvents.ENTITY_FOX_AGGRO, 1.0F, 1.0F);
      }

   }

   protected boolean isImmobile() {
      return this.isDead();
   }

   private boolean canEat(ItemStack stack) {
      return stack.contains(DataComponentTypes.FOOD) && this.getTarget() == null && this.isOnGround() && !this.isSleeping();
   }

   protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
      if (random.nextFloat() < 0.2F) {
         float f = random.nextFloat();
         ItemStack itemStack;
         if (f < 0.05F) {
            itemStack = new ItemStack(Items.EMERALD);
         } else if (f < 0.2F) {
            itemStack = new ItemStack(Items.EGG);
         } else if (f < 0.4F) {
            itemStack = random.nextBoolean() ? new ItemStack(Items.RABBIT_FOOT) : new ItemStack(Items.RABBIT_HIDE);
         } else if (f < 0.6F) {
            itemStack = new ItemStack(Items.WHEAT);
         } else if (f < 0.8F) {
            itemStack = new ItemStack(Items.LEATHER);
         } else {
            itemStack = new ItemStack(Items.FEATHER);
         }

         this.equipStack(EquipmentSlot.MAINHAND, itemStack);
      }

   }

   public void handleStatus(byte status) {
      if (status == 45) {
         ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
         if (!itemStack.isEmpty()) {
            for(int i = 0; i < 8; ++i) {
               Vec3d vec3d = (new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)).rotateX(-this.getPitch() * 0.017453292F).rotateY(-this.getYaw() * 0.017453292F);
               this.getWorld().addParticleClient(new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack), this.getX() + this.getRotationVector().x / 2.0, this.getY(), this.getZ() + this.getRotationVector().z / 2.0, vec3d.x, vec3d.y + 0.05, vec3d.z);
            }
         }
      } else {
         super.handleStatus(status);
      }

   }

   public static DefaultAttributeContainer.Builder createFoxAttributes() {
      return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MOVEMENT_SPEED, 0.30000001192092896).add(EntityAttributes.MAX_HEALTH, 10.0).add(EntityAttributes.ATTACK_DAMAGE, 2.0).add(EntityAttributes.SAFE_FALL_DISTANCE, 5.0).add(EntityAttributes.FOLLOW_RANGE, 32.0);
   }

   @Nullable
   public FoxEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
      FoxEntity foxEntity = (FoxEntity)EntityType.FOX.create(serverWorld, SpawnReason.BREEDING);
      if (foxEntity != null) {
         foxEntity.setVariant(this.random.nextBoolean() ? this.getVariant() : ((FoxEntity)passiveEntity).getVariant());
      }

      return foxEntity;
   }

   public static boolean canSpawn(EntityType type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
      return world.getBlockState(pos.down()).isIn(BlockTags.FOXES_SPAWNABLE_ON) && isLightLevelValidForNaturalSpawn(world, pos);
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      RegistryEntry registryEntry = world.getBiome(this.getBlockPos());
      Variant variant = FoxEntity.Variant.fromBiome(registryEntry);
      boolean bl = false;
      if (entityData instanceof FoxData foxData) {
         variant = foxData.type;
         if (foxData.getSpawnedCount() >= 2) {
            bl = true;
         }
      } else {
         entityData = new FoxData(variant);
      }

      this.setVariant(variant);
      if (bl) {
         this.setBreedingAge(-24000);
      }

      if (world instanceof ServerWorld) {
         this.addTypeSpecificGoals();
      }

      this.initEquipment(world.getRandom(), difficulty);
      return super.initialize(world, difficulty, spawnReason, (EntityData)entityData);
   }

   private void addTypeSpecificGoals() {
      if (this.getVariant() == FoxEntity.Variant.RED) {
         this.targetSelector.add(4, this.followChickenAndRabbitGoal);
         this.targetSelector.add(4, this.followBabyTurtleGoal);
         this.targetSelector.add(6, this.followFishGoal);
      } else {
         this.targetSelector.add(4, this.followFishGoal);
         this.targetSelector.add(6, this.followChickenAndRabbitGoal);
         this.targetSelector.add(6, this.followBabyTurtleGoal);
      }

   }

   protected void playEatSound() {
      this.playSound(SoundEvents.ENTITY_FOX_EAT, 1.0F, 1.0F);
   }

   public EntityDimensions getBaseDimensions(EntityPose pose) {
      return this.isBaby() ? BABY_BASE_DIMENSIONS : super.getBaseDimensions(pose);
   }

   public Variant getVariant() {
      return FoxEntity.Variant.byIndex((Integer)this.dataTracker.get(VARIANT));
   }

   private void setVariant(Variant variant) {
      this.dataTracker.set(VARIANT, variant.getIndex());
   }

   @Nullable
   public Object get(ComponentType type) {
      return type == DataComponentTypes.FOX_VARIANT ? castComponentValue(type, this.getVariant()) : super.get(type);
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.FOX_VARIANT);
      super.copyComponentsFrom(from);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.FOX_VARIANT) {
         this.setVariant((Variant)castComponentValue(DataComponentTypes.FOX_VARIANT, value));
         return true;
      } else {
         return super.setApplicableComponent(type, value);
      }
   }

   Stream getTrustedEntities() {
      return Stream.concat(((Optional)this.dataTracker.get(OWNER)).stream(), ((Optional)this.dataTracker.get(OTHER_TRUSTED)).stream());
   }

   void trust(LivingEntity entity) {
      this.trust(new LazyEntityReference(entity));
   }

   private void trust(LazyEntityReference entity) {
      if (((Optional)this.dataTracker.get(OWNER)).isPresent()) {
         this.dataTracker.set(OTHER_TRUSTED, Optional.of(entity));
      } else {
         this.dataTracker.set(OWNER, Optional.of(entity));
      }

   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.put("Trusted", TRUSTED_ENTITIES_CODEC, this.getTrustedEntities().toList());
      view.putBoolean("Sleeping", this.isSleeping());
      view.put("Type", FoxEntity.Variant.CODEC, this.getVariant());
      view.putBoolean("Sitting", this.isSitting());
      view.putBoolean("Crouching", this.isInSneakingPose());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.clearTrusted();
      ((List)view.read("Trusted", TRUSTED_ENTITIES_CODEC).orElse(List.of())).forEach(this::trust);
      this.setSleeping(view.getBoolean("Sleeping", false));
      this.setVariant((Variant)view.read("Type", FoxEntity.Variant.CODEC).orElse(FoxEntity.Variant.DEFAULT));
      this.setSitting(view.getBoolean("Sitting", false));
      this.setCrouching(view.getBoolean("Crouching", false));
      if (this.getWorld() instanceof ServerWorld) {
         this.addTypeSpecificGoals();
      }

   }

   private void clearTrusted() {
      this.dataTracker.set(OWNER, Optional.empty());
      this.dataTracker.set(OTHER_TRUSTED, Optional.empty());
   }

   public boolean isSitting() {
      return this.getFoxFlag(1);
   }

   public void setSitting(boolean sitting) {
      this.setFoxFlag(1, sitting);
   }

   public boolean isWalking() {
      return this.getFoxFlag(64);
   }

   void setWalking(boolean walking) {
      this.setFoxFlag(64, walking);
   }

   boolean isAggressive() {
      return this.getFoxFlag(128);
   }

   void setAggressive(boolean aggressive) {
      this.setFoxFlag(128, aggressive);
   }

   public boolean isSleeping() {
      return this.getFoxFlag(32);
   }

   void setSleeping(boolean sleeping) {
      this.setFoxFlag(32, sleeping);
   }

   private void setFoxFlag(int mask, boolean value) {
      if (value) {
         this.dataTracker.set(FOX_FLAGS, (byte)((Byte)this.dataTracker.get(FOX_FLAGS) | mask));
      } else {
         this.dataTracker.set(FOX_FLAGS, (byte)((Byte)this.dataTracker.get(FOX_FLAGS) & ~mask));
      }

   }

   private boolean getFoxFlag(int bitmask) {
      return ((Byte)this.dataTracker.get(FOX_FLAGS) & bitmask) != 0;
   }

   protected boolean canDispenserEquipSlot(EquipmentSlot slot) {
      return slot == EquipmentSlot.MAINHAND && this.canPickUpLoot();
   }

   public boolean canPickupItem(ItemStack stack) {
      ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
      return itemStack.isEmpty() || this.eatingTime > 0 && stack.contains(DataComponentTypes.FOOD) && !itemStack.contains(DataComponentTypes.FOOD);
   }

   private void spit(ItemStack stack) {
      if (!stack.isEmpty() && !this.getWorld().isClient) {
         ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX() + this.getRotationVector().x, this.getY() + 1.0, this.getZ() + this.getRotationVector().z, stack);
         itemEntity.setPickupDelay(40);
         itemEntity.setThrower(this);
         this.playSound(SoundEvents.ENTITY_FOX_SPIT, 1.0F, 1.0F);
         this.getWorld().spawnEntity(itemEntity);
      }
   }

   private void dropItem(ItemStack stack) {
      ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), stack);
      this.getWorld().spawnEntity(itemEntity);
   }

   protected void loot(ServerWorld world, ItemEntity itemEntity) {
      ItemStack itemStack = itemEntity.getStack();
      if (this.canPickupItem(itemStack)) {
         int i = itemStack.getCount();
         if (i > 1) {
            this.dropItem(itemStack.split(i - 1));
         }

         this.spit(this.getEquippedStack(EquipmentSlot.MAINHAND));
         this.triggerItemPickedUpByEntityCriteria(itemEntity);
         this.equipStack(EquipmentSlot.MAINHAND, itemStack.split(1));
         this.setDropGuaranteed(EquipmentSlot.MAINHAND);
         this.sendPickup(itemEntity, itemStack.getCount());
         itemEntity.discard();
         this.eatingTime = 0;
      }

   }

   public void tick() {
      super.tick();
      if (this.canActVoluntarily()) {
         boolean bl = this.isTouchingWater();
         if (bl || this.getTarget() != null || this.getWorld().isThundering()) {
            this.stopSleeping();
         }

         if (bl || this.isSleeping()) {
            this.setSitting(false);
         }

         if (this.isWalking() && this.getWorld().random.nextFloat() < 0.2F) {
            BlockPos blockPos = this.getBlockPos();
            BlockState blockState = this.getWorld().getBlockState(blockPos);
            this.getWorld().syncWorldEvent(2001, blockPos, Block.getRawIdFromState(blockState));
         }
      }

      this.lastHeadRollProgress = this.headRollProgress;
      if (this.isRollingHead()) {
         this.headRollProgress += (1.0F - this.headRollProgress) * 0.4F;
      } else {
         this.headRollProgress += (0.0F - this.headRollProgress) * 0.4F;
      }

      this.lastExtraRollingHeight = this.extraRollingHeight;
      if (this.isInSneakingPose()) {
         this.extraRollingHeight += 0.2F;
         if (this.extraRollingHeight > 3.0F) {
            this.extraRollingHeight = 3.0F;
         }
      } else {
         this.extraRollingHeight = 0.0F;
      }

   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack.isIn(ItemTags.FOX_FOOD);
   }

   protected void onPlayerSpawnedChild(PlayerEntity player, MobEntity child) {
      ((FoxEntity)child).trust((LivingEntity)player);
   }

   public boolean isChasing() {
      return this.getFoxFlag(16);
   }

   public void setChasing(boolean chasing) {
      this.setFoxFlag(16, chasing);
   }

   public boolean isFullyCrouched() {
      return this.extraRollingHeight == 3.0F;
   }

   public void setCrouching(boolean crouching) {
      this.setFoxFlag(4, crouching);
   }

   public boolean isInSneakingPose() {
      return this.getFoxFlag(4);
   }

   public void setRollingHead(boolean rollingHead) {
      this.setFoxFlag(8, rollingHead);
   }

   public boolean isRollingHead() {
      return this.getFoxFlag(8);
   }

   public float getHeadRoll(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastHeadRollProgress, this.headRollProgress) * 0.11F * 3.1415927F;
   }

   public float getBodyRotationHeightOffset(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastExtraRollingHeight, this.extraRollingHeight);
   }

   public void setTarget(@Nullable LivingEntity target) {
      if (this.isAggressive() && target == null) {
         this.setAggressive(false);
      }

      super.setTarget(target);
   }

   void stopSleeping() {
      this.setSleeping(false);
   }

   void stopActions() {
      this.setRollingHead(false);
      this.setCrouching(false);
      this.setSitting(false);
      this.setSleeping(false);
      this.setAggressive(false);
      this.setWalking(false);
   }

   boolean wantsToPickupItem() {
      return !this.isSleeping() && !this.isSitting() && !this.isWalking();
   }

   public void playAmbientSound() {
      SoundEvent soundEvent = this.getAmbientSound();
      if (soundEvent == SoundEvents.ENTITY_FOX_SCREECH) {
         this.playSound(soundEvent, 2.0F, this.getSoundPitch());
      } else {
         super.playAmbientSound();
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isSleeping()) {
         return SoundEvents.ENTITY_FOX_SLEEP;
      } else {
         if (!this.getWorld().isDay() && this.random.nextFloat() < 0.1F) {
            List list = this.getWorld().getEntitiesByClass(PlayerEntity.class, this.getBoundingBox().expand(16.0, 16.0, 16.0), EntityPredicates.EXCEPT_SPECTATOR);
            if (list.isEmpty()) {
               return SoundEvents.ENTITY_FOX_SCREECH;
            }
         }

         return SoundEvents.ENTITY_FOX_AMBIENT;
      }
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_FOX_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_FOX_DEATH;
   }

   boolean canTrust(LivingEntity entity) {
      return this.getTrustedEntities().anyMatch((trusted) -> {
         return trusted.uuidEquals(entity);
      });
   }

   protected void drop(ServerWorld world, DamageSource damageSource) {
      ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
      if (!itemStack.isEmpty()) {
         this.dropStack(world, itemStack);
         this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      }

      super.drop(world, damageSource);
   }

   public static boolean canJumpChase(FoxEntity fox, LivingEntity chasedEntity) {
      double d = chasedEntity.getZ() - fox.getZ();
      double e = chasedEntity.getX() - fox.getX();
      double f = d / e;
      int i = true;

      for(int j = 0; j < 6; ++j) {
         double g = f == 0.0 ? 0.0 : d * (double)((float)j / 6.0F);
         double h = f == 0.0 ? e * (double)((float)j / 6.0F) : g / f;

         for(int k = 1; k < 4; ++k) {
            if (!fox.getWorld().getBlockState(BlockPos.ofFloored(fox.getX() + h, fox.getY() + (double)k, fox.getZ() + g)).isReplaceable()) {
               return false;
            }
         }
      }

      return true;
   }

   public Vec3d getLeashOffset() {
      return new Vec3d(0.0, (double)(0.55F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
   }

   // $FF: synthetic method
   @Nullable
   public PassiveEntity createChild(final ServerWorld world, final PassiveEntity entity) {
      return this.createChild(world, entity);
   }

   static {
      VARIANT = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.INTEGER);
      FOX_FLAGS = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.BYTE);
      OWNER = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.LAZY_ENTITY_REFERENCE);
      OTHER_TRUSTED = DataTracker.registerData(FoxEntity.class, TrackedDataHandlerRegistry.LAZY_ENTITY_REFERENCE);
      PICKABLE_DROP_FILTER = (item) -> {
         return !item.cannotPickup() && item.isAlive();
      };
      JUST_ATTACKED_SOMETHING_FILTER = (entity) -> {
         if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
         } else {
            return livingEntity.getAttacking() != null && livingEntity.getLastAttackTime() < livingEntity.age + 600;
         }
      };
      CHICKEN_AND_RABBIT_FILTER = (entity) -> {
         return entity instanceof ChickenEntity || entity instanceof RabbitEntity;
      };
      NOTICEABLE_PLAYER_FILTER = (entity) -> {
         return !entity.isSneaky() && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity);
      };
      BABY_BASE_DIMENSIONS = EntityType.FOX.getDimensions().scaled(0.5F).withEyeHeight(0.2975F);
      TRUSTED_ENTITIES_CODEC = LazyEntityReference.createCodec().listOf();
   }

   public class FoxLookControl extends LookControl {
      public FoxLookControl() {
         super(FoxEntity.this);
      }

      public void tick() {
         if (!FoxEntity.this.isSleeping()) {
            super.tick();
         }

      }

      protected boolean shouldStayHorizontal() {
         return !FoxEntity.this.isChasing() && !FoxEntity.this.isInSneakingPose() && !FoxEntity.this.isRollingHead() && !FoxEntity.this.isWalking();
      }
   }

   class FoxMoveControl extends MoveControl {
      public FoxMoveControl() {
         super(FoxEntity.this);
      }

      public void tick() {
         if (FoxEntity.this.wantsToPickupItem()) {
            super.tick();
         }

      }
   }

   public static enum Variant implements StringIdentifiable {
      RED(0, "red"),
      SNOW(1, "snow");

      public static final Variant DEFAULT = RED;
      public static final StringIdentifiable.EnumCodec CODEC = StringIdentifiable.createCodec(Variant::values);
      private static final IntFunction INDEX_MAPPER = ValueLists.createIndexToValueFunction(Variant::getIndex, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
      public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, Variant::getIndex);
      private final int index;
      private final String id;

      private Variant(final int index, final String id) {
         this.index = index;
         this.id = id;
      }

      public String asString() {
         return this.id;
      }

      public int getIndex() {
         return this.index;
      }

      public static Variant byIndex(int index) {
         return (Variant)INDEX_MAPPER.apply(index);
      }

      public static Variant fromBiome(RegistryEntry biome) {
         return biome.isIn(BiomeTags.SPAWNS_SNOW_FOXES) ? SNOW : RED;
      }

      // $FF: synthetic method
      private static Variant[] method_36637() {
         return new Variant[]{RED, SNOW};
      }
   }

   class FoxSwimGoal extends SwimGoal {
      public FoxSwimGoal() {
         super(FoxEntity.this);
      }

      public void start() {
         super.start();
         FoxEntity.this.stopActions();
      }

      public boolean canStart() {
         return FoxEntity.this.isTouchingWater() && FoxEntity.this.getFluidHeight(FluidTags.WATER) > 0.25 || FoxEntity.this.isInLava();
      }
   }

   private class StopWanderingGoal extends Goal {
      int timer;

      public StopWanderingGoal() {
         this.setControls(EnumSet.of(Goal.Control.LOOK, Goal.Control.JUMP, Goal.Control.MOVE));
      }

      public boolean canStart() {
         return FoxEntity.this.isWalking();
      }

      public boolean shouldContinue() {
         return this.canStart() && this.timer > 0;
      }

      public void start() {
         this.timer = this.getTickCount(40);
      }

      public void stop() {
         FoxEntity.this.setWalking(false);
      }

      public void tick() {
         --this.timer;
      }
   }

   private class EscapeWhenNotAggressiveGoal extends EscapeDangerGoal {
      public EscapeWhenNotAggressiveGoal(final double speed) {
         super(FoxEntity.this, speed);
      }

      public boolean isInDanger() {
         return !FoxEntity.this.isAggressive() && super.isInDanger();
      }
   }

   private class MateGoal extends AnimalMateGoal {
      public MateGoal(final FoxEntity fox, final double chance) {
         super(fox, chance);
      }

      public void start() {
         ((FoxEntity)this.animal).stopActions();
         ((FoxEntity)this.mate).stopActions();
         super.start();
      }

      protected void breed() {
         ServerWorld serverWorld = this.world;
         FoxEntity foxEntity = (FoxEntity)this.animal.createChild(serverWorld, this.mate);
         if (foxEntity != null) {
            ServerPlayerEntity serverPlayerEntity = this.animal.getLovingPlayer();
            ServerPlayerEntity serverPlayerEntity2 = this.mate.getLovingPlayer();
            ServerPlayerEntity serverPlayerEntity3 = serverPlayerEntity;
            if (serverPlayerEntity != null) {
               foxEntity.trust((LivingEntity)serverPlayerEntity);
            } else {
               serverPlayerEntity3 = serverPlayerEntity2;
            }

            if (serverPlayerEntity2 != null && serverPlayerEntity != serverPlayerEntity2) {
               foxEntity.trust((LivingEntity)serverPlayerEntity2);
            }

            if (serverPlayerEntity3 != null) {
               serverPlayerEntity3.incrementStat(Stats.ANIMALS_BRED);
               Criteria.BRED_ANIMALS.trigger(serverPlayerEntity3, this.animal, this.mate, foxEntity);
            }

            this.animal.setBreedingAge(6000);
            this.mate.setBreedingAge(6000);
            this.animal.resetLoveTicks();
            this.mate.resetLoveTicks();
            foxEntity.setBreedingAge(-24000);
            foxEntity.refreshPositionAndAngles(this.animal.getX(), this.animal.getY(), this.animal.getZ(), 0.0F, 0.0F);
            serverWorld.spawnEntityAndPassengers(foxEntity);
            this.world.sendEntityStatus(this.animal, (byte)18);
            if (serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
               this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getRandom().nextInt(7) + 1));
            }

         }
      }
   }

   private class MoveToHuntGoal extends Goal {
      public MoveToHuntGoal() {
         this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
      }

      public boolean canStart() {
         if (FoxEntity.this.isSleeping()) {
            return false;
         } else {
            LivingEntity livingEntity = FoxEntity.this.getTarget();
            return livingEntity != null && livingEntity.isAlive() && FoxEntity.CHICKEN_AND_RABBIT_FILTER.test(livingEntity) && FoxEntity.this.squaredDistanceTo(livingEntity) > 36.0 && !FoxEntity.this.isInSneakingPose() && !FoxEntity.this.isRollingHead() && !FoxEntity.this.jumping;
         }
      }

      public void start() {
         FoxEntity.this.setSitting(false);
         FoxEntity.this.setWalking(false);
      }

      public void stop() {
         LivingEntity livingEntity = FoxEntity.this.getTarget();
         if (livingEntity != null && FoxEntity.canJumpChase(FoxEntity.this, livingEntity)) {
            FoxEntity.this.setRollingHead(true);
            FoxEntity.this.setCrouching(true);
            FoxEntity.this.getNavigation().stop();
            FoxEntity.this.getLookControl().lookAt(livingEntity, (float)FoxEntity.this.getMaxHeadRotation(), (float)FoxEntity.this.getMaxLookPitchChange());
         } else {
            FoxEntity.this.setRollingHead(false);
            FoxEntity.this.setCrouching(false);
         }

      }

      public void tick() {
         LivingEntity livingEntity = FoxEntity.this.getTarget();
         if (livingEntity != null) {
            FoxEntity.this.getLookControl().lookAt(livingEntity, (float)FoxEntity.this.getMaxHeadRotation(), (float)FoxEntity.this.getMaxLookPitchChange());
            if (FoxEntity.this.squaredDistanceTo(livingEntity) <= 36.0) {
               FoxEntity.this.setRollingHead(true);
               FoxEntity.this.setCrouching(true);
               FoxEntity.this.getNavigation().stop();
            } else {
               FoxEntity.this.getNavigation().startMovingTo(livingEntity, 1.5);
            }

         }
      }
   }

   public class JumpChasingGoal extends DiveJumpingGoal {
      public boolean canStart() {
         if (!FoxEntity.this.isFullyCrouched()) {
            return false;
         } else {
            LivingEntity livingEntity = FoxEntity.this.getTarget();
            if (livingEntity != null && livingEntity.isAlive()) {
               if (livingEntity.getMovementDirection() != livingEntity.getHorizontalFacing()) {
                  return false;
               } else {
                  boolean bl = FoxEntity.canJumpChase(FoxEntity.this, livingEntity);
                  if (!bl) {
                     FoxEntity.this.getNavigation().findPathTo((Entity)livingEntity, 0);
                     FoxEntity.this.setCrouching(false);
                     FoxEntity.this.setRollingHead(false);
                  }

                  return bl;
               }
            } else {
               return false;
            }
         }
      }

      public boolean shouldContinue() {
         LivingEntity livingEntity = FoxEntity.this.getTarget();
         if (livingEntity != null && livingEntity.isAlive()) {
            double d = FoxEntity.this.getVelocity().y;
            return (!(d * d < 0.05000000074505806) || !(Math.abs(FoxEntity.this.getPitch()) < 15.0F) || !FoxEntity.this.isOnGround()) && !FoxEntity.this.isWalking();
         } else {
            return false;
         }
      }

      public boolean canStop() {
         return false;
      }

      public void start() {
         FoxEntity.this.setJumping(true);
         FoxEntity.this.setChasing(true);
         FoxEntity.this.setRollingHead(false);
         LivingEntity livingEntity = FoxEntity.this.getTarget();
         if (livingEntity != null) {
            FoxEntity.this.getLookControl().lookAt(livingEntity, 60.0F, 30.0F);
            Vec3d vec3d = (new Vec3d(livingEntity.getX() - FoxEntity.this.getX(), livingEntity.getY() - FoxEntity.this.getY(), livingEntity.getZ() - FoxEntity.this.getZ())).normalize();
            FoxEntity.this.setVelocity(FoxEntity.this.getVelocity().add(vec3d.x * 0.8, 0.9, vec3d.z * 0.8));
         }

         FoxEntity.this.getNavigation().stop();
      }

      public void stop() {
         FoxEntity.this.setCrouching(false);
         FoxEntity.this.extraRollingHeight = 0.0F;
         FoxEntity.this.lastExtraRollingHeight = 0.0F;
         FoxEntity.this.setRollingHead(false);
         FoxEntity.this.setChasing(false);
      }

      public void tick() {
         LivingEntity livingEntity = FoxEntity.this.getTarget();
         if (livingEntity != null) {
            FoxEntity.this.getLookControl().lookAt(livingEntity, 60.0F, 30.0F);
         }

         if (!FoxEntity.this.isWalking()) {
            Vec3d vec3d = FoxEntity.this.getVelocity();
            if (vec3d.y * vec3d.y < 0.029999999329447746 && FoxEntity.this.getPitch() != 0.0F) {
               FoxEntity.this.setPitch(MathHelper.lerpAngleDegrees(0.2F, FoxEntity.this.getPitch(), 0.0F));
            } else {
               double d = vec3d.horizontalLength();
               double e = Math.signum(-vec3d.y) * Math.acos(d / vec3d.length()) * 57.2957763671875;
               FoxEntity.this.setPitch((float)e);
            }
         }

         if (livingEntity != null && FoxEntity.this.distanceTo(livingEntity) <= 2.0F) {
            FoxEntity.this.tryAttack(castToServerWorld(FoxEntity.this.getWorld()), livingEntity);
         } else if (FoxEntity.this.getPitch() > 0.0F && FoxEntity.this.isOnGround() && (float)FoxEntity.this.getVelocity().y != 0.0F && FoxEntity.this.getWorld().getBlockState(FoxEntity.this.getBlockPos()).isOf(Blocks.SNOW)) {
            FoxEntity.this.setPitch(60.0F);
            FoxEntity.this.setTarget((LivingEntity)null);
            FoxEntity.this.setWalking(true);
         }

      }
   }

   private class AvoidDaylightGoal extends EscapeSunlightGoal {
      private int timer = toGoalTicks(100);

      public AvoidDaylightGoal(final double speed) {
         super(FoxEntity.this, speed);
      }

      public boolean canStart() {
         if (!FoxEntity.this.isSleeping() && this.mob.getTarget() == null) {
            if (FoxEntity.this.getWorld().isThundering() && FoxEntity.this.getWorld().isSkyVisible(this.mob.getBlockPos())) {
               return this.targetShadedPos();
            } else if (this.timer > 0) {
               --this.timer;
               return false;
            } else {
               this.timer = 100;
               BlockPos blockPos = this.mob.getBlockPos();
               return FoxEntity.this.getWorld().isDay() && FoxEntity.this.getWorld().isSkyVisible(blockPos) && !((ServerWorld)FoxEntity.this.getWorld()).isNearOccupiedPointOfInterest(blockPos) && this.targetShadedPos();
            }
         } else {
            return false;
         }
      }

      public void start() {
         FoxEntity.this.stopActions();
         super.start();
      }
   }

   private class AttackGoal extends MeleeAttackGoal {
      public AttackGoal(final double speed, final boolean pauseWhenIdle) {
         super(FoxEntity.this, speed, pauseWhenIdle);
      }

      protected void attack(LivingEntity target) {
         if (this.canAttack(target)) {
            this.resetCooldown();
            this.mob.tryAttack(getServerWorld(this.mob), target);
            FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_BITE, 1.0F, 1.0F);
         }

      }

      public void start() {
         FoxEntity.this.setRollingHead(false);
         super.start();
      }

      public boolean canStart() {
         return !FoxEntity.this.isSitting() && !FoxEntity.this.isSleeping() && !FoxEntity.this.isInSneakingPose() && !FoxEntity.this.isWalking() && super.canStart();
      }
   }

   class DelayedCalmDownGoal extends CalmDownGoal {
      private static final int MAX_CALM_DOWN_TIME = toGoalTicks(140);
      private int timer;

      public DelayedCalmDownGoal() {
         super();
         this.timer = FoxEntity.this.random.nextInt(MAX_CALM_DOWN_TIME);
         this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Goal.Control.JUMP));
      }

      public boolean canStart() {
         if (FoxEntity.this.sidewaysSpeed == 0.0F && FoxEntity.this.upwardSpeed == 0.0F && FoxEntity.this.forwardSpeed == 0.0F) {
            return this.canNotCalmDown() || FoxEntity.this.isSleeping();
         } else {
            return false;
         }
      }

      public boolean shouldContinue() {
         return this.canNotCalmDown();
      }

      private boolean canNotCalmDown() {
         if (this.timer > 0) {
            --this.timer;
            return false;
         } else {
            return FoxEntity.this.getWorld().isDay() && this.isAtFavoredLocation() && !this.canCalmDown() && !FoxEntity.this.inPowderSnow;
         }
      }

      public void stop() {
         this.timer = FoxEntity.this.random.nextInt(MAX_CALM_DOWN_TIME);
         FoxEntity.this.stopActions();
      }

      public void start() {
         FoxEntity.this.setSitting(false);
         FoxEntity.this.setCrouching(false);
         FoxEntity.this.setRollingHead(false);
         FoxEntity.this.setJumping(false);
         FoxEntity.this.setSleeping(true);
         FoxEntity.this.getNavigation().stop();
         FoxEntity.this.getMoveControl().moveTo(FoxEntity.this.getX(), FoxEntity.this.getY(), FoxEntity.this.getZ(), 0.0);
      }
   }

   private static class FollowParentGoal extends net.minecraft.entity.ai.goal.FollowParentGoal {
      private final FoxEntity fox;

      public FollowParentGoal(FoxEntity fox, double speed) {
         super(fox, speed);
         this.fox = fox;
      }

      public boolean canStart() {
         return !this.fox.isAggressive() && super.canStart();
      }

      public boolean shouldContinue() {
         return !this.fox.isAggressive() && super.shouldContinue();
      }

      public void start() {
         this.fox.stopActions();
         super.start();
      }
   }

   private class GoToVillageGoal extends net.minecraft.entity.ai.goal.GoToVillageGoal {
      public GoToVillageGoal(final int unused, final int searchRange) {
         super(FoxEntity.this, searchRange);
      }

      public void start() {
         FoxEntity.this.stopActions();
         super.start();
      }

      public boolean canStart() {
         return super.canStart() && this.canGoToVillage();
      }

      public boolean shouldContinue() {
         return super.shouldContinue() && this.canGoToVillage();
      }

      private boolean canGoToVillage() {
         return !FoxEntity.this.isSleeping() && !FoxEntity.this.isSitting() && !FoxEntity.this.isAggressive() && FoxEntity.this.getTarget() == null;
      }
   }

   public class EatBerriesGoal extends MoveToTargetPosGoal {
      private static final int EATING_TIME = 40;
      protected int timer;

      public EatBerriesGoal(final double speed, final int range, final int maxYDifference) {
         super(FoxEntity.this, speed, range, maxYDifference);
      }

      public double getDesiredDistanceToTarget() {
         return 2.0;
      }

      public boolean shouldResetPath() {
         return this.tryingTime % 100 == 0;
      }

      protected boolean isTargetPos(WorldView world, BlockPos pos) {
         BlockState blockState = world.getBlockState(pos);
         return blockState.isOf(Blocks.SWEET_BERRY_BUSH) && (Integer)blockState.get(SweetBerryBushBlock.AGE) >= 2 || CaveVines.hasBerries(blockState);
      }

      public void tick() {
         if (this.hasReached()) {
            if (this.timer >= 40) {
               this.eatBerries();
            } else {
               ++this.timer;
            }
         } else if (!this.hasReached() && FoxEntity.this.random.nextFloat() < 0.05F) {
            FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_SNIFF, 1.0F, 1.0F);
         }

         super.tick();
      }

      protected void eatBerries() {
         if (castToServerWorld(FoxEntity.this.getWorld()).getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            BlockState blockState = FoxEntity.this.getWorld().getBlockState(this.targetPos);
            if (blockState.isOf(Blocks.SWEET_BERRY_BUSH)) {
               this.pickSweetBerries(blockState);
            } else if (CaveVines.hasBerries(blockState)) {
               this.pickGlowBerries(blockState);
            }

         }
      }

      private void pickGlowBerries(BlockState state) {
         CaveVines.pickBerries(FoxEntity.this, state, FoxEntity.this.getWorld(), this.targetPos);
      }

      private void pickSweetBerries(BlockState state) {
         int i = (Integer)state.get(SweetBerryBushBlock.AGE);
         state.with(SweetBerryBushBlock.AGE, 1);
         int j = 1 + FoxEntity.this.getWorld().random.nextInt(2) + (i == 3 ? 1 : 0);
         ItemStack itemStack = FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
         if (itemStack.isEmpty()) {
            FoxEntity.this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.SWEET_BERRIES));
            --j;
         }

         if (j > 0) {
            Block.dropStack(FoxEntity.this.getWorld(), this.targetPos, new ItemStack(Items.SWEET_BERRIES, j));
         }

         FoxEntity.this.playSound(SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, 1.0F, 1.0F);
         FoxEntity.this.getWorld().setBlockState(this.targetPos, (BlockState)state.with(SweetBerryBushBlock.AGE, 1), 2);
         FoxEntity.this.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, this.targetPos, GameEvent.Emitter.of((Entity)FoxEntity.this));
      }

      public boolean canStart() {
         return !FoxEntity.this.isSleeping() && super.canStart();
      }

      public void start() {
         this.timer = 0;
         FoxEntity.this.setSitting(false);
         super.start();
      }
   }

   class PickupItemGoal extends Goal {
      public PickupItemGoal() {
         this.setControls(EnumSet.of(Goal.Control.MOVE));
      }

      public boolean canStart() {
         if (!FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
            return false;
         } else if (FoxEntity.this.getTarget() == null && FoxEntity.this.getAttacker() == null) {
            if (!FoxEntity.this.wantsToPickupItem()) {
               return false;
            } else if (FoxEntity.this.getRandom().nextInt(toGoalTicks(10)) != 0) {
               return false;
            } else {
               List list = FoxEntity.this.getWorld().getEntitiesByClass(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), FoxEntity.PICKABLE_DROP_FILTER);
               return !list.isEmpty() && FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
            }
         } else {
            return false;
         }
      }

      public void tick() {
         List list = FoxEntity.this.getWorld().getEntitiesByClass(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), FoxEntity.PICKABLE_DROP_FILTER);
         ItemStack itemStack = FoxEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
         if (itemStack.isEmpty() && !list.isEmpty()) {
            FoxEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2000000476837158);
         }

      }

      public void start() {
         List list = FoxEntity.this.getWorld().getEntitiesByClass(ItemEntity.class, FoxEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), FoxEntity.PICKABLE_DROP_FILTER);
         if (!list.isEmpty()) {
            FoxEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2000000476837158);
         }

      }
   }

   class LookAtEntityGoal extends net.minecraft.entity.ai.goal.LookAtEntityGoal {
      public LookAtEntityGoal(final MobEntity fox, final Class targetType, final float range) {
         super(fox, targetType, range);
      }

      public boolean canStart() {
         return super.canStart() && !FoxEntity.this.isWalking() && !FoxEntity.this.isRollingHead();
      }

      public boolean shouldContinue() {
         return super.shouldContinue() && !FoxEntity.this.isWalking() && !FoxEntity.this.isRollingHead();
      }
   }

   class SitDownAndLookAroundGoal extends CalmDownGoal {
      private double lookX;
      private double lookZ;
      private int timer;
      private int counter;

      public SitDownAndLookAroundGoal() {
         super();
         this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
      }

      public boolean canStart() {
         return FoxEntity.this.getAttacker() == null && FoxEntity.this.getRandom().nextFloat() < 0.02F && !FoxEntity.this.isSleeping() && FoxEntity.this.getTarget() == null && FoxEntity.this.getNavigation().isIdle() && !this.canCalmDown() && !FoxEntity.this.isChasing() && !FoxEntity.this.isInSneakingPose();
      }

      public boolean shouldContinue() {
         return this.counter > 0;
      }

      public void start() {
         this.chooseNewAngle();
         this.counter = 2 + FoxEntity.this.getRandom().nextInt(3);
         FoxEntity.this.setSitting(true);
         FoxEntity.this.getNavigation().stop();
      }

      public void stop() {
         FoxEntity.this.setSitting(false);
      }

      public void tick() {
         --this.timer;
         if (this.timer <= 0) {
            --this.counter;
            this.chooseNewAngle();
         }

         FoxEntity.this.getLookControl().lookAt(FoxEntity.this.getX() + this.lookX, FoxEntity.this.getEyeY(), FoxEntity.this.getZ() + this.lookZ, (float)FoxEntity.this.getMaxHeadRotation(), (float)FoxEntity.this.getMaxLookPitchChange());
      }

      private void chooseNewAngle() {
         double d = 6.283185307179586 * FoxEntity.this.getRandom().nextDouble();
         this.lookX = Math.cos(d);
         this.lookZ = Math.sin(d);
         this.timer = this.getTickCount(80 + FoxEntity.this.getRandom().nextInt(20));
      }
   }

   private class DefendFriendGoal extends ActiveTargetGoal {
      @Nullable
      private LivingEntity offender;
      @Nullable
      private LivingEntity friend;
      private int lastAttackedTime;

      public DefendFriendGoal(final Class targetEntityClass, final boolean checkVisibility, final boolean checkCanNavigate, @Nullable final TargetPredicate.EntityPredicate targetPredicate) {
         super(FoxEntity.this, targetEntityClass, 10, checkVisibility, checkCanNavigate, targetPredicate);
      }

      public boolean canStart() {
         if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
            return false;
         } else {
            ServerWorld serverWorld = castToServerWorld(FoxEntity.this.getWorld());
            Iterator var2 = FoxEntity.this.getTrustedEntities().toList().iterator();

            LivingEntity livingEntity;
            do {
               if (!var2.hasNext()) {
                  return false;
               }

               LazyEntityReference lazyEntityReference = (LazyEntityReference)var2.next();
               livingEntity = (LivingEntity)lazyEntityReference.resolve(serverWorld, LivingEntity.class);
            } while(livingEntity == null);

            this.friend = livingEntity;
            this.offender = livingEntity.getAttacker();
            int i = livingEntity.getLastAttackedTime();
            return i != this.lastAttackedTime && this.canTrack(this.offender, this.targetPredicate);
         }
      }

      public void start() {
         this.setTargetEntity(this.offender);
         this.targetEntity = this.offender;
         if (this.friend != null) {
            this.lastAttackedTime = this.friend.getLastAttackedTime();
         }

         FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_AGGRO, 1.0F, 1.0F);
         FoxEntity.this.setAggressive(true);
         FoxEntity.this.stopSleeping();
         super.start();
      }
   }

   public static class FoxData extends PassiveEntity.PassiveData {
      public final Variant type;

      public FoxData(Variant type) {
         super(false);
         this.type = type;
      }
   }

   private abstract class CalmDownGoal extends Goal {
      private final TargetPredicate WORRIABLE_ENTITY_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(12.0).ignoreVisibility().setPredicate(FoxEntity.this.new WorriableEntityFilter());

      CalmDownGoal() {
      }

      protected boolean isAtFavoredLocation() {
         BlockPos blockPos = BlockPos.ofFloored(FoxEntity.this.getX(), FoxEntity.this.getBoundingBox().maxY, FoxEntity.this.getZ());
         return !FoxEntity.this.getWorld().isSkyVisible(blockPos) && FoxEntity.this.getPathfindingFavor(blockPos) >= 0.0F;
      }

      protected boolean canCalmDown() {
         return !castToServerWorld(FoxEntity.this.getWorld()).getTargets(LivingEntity.class, this.WORRIABLE_ENTITY_PREDICATE, FoxEntity.this, FoxEntity.this.getBoundingBox().expand(12.0, 6.0, 12.0)).isEmpty();
      }
   }

   public class WorriableEntityFilter implements TargetPredicate.EntityPredicate {
      public boolean test(LivingEntity livingEntity, ServerWorld serverWorld) {
         if (livingEntity instanceof FoxEntity) {
            return false;
         } else if (!(livingEntity instanceof ChickenEntity) && !(livingEntity instanceof RabbitEntity) && !(livingEntity instanceof HostileEntity)) {
            if (livingEntity instanceof TameableEntity) {
               return !((TameableEntity)livingEntity).isTamed();
            } else {
               if (livingEntity instanceof PlayerEntity) {
                  PlayerEntity playerEntity = (PlayerEntity)livingEntity;
                  if (playerEntity.isSpectator() || playerEntity.isCreative()) {
                     return false;
                  }
               }

               if (FoxEntity.this.canTrust(livingEntity)) {
                  return false;
               } else {
                  return !livingEntity.isSleeping() && !livingEntity.isSneaky();
               }
            }
         } else {
            return true;
         }
      }
   }
}
