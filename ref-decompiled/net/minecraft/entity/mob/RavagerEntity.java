package net.minecraft.entity.mob;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class RavagerEntity extends RaiderEntity {
   private static final Predicate CAN_KNOCK_BACK_WITH_ROAR = (entity) -> {
      return !(entity instanceof RavagerEntity) && entity.isAlive();
   };
   private static final Predicate CAN_KNOCK_BACK_WITH_ROAR_NO_MOB_GRIEFING = (entity) -> {
      return CAN_KNOCK_BACK_WITH_ROAR.test(entity) && !entity.getType().equals(EntityType.ARMOR_STAND);
   };
   private static final Predicate CAN_KNOCK_BACK_WITH_ROAR_ON_CLIENT = (entity) -> {
      return !(entity instanceof RavagerEntity) && entity.isAlive() && entity.isLogicalSideForUpdatingMovement();
   };
   private static final double field_30480 = 0.3;
   private static final double field_30481 = 0.35;
   private static final int field_30482 = 8356754;
   private static final float STUNNED_PARTICLE_BLUE = 0.57254905F;
   private static final float STUNNED_PARTICLE_GREEN = 0.5137255F;
   private static final float STUNNED_PARTICLE_RED = 0.49803922F;
   public static final int field_30486 = 10;
   public static final int field_30479 = 40;
   private static final int DEFAULT_ATTACK_TICK = 0;
   private static final int DEFAULT_STUN_TICK = 0;
   private static final int DEFAULT_ROAR_TICK = 0;
   private int attackTick = 0;
   private int stunTick = 0;
   private int roarTick = 0;

   public RavagerEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.experiencePoints = 20;
      this.setPathfindingPenalty(PathNodeType.LEAVES, 0.0F);
   }

   protected void initGoals() {
      super.initGoals();
      this.goalSelector.add(0, new SwimGoal(this));
      this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, true));
      this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.4));
      this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
      this.targetSelector.add(2, (new RevengeGoal(this, new Class[]{RaiderEntity.class})).setGroupRevenge());
      this.targetSelector.add(3, new ActiveTargetGoal(this, PlayerEntity.class, true));
      this.targetSelector.add(4, new ActiveTargetGoal(this, MerchantEntity.class, true, (entity, world) -> {
         return !entity.isBaby();
      }));
      this.targetSelector.add(4, new ActiveTargetGoal(this, IronGolemEntity.class, true));
   }

   protected void updateGoalControls() {
      boolean bl = !(this.getControllingPassenger() instanceof MobEntity) || this.getControllingPassenger().getType().isIn(EntityTypeTags.RAIDERS);
      boolean bl2 = !(this.getVehicle() instanceof AbstractBoatEntity);
      this.goalSelector.setControlEnabled(Goal.Control.MOVE, bl);
      this.goalSelector.setControlEnabled(Goal.Control.JUMP, bl && bl2);
      this.goalSelector.setControlEnabled(Goal.Control.LOOK, bl);
      this.goalSelector.setControlEnabled(Goal.Control.TARGET, bl);
   }

   public static DefaultAttributeContainer.Builder createRavagerAttributes() {
      return HostileEntity.createHostileAttributes().add(EntityAttributes.MAX_HEALTH, 100.0).add(EntityAttributes.MOVEMENT_SPEED, 0.3).add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.75).add(EntityAttributes.ATTACK_DAMAGE, 12.0).add(EntityAttributes.ATTACK_KNOCKBACK, 1.5).add(EntityAttributes.FOLLOW_RANGE, 32.0).add(EntityAttributes.STEP_HEIGHT, 1.0);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putInt("AttackTick", this.attackTick);
      view.putInt("StunTick", this.stunTick);
      view.putInt("RoarTick", this.roarTick);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.attackTick = view.getInt("AttackTick", 0);
      this.stunTick = view.getInt("StunTick", 0);
      this.roarTick = view.getInt("RoarTick", 0);
   }

   public SoundEvent getCelebratingSound() {
      return SoundEvents.ENTITY_RAVAGER_CELEBRATE;
   }

   public int getMaxHeadRotation() {
      return 45;
   }

   public void tickMovement() {
      super.tickMovement();
      if (this.isAlive()) {
         if (this.isImmobile()) {
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.0);
         } else {
            double d = this.getTarget() != null ? 0.35 : 0.3;
            double e = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue();
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(MathHelper.lerp(0.1, e, d));
         }

         World var2 = this.getWorld();
         if (var2 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var2;
            if (this.horizontalCollision && serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
               boolean bl = false;
               Box box = this.getBoundingBox().expand(0.2);
               Iterator var4 = BlockPos.iterate(MathHelper.floor(box.minX), MathHelper.floor(box.minY), MathHelper.floor(box.minZ), MathHelper.floor(box.maxX), MathHelper.floor(box.maxY), MathHelper.floor(box.maxZ)).iterator();

               label61:
               while(true) {
                  BlockPos blockPos;
                  Block block;
                  do {
                     if (!var4.hasNext()) {
                        if (!bl && this.isOnGround()) {
                           this.jump();
                        }
                        break label61;
                     }

                     blockPos = (BlockPos)var4.next();
                     BlockState blockState = serverWorld.getBlockState(blockPos);
                     block = blockState.getBlock();
                  } while(!(block instanceof LeavesBlock));

                  bl = serverWorld.breakBlock(blockPos, true, this) || bl;
               }
            }
         }

         if (this.roarTick > 0) {
            --this.roarTick;
            if (this.roarTick == 10) {
               this.roar();
            }
         }

         if (this.attackTick > 0) {
            --this.attackTick;
         }

         if (this.stunTick > 0) {
            --this.stunTick;
            this.spawnStunnedParticles();
            if (this.stunTick == 0) {
               this.playSound(SoundEvents.ENTITY_RAVAGER_ROAR, 1.0F, 1.0F);
               this.roarTick = 20;
            }
         }

      }
   }

   private void spawnStunnedParticles() {
      if (this.random.nextInt(6) == 0) {
         double d = this.getX() - (double)this.getWidth() * Math.sin((double)(this.bodyYaw * 0.017453292F)) + (this.random.nextDouble() * 0.6 - 0.3);
         double e = this.getY() + (double)this.getHeight() - 0.3;
         double f = this.getZ() + (double)this.getWidth() * Math.cos((double)(this.bodyYaw * 0.017453292F)) + (this.random.nextDouble() * 0.6 - 0.3);
         this.getWorld().addParticleClient(TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0.49803922F, 0.5137255F, 0.57254905F), d, e, f, 0.0, 0.0, 0.0);
      }

   }

   protected boolean isImmobile() {
      return super.isImmobile() || this.attackTick > 0 || this.stunTick > 0 || this.roarTick > 0;
   }

   public boolean canSee(Entity entity) {
      return this.stunTick <= 0 && this.roarTick <= 0 ? super.canSee(entity) : false;
   }

   protected void knockback(LivingEntity target) {
      if (this.roarTick == 0) {
         if (this.random.nextDouble() < 0.5) {
            this.stunTick = 40;
            this.playSound(SoundEvents.ENTITY_RAVAGER_STUNNED, 1.0F, 1.0F);
            this.getWorld().sendEntityStatus(this, (byte)39);
            target.pushAwayFrom(this);
         } else {
            this.knockBack(target);
         }

         target.velocityModified = true;
      }

   }

   private void roar() {
      if (this.isAlive()) {
         World var2 = this.getWorld();
         if (var2 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var2;
            Predicate predicate = serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) ? CAN_KNOCK_BACK_WITH_ROAR : CAN_KNOCK_BACK_WITH_ROAR_NO_MOB_GRIEFING;
            List list = this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(4.0), predicate);
            Iterator var4 = list.iterator();

            while(var4.hasNext()) {
               LivingEntity livingEntity = (LivingEntity)var4.next();
               if (!(livingEntity instanceof IllagerEntity)) {
                  livingEntity.damage(serverWorld, this.getDamageSources().mobAttack(this), 6.0F);
               }

               if (!(livingEntity instanceof PlayerEntity)) {
                  this.knockBack(livingEntity);
               }
            }

            this.emitGameEvent(GameEvent.ENTITY_ACTION);
            serverWorld.sendEntityStatus(this, (byte)69);
         }
      }

   }

   private void roarKnockBackOnClient() {
      List list = this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(4.0), CAN_KNOCK_BACK_WITH_ROAR_ON_CLIENT);
      Iterator var2 = list.iterator();

      while(var2.hasNext()) {
         LivingEntity livingEntity = (LivingEntity)var2.next();
         this.knockBack(livingEntity);
      }

   }

   private void knockBack(Entity entity) {
      double d = entity.getX() - this.getX();
      double e = entity.getZ() - this.getZ();
      double f = Math.max(d * d + e * e, 0.001);
      entity.addVelocity(d / f * 4.0, 0.2, e / f * 4.0);
   }

   public void handleStatus(byte status) {
      if (status == 4) {
         this.attackTick = 10;
         this.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0F, 1.0F);
      } else if (status == 39) {
         this.stunTick = 40;
      } else if (status == 69) {
         this.addRoarParticlesOnClient();
         this.roarKnockBackOnClient();
      }

      super.handleStatus(status);
   }

   private void addRoarParticlesOnClient() {
      Vec3d vec3d = this.getBoundingBox().getCenter();

      for(int i = 0; i < 40; ++i) {
         double d = this.random.nextGaussian() * 0.2;
         double e = this.random.nextGaussian() * 0.2;
         double f = this.random.nextGaussian() * 0.2;
         this.getWorld().addParticleClient(ParticleTypes.POOF, vec3d.x, vec3d.y, vec3d.z, d, e, f);
      }

   }

   public int getAttackTick() {
      return this.attackTick;
   }

   public int getStunTick() {
      return this.stunTick;
   }

   public int getRoarTick() {
      return this.roarTick;
   }

   public boolean tryAttack(ServerWorld world, Entity target) {
      this.attackTick = 10;
      world.sendEntityStatus(this, (byte)4);
      this.playSound(SoundEvents.ENTITY_RAVAGER_ATTACK, 1.0F, 1.0F);
      return super.tryAttack(world, target);
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_RAVAGER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_RAVAGER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_RAVAGER_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(SoundEvents.ENTITY_RAVAGER_STEP, 0.15F, 1.0F);
   }

   public boolean canSpawn(WorldView world) {
      return !world.containsFluid(this.getBoundingBox());
   }

   public void addBonusForWave(ServerWorld world, int wave, boolean unused) {
   }

   public boolean canLead() {
      return false;
   }

   protected Box getAttackBox() {
      Box box = super.getAttackBox();
      return box.contract(0.05, 0.0, 0.05);
   }
}
