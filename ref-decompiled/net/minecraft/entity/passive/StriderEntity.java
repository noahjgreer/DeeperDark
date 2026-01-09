package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SaddledComponent;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class StriderEntity extends AnimalEntity implements ItemSteerable {
   private static final Identifier SUFFOCATING_MODIFIER_ID = Identifier.ofVanilla("suffocating");
   private static final EntityAttributeModifier SUFFOCATING_MODIFIER;
   private static final float COLD_SADDLED_SPEED = 0.35F;
   private static final float DEFAULT_SADDLED_SPEED = 0.55F;
   private static final TrackedData BOOST_TIME;
   private static final TrackedData COLD;
   private final SaddledComponent saddledComponent;
   @Nullable
   private TemptGoal temptGoal;

   public StriderEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.saddledComponent = new SaddledComponent(this.dataTracker, BOOST_TIME);
      this.intersectionChecked = true;
      this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
      this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
      this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
      this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
   }

   public static boolean canSpawn(EntityType type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
      BlockPos.Mutable mutable = pos.mutableCopy();

      do {
         mutable.move(Direction.UP);
      } while(world.getFluidState(mutable).isIn(FluidTags.LAVA));

      return world.getBlockState(mutable).isAir();
   }

   public void onTrackedDataSet(TrackedData data) {
      if (BOOST_TIME.equals(data) && this.getWorld().isClient) {
         this.saddledComponent.boost();
      }

      super.onTrackedDataSet(data);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(BOOST_TIME, 0);
      builder.add(COLD, false);
   }

   public boolean canUseSlot(EquipmentSlot slot) {
      if (slot != EquipmentSlot.SADDLE) {
         return super.canUseSlot(slot);
      } else {
         return this.isAlive() && !this.isBaby();
      }
   }

   protected boolean canDispenserEquipSlot(EquipmentSlot slot) {
      return slot == EquipmentSlot.SADDLE || super.canDispenserEquipSlot(slot);
   }

   protected RegistryEntry getEquipSound(EquipmentSlot slot, ItemStack stack, EquippableComponent equippableComponent) {
      return (RegistryEntry)(slot == EquipmentSlot.SADDLE ? SoundEvents.ENTITY_STRIDER_SADDLE : super.getEquipSound(slot, stack, equippableComponent));
   }

   protected void initGoals() {
      this.goalSelector.add(1, new EscapeDangerGoal(this, 1.65));
      this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
      this.temptGoal = new TemptGoal(this, 1.4, (stack) -> {
         return stack.isIn(ItemTags.STRIDER_TEMPT_ITEMS);
      }, false);
      this.goalSelector.add(3, this.temptGoal);
      this.goalSelector.add(4, new GoBackToLavaGoal(this, 1.0));
      this.goalSelector.add(5, new FollowParentGoal(this, 1.0));
      this.goalSelector.add(7, new WanderAroundGoal(this, 1.0, 60));
      this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.add(8, new LookAroundGoal(this));
      this.goalSelector.add(9, new LookAtEntityGoal(this, StriderEntity.class, 8.0F));
   }

   public void setCold(boolean cold) {
      this.dataTracker.set(COLD, cold);
      EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
      if (entityAttributeInstance != null) {
         if (cold) {
            entityAttributeInstance.updateModifier(SUFFOCATING_MODIFIER);
         } else {
            entityAttributeInstance.removeModifier(SUFFOCATING_MODIFIER_ID);
         }
      }

   }

   public boolean isCold() {
      return (Boolean)this.dataTracker.get(COLD);
   }

   public boolean canWalkOnFluid(FluidState state) {
      return state.isIn(FluidTags.LAVA);
   }

   protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
      if (!this.getWorld().isClient()) {
         return super.getPassengerAttachmentPos(passenger, dimensions, scaleFactor);
      } else {
         float f = Math.min(0.25F, this.limbAnimator.getSpeed());
         float g = this.limbAnimator.getAnimationProgress();
         float h = 0.12F * MathHelper.cos(g * 1.5F) * 2.0F * f;
         return super.getPassengerAttachmentPos(passenger, dimensions, scaleFactor).add(0.0, (double)(h * scaleFactor), 0.0);
      }
   }

   public boolean canSpawn(WorldView world) {
      return world.doesNotIntersectEntities(this);
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      if (this.hasSaddleEquipped()) {
         Entity var2 = this.getFirstPassenger();
         if (var2 instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)var2;
            if (playerEntity.isHolding(Items.WARPED_FUNGUS_ON_A_STICK)) {
               return playerEntity;
            }
         }
      }

      return super.getControllingPassenger();
   }

   public Vec3d updatePassengerForDismount(LivingEntity passenger) {
      Vec3d[] vec3ds = new Vec3d[]{getPassengerDismountOffset((double)this.getWidth(), (double)passenger.getWidth(), passenger.getYaw()), getPassengerDismountOffset((double)this.getWidth(), (double)passenger.getWidth(), passenger.getYaw() - 22.5F), getPassengerDismountOffset((double)this.getWidth(), (double)passenger.getWidth(), passenger.getYaw() + 22.5F), getPassengerDismountOffset((double)this.getWidth(), (double)passenger.getWidth(), passenger.getYaw() - 45.0F), getPassengerDismountOffset((double)this.getWidth(), (double)passenger.getWidth(), passenger.getYaw() + 45.0F)};
      Set set = Sets.newLinkedHashSet();
      double d = this.getBoundingBox().maxY;
      double e = this.getBoundingBox().minY - 0.5;
      BlockPos.Mutable mutable = new BlockPos.Mutable();
      Vec3d[] var9 = vec3ds;
      int var10 = vec3ds.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         Vec3d vec3d = var9[var11];
         mutable.set(this.getX() + vec3d.x, d, this.getZ() + vec3d.z);

         for(double f = d; f > e; --f) {
            set.add(mutable.toImmutable());
            mutable.move(Direction.DOWN);
         }
      }

      Iterator var17 = set.iterator();

      while(true) {
         BlockPos blockPos;
         double g;
         do {
            do {
               if (!var17.hasNext()) {
                  return new Vec3d(this.getX(), this.getBoundingBox().maxY, this.getZ());
               }

               blockPos = (BlockPos)var17.next();
            } while(this.getWorld().getFluidState(blockPos).isIn(FluidTags.LAVA));

            g = this.getWorld().getDismountHeight(blockPos);
         } while(!Dismounting.canDismountInBlock(g));

         Vec3d vec3d2 = Vec3d.ofCenter(blockPos, g);
         UnmodifiableIterator var14 = passenger.getPoses().iterator();

         while(var14.hasNext()) {
            EntityPose entityPose = (EntityPose)var14.next();
            Box box = passenger.getBoundingBox(entityPose);
            if (Dismounting.canPlaceEntityAt(this.getWorld(), passenger, box.offset(vec3d2))) {
               passenger.setPose(entityPose);
               return vec3d2;
            }
         }
      }
   }

   protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
      this.setRotation(controllingPlayer.getYaw(), controllingPlayer.getPitch() * 0.5F);
      this.lastYaw = this.bodyYaw = this.headYaw = this.getYaw();
      this.saddledComponent.tickBoost();
      super.tickControlled(controllingPlayer, movementInput);
   }

   protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
      return new Vec3d(0.0, 0.0, 1.0);
   }

   protected float getSaddledSpeed(PlayerEntity controllingPlayer) {
      return (float)(this.getAttributeValue(EntityAttributes.MOVEMENT_SPEED) * (double)(this.isCold() ? 0.35F : 0.55F) * (double)this.saddledComponent.getMovementSpeedMultiplier());
   }

   protected float calculateNextStepSoundDistance() {
      return this.distanceTraveled + 0.6F;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(this.isInLava() ? SoundEvents.ENTITY_STRIDER_STEP_LAVA : SoundEvents.ENTITY_STRIDER_STEP, 1.0F, 1.0F);
   }

   public boolean consumeOnAStickItem() {
      return this.saddledComponent.boost(this.getRandom());
   }

   protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
      if (this.isInLava()) {
         this.onLanding();
      } else {
         super.fall(heightDifference, onGround, state, landedPosition);
      }
   }

   public void tick() {
      if (this.isBeingTempted() && this.random.nextInt(140) == 0) {
         this.playSound(SoundEvents.ENTITY_STRIDER_HAPPY);
      } else if (this.isPanicking() && this.random.nextInt(60) == 0) {
         this.playSound(SoundEvents.ENTITY_STRIDER_RETREAT);
      }

      if (!this.isAiDisabled()) {
         boolean var10000;
         boolean bl;
         label36: {
            BlockState blockState = this.getWorld().getBlockState(this.getBlockPos());
            BlockState blockState2 = this.getLandingBlockState();
            bl = blockState.isIn(BlockTags.STRIDER_WARM_BLOCKS) || blockState2.isIn(BlockTags.STRIDER_WARM_BLOCKS) || this.getFluidHeight(FluidTags.LAVA) > 0.0;
            Entity var6 = this.getVehicle();
            if (var6 instanceof StriderEntity) {
               StriderEntity striderEntity = (StriderEntity)var6;
               if (striderEntity.isCold()) {
                  var10000 = true;
                  break label36;
               }
            }

            var10000 = false;
         }

         boolean bl2 = var10000;
         this.setCold(!bl || bl2);
      }

      super.tick();
      this.updateFloating();
   }

   private boolean isBeingTempted() {
      return this.temptGoal != null && this.temptGoal.isActive();
   }

   protected boolean movesIndependently() {
      return true;
   }

   private void updateFloating() {
      if (this.isInLava()) {
         ShapeContext shapeContext = ShapeContext.of(this);
         if (shapeContext.isAbove(FluidBlock.COLLISION_SHAPE, this.getBlockPos(), true) && !this.getWorld().getFluidState(this.getBlockPos().up()).isIn(FluidTags.LAVA)) {
            this.setOnGround(true);
         } else {
            this.setVelocity(this.getVelocity().multiply(0.5).add(0.0, 0.05, 0.0));
         }
      }

   }

   public static DefaultAttributeContainer.Builder createStriderAttributes() {
      return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MOVEMENT_SPEED, 0.17499999701976776);
   }

   protected SoundEvent getAmbientSound() {
      return !this.isPanicking() && !this.isBeingTempted() ? SoundEvents.ENTITY_STRIDER_AMBIENT : null;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_STRIDER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_STRIDER_DEATH;
   }

   protected boolean canAddPassenger(Entity passenger) {
      return !this.hasPassengers() && !this.isSubmergedIn(FluidTags.LAVA);
   }

   public boolean hurtByWater() {
      return true;
   }

   public boolean isOnFire() {
      return false;
   }

   protected EntityNavigation createNavigation(World world) {
      return new Navigation(this, world);
   }

   public float getPathfindingFavor(BlockPos pos, WorldView world) {
      if (world.getBlockState(pos).getFluidState().isIn(FluidTags.LAVA)) {
         return 10.0F;
      } else {
         return this.isInLava() ? Float.NEGATIVE_INFINITY : 0.0F;
      }
   }

   @Nullable
   public StriderEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
      return (StriderEntity)EntityType.STRIDER.create(serverWorld, SpawnReason.BREEDING);
   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack.isIn(ItemTags.STRIDER_FOOD);
   }

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      boolean bl = this.isBreedingItem(player.getStackInHand(hand));
      if (!bl && this.hasSaddleEquipped() && !this.hasPassengers() && !player.shouldCancelInteraction()) {
         if (!this.getWorld().isClient) {
            player.startRiding(this);
         }

         return ActionResult.SUCCESS;
      } else {
         ActionResult actionResult = super.interactMob(player, hand);
         if (!actionResult.isAccepted()) {
            ItemStack itemStack = player.getStackInHand(hand);
            return (ActionResult)(this.canEquip(itemStack, EquipmentSlot.SADDLE) ? itemStack.useOnEntity(player, this, hand) : ActionResult.PASS);
         } else {
            if (bl && !this.isSilent()) {
               this.getWorld().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), (SoundEvent)SoundEvents.ENTITY_STRIDER_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }

            return actionResult;
         }
      }
   }

   public Vec3d getLeashOffset() {
      return new Vec3d(0.0, (double)(0.6F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      if (this.isBaby()) {
         return super.initialize(world, difficulty, spawnReason, (EntityData)entityData);
      } else {
         Random random = world.getRandom();
         if (random.nextInt(30) == 0) {
            MobEntity mobEntity = (MobEntity)EntityType.ZOMBIFIED_PIGLIN.create(world.toServerWorld(), SpawnReason.JOCKEY);
            if (mobEntity != null) {
               entityData = this.initializeRider(world, difficulty, mobEntity, new ZombieEntity.ZombieData(ZombieEntity.shouldBeBaby(random), false));
               mobEntity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK));
               this.equipStack(EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE));
               this.setDropGuaranteed(EquipmentSlot.SADDLE);
            }
         } else if (random.nextInt(10) == 0) {
            PassiveEntity passiveEntity = (PassiveEntity)EntityType.STRIDER.create(world.toServerWorld(), SpawnReason.JOCKEY);
            if (passiveEntity != null) {
               passiveEntity.setBreedingAge(-24000);
               entityData = this.initializeRider(world, difficulty, passiveEntity, (EntityData)null);
            }
         } else {
            entityData = new PassiveEntity.PassiveData(0.5F);
         }

         return super.initialize(world, difficulty, spawnReason, (EntityData)entityData);
      }
   }

   private EntityData initializeRider(ServerWorldAccess world, LocalDifficulty difficulty, MobEntity rider, @Nullable EntityData entityData) {
      rider.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
      rider.initialize(world, difficulty, SpawnReason.JOCKEY, entityData);
      rider.startRiding(this, true);
      return new PassiveEntity.PassiveData(0.0F);
   }

   // $FF: synthetic method
   @Nullable
   public PassiveEntity createChild(final ServerWorld world, final PassiveEntity entity) {
      return this.createChild(world, entity);
   }

   static {
      SUFFOCATING_MODIFIER = new EntityAttributeModifier(SUFFOCATING_MODIFIER_ID, -0.3400000035762787, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
      BOOST_TIME = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.INTEGER);
      COLD = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
   }

   private static class GoBackToLavaGoal extends MoveToTargetPosGoal {
      private final StriderEntity strider;

      GoBackToLavaGoal(StriderEntity strider, double speed) {
         super(strider, speed, 8, 2);
         this.strider = strider;
      }

      public BlockPos getTargetPos() {
         return this.targetPos;
      }

      public boolean shouldContinue() {
         return !this.strider.isInLava() && this.isTargetPos(this.strider.getWorld(), this.targetPos);
      }

      public boolean canStart() {
         return !this.strider.isInLava() && super.canStart();
      }

      public boolean shouldResetPath() {
         return this.tryingTime % 20 == 0;
      }

      protected boolean isTargetPos(WorldView world, BlockPos pos) {
         return world.getBlockState(pos).isOf(Blocks.LAVA) && world.getBlockState(pos.up()).canPathfindThrough(NavigationType.LAND);
      }
   }

   static class Navigation extends MobNavigation {
      Navigation(StriderEntity entity, World world) {
         super(entity, world);
      }

      protected PathNodeNavigator createPathNodeNavigator(int range) {
         this.nodeMaker = new LandPathNodeMaker();
         return new PathNodeNavigator(this.nodeMaker, range);
      }

      protected boolean canWalkOnPath(PathNodeType pathType) {
         return pathType != PathNodeType.LAVA && pathType != PathNodeType.DAMAGE_FIRE && pathType != PathNodeType.DANGER_FIRE ? super.canWalkOnPath(pathType) : true;
      }

      public boolean isValidPosition(BlockPos pos) {
         return this.world.getBlockState(pos).isOf(Blocks.LAVA) || super.isValidPosition(pos);
      }
   }
}
