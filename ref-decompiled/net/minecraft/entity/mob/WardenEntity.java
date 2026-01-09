package net.minecraft.entity.mob;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Dynamic;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.WardenAngerManager;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.SonicBoomTask;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.GameEventTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Unit;
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
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class WardenEntity extends HostileEntity implements Vibrations {
   private static final int field_38142 = 40;
   private static final int field_38860 = 200;
   private static final int MAX_HEALTH = 500;
   private static final float MOVEMENT_SPEED = 0.3F;
   private static final float KNOCKBACK_RESISTANCE = 1.0F;
   private static final float ATTACK_KNOCKBACK = 1.5F;
   private static final int ATTACK_DAMAGE = 30;
   private static final int FOLLOW_RANGE = 24;
   private static final TrackedData ANGER;
   private static final int field_38149 = 200;
   private static final int DARKNESS_EFFECT_DURATION = 260;
   private static final int field_38151 = 20;
   private static final int field_38152 = 120;
   private static final int field_38153 = 20;
   private static final int ANGRINESS_AMOUNT = 35;
   private static final int field_38156 = 10;
   private static final int field_39117 = 20;
   private static final int field_38157 = 100;
   private static final int field_38158 = 20;
   private static final int field_38159 = 30;
   private static final float field_38160 = 4.5F;
   private static final float field_38161 = 0.7F;
   private static final int field_39305 = 30;
   private int tendrilAlpha;
   private int lastTendrilAlpha;
   private int heartbeatCooldown;
   private int lastHeartbeatCooldown;
   public AnimationState roaringAnimationState = new AnimationState();
   public AnimationState sniffingAnimationState = new AnimationState();
   public AnimationState emergingAnimationState = new AnimationState();
   public AnimationState diggingAnimationState = new AnimationState();
   public AnimationState attackingAnimationState = new AnimationState();
   public AnimationState chargingSonicBoomAnimationState = new AnimationState();
   private final EntityGameEventHandler gameEventHandler = new EntityGameEventHandler(new Vibrations.VibrationListener(this));
   private final Vibrations.Callback vibrationCallback = new VibrationCallback();
   private Vibrations.ListenerData vibrationListenerData = new Vibrations.ListenerData();
   WardenAngerManager angerManager = new WardenAngerManager(this::isValidTarget, Collections.emptyList());

   public WardenEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.experiencePoints = 5;
      this.getNavigation().setCanSwim(true);
      this.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0.0F);
      this.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 8.0F);
      this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, 8.0F);
      this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
      this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
      this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
   }

   public Packet createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
      return new EntitySpawnS2CPacket(this, entityTrackerEntry, this.isInPose(EntityPose.EMERGING) ? 1 : 0);
   }

   public void onSpawnPacket(EntitySpawnS2CPacket packet) {
      super.onSpawnPacket(packet);
      if (packet.getEntityData() == 1) {
         this.setPose(EntityPose.EMERGING);
      }

   }

   public boolean canSpawn(WorldView world) {
      return super.canSpawn(world) && world.isSpaceEmpty(this, this.getType().getDimensions().getBoxAt(this.getPos()));
   }

   public float getPathfindingFavor(BlockPos pos, WorldView world) {
      return 0.0F;
   }

   public boolean isInvulnerableTo(ServerWorld world, DamageSource source) {
      return this.isDiggingOrEmerging() && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) ? true : super.isInvulnerableTo(world, source);
   }

   boolean isDiggingOrEmerging() {
      return this.isInPose(EntityPose.DIGGING) || this.isInPose(EntityPose.EMERGING);
   }

   protected boolean canStartRiding(Entity entity) {
      return false;
   }

   public float getWeaponDisableBlockingForSeconds() {
      return 5.0F;
   }

   protected float calculateNextStepSoundDistance() {
      return this.distanceTraveled + 0.55F;
   }

   public static DefaultAttributeContainer.Builder addAttributes() {
      return HostileEntity.createHostileAttributes().add(EntityAttributes.MAX_HEALTH, 500.0).add(EntityAttributes.MOVEMENT_SPEED, 0.30000001192092896).add(EntityAttributes.KNOCKBACK_RESISTANCE, 1.0).add(EntityAttributes.ATTACK_KNOCKBACK, 1.5).add(EntityAttributes.ATTACK_DAMAGE, 30.0).add(EntityAttributes.FOLLOW_RANGE, 24.0);
   }

   public boolean occludeVibrationSignals() {
      return true;
   }

   protected float getSoundVolume() {
      return 4.0F;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return !this.isInPose(EntityPose.ROARING) && !this.isDiggingOrEmerging() ? this.getAngriness().getSound() : null;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_WARDEN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_WARDEN_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(SoundEvents.ENTITY_WARDEN_STEP, 10.0F, 1.0F);
   }

   public boolean tryAttack(ServerWorld world, Entity target) {
      world.sendEntityStatus(this, (byte)4);
      this.playSound(SoundEvents.ENTITY_WARDEN_ATTACK_IMPACT, 10.0F, this.getSoundPitch());
      SonicBoomTask.cooldown(this, 40);
      return super.tryAttack(world, target);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(ANGER, 0);
   }

   public int getAnger() {
      return (Integer)this.dataTracker.get(ANGER);
   }

   private void updateAnger() {
      this.dataTracker.set(ANGER, this.getAngerAtTarget());
   }

   public void tick() {
      World var2 = this.getWorld();
      if (var2 instanceof ServerWorld serverWorld) {
         Vibrations.Ticker.tick(serverWorld, this.vibrationListenerData, this.vibrationCallback);
         if (this.isPersistent() || this.cannotDespawn()) {
            WardenBrain.resetDigCooldown(this);
         }
      }

      super.tick();
      if (this.getWorld().isClient()) {
         if (this.age % this.getHeartRate() == 0) {
            this.heartbeatCooldown = 10;
            if (!this.isSilent()) {
               this.getWorld().playSoundClient(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WARDEN_HEARTBEAT, this.getSoundCategory(), 5.0F, this.getSoundPitch(), false);
            }
         }

         this.lastTendrilAlpha = this.tendrilAlpha;
         if (this.tendrilAlpha > 0) {
            --this.tendrilAlpha;
         }

         this.lastHeartbeatCooldown = this.heartbeatCooldown;
         if (this.heartbeatCooldown > 0) {
            --this.heartbeatCooldown;
         }

         switch (this.getPose()) {
            case EMERGING:
               this.addDigParticles(this.emergingAnimationState);
               break;
            case DIGGING:
               this.addDigParticles(this.diggingAnimationState);
         }
      }

   }

   protected void mobTick(ServerWorld world) {
      Profiler profiler = Profilers.get();
      profiler.push("wardenBrain");
      this.getBrain().tick(world, this);
      profiler.pop();
      super.mobTick(world);
      if ((this.age + this.getId()) % 120 == 0) {
         addDarknessToClosePlayers(world, this.getPos(), this, 20);
      }

      if (this.age % 20 == 0) {
         this.angerManager.tick(world, this::isValidTarget);
         this.updateAnger();
      }

      WardenBrain.updateActivities(this);
   }

   public void handleStatus(byte status) {
      if (status == 4) {
         this.roaringAnimationState.stop();
         this.attackingAnimationState.start(this.age);
      } else if (status == 61) {
         this.tendrilAlpha = 10;
      } else if (status == 62) {
         this.chargingSonicBoomAnimationState.start(this.age);
      } else {
         super.handleStatus(status);
      }

   }

   private int getHeartRate() {
      float f = (float)this.getAnger() / (float)Angriness.ANGRY.getThreshold();
      return 40 - MathHelper.floor(MathHelper.clamp(f, 0.0F, 1.0F) * 30.0F);
   }

   public float getTendrilAlpha(float tickProgress) {
      return MathHelper.lerp(tickProgress, (float)this.lastTendrilAlpha, (float)this.tendrilAlpha) / 10.0F;
   }

   public float getHeartAlpha(float tickProgress) {
      return MathHelper.lerp(tickProgress, (float)this.lastHeartbeatCooldown, (float)this.heartbeatCooldown) / 10.0F;
   }

   private void addDigParticles(AnimationState animationState) {
      if ((float)animationState.getTimeInMilliseconds((float)this.age) < 4500.0F) {
         Random random = this.getRandom();
         BlockState blockState = this.getSteppingBlockState();
         if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
            for(int i = 0; i < 30; ++i) {
               double d = this.getX() + (double)MathHelper.nextBetween(random, -0.7F, 0.7F);
               double e = this.getY();
               double f = this.getZ() + (double)MathHelper.nextBetween(random, -0.7F, 0.7F);
               this.getWorld().addParticleClient(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, e, f, 0.0, 0.0, 0.0);
            }
         }
      }

   }

   public void onTrackedDataSet(TrackedData data) {
      if (POSE.equals(data)) {
         switch (this.getPose()) {
            case EMERGING:
               this.emergingAnimationState.start(this.age);
               break;
            case DIGGING:
               this.diggingAnimationState.start(this.age);
               break;
            case ROARING:
               this.roaringAnimationState.start(this.age);
               break;
            case SNIFFING:
               this.sniffingAnimationState.start(this.age);
         }
      }

      super.onTrackedDataSet(data);
   }

   public boolean isImmuneToExplosion(Explosion explosion) {
      return this.isDiggingOrEmerging();
   }

   protected Brain deserializeBrain(Dynamic dynamic) {
      return WardenBrain.create(this, dynamic);
   }

   public Brain getBrain() {
      return super.getBrain();
   }

   protected void sendAiDebugData() {
      super.sendAiDebugData();
      DebugInfoSender.sendBrainDebugData(this);
   }

   public void updateEventHandler(BiConsumer callback) {
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         callback.accept(this.gameEventHandler, serverWorld);
      }

   }

   @Contract("null->false")
   public boolean isValidTarget(@Nullable Entity entity) {
      boolean var10000;
      if (entity instanceof LivingEntity livingEntity) {
         if (this.getWorld() == entity.getWorld() && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity) && !this.isTeammate(entity) && livingEntity.getType() != EntityType.ARMOR_STAND && livingEntity.getType() != EntityType.WARDEN && !livingEntity.isInvulnerable() && !livingEntity.isDead() && this.getWorld().getWorldBorder().contains(livingEntity.getBoundingBox())) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public static void addDarknessToClosePlayers(ServerWorld world, Vec3d pos, @Nullable Entity entity, int range) {
      StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.DARKNESS, 260, 0, false, false);
      StatusEffectUtil.addEffectToPlayersWithinDistance(world, entity, pos, (double)range, statusEffectInstance, 200);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.put("anger", WardenAngerManager.createCodec(this::isValidTarget), this.angerManager);
      view.put("listener", Vibrations.ListenerData.CODEC, this.vibrationListenerData);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.angerManager = (WardenAngerManager)view.read("anger", WardenAngerManager.createCodec(this::isValidTarget)).orElseGet(() -> {
         return new WardenAngerManager(this::isValidTarget, Collections.emptyList());
      });
      this.updateAnger();
      this.vibrationListenerData = (Vibrations.ListenerData)view.read("listener", Vibrations.ListenerData.CODEC).orElseGet(Vibrations.ListenerData::new);
   }

   private void playListeningSound() {
      if (!this.isInPose(EntityPose.ROARING)) {
         this.playSound(this.getAngriness().getListeningSound(), 10.0F, this.getSoundPitch());
      }

   }

   public Angriness getAngriness() {
      return Angriness.getForAnger(this.getAngerAtTarget());
   }

   private int getAngerAtTarget() {
      return this.angerManager.getAngerFor(this.getTarget());
   }

   public void removeSuspect(Entity entity) {
      this.angerManager.removeSuspect(entity);
   }

   public void increaseAngerAt(@Nullable Entity entity) {
      this.increaseAngerAt(entity, 35, true);
   }

   @VisibleForTesting
   public void increaseAngerAt(@Nullable Entity entity, int amount, boolean listening) {
      if (!this.isAiDisabled() && this.isValidTarget(entity)) {
         WardenBrain.resetDigCooldown(this);
         boolean bl = !(this.getTarget() instanceof PlayerEntity);
         int i = this.angerManager.increaseAngerAt(entity, amount);
         if (entity instanceof PlayerEntity && bl && Angriness.getForAnger(i).isAngry()) {
            this.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
         }

         if (listening) {
            this.playListeningSound();
         }
      }

   }

   public Optional getPrimeSuspect() {
      return this.getAngriness().isAngry() ? this.angerManager.getPrimeSuspect() : Optional.empty();
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.getTargetInBrain();
   }

   public boolean canImmediatelyDespawn(double distanceSquared) {
      return false;
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      this.getBrain().remember(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
      if (spawnReason == SpawnReason.TRIGGERED) {
         this.setPose(EntityPose.EMERGING);
         this.getBrain().remember(MemoryModuleType.IS_EMERGING, Unit.INSTANCE, (long)WardenBrain.EMERGE_DURATION);
         this.playSound(SoundEvents.ENTITY_WARDEN_AGITATED, 5.0F, 1.0F);
      }

      return super.initialize(world, difficulty, spawnReason, entityData);
   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      boolean bl = super.damage(world, source, amount);
      if (!this.isAiDisabled() && !this.isDiggingOrEmerging()) {
         Entity entity = source.getAttacker();
         this.increaseAngerAt(entity, Angriness.ANGRY.getThreshold() + 20, false);
         if (this.brain.getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).isEmpty() && entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            if (source.isDirect() || this.isInRange(livingEntity, 5.0)) {
               this.updateAttackTarget(livingEntity);
            }
         }
      }

      return bl;
   }

   public void updateAttackTarget(LivingEntity target) {
      this.getBrain().forget(MemoryModuleType.ROAR_TARGET);
      this.getBrain().remember(MemoryModuleType.ATTACK_TARGET, (Object)target);
      this.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      SonicBoomTask.cooldown(this, 200);
   }

   public EntityDimensions getBaseDimensions(EntityPose pose) {
      EntityDimensions entityDimensions = super.getBaseDimensions(pose);
      return this.isDiggingOrEmerging() ? EntityDimensions.fixed(entityDimensions.width(), 1.0F) : entityDimensions;
   }

   public boolean isPushable() {
      return !this.isDiggingOrEmerging() && super.isPushable();
   }

   protected void pushAway(Entity entity) {
      if (!this.isAiDisabled() && !this.getBrain().hasMemoryModule(MemoryModuleType.TOUCH_COOLDOWN)) {
         this.getBrain().remember(MemoryModuleType.TOUCH_COOLDOWN, Unit.INSTANCE, 20L);
         this.increaseAngerAt(entity);
         WardenBrain.lookAtDisturbance(this, entity.getBlockPos());
      }

      super.pushAway(entity);
   }

   @VisibleForTesting
   public WardenAngerManager getAngerManager() {
      return this.angerManager;
   }

   protected EntityNavigation createNavigation(World world) {
      return new MobNavigation(this, this, world) {
         protected PathNodeNavigator createPathNodeNavigator(int range) {
            this.nodeMaker = new LandPathNodeMaker();
            return new PathNodeNavigator(this, this.nodeMaker, range) {
               protected float getDistance(PathNode a, PathNode b) {
                  return a.getHorizontalDistance(b);
               }
            };
         }
      };
   }

   public Vibrations.ListenerData getVibrationListenerData() {
      return this.vibrationListenerData;
   }

   public Vibrations.Callback getVibrationCallback() {
      return this.vibrationCallback;
   }

   static {
      ANGER = DataTracker.registerData(WardenEntity.class, TrackedDataHandlerRegistry.INTEGER);
   }

   private class VibrationCallback implements Vibrations.Callback {
      private static final int RANGE = 16;
      private final PositionSource positionSource = new EntityPositionSource(WardenEntity.this, WardenEntity.this.getStandingEyeHeight());

      VibrationCallback() {
      }

      public int getRange() {
         return 16;
      }

      public PositionSource getPositionSource() {
         return this.positionSource;
      }

      public TagKey getTag() {
         return GameEventTags.WARDEN_CAN_LISTEN;
      }

      public boolean triggersAvoidCriterion() {
         return true;
      }

      public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry event, GameEvent.Emitter emitter) {
         if (!WardenEntity.this.isAiDisabled() && !WardenEntity.this.isDead() && !WardenEntity.this.getBrain().hasMemoryModule(MemoryModuleType.VIBRATION_COOLDOWN) && !WardenEntity.this.isDiggingOrEmerging() && world.getWorldBorder().contains(pos)) {
            Entity var6 = emitter.sourceEntity();
            boolean var10000;
            if (var6 instanceof LivingEntity) {
               LivingEntity livingEntity = (LivingEntity)var6;
               if (!WardenEntity.this.isValidTarget(livingEntity)) {
                  var10000 = false;
                  return var10000;
               }
            }

            var10000 = true;
            return var10000;
         } else {
            return false;
         }
      }

      public void accept(ServerWorld world, BlockPos pos, RegistryEntry event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
         if (!WardenEntity.this.isDead()) {
            WardenEntity.this.brain.remember(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
            world.sendEntityStatus(WardenEntity.this, (byte)61);
            WardenEntity.this.playSound(SoundEvents.ENTITY_WARDEN_TENDRIL_CLICKS, 5.0F, WardenEntity.this.getSoundPitch());
            BlockPos blockPos = pos;
            if (entity != null) {
               if (WardenEntity.this.isInRange(entity, 30.0)) {
                  if (WardenEntity.this.getBrain().hasMemoryModule(MemoryModuleType.RECENT_PROJECTILE)) {
                     if (WardenEntity.this.isValidTarget(entity)) {
                        blockPos = entity.getBlockPos();
                     }

                     WardenEntity.this.increaseAngerAt(entity);
                  } else {
                     WardenEntity.this.increaseAngerAt(entity, 10, true);
                  }
               }

               WardenEntity.this.getBrain().remember(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
            } else {
               WardenEntity.this.increaseAngerAt(sourceEntity);
            }

            if (!WardenEntity.this.getAngriness().isAngry()) {
               Optional optional = WardenEntity.this.angerManager.getPrimeSuspect();
               if (entity != null || optional.isEmpty() || optional.get() == sourceEntity) {
                  WardenBrain.lookAtDisturbance(WardenEntity.this, blockPos);
               }
            }

         }
      }
   }
}
