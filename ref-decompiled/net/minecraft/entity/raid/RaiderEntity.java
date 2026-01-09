package net.minecraft.entity.raid;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToRaidCenterGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.raid.Raid;
import net.minecraft.village.raid.RaidManager;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.Nullable;

public abstract class RaiderEntity extends PatrolEntity {
   protected static final TrackedData CELEBRATING;
   static final Predicate OBTAINABLE_OMINOUS_BANNER_PREDICATE;
   private static final int DEFAULT_WAVE = 0;
   private static final boolean DEFAULT_ABLE_TO_JOIN_RAID = false;
   @Nullable
   protected Raid raid;
   private int wave = 0;
   private boolean ableToJoinRaid = false;
   private int outOfRaidCounter;

   protected RaiderEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected void initGoals() {
      super.initGoals();
      this.goalSelector.add(1, new PickUpBannerAsLeaderGoal(this));
      this.goalSelector.add(3, new MoveToRaidCenterGoal(this));
      this.goalSelector.add(4, new AttackHomeGoal(this, 1.0499999523162842, 1));
      this.goalSelector.add(5, new CelebrateGoal(this));
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(CELEBRATING, false);
   }

   public abstract void addBonusForWave(ServerWorld world, int wave, boolean unused);

   public boolean canJoinRaid() {
      return this.ableToJoinRaid;
   }

   public void setAbleToJoinRaid(boolean ableToJoinRaid) {
      this.ableToJoinRaid = ableToJoinRaid;
   }

   public void tickMovement() {
      World var2 = this.getWorld();
      if (var2 instanceof ServerWorld serverWorld) {
         if (this.isAlive()) {
            Raid raid = this.getRaid();
            if (this.canJoinRaid()) {
               if (raid == null) {
                  if (this.getWorld().getTime() % 20L == 0L) {
                     Raid raid2 = serverWorld.getRaidAt(this.getBlockPos());
                     if (raid2 != null && RaidManager.isValidRaiderFor(this)) {
                        raid2.addRaider(serverWorld, raid2.getGroupsSpawned(), this, (BlockPos)null, true);
                     }
                  }
               } else {
                  LivingEntity livingEntity = this.getTarget();
                  if (livingEntity != null && (livingEntity.getType() == EntityType.PLAYER || livingEntity.getType() == EntityType.IRON_GOLEM)) {
                     this.despawnCounter = 0;
                  }
               }
            }
         }
      }

      super.tickMovement();
   }

   protected void updateDespawnCounter() {
      this.despawnCounter += 2;
   }

   public void onDeath(DamageSource damageSource) {
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         Entity entity = damageSource.getAttacker();
         Raid raid = this.getRaid();
         if (raid != null) {
            if (this.isPatrolLeader()) {
               raid.removeLeader(this.getWave());
            }

            if (entity != null && entity.getType() == EntityType.PLAYER) {
               raid.addHero(entity);
            }

            raid.removeFromWave(serverWorld, this, false);
         }
      }

      super.onDeath(damageSource);
   }

   public boolean hasNoRaid() {
      return !this.hasActiveRaid();
   }

   public void setRaid(@Nullable Raid raid) {
      this.raid = raid;
   }

   @Nullable
   public Raid getRaid() {
      return this.raid;
   }

   public boolean isCaptain() {
      ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
      boolean bl = !itemStack.isEmpty() && ItemStack.areEqual(itemStack, Raid.createOminousBanner(this.getRegistryManager().getOrThrow(RegistryKeys.BANNER_PATTERN)));
      boolean bl2 = this.isPatrolLeader();
      return bl && bl2;
   }

   public boolean hasRaid() {
      World var2 = this.getWorld();
      if (!(var2 instanceof ServerWorld serverWorld)) {
         return false;
      } else {
         return this.getRaid() != null || serverWorld.getRaidAt(this.getBlockPos()) != null;
      }
   }

   public boolean hasActiveRaid() {
      return this.getRaid() != null && this.getRaid().isActive();
   }

   public void setWave(int wave) {
      this.wave = wave;
   }

   public int getWave() {
      return this.wave;
   }

   public boolean isCelebrating() {
      return (Boolean)this.dataTracker.get(CELEBRATING);
   }

   public void setCelebrating(boolean celebrating) {
      this.dataTracker.set(CELEBRATING, celebrating);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putInt("Wave", this.wave);
      view.putBoolean("CanJoinRaid", this.ableToJoinRaid);
      if (this.raid != null) {
         World var3 = this.getWorld();
         if (var3 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var3;
            serverWorld.getRaidManager().getRaidId(this.raid).ifPresent((raidId) -> {
               view.putInt("RaidId", raidId);
            });
         }
      }

   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.wave = view.getInt("Wave", 0);
      this.ableToJoinRaid = view.getBoolean("CanJoinRaid", false);
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         view.getOptionalInt("RaidId").ifPresent((raidId) -> {
            this.raid = serverWorld.getRaidManager().getRaid(raidId);
            if (this.raid != null) {
               this.raid.addToWave(serverWorld, this.wave, this, false);
               if (this.isPatrolLeader()) {
                  this.raid.setWaveCaptain(this.wave, this);
               }
            }

         });
      }

   }

   protected void loot(ServerWorld world, ItemEntity itemEntity) {
      ItemStack itemStack = itemEntity.getStack();
      boolean bl = this.hasActiveRaid() && this.getRaid().getCaptain(this.getWave()) != null;
      if (this.hasActiveRaid() && !bl && ItemStack.areEqual(itemStack, Raid.createOminousBanner(this.getRegistryManager().getOrThrow(RegistryKeys.BANNER_PATTERN)))) {
         EquipmentSlot equipmentSlot = EquipmentSlot.HEAD;
         ItemStack itemStack2 = this.getEquippedStack(equipmentSlot);
         double d = (double)this.getEquipmentDropChances().get(equipmentSlot);
         if (!itemStack2.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d) {
            this.dropStack(world, itemStack2);
         }

         this.triggerItemPickedUpByEntityCriteria(itemEntity);
         this.equipStack(equipmentSlot, itemStack);
         this.sendPickup(itemEntity, itemStack.getCount());
         itemEntity.discard();
         this.getRaid().setWaveCaptain(this.getWave(), this);
         this.setPatrolLeader(true);
      } else {
         super.loot(world, itemEntity);
      }

   }

   public boolean canImmediatelyDespawn(double distanceSquared) {
      return this.getRaid() == null ? super.canImmediatelyDespawn(distanceSquared) : false;
   }

   public boolean cannotDespawn() {
      return super.cannotDespawn() || this.getRaid() != null;
   }

   public int getOutOfRaidCounter() {
      return this.outOfRaidCounter;
   }

   public void setOutOfRaidCounter(int outOfRaidCounter) {
      this.outOfRaidCounter = outOfRaidCounter;
   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      if (this.hasActiveRaid()) {
         this.getRaid().updateBar();
      }

      return super.damage(world, source, amount);
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      this.setAbleToJoinRaid(this.getType() != EntityType.WITCH || spawnReason != SpawnReason.NATURAL);
      return super.initialize(world, difficulty, spawnReason, entityData);
   }

   public abstract SoundEvent getCelebratingSound();

   static {
      CELEBRATING = DataTracker.registerData(RaiderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      OBTAINABLE_OMINOUS_BANNER_PREDICATE = (itemEntity) -> {
         return !itemEntity.cannotPickup() && itemEntity.isAlive() && ItemStack.areEqual(itemEntity.getStack(), Raid.createOminousBanner(itemEntity.getRegistryManager().getOrThrow(RegistryKeys.BANNER_PATTERN)));
      };
   }

   public class PickUpBannerAsLeaderGoal extends Goal {
      private final RaiderEntity actor;
      private Int2LongOpenHashMap bannerItemCache = new Int2LongOpenHashMap();
      @Nullable
      private Path path;
      @Nullable
      private ItemEntity bannerItemEntity;

      public PickUpBannerAsLeaderGoal(final RaiderEntity actor) {
         this.actor = actor;
         this.setControls(EnumSet.of(Goal.Control.MOVE));
      }

      public boolean canStart() {
         if (this.shouldStop()) {
            return false;
         } else {
            Int2LongOpenHashMap int2LongOpenHashMap = new Int2LongOpenHashMap();
            double d = RaiderEntity.this.getAttributeValue(EntityAttributes.FOLLOW_RANGE);
            List list = this.actor.getWorld().getEntitiesByClass(ItemEntity.class, this.actor.getBoundingBox().expand(d, 8.0, d), RaiderEntity.OBTAINABLE_OMINOUS_BANNER_PREDICATE);
            Iterator var5 = list.iterator();

            while(var5.hasNext()) {
               ItemEntity itemEntity = (ItemEntity)var5.next();
               long l = this.bannerItemCache.getOrDefault(itemEntity.getId(), Long.MIN_VALUE);
               if (RaiderEntity.this.getWorld().getTime() < l) {
                  int2LongOpenHashMap.put(itemEntity.getId(), l);
               } else {
                  Path path = this.actor.getNavigation().findPathTo((Entity)itemEntity, 1);
                  if (path != null && path.reachesTarget()) {
                     this.path = path;
                     this.bannerItemEntity = itemEntity;
                     return true;
                  }

                  int2LongOpenHashMap.put(itemEntity.getId(), RaiderEntity.this.getWorld().getTime() + 600L);
               }
            }

            this.bannerItemCache = int2LongOpenHashMap;
            return false;
         }
      }

      public boolean shouldContinue() {
         if (this.bannerItemEntity != null && this.path != null) {
            if (this.bannerItemEntity.isRemoved()) {
               return false;
            } else if (this.path.isFinished()) {
               return false;
            } else {
               return !this.shouldStop();
            }
         } else {
            return false;
         }
      }

      private boolean shouldStop() {
         if (!this.actor.hasActiveRaid()) {
            return true;
         } else if (this.actor.getRaid().isFinished()) {
            return true;
         } else if (!this.actor.canLead()) {
            return true;
         } else if (ItemStack.areEqual(this.actor.getEquippedStack(EquipmentSlot.HEAD), Raid.createOminousBanner(this.actor.getRegistryManager().getOrThrow(RegistryKeys.BANNER_PATTERN)))) {
            return true;
         } else {
            RaiderEntity raiderEntity = RaiderEntity.this.raid.getCaptain(this.actor.getWave());
            return raiderEntity != null && raiderEntity.isAlive();
         }
      }

      public void start() {
         this.actor.getNavigation().startMovingAlong(this.path, 1.149999976158142);
      }

      public void stop() {
         this.path = null;
         this.bannerItemEntity = null;
      }

      public void tick() {
         if (this.bannerItemEntity != null && this.bannerItemEntity.isInRange(this.actor, 1.414)) {
            this.actor.loot(castToServerWorld(RaiderEntity.this.getWorld()), this.bannerItemEntity);
         }

      }
   }

   private static class AttackHomeGoal extends Goal {
      private final RaiderEntity raider;
      private final double speed;
      private BlockPos home;
      private final List lastHomes = Lists.newArrayList();
      private final int distance;
      private boolean finished;

      public AttackHomeGoal(RaiderEntity raider, double speed, int distance) {
         this.raider = raider;
         this.speed = speed;
         this.distance = distance;
         this.setControls(EnumSet.of(Goal.Control.MOVE));
      }

      public boolean canStart() {
         this.purgeMemory();
         return this.isRaiding() && this.tryFindHome() && this.raider.getTarget() == null;
      }

      private boolean isRaiding() {
         return this.raider.hasActiveRaid() && !this.raider.getRaid().isFinished();
      }

      private boolean tryFindHome() {
         ServerWorld serverWorld = (ServerWorld)this.raider.getWorld();
         BlockPos blockPos = this.raider.getBlockPos();
         Optional optional = serverWorld.getPointOfInterestStorage().getPosition((poi) -> {
            return poi.matchesKey(PointOfInterestTypes.HOME);
         }, this::canLootHome, PointOfInterestStorage.OccupationStatus.ANY, blockPos, 48, this.raider.random);
         if (optional.isEmpty()) {
            return false;
         } else {
            this.home = ((BlockPos)optional.get()).toImmutable();
            return true;
         }
      }

      public boolean shouldContinue() {
         if (this.raider.getNavigation().isIdle()) {
            return false;
         } else {
            return this.raider.getTarget() == null && !this.home.isWithinDistance(this.raider.getPos(), (double)(this.raider.getWidth() + (float)this.distance)) && !this.finished;
         }
      }

      public void stop() {
         if (this.home.isWithinDistance(this.raider.getPos(), (double)this.distance)) {
            this.lastHomes.add(this.home);
         }

      }

      public void start() {
         super.start();
         this.raider.setDespawnCounter(0);
         this.raider.getNavigation().startMovingTo((double)this.home.getX(), (double)this.home.getY(), (double)this.home.getZ(), this.speed);
         this.finished = false;
      }

      public void tick() {
         if (this.raider.getNavigation().isIdle()) {
            Vec3d vec3d = Vec3d.ofBottomCenter(this.home);
            Vec3d vec3d2 = NoPenaltyTargeting.findTo(this.raider, 16, 7, vec3d, 0.3141592741012573);
            if (vec3d2 == null) {
               vec3d2 = NoPenaltyTargeting.findTo(this.raider, 8, 7, vec3d, 1.5707963705062866);
            }

            if (vec3d2 == null) {
               this.finished = true;
               return;
            }

            this.raider.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
         }

      }

      private boolean canLootHome(BlockPos pos) {
         Iterator var2 = this.lastHomes.iterator();

         BlockPos blockPos;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            blockPos = (BlockPos)var2.next();
         } while(!Objects.equals(pos, blockPos));

         return false;
      }

      private void purgeMemory() {
         if (this.lastHomes.size() > 2) {
            this.lastHomes.remove(0);
         }

      }
   }

   public class CelebrateGoal extends Goal {
      private final RaiderEntity raider;

      CelebrateGoal(final RaiderEntity raider) {
         this.raider = raider;
         this.setControls(EnumSet.of(Goal.Control.MOVE));
      }

      public boolean canStart() {
         Raid raid = this.raider.getRaid();
         return this.raider.isAlive() && this.raider.getTarget() == null && raid != null && raid.hasLost();
      }

      public void start() {
         this.raider.setCelebrating(true);
         super.start();
      }

      public void stop() {
         this.raider.setCelebrating(false);
         super.stop();
      }

      public void tick() {
         if (!this.raider.isSilent() && this.raider.random.nextInt(this.getTickCount(100)) == 0) {
            RaiderEntity.this.playSound(RaiderEntity.this.getCelebratingSound());
         }

         if (!this.raider.hasVehicle() && this.raider.random.nextInt(this.getTickCount(50)) == 0) {
            this.raider.getJumpControl().setActive();
         }

         super.tick();
      }
   }

   protected static class PatrolApproachGoal extends Goal {
      private final RaiderEntity raider;
      private final float squaredDistance;
      public final TargetPredicate closeRaiderPredicate = TargetPredicate.createNonAttackable().setBaseMaxDistance(8.0).ignoreVisibility().ignoreDistanceScalingFactor();

      public PatrolApproachGoal(IllagerEntity raider, float distance) {
         this.raider = raider;
         this.squaredDistance = distance * distance;
         this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
      }

      public boolean canStart() {
         LivingEntity livingEntity = this.raider.getAttacker();
         return this.raider.getRaid() == null && this.raider.isRaidCenterSet() && this.raider.getTarget() != null && !this.raider.isAttacking() && (livingEntity == null || livingEntity.getType() != EntityType.PLAYER);
      }

      public void start() {
         super.start();
         this.raider.getNavigation().stop();
         List list = getServerWorld(this.raider).getTargets(RaiderEntity.class, this.closeRaiderPredicate, this.raider, this.raider.getBoundingBox().expand(8.0, 8.0, 8.0));
         Iterator var2 = list.iterator();

         while(var2.hasNext()) {
            RaiderEntity raiderEntity = (RaiderEntity)var2.next();
            raiderEntity.setTarget(this.raider.getTarget());
         }

      }

      public void stop() {
         super.stop();
         LivingEntity livingEntity = this.raider.getTarget();
         if (livingEntity != null) {
            List list = getServerWorld(this.raider).getTargets(RaiderEntity.class, this.closeRaiderPredicate, this.raider, this.raider.getBoundingBox().expand(8.0, 8.0, 8.0));
            Iterator var3 = list.iterator();

            while(var3.hasNext()) {
               RaiderEntity raiderEntity = (RaiderEntity)var3.next();
               raiderEntity.setTarget(livingEntity);
               raiderEntity.setAttacking(true);
            }

            this.raider.setAttacking(true);
         }

      }

      public boolean shouldRunEveryTick() {
         return true;
      }

      public void tick() {
         LivingEntity livingEntity = this.raider.getTarget();
         if (livingEntity != null) {
            if (this.raider.squaredDistanceTo(livingEntity) > (double)this.squaredDistance) {
               this.raider.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
               if (this.raider.random.nextInt(50) == 0) {
                  this.raider.playAmbientSound();
               }
            } else {
               this.raider.setAttacking(true);
            }

            super.tick();
         }
      }
   }
}
