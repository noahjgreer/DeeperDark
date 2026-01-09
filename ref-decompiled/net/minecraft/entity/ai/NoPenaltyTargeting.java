package net.minecraft.entity.ai;

import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class NoPenaltyTargeting {
   @Nullable
   public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange) {
      boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
      return FuzzyPositions.guessBestPathTarget(entity, () -> {
         BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange);
         return tryMake(entity, horizontalRange, bl, blockPos);
      });
   }

   @Nullable
   public static Vec3d findTo(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d end, double angleRange) {
      Vec3d vec3d = end.subtract(entity.getX(), entity.getY(), entity.getZ());
      boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
      return FuzzyPositions.guessBestPathTarget(entity, () -> {
         BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange, 0, vec3d.x, vec3d.z, angleRange);
         return blockPos == null ? null : tryMake(entity, horizontalRange, bl, blockPos);
      });
   }

   @Nullable
   public static Vec3d findFrom(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d start) {
      Vec3d vec3d = entity.getPos().subtract(start);
      boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
      return FuzzyPositions.guessBestPathTarget(entity, () -> {
         BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange, 0, vec3d.x, vec3d.z, 1.5707963705062866);
         return blockPos == null ? null : tryMake(entity, horizontalRange, bl, blockPos);
      });
   }

   @Nullable
   private static BlockPos tryMake(PathAwareEntity entity, int horizontalRange, boolean posTargetInRange, BlockPos fuzz) {
      BlockPos blockPos = FuzzyPositions.towardTarget(entity, horizontalRange, entity.getRandom(), fuzz);
      return !NavigationConditions.isHeightInvalid(blockPos, entity) && !NavigationConditions.isPositionTargetOutOfWalkRange(posTargetInRange, entity, blockPos) && !NavigationConditions.isInvalidPosition(entity.getNavigation(), blockPos) && !NavigationConditions.hasPathfindingPenalty(entity, blockPos) ? blockPos : null;
   }
}
