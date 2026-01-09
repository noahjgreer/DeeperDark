package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;

public class WaterPathNodeMaker extends PathNodeMaker {
   private final boolean canJumpOutOfWater;
   private final Long2ObjectMap nodePosToType = new Long2ObjectOpenHashMap();

   public WaterPathNodeMaker(boolean canJumpOutOfWater) {
      this.canJumpOutOfWater = canJumpOutOfWater;
   }

   public void init(ChunkCache cachedWorld, MobEntity entity) {
      super.init(cachedWorld, entity);
      this.nodePosToType.clear();
   }

   public void clear() {
      super.clear();
      this.nodePosToType.clear();
   }

   public PathNode getStart() {
      return this.getNode(MathHelper.floor(this.entity.getBoundingBox().minX), MathHelper.floor(this.entity.getBoundingBox().minY + 0.5), MathHelper.floor(this.entity.getBoundingBox().minZ));
   }

   public TargetPathNode getNode(double x, double y, double z) {
      return this.createNode(x, y, z);
   }

   public int getSuccessors(PathNode[] successors, PathNode node) {
      int i = 0;
      Map map = Maps.newEnumMap(Direction.class);
      Direction[] var5 = Direction.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction direction = var5[var7];
         PathNode pathNode = this.getPassableNode(node.x + direction.getOffsetX(), node.y + direction.getOffsetY(), node.z + direction.getOffsetZ());
         map.put(direction, pathNode);
         if (this.hasNotVisited(pathNode)) {
            successors[i++] = pathNode;
         }
      }

      Iterator var10 = Direction.Type.HORIZONTAL.iterator();

      while(var10.hasNext()) {
         Direction direction2 = (Direction)var10.next();
         Direction direction3 = direction2.rotateYClockwise();
         if (hasPenalty((PathNode)map.get(direction2)) && hasPenalty((PathNode)map.get(direction3))) {
            PathNode pathNode2 = this.getPassableNode(node.x + direction2.getOffsetX() + direction3.getOffsetX(), node.y, node.z + direction2.getOffsetZ() + direction3.getOffsetZ());
            if (this.hasNotVisited(pathNode2)) {
               successors[i++] = pathNode2;
            }
         }
      }

      return i;
   }

   protected boolean hasNotVisited(@Nullable PathNode node) {
      return node != null && !node.visited;
   }

   private static boolean hasPenalty(@Nullable PathNode node) {
      return node != null && node.penalty >= 0.0F;
   }

   @Nullable
   protected PathNode getPassableNode(int x, int y, int z) {
      PathNode pathNode = null;
      PathNodeType pathNodeType = this.addPathNodePos(x, y, z);
      if (this.canJumpOutOfWater && pathNodeType == PathNodeType.BREACH || pathNodeType == PathNodeType.WATER) {
         float f = this.entity.getPathfindingPenalty(pathNodeType);
         if (f >= 0.0F) {
            pathNode = this.getNode(x, y, z);
            pathNode.type = pathNodeType;
            pathNode.penalty = Math.max(pathNode.penalty, f);
            if (this.context.getWorld().getFluidState(new BlockPos(x, y, z)).isEmpty()) {
               pathNode.penalty += 8.0F;
            }
         }
      }

      return pathNode;
   }

   protected PathNodeType addPathNodePos(int x, int y, int z) {
      return (PathNodeType)this.nodePosToType.computeIfAbsent(BlockPos.asLong(x, y, z), (pos) -> {
         return this.getDefaultNodeType(this.context, x, y, z);
      });
   }

   public PathNodeType getDefaultNodeType(PathContext context, int x, int y, int z) {
      return this.getNodeType(context, x, y, z, this.entity);
   }

   public PathNodeType getNodeType(PathContext context, int x, int y, int z, MobEntity mob) {
      BlockPos.Mutable mutable = new BlockPos.Mutable();

      for(int i = x; i < x + this.entityBlockXSize; ++i) {
         for(int j = y; j < y + this.entityBlockYSize; ++j) {
            for(int k = z; k < z + this.entityBlockZSize; ++k) {
               BlockState blockState = context.getBlockState(mutable.set(i, j, k));
               FluidState fluidState = blockState.getFluidState();
               if (fluidState.isEmpty() && blockState.canPathfindThrough(NavigationType.WATER) && blockState.isAir()) {
                  return PathNodeType.BREACH;
               }

               if (!fluidState.isIn(FluidTags.WATER)) {
                  return PathNodeType.BLOCKED;
               }
            }
         }
      }

      BlockState blockState2 = context.getBlockState(mutable);
      if (blockState2.canPathfindThrough(NavigationType.WATER)) {
         return PathNodeType.WATER;
      } else {
         return PathNodeType.BLOCKED;
      }
   }
}
