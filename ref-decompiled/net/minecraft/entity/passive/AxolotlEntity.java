package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.InterpolatedFlipFlop;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class AxolotlEntity extends AnimalEntity implements Bucketable {
   public static final int PLAY_DEAD_TICKS = 200;
   private static final int field_52482 = 10;
   protected static final ImmutableList SENSORS;
   protected static final ImmutableList MEMORY_MODULES;
   private static final TrackedData VARIANT;
   private static final TrackedData PLAYING_DEAD;
   private static final TrackedData FROM_BUCKET;
   public static final double BUFF_RANGE = 20.0;
   public static final int BLUE_BABY_CHANCE = 1200;
   private static final int MAX_AIR = 6000;
   public static final String VARIANT_KEY = "Variant";
   private static final int HYDRATION_BY_POTION = 1800;
   private static final int MAX_REGENERATION_BUFF_DURATION = 2400;
   private static final boolean DEFAULT_FROM_BUCKET = false;
   public final InterpolatedFlipFlop playingDeadFf = new InterpolatedFlipFlop(10, MathHelper::easeInOutSine);
   public final InterpolatedFlipFlop inWaterFf = new InterpolatedFlipFlop(10, MathHelper::easeInOutSine);
   public final InterpolatedFlipFlop onGroundFf = new InterpolatedFlipFlop(10, MathHelper::easeInOutSine);
   public final InterpolatedFlipFlop isMovingFf = new InterpolatedFlipFlop(10, MathHelper::easeInOutSine);
   private static final int BUFF_DURATION = 100;

   public AxolotlEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
      this.moveControl = new AxolotlMoveControl(this);
      this.lookControl = new AxolotlLookControl(this, 20);
   }

   public float getPathfindingFavor(BlockPos pos, WorldView world) {
      return 0.0F;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(VARIANT, 0);
      builder.add(PLAYING_DEAD, false);
      builder.add(FROM_BUCKET, false);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.put("Variant", AxolotlEntity.Variant.INDEX_CODEC, this.getVariant());
      view.putBoolean("FromBucket", this.isFromBucket());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setVariant((Variant)view.read("Variant", AxolotlEntity.Variant.INDEX_CODEC).orElse(AxolotlEntity.Variant.DEFAULT));
      this.setFromBucket(view.getBoolean("FromBucket", false));
   }

   public void playAmbientSound() {
      if (!this.isPlayingDead()) {
         super.playAmbientSound();
      }
   }

   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      boolean bl = false;
      if (spawnReason == SpawnReason.BUCKET) {
         return (EntityData)entityData;
      } else {
         Random random = world.getRandom();
         if (entityData instanceof AxolotlData) {
            if (((AxolotlData)entityData).getSpawnedCount() >= 2) {
               bl = true;
            }
         } else {
            entityData = new AxolotlData(new Variant[]{AxolotlEntity.Variant.getRandomNatural(random), AxolotlEntity.Variant.getRandomNatural(random)});
         }

         this.setVariant(((AxolotlData)entityData).getRandomVariant(random));
         if (bl) {
            this.setBreedingAge(-24000);
         }

         return super.initialize(world, difficulty, spawnReason, (EntityData)entityData);
      }
   }

   public void baseTick() {
      int i = this.getAir();
      super.baseTick();
      if (!this.isAiDisabled()) {
         World var3 = this.getWorld();
         if (var3 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var3;
            this.tickAir(serverWorld, i);
         }
      }

      if (this.getWorld().isClient()) {
         this.tickClient();
      }

   }

   private void tickClient() {
      State state;
      if (this.isPlayingDead()) {
         state = AxolotlEntity.State.PLAYING_DEAD;
      } else if (this.isTouchingWater()) {
         state = AxolotlEntity.State.IN_WATER;
      } else if (this.isOnGround()) {
         state = AxolotlEntity.State.ON_GROUND;
      } else {
         state = AxolotlEntity.State.IN_AIR;
      }

      this.playingDeadFf.tick(state == AxolotlEntity.State.PLAYING_DEAD);
      this.inWaterFf.tick(state == AxolotlEntity.State.IN_WATER);
      this.onGroundFf.tick(state == AxolotlEntity.State.ON_GROUND);
      boolean bl = this.limbAnimator.isLimbMoving() || this.getPitch() != this.lastPitch || this.getYaw() != this.lastYaw;
      this.isMovingFf.tick(bl);
   }

   protected void tickAir(ServerWorld world, int air) {
      if (this.isAlive() && !this.isTouchingWaterOrRain()) {
         this.setAir(air - 1);
         if (this.getAir() == -20) {
            this.setAir(0);
            this.damage(world, this.getDamageSources().dryOut(), 2.0F);
         }
      } else {
         this.setAir(this.getMaxAir());
      }

   }

   public void hydrateFromPotion() {
      int i = this.getAir() + 1800;
      this.setAir(Math.min(i, this.getMaxAir()));
   }

   public int getMaxAir() {
      return 6000;
   }

   public Variant getVariant() {
      return AxolotlEntity.Variant.byIndex((Integer)this.dataTracker.get(VARIANT));
   }

   private void setVariant(Variant variant) {
      this.dataTracker.set(VARIANT, variant.getIndex());
   }

   @Nullable
   public Object get(ComponentType type) {
      return type == DataComponentTypes.AXOLOTL_VARIANT ? castComponentValue(type, this.getVariant()) : super.get(type);
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.AXOLOTL_VARIANT);
      super.copyComponentsFrom(from);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.AXOLOTL_VARIANT) {
         this.setVariant((Variant)castComponentValue(DataComponentTypes.AXOLOTL_VARIANT, value));
         return true;
      } else {
         return super.setApplicableComponent(type, value);
      }
   }

   private static boolean shouldBabyBeDifferent(Random random) {
      return random.nextInt(1200) == 0;
   }

   public boolean canSpawn(WorldView world) {
      return world.doesNotIntersectEntities(this);
   }

   public boolean isPushedByFluids() {
      return false;
   }

   public void setPlayingDead(boolean playingDead) {
      this.dataTracker.set(PLAYING_DEAD, playingDead);
   }

   public boolean isPlayingDead() {
      return (Boolean)this.dataTracker.get(PLAYING_DEAD);
   }

   public boolean isFromBucket() {
      return (Boolean)this.dataTracker.get(FROM_BUCKET);
   }

   public void setFromBucket(boolean fromBucket) {
      this.dataTracker.set(FROM_BUCKET, fromBucket);
   }

   @Nullable
   public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
      AxolotlEntity axolotlEntity = (AxolotlEntity)EntityType.AXOLOTL.create(world, SpawnReason.BREEDING);
      if (axolotlEntity != null) {
         Variant variant;
         if (shouldBabyBeDifferent(this.random)) {
            variant = AxolotlEntity.Variant.getRandomUnnatural(this.random);
         } else {
            variant = this.random.nextBoolean() ? this.getVariant() : ((AxolotlEntity)entity).getVariant();
         }

         axolotlEntity.setVariant(variant);
         axolotlEntity.setPersistent();
      }

      return axolotlEntity;
   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack.isIn(ItemTags.AXOLOTL_FOOD);
   }

   public boolean canBeLeashed() {
      return true;
   }

   protected void mobTick(ServerWorld world) {
      Profiler profiler = Profilers.get();
      profiler.push("axolotlBrain");
      this.getBrain().tick(world, this);
      profiler.pop();
      profiler.push("axolotlActivityUpdate");
      AxolotlBrain.updateActivities(this);
      profiler.pop();
      if (!this.isAiDisabled()) {
         Optional optional = this.getBrain().getOptionalRegisteredMemory(MemoryModuleType.PLAY_DEAD_TICKS);
         this.setPlayingDead(optional.isPresent() && (Integer)optional.get() > 0);
      }

   }

   public static DefaultAttributeContainer.Builder createAxolotlAttributes() {
      return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MAX_HEALTH, 14.0).add(EntityAttributes.MOVEMENT_SPEED, 1.0).add(EntityAttributes.ATTACK_DAMAGE, 2.0).add(EntityAttributes.STEP_HEIGHT, 1.0);
   }

   protected EntityNavigation createNavigation(World world) {
      return new AmphibiousSwimNavigation(this, world);
   }

   public void playAttackSound() {
      this.playSound(SoundEvents.ENTITY_AXOLOTL_ATTACK, 1.0F, 1.0F);
   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      float f = this.getHealth();
      if (!this.isAiDisabled() && this.getWorld().random.nextInt(3) == 0 && ((float)this.getWorld().random.nextInt(3) < amount || f / this.getMaxHealth() < 0.5F) && amount < f && this.isTouchingWater() && (source.getAttacker() != null || source.getSource() != null) && !this.isPlayingDead()) {
         this.brain.remember(MemoryModuleType.PLAY_DEAD_TICKS, (int)200);
      }

      return super.damage(world, source, amount);
   }

   public int getMaxLookPitchChange() {
      return 1;
   }

   public int getMaxHeadRotation() {
      return 1;
   }

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      return (ActionResult)Bucketable.tryBucket(player, hand, this).orElse(super.interactMob(player, hand));
   }

   public void copyDataToStack(ItemStack stack) {
      Bucketable.copyDataToStack(this, stack);
      stack.copy(DataComponentTypes.AXOLOTL_VARIANT, this);
      NbtComponent.set(DataComponentTypes.BUCKET_ENTITY_DATA, stack, (nbt) -> {
         nbt.putInt("Age", this.getBreedingAge());
         Brain brain = this.getBrain();
         if (brain.hasMemoryModule(MemoryModuleType.HAS_HUNTING_COOLDOWN)) {
            nbt.putLong("HuntingCooldown", brain.getMemoryExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN));
         }

      });
   }

   public void copyDataFromNbt(NbtCompound nbt) {
      Bucketable.copyDataFromNbt(this, nbt);
      this.setBreedingAge(nbt.getInt("Age", 0));
      nbt.getLong("HuntingCooldown").ifPresentOrElse((huntingCooldown) -> {
         this.getBrain().remember(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, nbt.getLong("HuntingCooldown", 0L));
      }, () -> {
         this.getBrain().remember(MemoryModuleType.HAS_HUNTING_COOLDOWN, Optional.empty());
      });
   }

   public ItemStack getBucketItem() {
      return new ItemStack(Items.AXOLOTL_BUCKET);
   }

   public SoundEvent getBucketFillSound() {
      return SoundEvents.ITEM_BUCKET_FILL_AXOLOTL;
   }

   public boolean canTakeDamage() {
      return !this.isPlayingDead() && super.canTakeDamage();
   }

   public static void appreciatePlayer(ServerWorld world, AxolotlEntity axolotl, LivingEntity target) {
      if (target.isDead()) {
         DamageSource damageSource = target.getRecentDamageSource();
         if (damageSource != null) {
            Entity entity = damageSource.getAttacker();
            if (entity != null && entity.getType() == EntityType.PLAYER) {
               PlayerEntity playerEntity = (PlayerEntity)entity;
               List list = world.getNonSpectatingEntities(PlayerEntity.class, axolotl.getBoundingBox().expand(20.0));
               if (list.contains(playerEntity)) {
                  axolotl.buffPlayer(playerEntity);
               }
            }
         }
      }

   }

   public void buffPlayer(PlayerEntity player) {
      StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.REGENERATION);
      if (statusEffectInstance == null || statusEffectInstance.isDurationBelow(2399)) {
         int i = statusEffectInstance != null ? statusEffectInstance.getDuration() : 0;
         int j = Math.min(2400, 100 + i);
         player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, j, 0), this);
      }

      player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
   }

   public boolean cannotDespawn() {
      return super.cannotDespawn() || this.isFromBucket();
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_AXOLOTL_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_AXOLOTL_DEATH;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return this.isTouchingWater() ? SoundEvents.ENTITY_AXOLOTL_IDLE_WATER : SoundEvents.ENTITY_AXOLOTL_IDLE_AIR;
   }

   protected SoundEvent getSplashSound() {
      return SoundEvents.ENTITY_AXOLOTL_SPLASH;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_AXOLOTL_SWIM;
   }

   protected Brain.Profile createBrainProfile() {
      return Brain.createProfile(MEMORY_MODULES, SENSORS);
   }

   protected Brain deserializeBrain(Dynamic dynamic) {
      return AxolotlBrain.create(this.createBrainProfile().deserialize(dynamic));
   }

   public Brain getBrain() {
      return super.getBrain();
   }

   protected void sendAiDebugData() {
      super.sendAiDebugData();
      DebugInfoSender.sendBrainDebugData(this);
   }

   public void travel(Vec3d movementInput) {
      if (this.isTouchingWater()) {
         this.updateVelocity(this.getMovementSpeed(), movementInput);
         this.move(MovementType.SELF, this.getVelocity());
         this.setVelocity(this.getVelocity().multiply(0.9));
      } else {
         super.travel(movementInput);
      }

   }

   protected void eat(PlayerEntity player, Hand hand, ItemStack stack) {
      if (stack.isOf(Items.TROPICAL_FISH_BUCKET)) {
         player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.WATER_BUCKET)));
      } else {
         super.eat(player, hand, stack);
      }

   }

   public boolean canImmediatelyDespawn(double distanceSquared) {
      return !this.isFromBucket() && !this.hasCustomName();
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.getTargetInBrain();
   }

   public static boolean canSpawn(EntityType type, ServerWorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
      return world.getBlockState(pos.down()).isIn(BlockTags.AXOLOTLS_SPAWNABLE_ON);
   }

   static {
      SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.AXOLOTL_ATTACKABLES, SensorType.AXOLOTL_TEMPTATIONS);
      MEMORY_MODULES = ImmutableList.of(MemoryModuleType.BREED_TARGET, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_ADULT, new MemoryModuleType[]{MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.HAS_HUNTING_COOLDOWN, MemoryModuleType.IS_PANICKING});
      VARIANT = DataTracker.registerData(AxolotlEntity.class, TrackedDataHandlerRegistry.INTEGER);
      PLAYING_DEAD = DataTracker.registerData(AxolotlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      FROM_BUCKET = DataTracker.registerData(AxolotlEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
   }

   static class AxolotlMoveControl extends AquaticMoveControl {
      private final AxolotlEntity axolotl;

      public AxolotlMoveControl(AxolotlEntity axolotl) {
         super(axolotl, 85, 10, 0.1F, 0.5F, false);
         this.axolotl = axolotl;
      }

      public void tick() {
         if (!this.axolotl.isPlayingDead()) {
            super.tick();
         }

      }
   }

   class AxolotlLookControl extends YawAdjustingLookControl {
      public AxolotlLookControl(final AxolotlEntity axolotl, final int yawAdjustThreshold) {
         super(axolotl, yawAdjustThreshold);
      }

      public void tick() {
         if (!AxolotlEntity.this.isPlayingDead()) {
            super.tick();
         }

      }
   }

   public static enum Variant implements StringIdentifiable {
      LUCY(0, "lucy", true),
      WILD(1, "wild", true),
      GOLD(2, "gold", true),
      CYAN(3, "cyan", true),
      BLUE(4, "blue", false);

      public static final Variant DEFAULT = LUCY;
      private static final IntFunction INDEX_MAPPER = ValueLists.createIndexToValueFunction(Variant::getIndex, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
      public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, Variant::getIndex);
      public static final Codec CODEC = StringIdentifiable.createCodec(Variant::values);
      /** @deprecated */
      @Deprecated
      public static final Codec INDEX_CODEC;
      private final int index;
      private final String id;
      private final boolean natural;

      private Variant(final int index, final String id, final boolean natural) {
         this.index = index;
         this.id = id;
         this.natural = natural;
      }

      public int getIndex() {
         return this.index;
      }

      public String getId() {
         return this.id;
      }

      public String asString() {
         return this.id;
      }

      public static Variant byIndex(int index) {
         return (Variant)INDEX_MAPPER.apply(index);
      }

      public static Variant getRandomNatural(Random random) {
         return getRandom(random, true);
      }

      public static Variant getRandomUnnatural(Random random) {
         return getRandom(random, false);
      }

      private static Variant getRandom(Random random, boolean natural) {
         Variant[] variants = (Variant[])Arrays.stream(values()).filter((variant) -> {
            return variant.natural == natural;
         }).toArray((i) -> {
            return new Variant[i];
         });
         return (Variant)Util.getRandom((Object[])variants, random);
      }

      // $FF: synthetic method
      private static Variant[] method_36644() {
         return new Variant[]{LUCY, WILD, GOLD, CYAN, BLUE};
      }

      static {
         PrimitiveCodec var10000 = Codec.INT;
         IntFunction var10001 = INDEX_MAPPER;
         Objects.requireNonNull(var10001);
         INDEX_CODEC = var10000.xmap(var10001::apply, Variant::getIndex);
      }
   }

   public static class AxolotlData extends PassiveEntity.PassiveData {
      public final Variant[] variants;

      public AxolotlData(Variant... variants) {
         super(false);
         this.variants = variants;
      }

      public Variant getRandomVariant(Random random) {
         return this.variants[random.nextInt(this.variants.length)];
      }
   }

   public static enum State {
      PLAYING_DEAD,
      IN_WATER,
      ON_GROUND,
      IN_AIR;

      // $FF: synthetic method
      private static State[] method_61480() {
         return new State[]{PLAYING_DEAD, IN_WATER, ON_GROUND, IN_AIR};
      }
   }
}
