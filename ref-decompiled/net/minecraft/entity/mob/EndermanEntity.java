package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.provider.EnchantmentProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.UniversalAngerGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class EndermanEntity extends HostileEntity implements Angerable {
   private static final Identifier ATTACKING_SPEED_MODIFIER_ID = Identifier.ofVanilla("attacking");
   private static final EntityAttributeModifier ATTACKING_SPEED_BOOST;
   private static final int field_30462 = 400;
   private static final int field_30461 = 600;
   private static final TrackedData CARRIED_BLOCK;
   private static final TrackedData ANGRY;
   private static final TrackedData PROVOKED;
   private int lastAngrySoundAge = Integer.MIN_VALUE;
   private int ageWhenTargetSet;
   private static final UniformIntProvider ANGER_TIME_RANGE;
   private int angerTime;
   @Nullable
   private UUID angryAt;

   public EndermanEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
   }

   protected void initGoals() {
      this.goalSelector.add(0, new SwimGoal(this));
      this.goalSelector.add(1, new ChasePlayerGoal(this));
      this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
      this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0, 0.0F));
      this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.add(8, new LookAroundGoal(this));
      this.goalSelector.add(10, new PlaceBlockGoal(this));
      this.goalSelector.add(11, new PickUpBlockGoal(this));
      this.targetSelector.add(1, new TeleportTowardsPlayerGoal(this, this::shouldAngerAt));
      this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
      this.targetSelector.add(3, new ActiveTargetGoal(this, EndermiteEntity.class, true, false));
      this.targetSelector.add(4, new UniversalAngerGoal(this, false));
   }

   public static DefaultAttributeContainer.Builder createEndermanAttributes() {
      return HostileEntity.createHostileAttributes().add(EntityAttributes.MAX_HEALTH, 40.0).add(EntityAttributes.MOVEMENT_SPEED, 0.30000001192092896).add(EntityAttributes.ATTACK_DAMAGE, 7.0).add(EntityAttributes.FOLLOW_RANGE, 64.0).add(EntityAttributes.STEP_HEIGHT, 1.0);
   }

   public void setTarget(@Nullable LivingEntity target) {
      super.setTarget(target);
      EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
      if (target == null) {
         this.ageWhenTargetSet = 0;
         this.dataTracker.set(ANGRY, false);
         this.dataTracker.set(PROVOKED, false);
         entityAttributeInstance.removeModifier(ATTACKING_SPEED_MODIFIER_ID);
      } else {
         this.ageWhenTargetSet = this.age;
         this.dataTracker.set(ANGRY, true);
         if (!entityAttributeInstance.hasModifier(ATTACKING_SPEED_MODIFIER_ID)) {
            entityAttributeInstance.addTemporaryModifier(ATTACKING_SPEED_BOOST);
         }
      }

   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(CARRIED_BLOCK, Optional.empty());
      builder.add(ANGRY, false);
      builder.add(PROVOKED, false);
   }

   public void chooseRandomAngerTime() {
      this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
   }

   public void setAngerTime(int angerTime) {
      this.angerTime = angerTime;
   }

   public int getAngerTime() {
      return this.angerTime;
   }

   public void setAngryAt(@Nullable UUID angryAt) {
      this.angryAt = angryAt;
   }

   @Nullable
   public UUID getAngryAt() {
      return this.angryAt;
   }

   public void playAngrySound() {
      if (this.age >= this.lastAngrySoundAge + 400) {
         this.lastAngrySoundAge = this.age;
         if (!this.isSilent()) {
            this.getWorld().playSoundClient(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENTITY_ENDERMAN_STARE, this.getSoundCategory(), 2.5F, 1.0F, false);
         }
      }

   }

   public void onTrackedDataSet(TrackedData data) {
      if (ANGRY.equals(data) && this.isProvoked() && this.getWorld().isClient) {
         this.playAngrySound();
      }

      super.onTrackedDataSet(data);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      BlockState blockState = this.getCarriedBlock();
      if (blockState != null) {
         view.put("carriedBlockState", BlockState.CODEC, blockState);
      }

      this.writeAngerToData(view);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setCarriedBlock((BlockState)view.read("carriedBlockState", BlockState.CODEC).filter((blockState) -> {
         return !blockState.isAir();
      }).orElse((Object)null));
      this.readAngerFromData(this.getWorld(), view);
   }

   boolean isPlayerStaring(PlayerEntity player) {
      return !LivingEntity.NOT_WEARING_GAZE_DISGUISE_PREDICATE.test(player) ? false : this.isEntityLookingAtMe(player, 0.025, true, false, new double[]{this.getEyeY()});
   }

   public void tickMovement() {
      if (this.getWorld().isClient) {
         for(int i = 0; i < 2; ++i) {
            this.getWorld().addParticleClient(ParticleTypes.PORTAL, this.getParticleX(0.5), this.getRandomBodyY() - 0.25, this.getParticleZ(0.5), (this.random.nextDouble() - 0.5) * 2.0, -this.random.nextDouble(), (this.random.nextDouble() - 0.5) * 2.0);
         }
      }

      this.jumping = false;
      if (!this.getWorld().isClient) {
         this.tickAngerLogic((ServerWorld)this.getWorld(), true);
      }

      super.tickMovement();
   }

   public boolean hurtByWater() {
      return true;
   }

   protected void mobTick(ServerWorld world) {
      if (world.isDay() && this.age >= this.ageWhenTargetSet + 600) {
         float f = this.getBrightnessAtEyes();
         if (f > 0.5F && world.isSkyVisible(this.getBlockPos()) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
            this.setTarget((LivingEntity)null);
            this.teleportRandomly();
         }
      }

      super.mobTick(world);
   }

   protected boolean teleportRandomly() {
      if (!this.getWorld().isClient() && this.isAlive()) {
         double d = this.getX() + (this.random.nextDouble() - 0.5) * 64.0;
         double e = this.getY() + (double)(this.random.nextInt(64) - 32);
         double f = this.getZ() + (this.random.nextDouble() - 0.5) * 64.0;
         return this.teleportTo(d, e, f);
      } else {
         return false;
      }
   }

   boolean teleportTo(Entity entity) {
      Vec3d vec3d = new Vec3d(this.getX() - entity.getX(), this.getBodyY(0.5) - entity.getEyeY(), this.getZ() - entity.getZ());
      vec3d = vec3d.normalize();
      double d = 16.0;
      double e = this.getX() + (this.random.nextDouble() - 0.5) * 8.0 - vec3d.x * 16.0;
      double f = this.getY() + (double)(this.random.nextInt(16) - 8) - vec3d.y * 16.0;
      double g = this.getZ() + (this.random.nextDouble() - 0.5) * 8.0 - vec3d.z * 16.0;
      return this.teleportTo(e, f, g);
   }

   private boolean teleportTo(double x, double y, double z) {
      BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);

      while(mutable.getY() > this.getWorld().getBottomY() && !this.getWorld().getBlockState(mutable).blocksMovement()) {
         mutable.move(Direction.DOWN);
      }

      BlockState blockState = this.getWorld().getBlockState(mutable);
      boolean bl = blockState.blocksMovement();
      boolean bl2 = blockState.getFluidState().isIn(FluidTags.WATER);
      if (bl && !bl2) {
         Vec3d vec3d = this.getPos();
         boolean bl3 = this.teleport(x, y, z, true);
         if (bl3) {
            this.getWorld().emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of((Entity)this));
            if (!this.isSilent()) {
               this.getWorld().playSound((Entity)null, this.lastX, this.lastY, this.lastZ, (SoundEvent)SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
               this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
         }

         return bl3;
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return this.isAngry() ? SoundEvents.ENTITY_ENDERMAN_SCREAM : SoundEvents.ENTITY_ENDERMAN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_ENDERMAN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ENDERMAN_DEATH;
   }

   protected void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {
      super.dropEquipment(world, source, causedByPlayer);
      BlockState blockState = this.getCarriedBlock();
      if (blockState != null) {
         ItemStack itemStack = new ItemStack(Items.DIAMOND_AXE);
         EnchantmentHelper.applyEnchantmentProvider(itemStack, world.getRegistryManager(), EnchantmentProviders.ENDERMAN_LOOT_DROP, world.getLocalDifficulty(this.getBlockPos()), this.getRandom());
         LootWorldContext.Builder builder = (new LootWorldContext.Builder((ServerWorld)this.getWorld())).add(LootContextParameters.ORIGIN, this.getPos()).add(LootContextParameters.TOOL, itemStack).addOptional(LootContextParameters.THIS_ENTITY, this);
         List list = blockState.getDroppedStacks(builder);
         Iterator var8 = list.iterator();

         while(var8.hasNext()) {
            ItemStack itemStack2 = (ItemStack)var8.next();
            this.dropStack(world, itemStack2);
         }
      }

   }

   public void setCarriedBlock(@Nullable BlockState state) {
      this.dataTracker.set(CARRIED_BLOCK, Optional.ofNullable(state));
   }

   @Nullable
   public BlockState getCarriedBlock() {
      return (BlockState)((Optional)this.dataTracker.get(CARRIED_BLOCK)).orElse((Object)null);
   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      if (this.isInvulnerableTo(world, source)) {
         return false;
      } else {
         Entity var6 = source.getSource();
         PotionEntity var10000;
         if (var6 instanceof PotionEntity) {
            PotionEntity potionEntity = (PotionEntity)var6;
            var10000 = potionEntity;
         } else {
            var10000 = null;
         }

         PotionEntity potionEntity2 = var10000;
         boolean bl;
         if (!source.isIn(DamageTypeTags.IS_PROJECTILE) && potionEntity2 == null) {
            bl = super.damage(world, source, amount);
            if (!(source.getAttacker() instanceof LivingEntity) && this.random.nextInt(10) != 0) {
               this.teleportRandomly();
            }

            return bl;
         } else {
            bl = potionEntity2 != null && this.damageFromPotion(world, source, potionEntity2, amount);

            for(int i = 0; i < 64; ++i) {
               if (this.teleportRandomly()) {
                  return true;
               }
            }

            return bl;
         }
      }
   }

   private boolean damageFromPotion(ServerWorld world, DamageSource source, PotionEntity potion, float amount) {
      ItemStack itemStack = potion.getStack();
      PotionContentsComponent potionContentsComponent = (PotionContentsComponent)itemStack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
      return potionContentsComponent.matches(Potions.WATER) ? super.damage(world, source, amount) : false;
   }

   public boolean isAngry() {
      return (Boolean)this.dataTracker.get(ANGRY);
   }

   public boolean isProvoked() {
      return (Boolean)this.dataTracker.get(PROVOKED);
   }

   public void setProvoked() {
      this.dataTracker.set(PROVOKED, true);
   }

   public boolean cannotDespawn() {
      return super.cannotDespawn() || this.getCarriedBlock() != null;
   }

   static {
      ATTACKING_SPEED_BOOST = new EntityAttributeModifier(ATTACKING_SPEED_MODIFIER_ID, 0.15000000596046448, EntityAttributeModifier.Operation.ADD_VALUE);
      CARRIED_BLOCK = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);
      ANGRY = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      PROVOKED = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
   }

   static class ChasePlayerGoal extends Goal {
      private final EndermanEntity enderman;
      @Nullable
      private LivingEntity target;

      public ChasePlayerGoal(EndermanEntity enderman) {
         this.enderman = enderman;
         this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
      }

      public boolean canStart() {
         this.target = this.enderman.getTarget();
         LivingEntity var2 = this.target;
         if (var2 instanceof PlayerEntity playerEntity) {
            double var4 = this.target.squaredDistanceTo(this.enderman);
            return var4 > 256.0 ? false : this.enderman.isPlayerStaring(playerEntity);
         } else {
            return false;
         }
      }

      public void start() {
         this.enderman.getNavigation().stop();
      }

      public void tick() {
         this.enderman.getLookControl().lookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
      }
   }

   static class PlaceBlockGoal extends Goal {
      private final EndermanEntity enderman;

      public PlaceBlockGoal(EndermanEntity enderman) {
         this.enderman = enderman;
      }

      public boolean canStart() {
         if (this.enderman.getCarriedBlock() == null) {
            return false;
         } else if (!getServerWorld(this.enderman).getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return false;
         } else {
            return this.enderman.getRandom().nextInt(toGoalTicks(2000)) == 0;
         }
      }

      public void tick() {
         Random random = this.enderman.getRandom();
         World world = this.enderman.getWorld();
         int i = MathHelper.floor(this.enderman.getX() - 1.0 + random.nextDouble() * 2.0);
         int j = MathHelper.floor(this.enderman.getY() + random.nextDouble() * 2.0);
         int k = MathHelper.floor(this.enderman.getZ() - 1.0 + random.nextDouble() * 2.0);
         BlockPos blockPos = new BlockPos(i, j, k);
         BlockState blockState = world.getBlockState(blockPos);
         BlockPos blockPos2 = blockPos.down();
         BlockState blockState2 = world.getBlockState(blockPos2);
         BlockState blockState3 = this.enderman.getCarriedBlock();
         if (blockState3 != null) {
            blockState3 = Block.postProcessState(blockState3, this.enderman.getWorld(), blockPos);
            if (this.canPlaceOn(world, blockPos, blockState3, blockState, blockState2, blockPos2)) {
               world.setBlockState(blockPos, blockState3, 3);
               world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(this.enderman, blockState3));
               this.enderman.setCarriedBlock((BlockState)null);
            }

         }
      }

      private boolean canPlaceOn(World world, BlockPos posAbove, BlockState carriedState, BlockState stateAbove, BlockState state, BlockPos pos) {
         return stateAbove.isAir() && !state.isAir() && !state.isOf(Blocks.BEDROCK) && state.isFullCube(world, pos) && carriedState.canPlaceAt(world, posAbove) && world.getOtherEntities(this.enderman, Box.from(Vec3d.of(posAbove))).isEmpty();
      }
   }

   static class PickUpBlockGoal extends Goal {
      private final EndermanEntity enderman;

      public PickUpBlockGoal(EndermanEntity enderman) {
         this.enderman = enderman;
      }

      public boolean canStart() {
         if (this.enderman.getCarriedBlock() != null) {
            return false;
         } else if (!getServerWorld(this.enderman).getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return false;
         } else {
            return this.enderman.getRandom().nextInt(toGoalTicks(20)) == 0;
         }
      }

      public void tick() {
         Random random = this.enderman.getRandom();
         World world = this.enderman.getWorld();
         int i = MathHelper.floor(this.enderman.getX() - 2.0 + random.nextDouble() * 4.0);
         int j = MathHelper.floor(this.enderman.getY() + random.nextDouble() * 3.0);
         int k = MathHelper.floor(this.enderman.getZ() - 2.0 + random.nextDouble() * 4.0);
         BlockPos blockPos = new BlockPos(i, j, k);
         BlockState blockState = world.getBlockState(blockPos);
         Vec3d vec3d = new Vec3d((double)this.enderman.getBlockX() + 0.5, (double)j + 0.5, (double)this.enderman.getBlockZ() + 0.5);
         Vec3d vec3d2 = new Vec3d((double)i + 0.5, (double)j + 0.5, (double)k + 0.5);
         BlockHitResult blockHitResult = world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, this.enderman));
         boolean bl = blockHitResult.getBlockPos().equals(blockPos);
         if (blockState.isIn(BlockTags.ENDERMAN_HOLDABLE) && bl) {
            world.removeBlock(blockPos, false);
            world.emitGameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Emitter.of(this.enderman, blockState));
            this.enderman.setCarriedBlock(blockState.getBlock().getDefaultState());
         }

      }
   }

   static class TeleportTowardsPlayerGoal extends ActiveTargetGoal {
      private final EndermanEntity enderman;
      @Nullable
      private PlayerEntity targetPlayer;
      private int lookAtPlayerWarmup;
      private int ticksSinceUnseenTeleport;
      private final TargetPredicate staringPlayerPredicate;
      private final TargetPredicate validTargetPredicate = TargetPredicate.createAttackable().ignoreVisibility();
      private final TargetPredicate.EntityPredicate angerPredicate;

      public TeleportTowardsPlayerGoal(EndermanEntity enderman, @Nullable TargetPredicate.EntityPredicate targetPredicate) {
         super(enderman, PlayerEntity.class, 10, false, false, targetPredicate);
         this.enderman = enderman;
         this.angerPredicate = (playerEntity, world) -> {
            return (enderman.isPlayerStaring((PlayerEntity)playerEntity) || enderman.shouldAngerAt(playerEntity, world)) && !enderman.hasPassengerDeep(playerEntity);
         };
         this.staringPlayerPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(this.getFollowRange()).setPredicate(this.angerPredicate);
      }

      public boolean canStart() {
         this.targetPlayer = getServerWorld(this.enderman).getClosestPlayer(this.staringPlayerPredicate.setBaseMaxDistance(this.getFollowRange()), this.enderman);
         return this.targetPlayer != null;
      }

      public void start() {
         this.lookAtPlayerWarmup = this.getTickCount(5);
         this.ticksSinceUnseenTeleport = 0;
         this.enderman.setProvoked();
      }

      public void stop() {
         this.targetPlayer = null;
         super.stop();
      }

      public boolean shouldContinue() {
         if (this.targetPlayer != null) {
            if (!this.angerPredicate.test(this.targetPlayer, getServerWorld(this.enderman))) {
               return false;
            } else {
               this.enderman.lookAtEntity(this.targetPlayer, 10.0F, 10.0F);
               return true;
            }
         } else {
            if (this.targetEntity != null) {
               if (this.enderman.hasPassengerDeep(this.targetEntity)) {
                  return false;
               }

               if (this.validTargetPredicate.test(getServerWorld(this.enderman), this.enderman, this.targetEntity)) {
                  return true;
               }
            }

            return super.shouldContinue();
         }
      }

      public void tick() {
         if (this.enderman.getTarget() == null) {
            super.setTargetEntity((LivingEntity)null);
         }

         if (this.targetPlayer != null) {
            if (--this.lookAtPlayerWarmup <= 0) {
               this.targetEntity = this.targetPlayer;
               this.targetPlayer = null;
               super.start();
            }
         } else {
            if (this.targetEntity != null && !this.enderman.hasVehicle()) {
               if (this.enderman.isPlayerStaring((PlayerEntity)this.targetEntity)) {
                  if (this.targetEntity.squaredDistanceTo(this.enderman) < 16.0) {
                     this.enderman.teleportRandomly();
                  }

                  this.ticksSinceUnseenTeleport = 0;
               } else if (this.targetEntity.squaredDistanceTo(this.enderman) > 256.0 && this.ticksSinceUnseenTeleport++ >= this.getTickCount(30) && this.enderman.teleportTo(this.targetEntity)) {
                  this.ticksSinceUnseenTeleport = 0;
               }
            }

            super.tick();
         }

      }
   }
}
