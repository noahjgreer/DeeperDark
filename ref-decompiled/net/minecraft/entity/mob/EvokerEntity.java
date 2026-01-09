package net.minecraft.entity.mob;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class EvokerEntity extends SpellcastingIllagerEntity {
   @Nullable
   private SheepEntity wololoTarget;

   public EvokerEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.experiencePoints = 10;
   }

   protected void initGoals() {
      super.initGoals();
      this.goalSelector.add(0, new SwimGoal(this));
      this.goalSelector.add(1, new LookAtTargetOrWololoTarget());
      this.goalSelector.add(2, new FleeEntityGoal(this, PlayerEntity.class, 8.0F, 0.6, 1.0));
      this.goalSelector.add(3, new FleeEntityGoal(this, CreakingEntity.class, 8.0F, 0.6, 1.0));
      this.goalSelector.add(4, new SummonVexGoal());
      this.goalSelector.add(5, new ConjureFangsGoal());
      this.goalSelector.add(6, new WololoGoal());
      this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
      this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
      this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
      this.targetSelector.add(1, (new RevengeGoal(this, new Class[]{RaiderEntity.class})).setGroupRevenge());
      this.targetSelector.add(2, (new ActiveTargetGoal(this, PlayerEntity.class, true)).setMaxTimeWithoutVisibility(300));
      this.targetSelector.add(3, (new ActiveTargetGoal(this, MerchantEntity.class, false)).setMaxTimeWithoutVisibility(300));
      this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, false));
   }

   public static DefaultAttributeContainer.Builder createEvokerAttributes() {
      return HostileEntity.createHostileAttributes().add(EntityAttributes.MOVEMENT_SPEED, 0.5).add(EntityAttributes.FOLLOW_RANGE, 12.0).add(EntityAttributes.MAX_HEALTH, 24.0);
   }

   public SoundEvent getCelebratingSound() {
      return SoundEvents.ENTITY_EVOKER_CELEBRATE;
   }

   protected boolean isInSameTeam(Entity other) {
      if (other == this) {
         return true;
      } else if (super.isInSameTeam(other)) {
         return true;
      } else {
         if (other instanceof VexEntity) {
            VexEntity vexEntity = (VexEntity)other;
            if (vexEntity.getOwner() != null) {
               return this.isInSameTeam(vexEntity.getOwner());
            }
         }

         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_EVOKER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_EVOKER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_EVOKER_HURT;
   }

   void setWololoTarget(@Nullable SheepEntity wololoTarget) {
      this.wololoTarget = wololoTarget;
   }

   @Nullable
   SheepEntity getWololoTarget() {
      return this.wololoTarget;
   }

   protected SoundEvent getCastSpellSound() {
      return SoundEvents.ENTITY_EVOKER_CAST_SPELL;
   }

   public void addBonusForWave(ServerWorld world, int wave, boolean unused) {
   }

   private class LookAtTargetOrWololoTarget extends SpellcastingIllagerEntity.LookAtTargetGoal {
      LookAtTargetOrWololoTarget() {
         super();
      }

      public void tick() {
         if (EvokerEntity.this.getTarget() != null) {
            EvokerEntity.this.getLookControl().lookAt(EvokerEntity.this.getTarget(), (float)EvokerEntity.this.getMaxHeadRotation(), (float)EvokerEntity.this.getMaxLookPitchChange());
         } else if (EvokerEntity.this.getWololoTarget() != null) {
            EvokerEntity.this.getLookControl().lookAt(EvokerEntity.this.getWololoTarget(), (float)EvokerEntity.this.getMaxHeadRotation(), (float)EvokerEntity.this.getMaxLookPitchChange());
         }

      }
   }

   private class SummonVexGoal extends SpellcastingIllagerEntity.CastSpellGoal {
      private final TargetPredicate closeVexPredicate = TargetPredicate.createNonAttackable().setBaseMaxDistance(16.0).ignoreVisibility().ignoreDistanceScalingFactor();

      SummonVexGoal() {
         super();
      }

      public boolean canStart() {
         if (!super.canStart()) {
            return false;
         } else {
            int i = castToServerWorld(EvokerEntity.this.getWorld()).getTargets(VexEntity.class, this.closeVexPredicate, EvokerEntity.this, EvokerEntity.this.getBoundingBox().expand(16.0)).size();
            return EvokerEntity.this.random.nextInt(8) + 1 > i;
         }
      }

      protected int getSpellTicks() {
         return 100;
      }

      protected int startTimeDelay() {
         return 340;
      }

      protected void castSpell() {
         ServerWorld serverWorld = (ServerWorld)EvokerEntity.this.getWorld();
         Team team = EvokerEntity.this.getScoreboardTeam();

         for(int i = 0; i < 3; ++i) {
            BlockPos blockPos = EvokerEntity.this.getBlockPos().add(-2 + EvokerEntity.this.random.nextInt(5), 1, -2 + EvokerEntity.this.random.nextInt(5));
            VexEntity vexEntity = (VexEntity)EntityType.VEX.create(EvokerEntity.this.getWorld(), SpawnReason.MOB_SUMMONED);
            if (vexEntity != null) {
               vexEntity.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);
               vexEntity.initialize(serverWorld, EvokerEntity.this.getWorld().getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, (EntityData)null);
               vexEntity.setOwner(EvokerEntity.this);
               vexEntity.setBounds(blockPos);
               vexEntity.setLifeTicks(20 * (30 + EvokerEntity.this.random.nextInt(90)));
               if (team != null) {
                  serverWorld.getScoreboard().addScoreHolderToTeam(vexEntity.getNameForScoreboard(), team);
               }

               serverWorld.spawnEntityAndPassengers(vexEntity);
               serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, blockPos, GameEvent.Emitter.of((Entity)EvokerEntity.this));
            }
         }

      }

      protected SoundEvent getSoundPrepare() {
         return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
      }

      protected SpellcastingIllagerEntity.Spell getSpell() {
         return SpellcastingIllagerEntity.Spell.SUMMON_VEX;
      }
   }

   private class ConjureFangsGoal extends SpellcastingIllagerEntity.CastSpellGoal {
      ConjureFangsGoal() {
         super();
      }

      protected int getSpellTicks() {
         return 40;
      }

      protected int startTimeDelay() {
         return 100;
      }

      protected void castSpell() {
         LivingEntity livingEntity = EvokerEntity.this.getTarget();
         double d = Math.min(livingEntity.getY(), EvokerEntity.this.getY());
         double e = Math.max(livingEntity.getY(), EvokerEntity.this.getY()) + 1.0;
         float f = (float)MathHelper.atan2(livingEntity.getZ() - EvokerEntity.this.getZ(), livingEntity.getX() - EvokerEntity.this.getX());
         int i;
         if (EvokerEntity.this.squaredDistanceTo(livingEntity) < 9.0) {
            float g;
            for(i = 0; i < 5; ++i) {
               g = f + (float)i * 3.1415927F * 0.4F;
               this.conjureFangs(EvokerEntity.this.getX() + (double)MathHelper.cos(g) * 1.5, EvokerEntity.this.getZ() + (double)MathHelper.sin(g) * 1.5, d, e, g, 0);
            }

            for(i = 0; i < 8; ++i) {
               g = f + (float)i * 3.1415927F * 2.0F / 8.0F + 1.2566371F;
               this.conjureFangs(EvokerEntity.this.getX() + (double)MathHelper.cos(g) * 2.5, EvokerEntity.this.getZ() + (double)MathHelper.sin(g) * 2.5, d, e, g, 3);
            }
         } else {
            for(i = 0; i < 16; ++i) {
               double h = 1.25 * (double)(i + 1);
               int j = 1 * i;
               this.conjureFangs(EvokerEntity.this.getX() + (double)MathHelper.cos(f) * h, EvokerEntity.this.getZ() + (double)MathHelper.sin(f) * h, d, e, f, j);
            }
         }

      }

      private void conjureFangs(double x, double z, double maxY, double y, float yaw, int warmup) {
         BlockPos blockPos = BlockPos.ofFloored(x, y, z);
         boolean bl = false;
         double d = 0.0;

         do {
            BlockPos blockPos2 = blockPos.down();
            BlockState blockState = EvokerEntity.this.getWorld().getBlockState(blockPos2);
            if (blockState.isSideSolidFullSquare(EvokerEntity.this.getWorld(), blockPos2, Direction.UP)) {
               if (!EvokerEntity.this.getWorld().isAir(blockPos)) {
                  BlockState blockState2 = EvokerEntity.this.getWorld().getBlockState(blockPos);
                  VoxelShape voxelShape = blockState2.getCollisionShape(EvokerEntity.this.getWorld(), blockPos);
                  if (!voxelShape.isEmpty()) {
                     d = voxelShape.getMax(Direction.Axis.Y);
                  }
               }

               bl = true;
               break;
            }

            blockPos = blockPos.down();
         } while(blockPos.getY() >= MathHelper.floor(maxY) - 1);

         if (bl) {
            EvokerEntity.this.getWorld().spawnEntity(new EvokerFangsEntity(EvokerEntity.this.getWorld(), x, (double)blockPos.getY() + d, z, yaw, warmup, EvokerEntity.this));
            EvokerEntity.this.getWorld().emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, (double)blockPos.getY() + d, z), GameEvent.Emitter.of((Entity)EvokerEntity.this));
         }

      }

      protected SoundEvent getSoundPrepare() {
         return SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK;
      }

      protected SpellcastingIllagerEntity.Spell getSpell() {
         return SpellcastingIllagerEntity.Spell.FANGS;
      }
   }

   public class WololoGoal extends SpellcastingIllagerEntity.CastSpellGoal {
      private final TargetPredicate convertibleSheepPredicate = TargetPredicate.createNonAttackable().setBaseMaxDistance(16.0).setPredicate((sheep, world) -> {
         return ((SheepEntity)sheep).getColor() == DyeColor.BLUE;
      });

      public WololoGoal() {
         super();
      }

      public boolean canStart() {
         if (EvokerEntity.this.getTarget() != null) {
            return false;
         } else if (EvokerEntity.this.isSpellcasting()) {
            return false;
         } else if (EvokerEntity.this.age < this.startTime) {
            return false;
         } else {
            ServerWorld serverWorld = castToServerWorld(EvokerEntity.this.getWorld());
            if (!serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
               return false;
            } else {
               List list = serverWorld.getTargets(SheepEntity.class, this.convertibleSheepPredicate, EvokerEntity.this, EvokerEntity.this.getBoundingBox().expand(16.0, 4.0, 16.0));
               if (list.isEmpty()) {
                  return false;
               } else {
                  EvokerEntity.this.setWololoTarget((SheepEntity)list.get(EvokerEntity.this.random.nextInt(list.size())));
                  return true;
               }
            }
         }
      }

      public boolean shouldContinue() {
         return EvokerEntity.this.getWololoTarget() != null && this.spellCooldown > 0;
      }

      public void stop() {
         super.stop();
         EvokerEntity.this.setWololoTarget((SheepEntity)null);
      }

      protected void castSpell() {
         SheepEntity sheepEntity = EvokerEntity.this.getWololoTarget();
         if (sheepEntity != null && sheepEntity.isAlive()) {
            sheepEntity.setColor(DyeColor.RED);
         }

      }

      protected int getInitialCooldown() {
         return 40;
      }

      protected int getSpellTicks() {
         return 60;
      }

      protected int startTimeDelay() {
         return 140;
      }

      protected SoundEvent getSoundPrepare() {
         return SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;
      }

      protected SpellcastingIllagerEntity.Spell getSpell() {
         return SpellcastingIllagerEntity.Spell.WOLOLO;
      }
   }
}
