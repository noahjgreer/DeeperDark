package net.minecraft.entity.ai.pathing;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public class MobNavigation extends EntityNavigation {
   private boolean avoidSunlight;

   public MobNavigation(MobEntity mobEntity, World world) {
      super(mobEntity, world);
   }

   protected PathNodeNavigator createPathNodeNavigator(int range) {
      this.nodeMaker = new LandPathNodeMaker();
      return new PathNodeNavigator(this.nodeMaker, range);
   }

   protected boolean isAtValidPosition() {
      return this.entity.isOnGround() || this.entity.isInFluid() || this.entity.hasVehicle();
   }

   protected Vec3d getPos() {
      return new Vec3d(this.entity.getX(), (double)this.getPathfindingY(), this.entity.getZ());
   }

   public Path findPathTo(BlockPos target, int distance) {
      WorldChunk worldChunk = this.world.getChunkManager().getWorldChunk(ChunkSectionPos.getSectionCoord(((BlockPos)target).getX()), ChunkSectionPos.getSectionCoord(((BlockPos)target).getZ()));
      if (worldChunk == null) {
         return null;
      } else {
         BlockPos.Mutable mutable;
         if (worldChunk.getBlockState((BlockPos)target).isAir()) {
            mutable = ((BlockPos)target).mutableCopy().move(Direction.DOWN);

            while(mutable.getY() >= this.world.getBottomY() && worldChunk.getBlockState(mutable).isAir()) {
               mutable.move(Direction.DOWN);
            }

            if (mutable.getY() >= this.world.getBottomY()) {
               return super.findPathTo(mutable.up(), distance);
            }

            mutable.setY(((BlockPos)target).getY() + 1);

            while(mutable.getY() <= this.world.getTopYInclusive() && worldChunk.getBlockState(mutable).isAir()) {
               mutable.move(Direction.UP);
            }

            target = mutable;
         }

         if (!worldChunk.getBlockState((BlockPos)target).isSolid()) {
            return super.findPathTo((BlockPos)target, distance);
         } else {
            mutable = ((BlockPos)target).mutableCopy().move(Direction.UP);

            while(mutable.getY() <= this.world.getTopYInclusive() && worldChunk.getBlockState(mutable).isSolid()) {
               mutable.move(Direction.UP);
            }

            return super.findPathTo(mutable.toImmutable(), distance);
         }
      }
   }

   public Path findPathTo(Entity entity, int distance) {
      return this.findPathTo(entity.getBlockPos(), distance);
   }

   private int getPathfindingY() {
      if (this.entity.isTouchingWater() && this.canSwim()) {
         int i = this.entity.getBlockY();
         BlockState blockState = this.world.getBlockState(BlockPos.ofFloored(this.entity.getX(), (double)i, this.entity.getZ()));
         int j = 0;

         do {
            if (!blockState.isOf(Blocks.WATER)) {
               return i;
            }

            ++i;
            blockState = this.world.getBlockState(BlockPos.ofFloored(this.entity.getX(), (double)i, this.entity.getZ()));
            ++j;
         } while(j <= 16);

         return this.entity.getBlockY();
      } else {
         return MathHelper.floor(this.entity.getY() + 0.5);
      }
   }

   protected void adjustPath() {
      super.adjustPath();
      if (this.avoidSunlight) {
         if (this.world.isSkyVisible(BlockPos.ofFloored(this.entity.getX(), this.entity.getY() + 0.5, this.entity.getZ()))) {
            return;
         }

         for(int i = 0; i < this.currentPath.getLength(); ++i) {
            PathNode pathNode = this.currentPath.getNode(i);
            if (this.world.isSkyVisible(new BlockPos(pathNode.x, pathNode.y, pathNode.z))) {
               this.currentPath.setLength(i);
               return;
            }
         }
      }

   }

   public boolean canControlOpeningDoors() {
      return true;
   }

   protected boolean canWalkOnPath(PathNodeType pathType) {
      if (pathType == PathNodeType.WATER) {
         return false;
      } else if (pathType == PathNodeType.LAVA) {
         return false;
      } else {
         return pathType != PathNodeType.OPEN;
      }
   }

   public void setAvoidSunlight(boolean avoidSunlight) {
      this.avoidSunlight = avoidSunlight;
   }

   public void setCanWalkOverFences(boolean canWalkOverFences) {
      this.nodeMaker.setCanWalkOverFences(canWalkOverFences);
   }
}
