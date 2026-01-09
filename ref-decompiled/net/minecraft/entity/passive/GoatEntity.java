package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.InstrumentTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class GoatEntity extends AnimalEntity {
   public static final EntityDimensions LONG_JUMPING_DIMENSIONS = EntityDimensions.changing(0.9F, 1.3F).scaled(0.7F);
   private static final int DEFAULT_ATTACK_DAMAGE = 2;
   private static final int BABY_ATTACK_DAMAGE = 1;
   protected static final ImmutableList SENSORS;
   protected static final ImmutableList MEMORY_MODULES;
   public static final int FALL_DAMAGE_SUBTRACTOR = 10;
   public static final double SCREAMING_CHANCE = 0.02;
   public static final double field_39046 = 0.10000000149011612;
   private static final TrackedData SCREAMING;
   private static final TrackedData LEFT_HORN;
   private static final TrackedData RIGHT_HORN;
   private static final boolean DEFAULT_SCREAMING = false;
   private static final boolean DEFAULT_LEFT_HORN = true;
   private static final boolean DEFAULT_RIGHT_HORN = true;
   private boolean preparingRam;
   private int headPitch;

   public GoatEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.getNavigation().setCanSwim(true);
      this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, -1.0F);
      this.setPathfindingPenalty(PathNodeType.DANGER_POWDER_SNOW, -1.0F);
   }

   public ItemStack getGoatHornStack() {
      Random random = Random.create((long)this.getUuid().hashCode());
      TagKey tagKey = this.isScreaming() ? InstrumentTags.SCREAMING_GOAT_HORNS : InstrumentTags.REGULAR_GOAT_HORNS;
      return (ItemStack)this.getWorld().getRegistryManager().getOrThrow(RegistryKeys.INSTRUMENT).getRandomEntry(tagKey, random).map((instrument) -> {
         return GoatHornItem.getStackForInstrument(Items.GOAT_HORN, instrument);
      }).orElseGet(() -> {
         return new ItemStack(Items.GOAT_HORN);
      });
   }

   protected Brain.Profile createBrainProfile() {
      return Brain.createProfile(MEMORY_MODULES, SENSORS);
   }

   protected Brain deserializeBrain(Dynamic dynamic) {
      return GoatBrain.create(this.createBrainProfile().deserialize(dynamic));
   }

   public static DefaultAttributeContainer.Builder createGoatAttributes() {
      return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MAX_HEALTH, 10.0).add(EntityAttributes.MOVEMENT_SPEED, 0.20000000298023224).add(EntityAttributes.ATTACK_DAMAGE, 2.0);
   }

   protected void onGrowUp() {
      if (this.isBaby()) {
         this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
         this.removeHorns();
      } else {
         this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
         this.addHorns();
      }

   }

   protected int computeFallDamage(double fallDistance, float damagePerDistance) {
      return super.computeFallDamage(fallDistance, damagePerDistance) - 10;
   }

   protected SoundEvent getAmbientSound() {
      return this.isScreaming() ? SoundEvents.ENTITY_GOAT_SCREAMING_AMBIENT : SoundEvents.ENTITY_GOAT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return this.isScreaming() ? SoundEvents.ENTITY_GOAT_SCREAMING_HURT : SoundEvents.ENTITY_GOAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isScreaming() ? SoundEvents.ENTITY_GOAT_SCREAMING_DEATH : SoundEvents.ENTITY_GOAT_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(SoundEvents.ENTITY_GOAT_STEP, 0.15F, 1.0F);
   }

   protected SoundEvent getMilkingSound() {
      return this.isScreaming() ? SoundEvents.ENTITY_GOAT_SCREAMING_MILK : SoundEvents.ENTITY_GOAT_MILK;
   }

   @Nullable
   public GoatEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
      GoatEntity goatEntity = (GoatEntity)EntityType.GOAT.create(serverWorld, SpawnReason.BREEDING);
      if (goatEntity != null) {
         boolean var10000;
         label22: {
            label21: {
               GoatBrain.resetLongJumpCooldown(goatEntity, serverWorld.getRandom());
               PassiveEntity passiveEntity2 = serverWorld.getRandom().nextBoolean() ? this : passiveEntity;
               if (passiveEntity2 instanceof GoatEntity) {
                  GoatEntity goatEntity2 = (GoatEntity)passiveEntity2;
                  if (goatEntity2.isScreaming()) {
                     break label21;
                  }
               }

               if (!(serverWorld.getRandom().nextDouble() < 0.02)) {
                  var10000 = false;
                  break label22;
               }
            }

            var10000 = true;
         }

         boolean bl = var10000;
         goatEntity.setScreaming(bl);
      }

      return goatEntity;
   }

   public Brain getBrain() {
      return super.getBrain();
   }

   protected void mobTick(ServerWorld world) {
      Profiler profiler = Profilers.get();
      profiler.push("goatBrain");
      this.getBrain().tick(world, this);
      profiler.pop();
      profiler.push("goatActivityUpdate");
      GoatBrain.updateActivities(this);
      profiler.pop();
      super.mobTick(world);
   }

   public int getMaxHeadRotation() {
      return 15;
   }

   public void setHeadYaw(float headYaw) {
      int i = this.getMaxHeadRotation();
      float f = MathHelper.subtractAngles(this.bodyYaw, headYaw);
      float g = MathHelper.clamp(f, (float)(-i), (float)i);
      super.setHeadYaw(this.bodyYaw + g);
   }

   protected void playEatSound() {
      this.getWorld().playSoundFromEntity((Entity)null, this, this.isScreaming() ? SoundEvents.ENTITY_GOAT_SCREAMING_EAT : SoundEvents.ENTITY_GOAT_EAT, SoundCategory.NEUTRAL, 1.0F, MathHelper.nextBetween(this.getWorld().random, 0.8F, 1.2F));
   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack.isIn(ItemTags.GOAT_FOOD);
   }

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      if (itemStack.isOf(Items.BUCKET) && !this.isBaby()) {
         player.playSound(this.getMilkingSound(), 1.0F, 1.0F);
         ItemStack itemStack2 = ItemUsage.exchangeStack(itemStack, player, Items.MILK_BUCKET.getDefaultStack());
         player.setStackInHand(hand, itemStack2);
         return ActionResult.SUCCESS;
      } else {
         ActionResult actionResult = super.interactMob(player, hand);
         if (actionResult.isAccepted() && this.isBreedingItem(itemStack)) {
            this.playEatSound();
         }

         return actionResult;
      }
   }

   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      Random random = world.getRandom();
      GoatBrain.resetLongJumpCooldown(this, random);
      this.setScreaming(random.nextDouble() < 0.02);
      this.onGrowUp();
      if (!this.isBaby() && (double)random.nextFloat() < 0.10000000149011612) {
         TrackedData trackedData = random.nextBoolean() ? LEFT_HORN : RIGHT_HORN;
         this.dataTracker.set(trackedData, false);
      }

      return super.initialize(world, difficulty, spawnReason, entityData);
   }

   protected void sendAiDebugData() {
      super.sendAiDebugData();
      DebugInfoSender.sendBrainDebugData(this);
   }

   public EntityDimensions getBaseDimensions(EntityPose pose) {
      return pose == EntityPose.LONG_JUMPING ? LONG_JUMPING_DIMENSIONS.scaled(this.getScaleFactor()) : super.getBaseDimensions(pose);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putBoolean("IsScreamingGoat", this.isScreaming());
      view.putBoolean("HasLeftHorn", this.hasLeftHorn());
      view.putBoolean("HasRightHorn", this.hasRightHorn());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setScreaming(view.getBoolean("IsScreamingGoat", false));
      this.dataTracker.set(LEFT_HORN, view.getBoolean("HasLeftHorn", true));
      this.dataTracker.set(RIGHT_HORN, view.getBoolean("HasRightHorn", true));
   }

   public void handleStatus(byte status) {
      if (status == 58) {
         this.preparingRam = true;
      } else if (status == 59) {
         this.preparingRam = false;
      } else {
         super.handleStatus(status);
      }

   }

   public void tickMovement() {
      if (this.preparingRam) {
         ++this.headPitch;
      } else {
         this.headPitch -= 2;
      }

      this.headPitch = MathHelper.clamp(this.headPitch, 0, 20);
      super.tickMovement();
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(SCREAMING, false);
      builder.add(LEFT_HORN, true);
      builder.add(RIGHT_HORN, true);
   }

   public boolean hasLeftHorn() {
      return (Boolean)this.dataTracker.get(LEFT_HORN);
   }

   public boolean hasRightHorn() {
      return (Boolean)this.dataTracker.get(RIGHT_HORN);
   }

   public boolean dropHorn() {
      boolean bl = this.hasLeftHorn();
      boolean bl2 = this.hasRightHorn();
      if (!bl && !bl2) {
         return false;
      } else {
         TrackedData trackedData;
         if (!bl) {
            trackedData = RIGHT_HORN;
         } else if (!bl2) {
            trackedData = LEFT_HORN;
         } else {
            trackedData = this.random.nextBoolean() ? LEFT_HORN : RIGHT_HORN;
         }

         this.dataTracker.set(trackedData, false);
         Vec3d vec3d = this.getPos();
         ItemStack itemStack = this.getGoatHornStack();
         double d = (double)MathHelper.nextBetween(this.random, -0.2F, 0.2F);
         double e = (double)MathHelper.nextBetween(this.random, 0.3F, 0.7F);
         double f = (double)MathHelper.nextBetween(this.random, -0.2F, 0.2F);
         ItemEntity itemEntity = new ItemEntity(this.getWorld(), vec3d.getX(), vec3d.getY(), vec3d.getZ(), itemStack, d, e, f);
         this.getWorld().spawnEntity(itemEntity);
         return true;
      }
   }

   public void addHorns() {
      this.dataTracker.set(LEFT_HORN, true);
      this.dataTracker.set(RIGHT_HORN, true);
   }

   public void removeHorns() {
      this.dataTracker.set(LEFT_HORN, false);
      this.dataTracker.set(RIGHT_HORN, false);
   }

   public boolean isScreaming() {
      return (Boolean)this.dataTracker.get(SCREAMING);
   }

   public void setScreaming(boolean screaming) {
      this.dataTracker.set(SCREAMING, screaming);
   }

   public float getHeadPitch() {
      return (float)this.headPitch / 20.0F * 30.0F * 0.017453292F;
   }

   public static boolean canSpawn(EntityType entityType, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
      return world.getBlockState(pos.down()).isIn(BlockTags.GOATS_SPAWNABLE_ON) && isLightLevelValidForNaturalSpawn(world, pos);
   }

   // $FF: synthetic method
   @Nullable
   public PassiveEntity createChild(final ServerWorld world, final PassiveEntity entity) {
      return this.createChild(world, entity);
   }

   static {
      SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.GOAT_TEMPTATIONS);
      MEMORY_MODULES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.BREED_TARGET, MemoryModuleType.LONG_JUMP_COOLING_DOWN, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, new MemoryModuleType[]{MemoryModuleType.IS_TEMPTED, MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleType.RAM_TARGET, MemoryModuleType.IS_PANICKING});
      SCREAMING = DataTracker.registerData(GoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      LEFT_HORN = DataTracker.registerData(GoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      RIGHT_HORN = DataTracker.registerData(GoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
   }
}
