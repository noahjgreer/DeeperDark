package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.ai.goal.FollowMobGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SitOnOwnerShoulderGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class ParrotEntity extends TameableShoulderEntity implements Flutterer {
   private static final TrackedData VARIANT;
   private static final Predicate CAN_IMITATE;
   static final Map MOB_SOUNDS;
   public float flapProgress;
   public float maxWingDeviation;
   public float lastMaxWingDeviation;
   public float lastFlapProgress;
   private float flapSpeed = 1.0F;
   private float field_28640 = 1.0F;
   private boolean songPlaying;
   @Nullable
   private BlockPos songSource;

   public ParrotEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.moveControl = new FlightMoveControl(this, 10, false);
      this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
      this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
      this.setPathfindingPenalty(PathNodeType.COCOA, -1.0F);
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      this.setVariant((Variant)Util.getRandom((Object[])ParrotEntity.Variant.values(), world.getRandom()));
      if (entityData == null) {
         entityData = new PassiveEntity.PassiveData(false);
      }

      return super.initialize(world, difficulty, spawnReason, (EntityData)entityData);
   }

   public boolean isBaby() {
      return false;
   }

   protected void initGoals() {
      this.goalSelector.add(0, new TameableEntity.TameableEscapeDangerGoal(1.25));
      this.goalSelector.add(0, new SwimGoal(this));
      this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.add(2, new SitGoal(this));
      this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0, 5.0F, 1.0F));
      this.goalSelector.add(2, new FlyOntoTreeGoal(this, 1.0));
      this.goalSelector.add(3, new SitOnOwnerShoulderGoal(this));
      this.goalSelector.add(3, new FollowMobGoal(this, 1.0, 3.0F, 7.0F));
   }

   public static DefaultAttributeContainer.Builder createParrotAttributes() {
      return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MAX_HEALTH, 6.0).add(EntityAttributes.FLYING_SPEED, 0.4000000059604645).add(EntityAttributes.MOVEMENT_SPEED, 0.20000000298023224).add(EntityAttributes.ATTACK_DAMAGE, 3.0);
   }

   protected EntityNavigation createNavigation(World world) {
      BirdNavigation birdNavigation = new BirdNavigation(this, world);
      birdNavigation.setCanOpenDoors(false);
      birdNavigation.setCanSwim(true);
      return birdNavigation;
   }

   public void tickMovement() {
      if (this.songSource == null || !this.songSource.isWithinDistance(this.getPos(), 3.46) || !this.getWorld().getBlockState(this.songSource).isOf(Blocks.JUKEBOX)) {
         this.songPlaying = false;
         this.songSource = null;
      }

      if (this.getWorld().random.nextInt(400) == 0) {
         imitateNearbyMob(this.getWorld(), this);
      }

      super.tickMovement();
      this.flapWings();
   }

   public void setNearbySongPlaying(BlockPos songPosition, boolean playing) {
      this.songSource = songPosition;
      this.songPlaying = playing;
   }

   public boolean isSongPlaying() {
      return this.songPlaying;
   }

   private void flapWings() {
      this.lastFlapProgress = this.flapProgress;
      this.lastMaxWingDeviation = this.maxWingDeviation;
      this.maxWingDeviation += (float)(!this.isOnGround() && !this.hasVehicle() ? 4 : -1) * 0.3F;
      this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0F, 1.0F);
      if (!this.isOnGround() && this.flapSpeed < 1.0F) {
         this.flapSpeed = 1.0F;
      }

      this.flapSpeed *= 0.9F;
      Vec3d vec3d = this.getVelocity();
      if (!this.isOnGround() && vec3d.y < 0.0) {
         this.setVelocity(vec3d.multiply(1.0, 0.6, 1.0));
      }

      this.flapProgress += this.flapSpeed * 2.0F;
   }

   public static boolean imitateNearbyMob(World world, Entity parrot) {
      if (parrot.isAlive() && !parrot.isSilent() && world.random.nextInt(2) == 0) {
         List list = world.getEntitiesByClass(MobEntity.class, parrot.getBoundingBox().expand(20.0), CAN_IMITATE);
         if (!list.isEmpty()) {
            MobEntity mobEntity = (MobEntity)list.get(world.random.nextInt(list.size()));
            if (!mobEntity.isSilent()) {
               SoundEvent soundEvent = getSound(mobEntity.getType());
               world.playSound((Entity)null, parrot.getX(), parrot.getY(), parrot.getZ(), (SoundEvent)soundEvent, parrot.getSoundCategory(), 0.7F, getSoundPitch(world.random));
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      if (!this.isTamed() && itemStack.isIn(ItemTags.PARROT_FOOD)) {
         this.eat(player, hand, itemStack);
         if (!this.isSilent()) {
            this.getWorld().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), (SoundEvent)SoundEvents.ENTITY_PARROT_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
         }

         if (!this.getWorld().isClient) {
            if (this.random.nextInt(10) == 0) {
               this.setTamedBy(player);
               this.getWorld().sendEntityStatus(this, (byte)7);
            } else {
               this.getWorld().sendEntityStatus(this, (byte)6);
            }
         }

         return ActionResult.SUCCESS;
      } else if (!itemStack.isIn(ItemTags.PARROT_POISONOUS_FOOD)) {
         if (!this.isInAir() && this.isTamed() && this.isOwner(player)) {
            if (!this.getWorld().isClient) {
               this.setSitting(!this.isSitting());
            }

            return ActionResult.SUCCESS;
         } else {
            return super.interactMob(player, hand);
         }
      } else {
         this.eat(player, hand, itemStack);
         this.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 900));
         if (player.isCreative() || !this.isInvulnerable()) {
            this.serverDamage(this.getDamageSources().playerAttack(player), Float.MAX_VALUE);
         }

         return ActionResult.SUCCESS;
      }
   }

   public boolean isBreedingItem(ItemStack stack) {
      return false;
   }

   public static boolean canSpawn(EntityType type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
      return world.getBlockState(pos.down()).isIn(BlockTags.PARROTS_SPAWNABLE_ON) && isLightLevelValidForNaturalSpawn(world, pos);
   }

   protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
   }

   public boolean canBreedWith(AnimalEntity other) {
      return false;
   }

   @Nullable
   public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
      return null;
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return getRandomSound(this.getWorld(), this.getWorld().random);
   }

   public static SoundEvent getRandomSound(World world, Random random) {
      if (world.getDifficulty() != Difficulty.PEACEFUL && random.nextInt(1000) == 0) {
         List list = Lists.newArrayList(MOB_SOUNDS.keySet());
         return getSound((EntityType)list.get(random.nextInt(list.size())));
      } else {
         return SoundEvents.ENTITY_PARROT_AMBIENT;
      }
   }

   private static SoundEvent getSound(EntityType imitate) {
      return (SoundEvent)MOB_SOUNDS.getOrDefault(imitate, SoundEvents.ENTITY_PARROT_AMBIENT);
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_PARROT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PARROT_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(SoundEvents.ENTITY_PARROT_STEP, 0.15F, 1.0F);
   }

   protected boolean isFlappingWings() {
      return this.speed > this.field_28640;
   }

   protected void addFlapEffects() {
      this.playSound(SoundEvents.ENTITY_PARROT_FLY, 0.15F, 1.0F);
      this.field_28640 = this.speed + this.maxWingDeviation / 2.0F;
   }

   public float getSoundPitch() {
      return getSoundPitch(this.random);
   }

   public static float getSoundPitch(Random random) {
      return (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.NEUTRAL;
   }

   public boolean isPushable() {
      return true;
   }

   protected void pushAway(Entity entity) {
      if (!(entity instanceof PlayerEntity)) {
         super.pushAway(entity);
      }
   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      if (this.isInvulnerableTo(world, source)) {
         return false;
      } else {
         this.setSitting(false);
         return super.damage(world, source, amount);
      }
   }

   public Variant getVariant() {
      return ParrotEntity.Variant.byIndex((Integer)this.dataTracker.get(VARIANT));
   }

   private void setVariant(Variant variant) {
      this.dataTracker.set(VARIANT, variant.index);
   }

   @Nullable
   public Object get(ComponentType type) {
      return type == DataComponentTypes.PARROT_VARIANT ? castComponentValue(type, this.getVariant()) : super.get(type);
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.PARROT_VARIANT);
      super.copyComponentsFrom(from);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.PARROT_VARIANT) {
         this.setVariant((Variant)castComponentValue(DataComponentTypes.PARROT_VARIANT, value));
         return true;
      } else {
         return super.setApplicableComponent(type, value);
      }
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(VARIANT, ParrotEntity.Variant.DEFAULT.index);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.put("Variant", ParrotEntity.Variant.INDEX_CODEC, this.getVariant());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setVariant((Variant)view.read("Variant", ParrotEntity.Variant.INDEX_CODEC).orElse(ParrotEntity.Variant.DEFAULT));
   }

   public boolean isInAir() {
      return !this.isOnGround();
   }

   protected boolean canTeleportOntoLeaves() {
      return true;
   }

   public Vec3d getLeashOffset() {
      return new Vec3d(0.0, (double)(0.5F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
   }

   static {
      VARIANT = DataTracker.registerData(ParrotEntity.class, TrackedDataHandlerRegistry.INTEGER);
      CAN_IMITATE = new Predicate() {
         public boolean test(@Nullable MobEntity mobEntity) {
            return mobEntity != null && ParrotEntity.MOB_SOUNDS.containsKey(mobEntity.getType());
         }

         // $FF: synthetic method
         public boolean test(@Nullable final Object entity) {
            return this.test((MobEntity)entity);
         }
      };
      MOB_SOUNDS = (Map)Util.make(Maps.newHashMap(), (map) -> {
         map.put(EntityType.BLAZE, SoundEvents.ENTITY_PARROT_IMITATE_BLAZE);
         map.put(EntityType.BOGGED, SoundEvents.ENTITY_PARROT_IMITATE_BOGGED);
         map.put(EntityType.BREEZE, SoundEvents.ENTITY_PARROT_IMITATE_BREEZE);
         map.put(EntityType.CAVE_SPIDER, SoundEvents.ENTITY_PARROT_IMITATE_SPIDER);
         map.put(EntityType.CREAKING, SoundEvents.ENTITY_PARROT_IMITATE_CREAKING);
         map.put(EntityType.CREEPER, SoundEvents.ENTITY_PARROT_IMITATE_CREEPER);
         map.put(EntityType.DROWNED, SoundEvents.ENTITY_PARROT_IMITATE_DROWNED);
         map.put(EntityType.ELDER_GUARDIAN, SoundEvents.ENTITY_PARROT_IMITATE_ELDER_GUARDIAN);
         map.put(EntityType.ENDER_DRAGON, SoundEvents.ENTITY_PARROT_IMITATE_ENDER_DRAGON);
         map.put(EntityType.ENDERMITE, SoundEvents.ENTITY_PARROT_IMITATE_ENDERMITE);
         map.put(EntityType.EVOKER, SoundEvents.ENTITY_PARROT_IMITATE_EVOKER);
         map.put(EntityType.GHAST, SoundEvents.ENTITY_PARROT_IMITATE_GHAST);
         map.put(EntityType.HAPPY_GHAST, SoundEvents.INTENTIONALLY_EMPTY);
         map.put(EntityType.GUARDIAN, SoundEvents.ENTITY_PARROT_IMITATE_GUARDIAN);
         map.put(EntityType.HOGLIN, SoundEvents.ENTITY_PARROT_IMITATE_HOGLIN);
         map.put(EntityType.HUSK, SoundEvents.ENTITY_PARROT_IMITATE_HUSK);
         map.put(EntityType.ILLUSIONER, SoundEvents.ENTITY_PARROT_IMITATE_ILLUSIONER);
         map.put(EntityType.MAGMA_CUBE, SoundEvents.ENTITY_PARROT_IMITATE_MAGMA_CUBE);
         map.put(EntityType.PHANTOM, SoundEvents.ENTITY_PARROT_IMITATE_PHANTOM);
         map.put(EntityType.PIGLIN, SoundEvents.ENTITY_PARROT_IMITATE_PIGLIN);
         map.put(EntityType.PIGLIN_BRUTE, SoundEvents.ENTITY_PARROT_IMITATE_PIGLIN_BRUTE);
         map.put(EntityType.PILLAGER, SoundEvents.ENTITY_PARROT_IMITATE_PILLAGER);
         map.put(EntityType.RAVAGER, SoundEvents.ENTITY_PARROT_IMITATE_RAVAGER);
         map.put(EntityType.SHULKER, SoundEvents.ENTITY_PARROT_IMITATE_SHULKER);
         map.put(EntityType.SILVERFISH, SoundEvents.ENTITY_PARROT_IMITATE_SILVERFISH);
         map.put(EntityType.SKELETON, SoundEvents.ENTITY_PARROT_IMITATE_SKELETON);
         map.put(EntityType.SLIME, SoundEvents.ENTITY_PARROT_IMITATE_SLIME);
         map.put(EntityType.SPIDER, SoundEvents.ENTITY_PARROT_IMITATE_SPIDER);
         map.put(EntityType.STRAY, SoundEvents.ENTITY_PARROT_IMITATE_STRAY);
         map.put(EntityType.VEX, SoundEvents.ENTITY_PARROT_IMITATE_VEX);
         map.put(EntityType.VINDICATOR, SoundEvents.ENTITY_PARROT_IMITATE_VINDICATOR);
         map.put(EntityType.WARDEN, SoundEvents.ENTITY_PARROT_IMITATE_WARDEN);
         map.put(EntityType.WITCH, SoundEvents.ENTITY_PARROT_IMITATE_WITCH);
         map.put(EntityType.WITHER, SoundEvents.ENTITY_PARROT_IMITATE_WITHER);
         map.put(EntityType.WITHER_SKELETON, SoundEvents.ENTITY_PARROT_IMITATE_WITHER_SKELETON);
         map.put(EntityType.ZOGLIN, SoundEvents.ENTITY_PARROT_IMITATE_ZOGLIN);
         map.put(EntityType.ZOMBIE, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE);
         map.put(EntityType.ZOMBIE_VILLAGER, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE_VILLAGER);
      });
   }

   public static enum Variant implements StringIdentifiable {
      RED_BLUE(0, "red_blue"),
      BLUE(1, "blue"),
      GREEN(2, "green"),
      YELLOW_BLUE(3, "yellow_blue"),
      GRAY(4, "gray");

      public static final Variant DEFAULT = RED_BLUE;
      private static final IntFunction INDEX_MAPPER = ValueLists.createIndexToValueFunction(Variant::getIndex, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.CLAMP);
      public static final Codec CODEC = StringIdentifiable.createCodec(Variant::values);
      /** @deprecated */
      @Deprecated
      public static final Codec INDEX_CODEC;
      public static final PacketCodec PACKET_CODEC;
      final int index;
      private final String id;

      private Variant(final int index, final String id) {
         this.index = index;
         this.id = id;
      }

      public int getIndex() {
         return this.index;
      }

      public static Variant byIndex(int index) {
         return (Variant)INDEX_MAPPER.apply(index);
      }

      public String asString() {
         return this.id;
      }

      // $FF: synthetic method
      private static Variant[] method_47851() {
         return new Variant[]{RED_BLUE, BLUE, GREEN, YELLOW_BLUE, GRAY};
      }

      static {
         PrimitiveCodec var10000 = Codec.INT;
         IntFunction var10001 = INDEX_MAPPER;
         Objects.requireNonNull(var10001);
         INDEX_CODEC = var10000.xmap(var10001::apply, Variant::getIndex);
         PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, Variant::getIndex);
      }
   }

   static class FlyOntoTreeGoal extends FlyGoal {
      public FlyOntoTreeGoal(PathAwareEntity pathAwareEntity, double d) {
         super(pathAwareEntity, d);
      }

      @Nullable
      protected Vec3d getWanderTarget() {
         Vec3d vec3d = null;
         if (this.mob.isTouchingWater()) {
            vec3d = FuzzyTargeting.find(this.mob, 15, 15);
         }

         if (this.mob.getRandom().nextFloat() >= this.probability) {
            vec3d = this.locateTree();
         }

         return vec3d == null ? super.getWanderTarget() : vec3d;
      }

      @Nullable
      private Vec3d locateTree() {
         BlockPos blockPos = this.mob.getBlockPos();
         BlockPos.Mutable mutable = new BlockPos.Mutable();
         BlockPos.Mutable mutable2 = new BlockPos.Mutable();
         Iterable iterable = BlockPos.iterate(MathHelper.floor(this.mob.getX() - 3.0), MathHelper.floor(this.mob.getY() - 6.0), MathHelper.floor(this.mob.getZ() - 3.0), MathHelper.floor(this.mob.getX() + 3.0), MathHelper.floor(this.mob.getY() + 6.0), MathHelper.floor(this.mob.getZ() + 3.0));
         Iterator var5 = iterable.iterator();

         BlockPos blockPos2;
         boolean bl;
         do {
            do {
               if (!var5.hasNext()) {
                  return null;
               }

               blockPos2 = (BlockPos)var5.next();
            } while(blockPos.equals(blockPos2));

            BlockState blockState = this.mob.getWorld().getBlockState(mutable2.set(blockPos2, (Direction)Direction.DOWN));
            bl = blockState.getBlock() instanceof LeavesBlock || blockState.isIn(BlockTags.LOGS);
         } while(!bl || !this.mob.getWorld().isAir(blockPos2) || !this.mob.getWorld().isAir(mutable.set(blockPos2, (Direction)Direction.UP)));

         return Vec3d.ofBottomCenter(blockPos2);
      }
   }
}
