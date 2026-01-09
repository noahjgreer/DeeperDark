package net.minecraft.world.biome.source.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import org.jetbrains.annotations.Nullable;

public class MultiNoiseUtil {
   private static final boolean field_34477 = false;
   private static final float TO_LONG_FACTOR = 10000.0F;
   @VisibleForTesting
   protected static final int HYPERCUBE_DIMENSION = 7;

   public static NoiseValuePoint createNoiseValuePoint(float temperatureNoise, float humidityNoise, float continentalnessNoise, float erosionNoise, float depth, float weirdnessNoise) {
      return new NoiseValuePoint(toLong(temperatureNoise), toLong(humidityNoise), toLong(continentalnessNoise), toLong(erosionNoise), toLong(depth), toLong(weirdnessNoise));
   }

   public static NoiseHypercube createNoiseHypercube(float temperature, float humidity, float continentalness, float erosion, float depth, float weirdness, float offset) {
      return new NoiseHypercube(MultiNoiseUtil.ParameterRange.of(temperature), MultiNoiseUtil.ParameterRange.of(humidity), MultiNoiseUtil.ParameterRange.of(continentalness), MultiNoiseUtil.ParameterRange.of(erosion), MultiNoiseUtil.ParameterRange.of(depth), MultiNoiseUtil.ParameterRange.of(weirdness), toLong(offset));
   }

   public static NoiseHypercube createNoiseHypercube(ParameterRange temperature, ParameterRange humidity, ParameterRange continentalness, ParameterRange erosion, ParameterRange depth, ParameterRange weirdness, float offset) {
      return new NoiseHypercube(temperature, humidity, continentalness, erosion, depth, weirdness, toLong(offset));
   }

   public static long toLong(float value) {
      return (long)(value * 10000.0F);
   }

   public static float toFloat(long value) {
      return (float)value / 10000.0F;
   }

   public static MultiNoiseSampler createEmptyMultiNoiseSampler() {
      DensityFunction densityFunction = DensityFunctionTypes.zero();
      return new MultiNoiseSampler(densityFunction, densityFunction, densityFunction, densityFunction, densityFunction, densityFunction, List.of());
   }

   public static BlockPos findFittestPosition(List noises, MultiNoiseSampler sampler) {
      return (new FittestPositionFinder(noises, sampler)).bestResult.location();
   }

   public static record NoiseValuePoint(long temperatureNoise, long humidityNoise, long continentalnessNoise, long erosionNoise, long depth, long weirdnessNoise) {
      final long temperatureNoise;
      final long humidityNoise;
      final long continentalnessNoise;
      final long erosionNoise;
      final long depth;
      final long weirdnessNoise;

      public NoiseValuePoint(long l, long m, long n, long o, long p, long q) {
         this.temperatureNoise = l;
         this.humidityNoise = m;
         this.continentalnessNoise = n;
         this.erosionNoise = o;
         this.depth = p;
         this.weirdnessNoise = q;
      }

      @VisibleForTesting
      protected long[] getNoiseValueList() {
         return new long[]{this.temperatureNoise, this.humidityNoise, this.continentalnessNoise, this.erosionNoise, this.depth, this.weirdnessNoise, 0L};
      }

      public long temperatureNoise() {
         return this.temperatureNoise;
      }

      public long humidityNoise() {
         return this.humidityNoise;
      }

      public long continentalnessNoise() {
         return this.continentalnessNoise;
      }

      public long erosionNoise() {
         return this.erosionNoise;
      }

      public long depth() {
         return this.depth;
      }

      public long weirdnessNoise() {
         return this.weirdnessNoise;
      }
   }

   public static record NoiseHypercube(ParameterRange temperature, ParameterRange humidity, ParameterRange continentalness, ParameterRange erosion, ParameterRange depth, ParameterRange weirdness, long offset) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(MultiNoiseUtil.ParameterRange.CODEC.fieldOf("temperature").forGetter((noiseHypercube) -> {
            return noiseHypercube.temperature;
         }), MultiNoiseUtil.ParameterRange.CODEC.fieldOf("humidity").forGetter((noiseHypercube) -> {
            return noiseHypercube.humidity;
         }), MultiNoiseUtil.ParameterRange.CODEC.fieldOf("continentalness").forGetter((noiseHypercube) -> {
            return noiseHypercube.continentalness;
         }), MultiNoiseUtil.ParameterRange.CODEC.fieldOf("erosion").forGetter((noiseHypercube) -> {
            return noiseHypercube.erosion;
         }), MultiNoiseUtil.ParameterRange.CODEC.fieldOf("depth").forGetter((noiseHypercube) -> {
            return noiseHypercube.depth;
         }), MultiNoiseUtil.ParameterRange.CODEC.fieldOf("weirdness").forGetter((noiseHypercube) -> {
            return noiseHypercube.weirdness;
         }), Codec.floatRange(0.0F, 1.0F).fieldOf("offset").xmap(MultiNoiseUtil::toLong, MultiNoiseUtil::toFloat).forGetter((noiseHypercube) -> {
            return noiseHypercube.offset;
         })).apply(instance, NoiseHypercube::new);
      });

      public NoiseHypercube(ParameterRange temperature, ParameterRange humidity, ParameterRange continentalness, ParameterRange erosion, ParameterRange depth, ParameterRange weirdness, long l) {
         this.temperature = temperature;
         this.humidity = humidity;
         this.continentalness = continentalness;
         this.erosion = erosion;
         this.depth = depth;
         this.weirdness = weirdness;
         this.offset = l;
      }

      long getSquaredDistance(NoiseValuePoint point) {
         return MathHelper.square(this.temperature.getDistance(point.temperatureNoise)) + MathHelper.square(this.humidity.getDistance(point.humidityNoise)) + MathHelper.square(this.continentalness.getDistance(point.continentalnessNoise)) + MathHelper.square(this.erosion.getDistance(point.erosionNoise)) + MathHelper.square(this.depth.getDistance(point.depth)) + MathHelper.square(this.weirdness.getDistance(point.weirdnessNoise)) + MathHelper.square(this.offset);
      }

      protected List getParameters() {
         return ImmutableList.of(this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, new ParameterRange(this.offset, this.offset));
      }

      public ParameterRange temperature() {
         return this.temperature;
      }

      public ParameterRange humidity() {
         return this.humidity;
      }

      public ParameterRange continentalness() {
         return this.continentalness;
      }

      public ParameterRange erosion() {
         return this.erosion;
      }

      public ParameterRange depth() {
         return this.depth;
      }

      public ParameterRange weirdness() {
         return this.weirdness;
      }

      public long offset() {
         return this.offset;
      }
   }

   public static record ParameterRange(long min, long max) {
      public static final Codec CODEC = Codecs.createCodecForPairObject(Codec.floatRange(-2.0F, 2.0F), "min", "max", (min, max) -> {
         return min.compareTo(max) > 0 ? DataResult.error(() -> {
            return "Cannon construct interval, min > max (" + min + " > " + max + ")";
         }) : DataResult.success(new ParameterRange(MultiNoiseUtil.toLong(min), MultiNoiseUtil.toLong(max)));
      }, (parameterRange) -> {
         return MultiNoiseUtil.toFloat(parameterRange.min());
      }, (parameterRange) -> {
         return MultiNoiseUtil.toFloat(parameterRange.max());
      });

      public ParameterRange(long l, long m) {
         this.min = l;
         this.max = m;
      }

      public static ParameterRange of(float point) {
         return of(point, point);
      }

      public static ParameterRange of(float min, float max) {
         if (min > max) {
            throw new IllegalArgumentException("min > max: " + min + " " + max);
         } else {
            return new ParameterRange(MultiNoiseUtil.toLong(min), MultiNoiseUtil.toLong(max));
         }
      }

      public static ParameterRange combine(ParameterRange min, ParameterRange max) {
         if (min.min() > max.max()) {
            String var10002 = String.valueOf(min);
            throw new IllegalArgumentException("min > max: " + var10002 + " " + String.valueOf(max));
         } else {
            return new ParameterRange(min.min(), max.max());
         }
      }

      public String toString() {
         return this.min == this.max ? String.format(Locale.ROOT, "%d", this.min) : String.format(Locale.ROOT, "[%d-%d]", this.min, this.max);
      }

      public long getDistance(long noise) {
         long l = noise - this.max;
         long m = this.min - noise;
         return l > 0L ? l : Math.max(m, 0L);
      }

      public long getDistance(ParameterRange other) {
         long l = other.min() - this.max;
         long m = this.min - other.max();
         return l > 0L ? l : Math.max(m, 0L);
      }

      public ParameterRange combine(@Nullable ParameterRange other) {
         return other == null ? this : new ParameterRange(Math.min(this.min, other.min()), Math.max(this.max, other.max()));
      }

      public long min() {
         return this.min;
      }

      public long max() {
         return this.max;
      }
   }

   public static record MultiNoiseSampler(DensityFunction temperature, DensityFunction humidity, DensityFunction continentalness, DensityFunction erosion, DensityFunction depth, DensityFunction weirdness, List spawnTarget) {
      public MultiNoiseSampler(DensityFunction densityFunction, DensityFunction densityFunction2, DensityFunction densityFunction3, DensityFunction densityFunction4, DensityFunction densityFunction5, DensityFunction densityFunction6, List list) {
         this.temperature = densityFunction;
         this.humidity = densityFunction2;
         this.continentalness = densityFunction3;
         this.erosion = densityFunction4;
         this.depth = densityFunction5;
         this.weirdness = densityFunction6;
         this.spawnTarget = list;
      }

      public NoiseValuePoint sample(int x, int y, int z) {
         int i = BiomeCoords.toBlock(x);
         int j = BiomeCoords.toBlock(y);
         int k = BiomeCoords.toBlock(z);
         DensityFunction.UnblendedNoisePos unblendedNoisePos = new DensityFunction.UnblendedNoisePos(i, j, k);
         return MultiNoiseUtil.createNoiseValuePoint((float)this.temperature.sample(unblendedNoisePos), (float)this.humidity.sample(unblendedNoisePos), (float)this.continentalness.sample(unblendedNoisePos), (float)this.erosion.sample(unblendedNoisePos), (float)this.depth.sample(unblendedNoisePos), (float)this.weirdness.sample(unblendedNoisePos));
      }

      public BlockPos findBestSpawnPosition() {
         return this.spawnTarget.isEmpty() ? BlockPos.ORIGIN : MultiNoiseUtil.findFittestPosition(this.spawnTarget, this);
      }

      public DensityFunction temperature() {
         return this.temperature;
      }

      public DensityFunction humidity() {
         return this.humidity;
      }

      public DensityFunction continentalness() {
         return this.continentalness;
      }

      public DensityFunction erosion() {
         return this.erosion;
      }

      public DensityFunction depth() {
         return this.depth;
      }

      public DensityFunction weirdness() {
         return this.weirdness;
      }

      public List spawnTarget() {
         return this.spawnTarget;
      }
   }

   private static class FittestPositionFinder {
      private static final long field_54705 = 2048L;
      Result bestResult;

      FittestPositionFinder(List noises, MultiNoiseSampler sampler) {
         this.bestResult = calculateFitness(noises, sampler, 0, 0);
         this.findFittest(noises, sampler, 2048.0F, 512.0F);
         this.findFittest(noises, sampler, 512.0F, 32.0F);
      }

      private void findFittest(List noises, MultiNoiseSampler sampler, float maxDistance, float step) {
         float f = 0.0F;
         float g = step;
         BlockPos blockPos = this.bestResult.location();

         while(g <= maxDistance) {
            int i = blockPos.getX() + (int)(Math.sin((double)f) * (double)g);
            int j = blockPos.getZ() + (int)(Math.cos((double)f) * (double)g);
            Result result = calculateFitness(noises, sampler, i, j);
            if (result.fitness() < this.bestResult.fitness()) {
               this.bestResult = result;
            }

            f += step / g;
            if ((double)f > 6.283185307179586) {
               f = 0.0F;
               g += step;
            }
         }

      }

      private static Result calculateFitness(List noises, MultiNoiseSampler sampler, int x, int z) {
         NoiseValuePoint noiseValuePoint = sampler.sample(BiomeCoords.fromBlock(x), 0, BiomeCoords.fromBlock(z));
         NoiseValuePoint noiseValuePoint2 = new NoiseValuePoint(noiseValuePoint.temperatureNoise(), noiseValuePoint.humidityNoise(), noiseValuePoint.continentalnessNoise(), noiseValuePoint.erosionNoise(), 0L, noiseValuePoint.weirdnessNoise());
         long l = Long.MAX_VALUE;

         NoiseHypercube noiseHypercube;
         for(Iterator var8 = noises.iterator(); var8.hasNext(); l = Math.min(l, noiseHypercube.getSquaredDistance(noiseValuePoint2))) {
            noiseHypercube = (NoiseHypercube)var8.next();
         }

         long m = MathHelper.square((long)x) + MathHelper.square((long)z);
         long n = l * MathHelper.square(2048L) + m;
         return new Result(new BlockPos(x, 0, z), n);
      }

      private static record Result(BlockPos location, long fitness) {
         Result(BlockPos blockPos, long l) {
            this.location = blockPos;
            this.fitness = l;
         }

         public BlockPos location() {
            return this.location;
         }

         public long fitness() {
            return this.fitness;
         }
      }
   }

   public static class Entries {
      private final List entries;
      private final SearchTree tree;

      public static Codec createCodec(MapCodec entryCodec) {
         return Codecs.nonEmptyList(RecordCodecBuilder.create((instance) -> {
            return instance.group(MultiNoiseUtil.NoiseHypercube.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), entryCodec.forGetter(Pair::getSecond)).apply(instance, Pair::of);
         }).listOf()).xmap(Entries::new, Entries::getEntries);
      }

      public Entries(List entries) {
         this.entries = entries;
         this.tree = MultiNoiseUtil.SearchTree.create(entries);
      }

      public List getEntries() {
         return this.entries;
      }

      public Object get(NoiseValuePoint point) {
         return this.getValue(point);
      }

      @VisibleForTesting
      public Object getValueSimple(NoiseValuePoint point) {
         Iterator iterator = this.getEntries().iterator();
         Pair pair = (Pair)iterator.next();
         long l = ((NoiseHypercube)pair.getFirst()).getSquaredDistance(point);
         Object object = pair.getSecond();

         while(iterator.hasNext()) {
            Pair pair2 = (Pair)iterator.next();
            long m = ((NoiseHypercube)pair2.getFirst()).getSquaredDistance(point);
            if (m < l) {
               l = m;
               object = pair2.getSecond();
            }
         }

         return object;
      }

      public Object getValue(NoiseValuePoint point) {
         return this.getValue(point, SearchTree.TreeNode::getSquaredDistance);
      }

      protected Object getValue(NoiseValuePoint point, NodeDistanceFunction distanceFunction) {
         return this.tree.get(point, distanceFunction);
      }
   }

   protected static final class SearchTree {
      private static final int MAX_NODES_FOR_SIMPLE_TREE = 6;
      private final TreeNode firstNode;
      private final ThreadLocal previousResultNode = new ThreadLocal();

      private SearchTree(TreeNode firstNode) {
         this.firstNode = firstNode;
      }

      public static SearchTree create(List entries) {
         if (entries.isEmpty()) {
            throw new IllegalArgumentException("Need at least one value to build the search tree.");
         } else {
            int i = ((NoiseHypercube)((Pair)entries.get(0)).getFirst()).getParameters().size();
            if (i != 7) {
               throw new IllegalStateException("Expecting parameter space to be 7, got " + i);
            } else {
               List list = (List)entries.stream().map((entry) -> {
                  return new TreeLeafNode((NoiseHypercube)entry.getFirst(), entry.getSecond());
               }).collect(Collectors.toCollection(ArrayList::new));
               return new SearchTree(createNode(i, list));
            }
         }
      }

      private static TreeNode createNode(int parameterNumber, List subTree) {
         if (subTree.isEmpty()) {
            throw new IllegalStateException("Need at least one child to build a node");
         } else if (subTree.size() == 1) {
            return (TreeNode)subTree.get(0);
         } else if (subTree.size() <= 6) {
            subTree.sort(Comparator.comparingLong((node) -> {
               long l = 0L;

               for(int j = 0; j < parameterNumber; ++j) {
                  ParameterRange parameterRange = node.parameters[j];
                  l += Math.abs((parameterRange.min() + parameterRange.max()) / 2L);
               }

               return l;
            }));
            return new TreeBranchNode(subTree);
         } else {
            long l = Long.MAX_VALUE;
            int i = -1;
            List list = null;

            for(int j = 0; j < parameterNumber; ++j) {
               sortTree(subTree, parameterNumber, j, false);
               List list2 = getBatchedTree(subTree);
               long m = 0L;

               TreeBranchNode treeBranchNode;
               for(Iterator var10 = list2.iterator(); var10.hasNext(); m += getRangeLengthSum(treeBranchNode.parameters)) {
                  treeBranchNode = (TreeBranchNode)var10.next();
               }

               if (l > m) {
                  l = m;
                  i = j;
                  list = list2;
               }
            }

            sortTree(list, parameterNumber, i, true);
            return new TreeBranchNode((List)list.stream().map((node) -> {
               return createNode(parameterNumber, Arrays.asList(node.subTree));
            }).collect(Collectors.toList()));
         }
      }

      private static void sortTree(List subTree, int parameterNumber, int currentParameter, boolean abs) {
         Comparator comparator = createNodeComparator(currentParameter, abs);

         for(int i = 1; i < parameterNumber; ++i) {
            comparator = comparator.thenComparing(createNodeComparator((currentParameter + i) % parameterNumber, abs));
         }

         subTree.sort(comparator);
      }

      private static Comparator createNodeComparator(int currentParameter, boolean abs) {
         return Comparator.comparingLong((treeNode) -> {
            ParameterRange parameterRange = treeNode.parameters[currentParameter];
            long l = (parameterRange.min() + parameterRange.max()) / 2L;
            return abs ? Math.abs(l) : l;
         });
      }

      private static List getBatchedTree(List nodes) {
         List list = Lists.newArrayList();
         List list2 = Lists.newArrayList();
         int i = (int)Math.pow(6.0, Math.floor(Math.log((double)nodes.size() - 0.01) / Math.log(6.0)));
         Iterator var4 = nodes.iterator();

         while(var4.hasNext()) {
            TreeNode treeNode = (TreeNode)var4.next();
            list2.add(treeNode);
            if (list2.size() >= i) {
               list.add(new TreeBranchNode(list2));
               list2 = Lists.newArrayList();
            }
         }

         if (!list2.isEmpty()) {
            list.add(new TreeBranchNode(list2));
         }

         return list;
      }

      private static long getRangeLengthSum(ParameterRange[] parameters) {
         long l = 0L;
         ParameterRange[] var3 = parameters;
         int var4 = parameters.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ParameterRange parameterRange = var3[var5];
            l += Math.abs(parameterRange.max() - parameterRange.min());
         }

         return l;
      }

      static List getEnclosingParameters(List subTree) {
         if (subTree.isEmpty()) {
            throw new IllegalArgumentException("SubTree needs at least one child");
         } else {
            int i = true;
            List list = Lists.newArrayList();

            for(int j = 0; j < 7; ++j) {
               list.add((Object)null);
            }

            Iterator var6 = subTree.iterator();

            while(var6.hasNext()) {
               TreeNode treeNode = (TreeNode)var6.next();

               for(int k = 0; k < 7; ++k) {
                  list.set(k, treeNode.parameters[k].combine((ParameterRange)list.get(k)));
               }
            }

            return list;
         }
      }

      public Object get(NoiseValuePoint point, NodeDistanceFunction distanceFunction) {
         long[] ls = point.getNoiseValueList();
         TreeLeafNode treeLeafNode = this.firstNode.getResultingNode(ls, (TreeLeafNode)this.previousResultNode.get(), distanceFunction);
         this.previousResultNode.set(treeLeafNode);
         return treeLeafNode.value;
      }

      abstract static class TreeNode {
         protected final ParameterRange[] parameters;

         protected TreeNode(List parameters) {
            this.parameters = (ParameterRange[])parameters.toArray(new ParameterRange[0]);
         }

         protected abstract TreeLeafNode getResultingNode(long[] otherParameters, @Nullable TreeLeafNode alternative, NodeDistanceFunction distanceFunction);

         protected long getSquaredDistance(long[] otherParameters) {
            long l = 0L;

            for(int i = 0; i < 7; ++i) {
               l += MathHelper.square(this.parameters[i].getDistance(otherParameters[i]));
            }

            return l;
         }

         public String toString() {
            return Arrays.toString(this.parameters);
         }
      }

      static final class TreeBranchNode extends TreeNode {
         final TreeNode[] subTree;

         protected TreeBranchNode(List list) {
            this(MultiNoiseUtil.SearchTree.getEnclosingParameters(list), list);
         }

         protected TreeBranchNode(List parameters, List subTree) {
            super(parameters);
            this.subTree = (TreeNode[])subTree.toArray(new TreeNode[0]);
         }

         protected TreeLeafNode getResultingNode(long[] otherParameters, @Nullable TreeLeafNode alternative, NodeDistanceFunction distanceFunction) {
            long l = alternative == null ? Long.MAX_VALUE : distanceFunction.getDistance(alternative, otherParameters);
            TreeLeafNode treeLeafNode = alternative;
            TreeNode[] var7 = this.subTree;
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               TreeNode treeNode = var7[var9];
               long m = distanceFunction.getDistance(treeNode, otherParameters);
               if (l > m) {
                  TreeLeafNode treeLeafNode2 = treeNode.getResultingNode(otherParameters, treeLeafNode, distanceFunction);
                  long n = treeNode == treeLeafNode2 ? m : distanceFunction.getDistance(treeLeafNode2, otherParameters);
                  if (l > n) {
                     l = n;
                     treeLeafNode = treeLeafNode2;
                  }
               }
            }

            return treeLeafNode;
         }
      }

      private static final class TreeLeafNode extends TreeNode {
         final Object value;

         TreeLeafNode(NoiseHypercube parameters, Object value) {
            super(parameters.getParameters());
            this.value = value;
         }

         protected TreeLeafNode getResultingNode(long[] otherParameters, @Nullable TreeLeafNode alternative, NodeDistanceFunction distanceFunction) {
            return this;
         }
      }
   }

   interface NodeDistanceFunction {
      long getDistance(SearchTree.TreeNode node, long[] otherParameters);
   }
}
