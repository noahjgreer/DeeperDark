package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

public class ActiveTargetGoal extends TrackTargetGoal {
   private static final int DEFAULT_RECIPROCAL_CHANCE = 10;
   protected final Class targetClass;
   protected final int reciprocalChance;
   @Nullable
   protected LivingEntity targetEntity;
   protected TargetPredicate targetPredicate;

   public ActiveTargetGoal(MobEntity mob, Class targetClass, boolean checkVisibility) {
      this(mob, targetClass, 10, checkVisibility, false, (TargetPredicate.EntityPredicate)null);
   }

   public ActiveTargetGoal(MobEntity mob, Class targetClass, boolean checkVisibility, TargetPredicate.EntityPredicate predicate) {
      this(mob, targetClass, 10, checkVisibility, false, predicate);
   }

   public ActiveTargetGoal(MobEntity mob, Class targetClass, boolean checkVisibility, boolean checkCanNavigate) {
      this(mob, targetClass, 10, checkVisibility, checkCanNavigate, (TargetPredicate.EntityPredicate)null);
   }

   public ActiveTargetGoal(MobEntity mob, Class targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable TargetPredicate.EntityPredicate targetPredicate) {
      super(mob, checkVisibility, checkCanNavigate);
      this.targetClass = targetClass;
      this.reciprocalChance = toGoalTicks(reciprocalChance);
      this.setControls(EnumSet.of(Goal.Control.TARGET));
      this.targetPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(this.getFollowRange()).setPredicate(targetPredicate);
   }

   public boolean canStart() {
      if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
         return false;
      } else {
         this.findClosestTarget();
         return this.targetEntity != null;
      }
   }

   protected Box getSearchBox(double distance) {
      return this.mob.getBoundingBox().expand(distance, distance, distance);
   }

   protected void findClosestTarget() {
      ServerWorld serverWorld = getServerWorld(this.mob);
      if (this.targetClass != PlayerEntity.class && this.targetClass != ServerPlayerEntity.class) {
         this.targetEntity = serverWorld.getClosestEntity(this.mob.getWorld().getEntitiesByClass(this.targetClass, this.getSearchBox(this.getFollowRange()), (livingEntity) -> {
            return true;
         }), this.getAndUpdateTargetPredicate(), this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
      } else {
         this.targetEntity = serverWorld.getClosestPlayer(this.getAndUpdateTargetPredicate(), this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
      }

   }

   public void start() {
      this.mob.setTarget(this.targetEntity);
      super.start();
   }

   public void setTargetEntity(@Nullable LivingEntity targetEntity) {
      this.targetEntity = targetEntity;
   }

   private TargetPredicate getAndUpdateTargetPredicate() {
      return this.targetPredicate.setBaseMaxDistance(this.getFollowRange());
   }
}
