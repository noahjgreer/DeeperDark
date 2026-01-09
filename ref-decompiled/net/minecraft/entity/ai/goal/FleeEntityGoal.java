package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class FleeEntityGoal extends Goal {
   protected final PathAwareEntity mob;
   private final double slowSpeed;
   private final double fastSpeed;
   @Nullable
   protected LivingEntity targetEntity;
   protected final float fleeDistance;
   @Nullable
   protected Path fleePath;
   protected final EntityNavigation fleeingEntityNavigation;
   protected final Class classToFleeFrom;
   protected final Predicate extraInclusionSelector;
   protected final Predicate inclusionSelector;
   private final TargetPredicate withinRangePredicate;

   public FleeEntityGoal(PathAwareEntity mob, Class fleeFromType, float distance, double slowSpeed, double fastSpeed) {
      Predicate var10003 = (entity) -> {
         return true;
      };
      Predicate var10007 = EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR;
      Objects.requireNonNull(var10007);
      this(mob, fleeFromType, var10003, distance, slowSpeed, fastSpeed, var10007::test);
   }

   public FleeEntityGoal(PathAwareEntity mob, Class fleeFromType, Predicate extraInclusionSelector, float distance, double slowSpeed, double fastSpeed, Predicate inclusionSelector) {
      this.mob = mob;
      this.classToFleeFrom = fleeFromType;
      this.extraInclusionSelector = extraInclusionSelector;
      this.fleeDistance = distance;
      this.slowSpeed = slowSpeed;
      this.fastSpeed = fastSpeed;
      this.inclusionSelector = inclusionSelector;
      this.fleeingEntityNavigation = mob.getNavigation();
      this.setControls(EnumSet.of(Goal.Control.MOVE));
      this.withinRangePredicate = TargetPredicate.createAttackable().setBaseMaxDistance((double)distance).setPredicate((entity, world) -> {
         return inclusionSelector.test(entity) && extraInclusionSelector.test(entity);
      });
   }

   public FleeEntityGoal(PathAwareEntity fleeingEntity, Class classToFleeFrom, float fleeDistance, double fleeSlowSpeed, double fleeFastSpeed, Predicate inclusionSelector) {
      this(fleeingEntity, classToFleeFrom, (entity) -> {
         return true;
      }, fleeDistance, fleeSlowSpeed, fleeFastSpeed, inclusionSelector);
   }

   public boolean canStart() {
      this.targetEntity = getServerWorld(this.mob).getClosestEntity(this.mob.getWorld().getEntitiesByClass(this.classToFleeFrom, this.mob.getBoundingBox().expand((double)this.fleeDistance, 3.0, (double)this.fleeDistance), (livingEntity) -> {
         return true;
      }), this.withinRangePredicate, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());
      if (this.targetEntity == null) {
         return false;
      } else {
         Vec3d vec3d = NoPenaltyTargeting.findFrom(this.mob, 16, 7, this.targetEntity.getPos());
         if (vec3d == null) {
            return false;
         } else if (this.targetEntity.squaredDistanceTo(vec3d.x, vec3d.y, vec3d.z) < this.targetEntity.squaredDistanceTo(this.mob)) {
            return false;
         } else {
            this.fleePath = this.fleeingEntityNavigation.findPathTo(vec3d.x, vec3d.y, vec3d.z, 0);
            return this.fleePath != null;
         }
      }
   }

   public boolean shouldContinue() {
      return !this.fleeingEntityNavigation.isIdle();
   }

   public void start() {
      this.fleeingEntityNavigation.startMovingAlong(this.fleePath, this.slowSpeed);
   }

   public void stop() {
      this.targetEntity = null;
   }

   public void tick() {
      if (this.mob.squaredDistanceTo(this.targetEntity) < 49.0) {
         this.mob.getNavigation().setSpeed(this.fastSpeed);
      } else {
         this.mob.getNavigation().setSpeed(this.slowSpeed);
      }

   }
}
