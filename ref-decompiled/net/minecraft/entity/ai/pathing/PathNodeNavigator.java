package net.minecraft.entity.ai.pathing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.SampleType;
import net.minecraft.world.chunk.ChunkCache;
import org.jetbrains.annotations.Nullable;

public class PathNodeNavigator {
   private static final float TARGET_DISTANCE_MULTIPLIER = 1.5F;
   private final PathNode[] successors = new PathNode[32];
   private int range;
   private final PathNodeMaker pathNodeMaker;
   private static final boolean field_31808 = false;
   private final PathMinHeap minHeap = new PathMinHeap();

   public PathNodeNavigator(PathNodeMaker pathNodeMaker, int range) {
      this.pathNodeMaker = pathNodeMaker;
      this.range = range;
   }

   public void setRange(int range) {
      this.range = range;
   }

   @Nullable
   public Path findPathToAny(ChunkCache world, MobEntity mob, Set positions, float followRange, int distance, float rangeMultiplier) {
      this.minHeap.clear();
      this.pathNodeMaker.init(world, mob);
      PathNode pathNode = this.pathNodeMaker.getStart();
      if (pathNode == null) {
         return null;
      } else {
         Map map = (Map)positions.stream().collect(Collectors.toMap((pos) -> {
            return this.pathNodeMaker.getNode((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
         }, Function.identity()));
         Path path = this.findPathToAny(pathNode, map, followRange, distance, rangeMultiplier);
         this.pathNodeMaker.clear();
         return path;
      }
   }

   @Nullable
   private Path findPathToAny(PathNode startNode, Map positions, float followRange, int distance, float rangeMultiplier) {
      Profiler profiler = Profilers.get();
      profiler.push("find_path");
      profiler.markSampleType(SampleType.PATH_FINDING);
      Set set = positions.keySet();
      startNode.penalizedPathLength = 0.0F;
      startNode.distanceToNearestTarget = this.calculateDistances(startNode, set);
      startNode.heapWeight = startNode.distanceToNearestTarget;
      this.minHeap.clear();
      this.minHeap.push(startNode);
      Set set2 = ImmutableSet.of();
      int i = 0;
      Set set3 = Sets.newHashSetWithExpectedSize(set.size());
      int j = (int)((float)this.range * rangeMultiplier);

      while(!this.minHeap.isEmpty()) {
         ++i;
         if (i >= j) {
            break;
         }

         PathNode pathNode = this.minHeap.pop();
         pathNode.visited = true;
         Iterator var13 = set.iterator();

         while(var13.hasNext()) {
            TargetPathNode targetPathNode = (TargetPathNode)var13.next();
            if (pathNode.getManhattanDistance((PathNode)targetPathNode) <= (float)distance) {
               targetPathNode.markReached();
               set3.add(targetPathNode);
            }
         }

         if (!set3.isEmpty()) {
            break;
         }

         if (!(pathNode.getDistance(startNode) >= followRange)) {
            int k = this.pathNodeMaker.getSuccessors(this.successors, pathNode);

            for(int l = 0; l < k; ++l) {
               PathNode pathNode2 = this.successors[l];
               float f = this.getDistance(pathNode, pathNode2);
               pathNode2.pathLength = pathNode.pathLength + f;
               float g = pathNode.penalizedPathLength + f + pathNode2.penalty;
               if (pathNode2.pathLength < followRange && (!pathNode2.isInHeap() || g < pathNode2.penalizedPathLength)) {
                  pathNode2.previous = pathNode;
                  pathNode2.penalizedPathLength = g;
                  pathNode2.distanceToNearestTarget = this.calculateDistances(pathNode2, set) * 1.5F;
                  if (pathNode2.isInHeap()) {
                     this.minHeap.setNodeWeight(pathNode2, pathNode2.penalizedPathLength + pathNode2.distanceToNearestTarget);
                  } else {
                     pathNode2.heapWeight = pathNode2.penalizedPathLength + pathNode2.distanceToNearestTarget;
                     this.minHeap.push(pathNode2);
                  }
               }
            }
         }
      }

      Optional optional = !set3.isEmpty() ? set3.stream().map((node) -> {
         return this.createPath(node.getNearestNode(), (BlockPos)positions.get(node), true);
      }).min(Comparator.comparingInt(Path::getLength)) : set.stream().map((targetPathNodex) -> {
         return this.createPath(targetPathNodex.getNearestNode(), (BlockPos)positions.get(targetPathNodex), false);
      }).min(Comparator.comparingDouble(Path::getManhattanDistanceFromTarget).thenComparingInt(Path::getLength));
      profiler.pop();
      if (optional.isEmpty()) {
         return null;
      } else {
         Path path = (Path)optional.get();
         return path;
      }
   }

   protected float getDistance(PathNode a, PathNode b) {
      return a.getDistance(b);
   }

   private float calculateDistances(PathNode node, Set targets) {
      float f = Float.MAX_VALUE;

      float g;
      for(Iterator var4 = targets.iterator(); var4.hasNext(); f = Math.min(g, f)) {
         TargetPathNode targetPathNode = (TargetPathNode)var4.next();
         g = node.getDistance((PathNode)targetPathNode);
         targetPathNode.updateNearestNode(g, node);
      }

      return f;
   }

   private Path createPath(PathNode endNode, BlockPos target, boolean reachesTarget) {
      List list = Lists.newArrayList();
      PathNode pathNode = endNode;
      list.add(0, endNode);

      while(pathNode.previous != null) {
         pathNode = pathNode.previous;
         list.add(0, pathNode);
      }

      return new Path(list, target, reachesTarget);
   }

   // $FF: synthetic method
   private static PathNode[] method_52609(int i) {
      return new PathNode[i];
   }
}
