package net.minecraft.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.jetbrains.annotations.Nullable;

public class MoveThroughVillageGoal extends Goal {
   protected final PathAwareEntity mob;
   private final double speed;
   @Nullable
   private Path targetPath;
   private BlockPos target;
   private final boolean requiresNighttime;
   private final List visitedTargets = Lists.newArrayList();
   private final int distance;
   private final BooleanSupplier doorPassingThroughGetter;

   public MoveThroughVillageGoal(PathAwareEntity entity, double speed, boolean requiresNighttime, int distance, BooleanSupplier doorPassingThroughGetter) {
      this.mob = entity;
      this.speed = speed;
      this.requiresNighttime = requiresNighttime;
      this.distance = distance;
      this.doorPassingThroughGetter = doorPassingThroughGetter;
      this.setControls(EnumSet.of(Goal.Control.MOVE));
      if (!NavigationConditions.hasMobNavigation(entity)) {
         throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
      }
   }

   public boolean canStart() {
      if (!NavigationConditions.hasMobNavigation(this.mob)) {
         return false;
      } else {
         this.forgetOldTarget();
         if (this.requiresNighttime && this.mob.getWorld().isDay()) {
            return false;
         } else {
            ServerWorld serverWorld = (ServerWorld)this.mob.getWorld();
            BlockPos blockPos = this.mob.getBlockPos();
            if (!serverWorld.isNearOccupiedPointOfInterest(blockPos, 6)) {
               return false;
            } else {
               Vec3d vec3d = FuzzyTargeting.find(this.mob, 15, 7, (pos) -> {
                  if (!serverWorld.isNearOccupiedPointOfInterest(pos)) {
                     return Double.NEGATIVE_INFINITY;
                  } else {
                     Optional optional = serverWorld.getPointOfInterestStorage().getPosition((poiType) -> {
                        return poiType.isIn(PointOfInterestTypeTags.VILLAGE);
                     }, this::shouldVisit, pos, 10, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
                     return (Double)optional.map((blockPos2) -> {
                        return -blockPos2.getSquaredDistance(blockPos);
                     }).orElse(Double.NEGATIVE_INFINITY);
                  }
               });
               if (vec3d == null) {
                  return false;
               } else {
                  Optional optional = serverWorld.getPointOfInterestStorage().getPosition((poiType) -> {
                     return poiType.isIn(PointOfInterestTypeTags.VILLAGE);
                  }, this::shouldVisit, BlockPos.ofFloored(vec3d), 10, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
                  if (optional.isEmpty()) {
                     return false;
                  } else {
                     this.target = ((BlockPos)optional.get()).toImmutable();
                     EntityNavigation entityNavigation = this.mob.getNavigation();
                     entityNavigation.setCanOpenDoors(this.doorPassingThroughGetter.getAsBoolean());
                     this.targetPath = entityNavigation.findPathTo((BlockPos)this.target, 0);
                     entityNavigation.setCanOpenDoors(true);
                     if (this.targetPath == null) {
                        Vec3d vec3d2 = NoPenaltyTargeting.findTo(this.mob, 10, 7, Vec3d.ofBottomCenter(this.target), 1.5707963705062866);
                        if (vec3d2 == null) {
                           return false;
                        }

                        entityNavigation.setCanOpenDoors(this.doorPassingThroughGetter.getAsBoolean());
                        this.targetPath = this.mob.getNavigation().findPathTo(vec3d2.x, vec3d2.y, vec3d2.z, 0);
                        entityNavigation.setCanOpenDoors(true);
                        if (this.targetPath == null) {
                           return false;
                        }
                     }

                     for(int i = 0; i < this.targetPath.getLength(); ++i) {
                        PathNode pathNode = this.targetPath.getNode(i);
                        BlockPos blockPos2 = new BlockPos(pathNode.x, pathNode.y + 1, pathNode.z);
                        if (DoorBlock.canOpenByHand(this.mob.getWorld(), blockPos2)) {
                           this.targetPath = this.mob.getNavigation().findPathTo((double)pathNode.x, (double)pathNode.y, (double)pathNode.z, 0);
                           break;
                        }
                     }

                     return this.targetPath != null;
                  }
               }
            }
         }
      }
   }

   public boolean shouldContinue() {
      if (this.mob.getNavigation().isIdle()) {
         return false;
      } else {
         return !this.target.isWithinDistance(this.mob.getPos(), (double)(this.mob.getWidth() + (float)this.distance));
      }
   }

   public void start() {
      this.mob.getNavigation().startMovingAlong(this.targetPath, this.speed);
   }

   public void stop() {
      if (this.mob.getNavigation().isIdle() || this.target.isWithinDistance(this.mob.getPos(), (double)this.distance)) {
         this.visitedTargets.add(this.target);
      }

   }

   private boolean shouldVisit(BlockPos pos) {
      Iterator var2 = this.visitedTargets.iterator();

      BlockPos blockPos;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         blockPos = (BlockPos)var2.next();
      } while(!Objects.equals(pos, blockPos));

      return false;
   }

   private void forgetOldTarget() {
      if (this.visitedTargets.size() > 15) {
         this.visitedTargets.remove(0);
      }

   }
}
