package net.minecraft.entity.boss;

import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WitherEntity extends HostileEntity implements RangedAttackMob {
   private static final TrackedData TRACKED_ENTITY_ID_1;
   private static final TrackedData TRACKED_ENTITY_ID_2;
   private static final TrackedData TRACKED_ENTITY_ID_3;
   private static final List TRACKED_ENTITY_IDS;
   private static final TrackedData INVUL_TIMER;
   private static final int ON_SUMMONED_INVUL_TIMER = 220;
   private static final int DEFAULT_INVUL_TIMER = 0;
   private final float[] sideHeadPitches = new float[2];
   private final float[] sideHeadYaws = new float[2];
   private final float[] lastSideHeadPitches = new float[2];
   private final float[] lastSideHeadYaws = new float[2];
   private final int[] skullCooldowns = new int[2];
   private final int[] chargedSkullCooldowns = new int[2];
   private int blockBreakingCooldown;
   private final ServerBossBar bossBar;
   private static final TargetPredicate.EntityPredicate CAN_ATTACK_PREDICATE;
   private static final TargetPredicate HEAD_TARGET_PREDICATE;

   public WitherEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.bossBar = (ServerBossBar)(new ServerBossBar(this.getDisplayName(), BossBar.Color.PURPLE, BossBar.Style.PROGRESS)).setDarkenSky(true);
      this.moveControl = new FlightMoveControl(this, 10, false);
      this.setHealth(this.getMaxHealth());
      this.experiencePoints = 50;
   }

   protected EntityNavigation createNavigation(World world) {
      BirdNavigation birdNavigation = new BirdNavigation(this, world);
      birdNavigation.setCanOpenDoors(false);
      birdNavigation.setCanSwim(true);
      return birdNavigation;
   }

   protected void initGoals() {
      this.goalSelector.add(0, new DescendAtHalfHealthGoal());
      this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 40, 20.0F));
      this.goalSelector.add(5, new FlyGoal(this, 1.0));
      this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.add(7, new LookAroundGoal(this));
      this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
      this.targetSelector.add(2, new ActiveTargetGoal(this, LivingEntity.class, 0, false, false, CAN_ATTACK_PREDICATE));
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(TRACKED_ENTITY_ID_1, 0);
      builder.add(TRACKED_ENTITY_ID_2, 0);
      builder.add(TRACKED_ENTITY_ID_3, 0);
      builder.add(INVUL_TIMER, 0);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putInt("Invul", this.getInvulnerableTimer());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setInvulTimer(view.getInt("Invul", 0));
      if (this.hasCustomName()) {
         this.bossBar.setName(this.getDisplayName());
      }

   }

   public void setCustomName(@Nullable Text name) {
      super.setCustomName(name);
      this.bossBar.setName(this.getDisplayName());
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_WITHER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_WITHER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_WITHER_DEATH;
   }

   public void tickMovement() {
      Vec3d vec3d = this.getVelocity().multiply(1.0, 0.6, 1.0);
      if (!this.getWorld().isClient && this.getTrackedEntityId(0) > 0) {
         Entity entity = this.getWorld().getEntityById(this.getTrackedEntityId(0));
         if (entity != null) {
            double d = vec3d.y;
            if (this.getY() < entity.getY() || !this.shouldRenderOverlay() && this.getY() < entity.getY() + 5.0) {
               d = Math.max(0.0, d);
               d += 0.3 - d * 0.6000000238418579;
            }

            vec3d = new Vec3d(vec3d.x, d, vec3d.z);
            Vec3d vec3d2 = new Vec3d(entity.getX() - this.getX(), 0.0, entity.getZ() - this.getZ());
            if (vec3d2.horizontalLengthSquared() > 9.0) {
               Vec3d vec3d3 = vec3d2.normalize();
               vec3d = vec3d.add(vec3d3.x * 0.3 - vec3d.x * 0.6, 0.0, vec3d3.z * 0.3 - vec3d.z * 0.6);
            }
         }
      }

      this.setVelocity(vec3d);
      if (vec3d.horizontalLengthSquared() > 0.05) {
         this.setYaw((float)MathHelper.atan2(vec3d.z, vec3d.x) * 57.295776F - 90.0F);
      }

      super.tickMovement();

      int i;
      for(i = 0; i < 2; ++i) {
         this.lastSideHeadYaws[i] = this.sideHeadYaws[i];
         this.lastSideHeadPitches[i] = this.sideHeadPitches[i];
      }

      int j;
      for(i = 0; i < 2; ++i) {
         j = this.getTrackedEntityId(i + 1);
         Entity entity2 = null;
         if (j > 0) {
            entity2 = this.getWorld().getEntityById(j);
         }

         if (entity2 != null) {
            double e = this.getHeadX(i + 1);
            double f = this.getHeadY(i + 1);
            double g = this.getHeadZ(i + 1);
            double h = entity2.getX() - e;
            double k = entity2.getEyeY() - f;
            double l = entity2.getZ() - g;
            double m = Math.sqrt(h * h + l * l);
            float n = (float)(MathHelper.atan2(l, h) * 57.2957763671875) - 90.0F;
            float o = (float)(-(MathHelper.atan2(k, m) * 57.2957763671875));
            this.sideHeadPitches[i] = this.getNextAngle(this.sideHeadPitches[i], o, 40.0F);
            this.sideHeadYaws[i] = this.getNextAngle(this.sideHeadYaws[i], n, 10.0F);
         } else {
            this.sideHeadYaws[i] = this.getNextAngle(this.sideHeadYaws[i], this.bodyYaw, 10.0F);
         }
      }

      boolean bl = this.shouldRenderOverlay();

      for(j = 0; j < 3; ++j) {
         double p = this.getHeadX(j);
         double q = this.getHeadY(j);
         double r = this.getHeadZ(j);
         float s = 0.3F * this.getScale();
         this.getWorld().addParticleClient(ParticleTypes.SMOKE, p + this.random.nextGaussian() * (double)s, q + this.random.nextGaussian() * (double)s, r + this.random.nextGaussian() * (double)s, 0.0, 0.0, 0.0);
         if (bl && this.getWorld().random.nextInt(4) == 0) {
            this.getWorld().addParticleClient(TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0.7F, 0.7F, 0.5F), p + this.random.nextGaussian() * (double)s, q + this.random.nextGaussian() * (double)s, r + this.random.nextGaussian() * (double)s, 0.0, 0.0, 0.0);
         }
      }

      if (this.getInvulnerableTimer() > 0) {
         float t = 3.3F * this.getScale();

         for(int u = 0; u < 3; ++u) {
            this.getWorld().addParticleClient(TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0.7F, 0.7F, 0.9F), this.getX() + this.random.nextGaussian(), this.getY() + (double)(this.random.nextFloat() * t), this.getZ() + this.random.nextGaussian(), 0.0, 0.0, 0.0);
         }
      }

   }

   protected void mobTick(ServerWorld world) {
      int i;
      if (this.getInvulnerableTimer() > 0) {
         i = this.getInvulnerableTimer() - 1;
         this.bossBar.setPercent(1.0F - (float)i / 220.0F);
         if (i <= 0) {
            world.createExplosion(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, World.ExplosionSourceType.MOB);
            if (!this.isSilent()) {
               world.syncGlobalEvent(1023, this.getBlockPos(), 0);
            }
         }

         this.setInvulTimer(i);
         if (this.age % 10 == 0) {
            this.heal(10.0F);
         }

      } else {
         super.mobTick(world);

         int j;
         for(i = 1; i < 3; ++i) {
            if (this.age >= this.skullCooldowns[i - 1]) {
               this.skullCooldowns[i - 1] = this.age + 10 + this.random.nextInt(10);
               if (world.getDifficulty() == Difficulty.NORMAL || world.getDifficulty() == Difficulty.HARD) {
                  int[] var10000 = this.chargedSkullCooldowns;
                  int var10001 = i - 1;
                  int var10003 = var10000[i - 1];
                  var10000[var10001] = var10000[i - 1] + 1;
                  if (var10003 > 15) {
                     float f = 10.0F;
                     float g = 5.0F;
                     double d = MathHelper.nextDouble(this.random, this.getX() - 10.0, this.getX() + 10.0);
                     double e = MathHelper.nextDouble(this.random, this.getY() - 5.0, this.getY() + 5.0);
                     double h = MathHelper.nextDouble(this.random, this.getZ() - 10.0, this.getZ() + 10.0);
                     this.shootSkullAt(i + 1, d, e, h, true);
                     this.chargedSkullCooldowns[i - 1] = 0;
                  }
               }

               j = this.getTrackedEntityId(i);
               if (j > 0) {
                  LivingEntity livingEntity = (LivingEntity)world.getEntityById(j);
                  if (livingEntity != null && this.canTarget(livingEntity) && !(this.squaredDistanceTo(livingEntity) > 900.0) && this.canSee(livingEntity)) {
                     this.shootSkullAt(i + 1, livingEntity);
                     this.skullCooldowns[i - 1] = this.age + 40 + this.random.nextInt(20);
                     this.chargedSkullCooldowns[i - 1] = 0;
                  } else {
                     this.setTrackedEntityId(i, 0);
                  }
               } else {
                  List list = world.getTargets(LivingEntity.class, HEAD_TARGET_PREDICATE, this, this.getBoundingBox().expand(20.0, 8.0, 20.0));
                  if (!list.isEmpty()) {
                     LivingEntity livingEntity2 = (LivingEntity)list.get(this.random.nextInt(list.size()));
                     this.setTrackedEntityId(i, livingEntity2.getId());
                  }
               }
            }
         }

         if (this.getTarget() != null) {
            this.setTrackedEntityId(0, this.getTarget().getId());
         } else {
            this.setTrackedEntityId(0, 0);
         }

         if (this.blockBreakingCooldown > 0) {
            --this.blockBreakingCooldown;
            if (this.blockBreakingCooldown == 0 && world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
               boolean bl = false;
               j = MathHelper.floor(this.getWidth() / 2.0F + 1.0F);
               int k = MathHelper.floor(this.getHeight());
               Iterator var17 = BlockPos.iterate(this.getBlockX() - j, this.getBlockY(), this.getBlockZ() - j, this.getBlockX() + j, this.getBlockY() + k, this.getBlockZ() + j).iterator();

               label74:
               while(true) {
                  BlockPos blockPos;
                  BlockState blockState;
                  do {
                     if (!var17.hasNext()) {
                        if (bl) {
                           world.syncWorldEvent((Entity)null, 1022, this.getBlockPos(), 0);
                        }
                        break label74;
                     }

                     blockPos = (BlockPos)var17.next();
                     blockState = world.getBlockState(blockPos);
                  } while(!canDestroy(blockState));

                  bl = world.breakBlock(blockPos, true, this) || bl;
               }
            }
         }

         if (this.age % 20 == 0) {
            this.heal(1.0F);
         }

         this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
      }
   }

   public static boolean canDestroy(BlockState block) {
      return !block.isAir() && !block.isIn(BlockTags.WITHER_IMMUNE);
   }

   public void onSummoned() {
      this.setInvulTimer(220);
      this.bossBar.setPercent(0.0F);
      this.setHealth(this.getMaxHealth() / 3.0F);
   }

   public void slowMovement(BlockState state, Vec3d multiplier) {
   }

   public void onStartedTrackingBy(ServerPlayerEntity player) {
      super.onStartedTrackingBy(player);
      this.bossBar.addPlayer(player);
   }

   public void onStoppedTrackingBy(ServerPlayerEntity player) {
      super.onStoppedTrackingBy(player);
      this.bossBar.removePlayer(player);
   }

   private double getHeadX(int headIndex) {
      if (headIndex <= 0) {
         return this.getX();
      } else {
         float f = (this.bodyYaw + (float)(180 * (headIndex - 1))) * 0.017453292F;
         float g = MathHelper.cos(f);
         return this.getX() + (double)g * 1.3 * (double)this.getScale();
      }
   }

   private double getHeadY(int headIndex) {
      float f = headIndex <= 0 ? 3.0F : 2.2F;
      return this.getY() + (double)(f * this.getScale());
   }

   private double getHeadZ(int headIndex) {
      if (headIndex <= 0) {
         return this.getZ();
      } else {
         float f = (this.bodyYaw + (float)(180 * (headIndex - 1))) * 0.017453292F;
         float g = MathHelper.sin(f);
         return this.getZ() + (double)g * 1.3 * (double)this.getScale();
      }
   }

   private float getNextAngle(float lastAngle, float desiredAngle, float maxDifference) {
      float f = MathHelper.wrapDegrees(desiredAngle - lastAngle);
      if (f > maxDifference) {
         f = maxDifference;
      }

      if (f < -maxDifference) {
         f = -maxDifference;
      }

      return lastAngle + f;
   }

   private void shootSkullAt(int headIndex, LivingEntity target) {
      this.shootSkullAt(headIndex, target.getX(), target.getY() + (double)target.getStandingEyeHeight() * 0.5, target.getZ(), headIndex == 0 && this.random.nextFloat() < 0.001F);
   }

   private void shootSkullAt(int headIndex, double targetX, double targetY, double targetZ, boolean charged) {
      if (!this.isSilent()) {
         this.getWorld().syncWorldEvent((Entity)null, 1024, this.getBlockPos(), 0);
      }

      double d = this.getHeadX(headIndex);
      double e = this.getHeadY(headIndex);
      double f = this.getHeadZ(headIndex);
      double g = targetX - d;
      double h = targetY - e;
      double i = targetZ - f;
      Vec3d vec3d = new Vec3d(g, h, i);
      WitherSkullEntity witherSkullEntity = new WitherSkullEntity(this.getWorld(), this, vec3d.normalize());
      witherSkullEntity.setOwner(this);
      if (charged) {
         witherSkullEntity.setCharged(true);
      }

      witherSkullEntity.setPosition(d, e, f);
      this.getWorld().spawnEntity(witherSkullEntity);
   }

   public void shootAt(LivingEntity target, float pullProgress) {
      this.shootSkullAt(0, target);
   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      if (this.isInvulnerableTo(world, source)) {
         return false;
      } else if (!source.isIn(DamageTypeTags.WITHER_IMMUNE_TO) && !(source.getAttacker() instanceof WitherEntity)) {
         if (this.getInvulnerableTimer() > 0 && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
         } else {
            Entity entity;
            if (this.shouldRenderOverlay()) {
               entity = source.getSource();
               if (entity instanceof PersistentProjectileEntity || entity instanceof WindChargeEntity) {
                  return false;
               }
            }

            entity = source.getAttacker();
            if (entity != null && entity.getType().isIn(EntityTypeTags.WITHER_FRIENDS)) {
               return false;
            } else {
               if (this.blockBreakingCooldown <= 0) {
                  this.blockBreakingCooldown = 20;
               }

               for(int i = 0; i < this.chargedSkullCooldowns.length; ++i) {
                  int[] var10000 = this.chargedSkullCooldowns;
                  var10000[i] += 3;
               }

               return super.damage(world, source, amount);
            }
         }
      } else {
         return false;
      }
   }

   protected void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {
      super.dropEquipment(world, source, causedByPlayer);
      ItemEntity itemEntity = this.dropItem(world, Items.NETHER_STAR);
      if (itemEntity != null) {
         itemEntity.setCovetedItem();
      }

   }

   public void checkDespawn() {
      if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL && this.isDisallowedInPeaceful()) {
         this.discard();
      } else {
         this.despawnCounter = 0;
      }
   }

   public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
      return false;
   }

   public static DefaultAttributeContainer.Builder createWitherAttributes() {
      return HostileEntity.createHostileAttributes().add(EntityAttributes.MAX_HEALTH, 300.0).add(EntityAttributes.MOVEMENT_SPEED, 0.6000000238418579).add(EntityAttributes.FLYING_SPEED, 0.6000000238418579).add(EntityAttributes.FOLLOW_RANGE, 40.0).add(EntityAttributes.ARMOR, 4.0);
   }

   public float[] getSideHeadYaws() {
      return this.sideHeadYaws;
   }

   public float[] getSideHeadPitches() {
      return this.sideHeadPitches;
   }

   public int getInvulnerableTimer() {
      return (Integer)this.dataTracker.get(INVUL_TIMER);
   }

   public void setInvulTimer(int ticks) {
      this.dataTracker.set(INVUL_TIMER, ticks);
   }

   public int getTrackedEntityId(int headIndex) {
      return (Integer)this.dataTracker.get((TrackedData)TRACKED_ENTITY_IDS.get(headIndex));
   }

   public void setTrackedEntityId(int headIndex, int id) {
      this.dataTracker.set((TrackedData)TRACKED_ENTITY_IDS.get(headIndex), id);
   }

   public boolean shouldRenderOverlay() {
      return this.getHealth() <= this.getMaxHealth() / 2.0F;
   }

   protected boolean canStartRiding(Entity entity) {
      return false;
   }

   public boolean canUsePortals(boolean allowVehicles) {
      return false;
   }

   public boolean canHaveStatusEffect(StatusEffectInstance effect) {
      return effect.equals(StatusEffects.WITHER) ? false : super.canHaveStatusEffect(effect);
   }

   static {
      TRACKED_ENTITY_ID_1 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
      TRACKED_ENTITY_ID_2 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
      TRACKED_ENTITY_ID_3 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
      TRACKED_ENTITY_IDS = ImmutableList.of(TRACKED_ENTITY_ID_1, TRACKED_ENTITY_ID_2, TRACKED_ENTITY_ID_3);
      INVUL_TIMER = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
      CAN_ATTACK_PREDICATE = (entity, world) -> {
         return !entity.getType().isIn(EntityTypeTags.WITHER_FRIENDS) && entity.isMobOrPlayer();
      };
      HEAD_TARGET_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(20.0).setPredicate(CAN_ATTACK_PREDICATE);
   }

   class DescendAtHalfHealthGoal extends Goal {
      public DescendAtHalfHealthGoal() {
         this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.JUMP, Goal.Control.LOOK));
      }

      public boolean canStart() {
         return WitherEntity.this.getInvulnerableTimer() > 0;
      }
   }
}
