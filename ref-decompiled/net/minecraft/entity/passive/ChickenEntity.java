package net.minecraft.entity.passive;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.Variants;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.LazyRegistryEntryReference;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class ChickenEntity extends AnimalEntity {
   private static final EntityDimensions BABY_BASE_DIMENSIONS;
   private static final TrackedData VARIANT;
   private static final boolean DEFAULT_HAS_JOCKEY = false;
   public float flapProgress;
   public float maxWingDeviation;
   public float lastMaxWingDeviation;
   public float lastFlapProgress;
   public float flapSpeed = 1.0F;
   private float field_28639 = 1.0F;
   public int eggLayTime;
   public boolean hasJockey = false;

   public ChickenEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.eggLayTime = this.random.nextInt(6000) + 6000;
      this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
   }

   protected void initGoals() {
      this.goalSelector.add(0, new SwimGoal(this));
      this.goalSelector.add(1, new EscapeDangerGoal(this, 1.4));
      this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
      this.goalSelector.add(3, new TemptGoal(this, 1.0, (stack) -> {
         return stack.isIn(ItemTags.CHICKEN_FOOD);
      }, false));
      this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
      this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
      this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.add(7, new LookAroundGoal(this));
   }

   public EntityDimensions getBaseDimensions(EntityPose pose) {
      return this.isBaby() ? BABY_BASE_DIMENSIONS : super.getBaseDimensions(pose);
   }

   public static DefaultAttributeContainer.Builder createChickenAttributes() {
      return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MAX_HEALTH, 4.0).add(EntityAttributes.MOVEMENT_SPEED, 0.25);
   }

   public void tickMovement() {
      super.tickMovement();
      this.lastFlapProgress = this.flapProgress;
      this.lastMaxWingDeviation = this.maxWingDeviation;
      this.maxWingDeviation += (this.isOnGround() ? -1.0F : 4.0F) * 0.3F;
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
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         if (this.isAlive() && !this.isBaby() && !this.hasJockey() && --this.eggLayTime <= 0) {
            if (this.forEachGiftedItem(serverWorld, LootTables.CHICKEN_LAY_GAMEPLAY, this::dropStack)) {
               this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
               this.emitGameEvent(GameEvent.ENTITY_PLACE);
            }

            this.eggLayTime = this.random.nextInt(6000) + 6000;
         }
      }

   }

   protected boolean isFlappingWings() {
      return this.speed > this.field_28639;
   }

   protected void addFlapEffects() {
      this.field_28639 = this.speed + this.maxWingDeviation / 2.0F;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_CHICKEN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_CHICKEN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_CHICKEN_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15F, 1.0F);
   }

   @Nullable
   public ChickenEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
      ChickenEntity chickenEntity = (ChickenEntity)EntityType.CHICKEN.create(serverWorld, SpawnReason.BREEDING);
      if (chickenEntity != null && passiveEntity instanceof ChickenEntity chickenEntity2) {
         chickenEntity.setVariant(this.random.nextBoolean() ? this.getVariant() : chickenEntity2.getVariant());
      }

      return chickenEntity;
   }

   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      Variants.select(SpawnContext.of(world, this.getBlockPos()), RegistryKeys.CHICKEN_VARIANT).ifPresent(this::setVariant);
      return super.initialize(world, difficulty, spawnReason, entityData);
   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack.isIn(ItemTags.CHICKEN_FOOD);
   }

   protected int getExperienceToDrop(ServerWorld world) {
      return this.hasJockey() ? 10 : super.getExperienceToDrop(world);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(VARIANT, Variants.getOrDefaultOrThrow(this.getRegistryManager(), ChickenVariants.TEMPERATE));
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.hasJockey = view.getBoolean("IsChickenJockey", false);
      view.getOptionalInt("EggLayTime").ifPresent((eggLayTime) -> {
         this.eggLayTime = eggLayTime;
      });
      Variants.readVariantFromNbt(view, RegistryKeys.CHICKEN_VARIANT).ifPresent(this::setVariant);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putBoolean("IsChickenJockey", this.hasJockey);
      view.putInt("EggLayTime", this.eggLayTime);
      Variants.writeVariantToNbt(view, this.getVariant());
   }

   public void setVariant(RegistryEntry variant) {
      this.dataTracker.set(VARIANT, variant);
   }

   public RegistryEntry getVariant() {
      return (RegistryEntry)this.dataTracker.get(VARIANT);
   }

   @Nullable
   public Object get(ComponentType type) {
      return type == DataComponentTypes.CHICKEN_VARIANT ? castComponentValue(type, new LazyRegistryEntryReference(this.getVariant())) : super.get(type);
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.CHICKEN_VARIANT);
      super.copyComponentsFrom(from);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.CHICKEN_VARIANT) {
         Optional optional = ((LazyRegistryEntryReference)castComponentValue(DataComponentTypes.CHICKEN_VARIANT, value)).resolveEntry(this.getRegistryManager());
         if (optional.isPresent()) {
            this.setVariant((RegistryEntry)optional.get());
            return true;
         } else {
            return false;
         }
      } else {
         return super.setApplicableComponent(type, value);
      }
   }

   public boolean canImmediatelyDespawn(double distanceSquared) {
      return this.hasJockey();
   }

   protected void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater) {
      super.updatePassengerPosition(passenger, positionUpdater);
      if (passenger instanceof LivingEntity) {
         ((LivingEntity)passenger).bodyYaw = this.bodyYaw;
      }

   }

   public boolean hasJockey() {
      return this.hasJockey;
   }

   public void setHasJockey(boolean hasJockey) {
      this.hasJockey = hasJockey;
   }

   // $FF: synthetic method
   @Nullable
   public PassiveEntity createChild(final ServerWorld world, final PassiveEntity entity) {
      return this.createChild(world, entity);
   }

   static {
      BABY_BASE_DIMENSIONS = EntityType.CHICKEN.getDimensions().scaled(0.5F).withEyeHeight(0.2975F);
      VARIANT = DataTracker.registerData(ChickenEntity.class, TrackedDataHandlerRegistry.CHICKEN_VARIANT);
   }
}
