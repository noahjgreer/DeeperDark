package net.minecraft.entity.passive;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.Variants;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.GoToBedAndSleepGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CatEntity extends TameableEntity {
   public static final double CROUCHING_SPEED = 0.6;
   public static final double NORMAL_SPEED = 0.8;
   public static final double SPRINTING_SPEED = 1.33;
   private static final TrackedData CAT_VARIANT;
   private static final TrackedData IN_SLEEPING_POSE;
   private static final TrackedData HEAD_DOWN;
   private static final TrackedData COLLAR_COLOR;
   private static final RegistryKey DEFAULT_VARIANT;
   private static final DyeColor DEFAULT_COLLAR_COLOR;
   @Nullable
   private CatFleeGoal fleeGoal;
   @Nullable
   private net.minecraft.entity.ai.goal.TemptGoal temptGoal;
   private float sleepAnimation;
   private float lastSleepAnimation;
   private float tailCurlAnimation;
   private float lastTailCurlAnimation;
   private boolean nearSleepingPlayer;
   private float headDownAnimation;
   private float lastHeadDownAnimation;

   public CatEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.onTamedChanged();
   }

   protected void initGoals() {
      this.temptGoal = new TemptGoal(this, 0.6, (stack) -> {
         return stack.isIn(ItemTags.CAT_FOOD);
      }, true);
      this.goalSelector.add(1, new SwimGoal(this));
      this.goalSelector.add(1, new TameableEntity.TameableEscapeDangerGoal(1.5));
      this.goalSelector.add(2, new SitGoal(this));
      this.goalSelector.add(3, new SleepWithOwnerGoal(this));
      this.goalSelector.add(4, this.temptGoal);
      this.goalSelector.add(5, new GoToBedAndSleepGoal(this, 1.1, 8));
      this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0, 10.0F, 5.0F));
      this.goalSelector.add(7, new CatSitOnBlockGoal(this, 0.8));
      this.goalSelector.add(8, new PounceAtTargetGoal(this, 0.3F));
      this.goalSelector.add(9, new AttackGoal(this));
      this.goalSelector.add(10, new AnimalMateGoal(this, 0.8));
      this.goalSelector.add(11, new WanderAroundFarGoal(this, 0.8, 1.0000001E-5F));
      this.goalSelector.add(12, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
      this.targetSelector.add(1, new UntamedActiveTargetGoal(this, RabbitEntity.class, false, (TargetPredicate.EntityPredicate)null));
      this.targetSelector.add(1, new UntamedActiveTargetGoal(this, TurtleEntity.class, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
   }

   public RegistryEntry getVariant() {
      return (RegistryEntry)this.dataTracker.get(CAT_VARIANT);
   }

   private void setVariant(RegistryEntry variant) {
      this.dataTracker.set(CAT_VARIANT, variant);
   }

   @Nullable
   public Object get(ComponentType type) {
      if (type == DataComponentTypes.CAT_VARIANT) {
         return castComponentValue(type, this.getVariant());
      } else {
         return type == DataComponentTypes.CAT_COLLAR ? castComponentValue(type, this.getCollarColor()) : super.get(type);
      }
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.CAT_VARIANT);
      this.copyComponentFrom(from, DataComponentTypes.CAT_COLLAR);
      super.copyComponentsFrom(from);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.CAT_VARIANT) {
         this.setVariant((RegistryEntry)castComponentValue(DataComponentTypes.CAT_VARIANT, value));
         return true;
      } else if (type == DataComponentTypes.CAT_COLLAR) {
         this.setCollarColor((DyeColor)castComponentValue(DataComponentTypes.CAT_COLLAR, value));
         return true;
      } else {
         return super.setApplicableComponent(type, value);
      }
   }

   public void setInSleepingPose(boolean sleeping) {
      this.dataTracker.set(IN_SLEEPING_POSE, sleeping);
   }

   public boolean isInSleepingPose() {
      return (Boolean)this.dataTracker.get(IN_SLEEPING_POSE);
   }

   void setHeadDown(boolean headDown) {
      this.dataTracker.set(HEAD_DOWN, headDown);
   }

   boolean isHeadDown() {
      return (Boolean)this.dataTracker.get(HEAD_DOWN);
   }

   public DyeColor getCollarColor() {
      return DyeColor.byIndex((Integer)this.dataTracker.get(COLLAR_COLOR));
   }

   private void setCollarColor(DyeColor color) {
      this.dataTracker.set(COLLAR_COLOR, color.getIndex());
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(CAT_VARIANT, Variants.getOrDefaultOrThrow(this.getRegistryManager(), DEFAULT_VARIANT));
      builder.add(IN_SLEEPING_POSE, false);
      builder.add(HEAD_DOWN, false);
      builder.add(COLLAR_COLOR, DEFAULT_COLLAR_COLOR.getIndex());
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      Variants.writeVariantToNbt(view, this.getVariant());
      view.put("CollarColor", DyeColor.INDEX_CODEC, this.getCollarColor());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      Variants.readVariantFromNbt(view, RegistryKeys.CAT_VARIANT).ifPresent(this::setVariant);
      this.setCollarColor((DyeColor)view.read("CollarColor", DyeColor.INDEX_CODEC).orElse(DEFAULT_COLLAR_COLOR));
   }

   public void mobTick(ServerWorld world) {
      if (this.getMoveControl().isMoving()) {
         double d = this.getMoveControl().getSpeed();
         if (d == 0.6) {
            this.setPose(EntityPose.CROUCHING);
            this.setSprinting(false);
         } else if (d == 1.33) {
            this.setPose(EntityPose.STANDING);
            this.setSprinting(true);
         } else {
            this.setPose(EntityPose.STANDING);
            this.setSprinting(false);
         }
      } else {
         this.setPose(EntityPose.STANDING);
         this.setSprinting(false);
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isTamed()) {
         if (this.isInLove()) {
            return SoundEvents.ENTITY_CAT_PURR;
         } else {
            return this.random.nextInt(4) == 0 ? SoundEvents.ENTITY_CAT_PURREOW : SoundEvents.ENTITY_CAT_AMBIENT;
         }
      } else {
         return SoundEvents.ENTITY_CAT_STRAY_AMBIENT;
      }
   }

   public int getMinAmbientSoundDelay() {
      return 120;
   }

   public void hiss() {
      this.playSound(SoundEvents.ENTITY_CAT_HISS);
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_CAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_CAT_DEATH;
   }

   public static DefaultAttributeContainer.Builder createCatAttributes() {
      return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MAX_HEALTH, 10.0).add(EntityAttributes.MOVEMENT_SPEED, 0.30000001192092896).add(EntityAttributes.ATTACK_DAMAGE, 3.0);
   }

   protected void playEatSound() {
      this.playSound(SoundEvents.ENTITY_CAT_EAT, 1.0F, 1.0F);
   }

   public void tick() {
      super.tick();
      if (this.temptGoal != null && this.temptGoal.isActive() && !this.isTamed() && this.age % 100 == 0) {
         this.playSound(SoundEvents.ENTITY_CAT_BEG_FOR_FOOD, 1.0F, 1.0F);
      }

      this.updateAnimations();
   }

   private void updateAnimations() {
      if ((this.isInSleepingPose() || this.isHeadDown()) && this.age % 5 == 0) {
         this.playSound(SoundEvents.ENTITY_CAT_PURR, 0.6F + 0.4F * (this.random.nextFloat() - this.random.nextFloat()), 1.0F);
      }

      this.updateSleepAnimation();
      this.updateHeadDownAnimation();
      this.nearSleepingPlayer = false;
      if (this.isInSleepingPose()) {
         BlockPos blockPos = this.getBlockPos();
         List list = this.getWorld().getNonSpectatingEntities(PlayerEntity.class, (new Box(blockPos)).expand(2.0, 2.0, 2.0));
         Iterator var3 = list.iterator();

         while(var3.hasNext()) {
            PlayerEntity playerEntity = (PlayerEntity)var3.next();
            if (playerEntity.isSleeping()) {
               this.nearSleepingPlayer = true;
               break;
            }
         }
      }

   }

   public boolean isNearSleepingPlayer() {
      return this.nearSleepingPlayer;
   }

   private void updateSleepAnimation() {
      this.lastSleepAnimation = this.sleepAnimation;
      this.lastTailCurlAnimation = this.tailCurlAnimation;
      if (this.isInSleepingPose()) {
         this.sleepAnimation = Math.min(1.0F, this.sleepAnimation + 0.15F);
         this.tailCurlAnimation = Math.min(1.0F, this.tailCurlAnimation + 0.08F);
      } else {
         this.sleepAnimation = Math.max(0.0F, this.sleepAnimation - 0.22F);
         this.tailCurlAnimation = Math.max(0.0F, this.tailCurlAnimation - 0.13F);
      }

   }

   private void updateHeadDownAnimation() {
      this.lastHeadDownAnimation = this.headDownAnimation;
      if (this.isHeadDown()) {
         this.headDownAnimation = Math.min(1.0F, this.headDownAnimation + 0.1F);
      } else {
         this.headDownAnimation = Math.max(0.0F, this.headDownAnimation - 0.13F);
      }

   }

   public float getSleepAnimationProgress(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastSleepAnimation, this.sleepAnimation);
   }

   public float getTailCurlAnimationProgress(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastTailCurlAnimation, this.tailCurlAnimation);
   }

   public float getHeadDownAnimationProgress(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastHeadDownAnimation, this.headDownAnimation);
   }

   @Nullable
   public CatEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
      CatEntity catEntity = (CatEntity)EntityType.CAT.create(serverWorld, SpawnReason.BREEDING);
      if (catEntity != null && passiveEntity instanceof CatEntity catEntity2) {
         if (this.random.nextBoolean()) {
            catEntity.setVariant(this.getVariant());
         } else {
            catEntity.setVariant(catEntity2.getVariant());
         }

         if (this.isTamed()) {
            catEntity.setOwner(this.getOwnerReference());
            catEntity.setTamed(true, true);
            DyeColor dyeColor = this.getCollarColor();
            DyeColor dyeColor2 = catEntity2.getCollarColor();
            catEntity.setCollarColor(DyeColor.mixColors(serverWorld, dyeColor, dyeColor2));
         }
      }

      return catEntity;
   }

   public boolean canBreedWith(AnimalEntity other) {
      if (!this.isTamed()) {
         return false;
      } else if (!(other instanceof CatEntity)) {
         return false;
      } else {
         CatEntity catEntity = (CatEntity)other;
         return catEntity.isTamed() && super.canBreedWith(other);
      }
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      entityData = super.initialize(world, difficulty, spawnReason, entityData);
      Variants.select(SpawnContext.of(world, this.getBlockPos()), RegistryKeys.CAT_VARIANT).ifPresent(this::setVariant);
      return entityData;
   }

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      Item item = itemStack.getItem();
      ActionResult actionResult;
      if (this.isTamed()) {
         if (this.isOwner(player)) {
            if (item instanceof DyeItem) {
               DyeItem dyeItem = (DyeItem)item;
               DyeColor dyeColor = dyeItem.getColor();
               if (dyeColor != this.getCollarColor()) {
                  if (!this.getWorld().isClient()) {
                     this.setCollarColor(dyeColor);
                     itemStack.decrementUnlessCreative(1, player);
                     this.setPersistent();
                  }

                  return ActionResult.SUCCESS;
               }
            } else if (this.isBreedingItem(itemStack) && this.getHealth() < this.getMaxHealth()) {
               if (!this.getWorld().isClient()) {
                  this.eat(player, hand, itemStack);
                  FoodComponent foodComponent = (FoodComponent)itemStack.get(DataComponentTypes.FOOD);
                  this.heal(foodComponent != null ? (float)foodComponent.nutrition() : 1.0F);
                  this.playEatSound();
               }

               return ActionResult.SUCCESS;
            }

            actionResult = super.interactMob(player, hand);
            if (!actionResult.isAccepted()) {
               this.setSitting(!this.isSitting());
               return ActionResult.SUCCESS;
            }

            return actionResult;
         }
      } else if (this.isBreedingItem(itemStack)) {
         if (!this.getWorld().isClient()) {
            this.eat(player, hand, itemStack);
            this.tryTame(player);
            this.setPersistent();
            this.playEatSound();
         }

         return ActionResult.SUCCESS;
      }

      actionResult = super.interactMob(player, hand);
      if (actionResult.isAccepted()) {
         this.setPersistent();
      }

      return actionResult;
   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack.isIn(ItemTags.CAT_FOOD);
   }

   public boolean canImmediatelyDespawn(double distanceSquared) {
      return !this.isTamed() && this.age > 2400;
   }

   public void setTamed(boolean tamed, boolean updateAttributes) {
      super.setTamed(tamed, updateAttributes);
      this.onTamedChanged();
   }

   protected void onTamedChanged() {
      if (this.fleeGoal == null) {
         this.fleeGoal = new CatFleeGoal(this, PlayerEntity.class, 16.0F, 0.8, 1.33);
      }

      this.goalSelector.remove(this.fleeGoal);
      if (!this.isTamed()) {
         this.goalSelector.add(4, this.fleeGoal);
      }

   }

   private void tryTame(PlayerEntity player) {
      if (this.random.nextInt(3) == 0) {
         this.setTamedBy(player);
         this.setSitting(true);
         this.getWorld().sendEntityStatus(this, (byte)7);
      } else {
         this.getWorld().sendEntityStatus(this, (byte)6);
      }

   }

   public boolean bypassesSteppingEffects() {
      return this.isInSneakingPose() || super.bypassesSteppingEffects();
   }

   // $FF: synthetic method
   @Nullable
   public PassiveEntity createChild(final ServerWorld world, final PassiveEntity entity) {
      return this.createChild(world, entity);
   }

   static {
      CAT_VARIANT = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.CAT_VARIANT);
      IN_SLEEPING_POSE = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      HEAD_DOWN = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      COLLAR_COLOR = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.INTEGER);
      DEFAULT_VARIANT = CatVariants.BLACK;
      DEFAULT_COLLAR_COLOR = DyeColor.RED;
   }

   static class TemptGoal extends net.minecraft.entity.ai.goal.TemptGoal {
      @Nullable
      private PlayerEntity player;
      private final CatEntity cat;

      public TemptGoal(CatEntity cat, double speed, Predicate foodPredicate, boolean canBeScared) {
         super(cat, speed, foodPredicate, canBeScared);
         this.cat = cat;
      }

      public void tick() {
         super.tick();
         if (this.player == null && this.mob.getRandom().nextInt(this.getTickCount(600)) == 0) {
            this.player = this.closestPlayer;
         } else if (this.mob.getRandom().nextInt(this.getTickCount(500)) == 0) {
            this.player = null;
         }

      }

      protected boolean canBeScared() {
         return this.player != null && this.player.equals(this.closestPlayer) ? false : super.canBeScared();
      }

      public boolean canStart() {
         return super.canStart() && !this.cat.isTamed();
      }
   }

   private static class SleepWithOwnerGoal extends Goal {
      private final CatEntity cat;
      @Nullable
      private PlayerEntity owner;
      @Nullable
      private BlockPos bedPos;
      private int ticksOnBed;

      public SleepWithOwnerGoal(CatEntity cat) {
         this.cat = cat;
      }

      public boolean canStart() {
         if (!this.cat.isTamed()) {
            return false;
         } else if (this.cat.isSitting()) {
            return false;
         } else {
            LivingEntity livingEntity = this.cat.getOwner();
            if (livingEntity instanceof PlayerEntity) {
               PlayerEntity playerEntity = (PlayerEntity)livingEntity;
               this.owner = playerEntity;
               if (!livingEntity.isSleeping()) {
                  return false;
               }

               if (this.cat.squaredDistanceTo(this.owner) > 100.0) {
                  return false;
               }

               BlockPos blockPos = this.owner.getBlockPos();
               BlockState blockState = this.cat.getWorld().getBlockState(blockPos);
               if (blockState.isIn(BlockTags.BEDS)) {
                  this.bedPos = (BlockPos)blockState.getOrEmpty(BedBlock.FACING).map((direction) -> {
                     return blockPos.offset(direction.getOpposite());
                  }).orElseGet(() -> {
                     return new BlockPos(blockPos);
                  });
                  return !this.cannotSleep();
               }
            }

            return false;
         }
      }

      private boolean cannotSleep() {
         List list = this.cat.getWorld().getNonSpectatingEntities(CatEntity.class, (new Box(this.bedPos)).expand(2.0));
         Iterator var2 = list.iterator();

         CatEntity catEntity;
         do {
            do {
               if (!var2.hasNext()) {
                  return false;
               }

               catEntity = (CatEntity)var2.next();
            } while(catEntity == this.cat);
         } while(!catEntity.isInSleepingPose() && !catEntity.isHeadDown());

         return true;
      }

      public boolean shouldContinue() {
         return this.cat.isTamed() && !this.cat.isSitting() && this.owner != null && this.owner.isSleeping() && this.bedPos != null && !this.cannotSleep();
      }

      public void start() {
         if (this.bedPos != null) {
            this.cat.setInSittingPose(false);
            this.cat.getNavigation().startMovingTo((double)this.bedPos.getX(), (double)this.bedPos.getY(), (double)this.bedPos.getZ(), 1.100000023841858);
         }

      }

      public void stop() {
         this.cat.setInSleepingPose(false);
         float f = this.cat.getWorld().getSkyAngle(1.0F);
         if (this.owner.getSleepTimer() >= 100 && (double)f > 0.77 && (double)f < 0.8 && (double)this.cat.getWorld().getRandom().nextFloat() < 0.7) {
            this.dropMorningGifts();
         }

         this.ticksOnBed = 0;
         this.cat.setHeadDown(false);
         this.cat.getNavigation().stop();
      }

      private void dropMorningGifts() {
         Random random = this.cat.getRandom();
         BlockPos.Mutable mutable = new BlockPos.Mutable();
         mutable.set(this.cat.isLeashed() ? this.cat.getLeashHolder().getBlockPos() : this.cat.getBlockPos());
         this.cat.teleport((double)(mutable.getX() + random.nextInt(11) - 5), (double)(mutable.getY() + random.nextInt(5) - 2), (double)(mutable.getZ() + random.nextInt(11) - 5), false);
         mutable.set(this.cat.getBlockPos());
         this.cat.forEachGiftedItem(getServerWorld(this.cat), LootTables.CAT_MORNING_GIFT_GAMEPLAY, (world, stack) -> {
            world.spawnEntity(new ItemEntity(world, (double)mutable.getX() - (double)MathHelper.sin(this.cat.bodyYaw * 0.017453292F), (double)mutable.getY(), (double)mutable.getZ() + (double)MathHelper.cos(this.cat.bodyYaw * 0.017453292F), stack));
         });
      }

      public void tick() {
         if (this.owner != null && this.bedPos != null) {
            this.cat.setInSittingPose(false);
            this.cat.getNavigation().startMovingTo((double)this.bedPos.getX(), (double)this.bedPos.getY(), (double)this.bedPos.getZ(), 1.100000023841858);
            if (this.cat.squaredDistanceTo(this.owner) < 2.5) {
               ++this.ticksOnBed;
               if (this.ticksOnBed > this.getTickCount(16)) {
                  this.cat.setInSleepingPose(true);
                  this.cat.setHeadDown(false);
               } else {
                  this.cat.lookAtEntity(this.owner, 45.0F, 45.0F);
                  this.cat.setHeadDown(true);
               }
            } else {
               this.cat.setInSleepingPose(false);
            }
         }

      }
   }

   static class CatFleeGoal extends FleeEntityGoal {
      private final CatEntity cat;

      public CatFleeGoal(CatEntity cat, Class fleeFromType, float distance, double slowSpeed, double fastSpeed) {
         Predicate var10006 = EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR;
         Objects.requireNonNull(var10006);
         super(cat, fleeFromType, distance, slowSpeed, fastSpeed, var10006::test);
         this.cat = cat;
      }

      public boolean canStart() {
         return !this.cat.isTamed() && super.canStart();
      }

      public boolean shouldContinue() {
         return !this.cat.isTamed() && super.shouldContinue();
      }
   }
}
