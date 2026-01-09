package net.minecraft.entity.passive;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SaddledComponent;
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
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PigEntity extends AnimalEntity implements ItemSteerable {
   private static final TrackedData BOOST_TIME;
   private static final TrackedData VARIANT;
   private final SaddledComponent saddledComponent;

   public PigEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.saddledComponent = new SaddledComponent(this.dataTracker, BOOST_TIME);
   }

   protected void initGoals() {
      this.goalSelector.add(0, new SwimGoal(this));
      this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25));
      this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
      this.goalSelector.add(4, new TemptGoal(this, 1.2, (stack) -> {
         return stack.isOf(Items.CARROT_ON_A_STICK);
      }, false));
      this.goalSelector.add(4, new TemptGoal(this, 1.2, (stack) -> {
         return stack.isIn(ItemTags.PIG_FOOD);
      }, false));
      this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
      this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
      this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.add(8, new LookAroundGoal(this));
   }

   public static DefaultAttributeContainer.Builder createPigAttributes() {
      return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MAX_HEALTH, 10.0).add(EntityAttributes.MOVEMENT_SPEED, 0.25);
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      if (this.hasSaddleEquipped()) {
         Entity var2 = this.getFirstPassenger();
         if (var2 instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)var2;
            if (playerEntity.isHolding(Items.CARROT_ON_A_STICK)) {
               return playerEntity;
            }
         }
      }

      return super.getControllingPassenger();
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
      builder.add(VARIANT, Variants.getOrDefaultOrThrow(this.getRegistryManager(), PigVariants.DEFAULT));
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      Variants.writeVariantToNbt(view, this.getVariant());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      Variants.readVariantFromNbt(view, RegistryKeys.PIG_VARIANT).ifPresent(this::setVariant);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_PIG_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_PIG_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PIG_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(SoundEvents.ENTITY_PIG_STEP, 0.15F, 1.0F);
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
            return actionResult;
         }
      }
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
      return (RegistryEntry)(slot == EquipmentSlot.SADDLE ? SoundEvents.ENTITY_PIG_SADDLE : super.getEquipSound(slot, stack, equippableComponent));
   }

   public Vec3d updatePassengerForDismount(LivingEntity passenger) {
      Direction direction = this.getMovementDirection();
      if (direction.getAxis() == Direction.Axis.Y) {
         return super.updatePassengerForDismount(passenger);
      } else {
         int[][] is = Dismounting.getDismountOffsets(direction);
         BlockPos blockPos = this.getBlockPos();
         BlockPos.Mutable mutable = new BlockPos.Mutable();
         UnmodifiableIterator var6 = passenger.getPoses().iterator();

         while(var6.hasNext()) {
            EntityPose entityPose = (EntityPose)var6.next();
            Box box = passenger.getBoundingBox(entityPose);
            int[][] var9 = is;
            int var10 = is.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               int[] js = var9[var11];
               mutable.set(blockPos.getX() + js[0], blockPos.getY(), blockPos.getZ() + js[1]);
               double d = this.getWorld().getDismountHeight(mutable);
               if (Dismounting.canDismountInBlock(d)) {
                  Vec3d vec3d = Vec3d.ofCenter(mutable, d);
                  if (Dismounting.canPlaceEntityAt(this.getWorld(), passenger, box.offset(vec3d))) {
                     passenger.setPose(entityPose);
                     return vec3d;
                  }
               }
            }
         }

         return super.updatePassengerForDismount(passenger);
      }
   }

   public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
      if (world.getDifficulty() != Difficulty.PEACEFUL) {
         ZombifiedPiglinEntity zombifiedPiglinEntity = (ZombifiedPiglinEntity)this.convertTo(EntityType.ZOMBIFIED_PIGLIN, EntityConversionContext.create(this, false, true), (zombifiedPiglin) -> {
            if (this.getMainHandStack().isEmpty()) {
               zombifiedPiglin.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
            }

            zombifiedPiglin.setPersistent();
         });
         if (zombifiedPiglinEntity == null) {
            super.onStruckByLightning(world, lightning);
         }
      } else {
         super.onStruckByLightning(world, lightning);
      }

   }

   protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
      super.tickControlled(controllingPlayer, movementInput);
      this.setRotation(controllingPlayer.getYaw(), controllingPlayer.getPitch() * 0.5F);
      this.lastYaw = this.bodyYaw = this.headYaw = this.getYaw();
      this.saddledComponent.tickBoost();
   }

   protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
      return new Vec3d(0.0, 0.0, 1.0);
   }

   protected float getSaddledSpeed(PlayerEntity controllingPlayer) {
      return (float)(this.getAttributeValue(EntityAttributes.MOVEMENT_SPEED) * 0.225 * (double)this.saddledComponent.getMovementSpeedMultiplier());
   }

   public boolean consumeOnAStickItem() {
      return this.saddledComponent.boost(this.getRandom());
   }

   @Nullable
   public PigEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
      PigEntity pigEntity = (PigEntity)EntityType.PIG.create(serverWorld, SpawnReason.BREEDING);
      if (pigEntity != null && passiveEntity instanceof PigEntity pigEntity2) {
         pigEntity.setVariant(this.random.nextBoolean() ? this.getVariant() : pigEntity2.getVariant());
      }

      return pigEntity;
   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack.isIn(ItemTags.PIG_FOOD);
   }

   public Vec3d getLeashOffset() {
      return new Vec3d(0.0, (double)(0.6F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
   }

   private void setVariant(RegistryEntry variant) {
      this.dataTracker.set(VARIANT, variant);
   }

   public RegistryEntry getVariant() {
      return (RegistryEntry)this.dataTracker.get(VARIANT);
   }

   @Nullable
   public Object get(ComponentType type) {
      return type == DataComponentTypes.PIG_VARIANT ? castComponentValue(type, this.getVariant()) : super.get(type);
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.PIG_VARIANT);
      super.copyComponentsFrom(from);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.PIG_VARIANT) {
         this.setVariant((RegistryEntry)castComponentValue(DataComponentTypes.PIG_VARIANT, value));
         return true;
      } else {
         return super.setApplicableComponent(type, value);
      }
   }

   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      Variants.select(SpawnContext.of(world, this.getBlockPos()), RegistryKeys.PIG_VARIANT).ifPresent(this::setVariant);
      return super.initialize(world, difficulty, spawnReason, entityData);
   }

   // $FF: synthetic method
   @Nullable
   public PassiveEntity createChild(final ServerWorld world, final PassiveEntity entity) {
      return this.createChild(world, entity);
   }

   static {
      BOOST_TIME = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.INTEGER);
      VARIANT = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.PIG_VARIANT);
   }
}
