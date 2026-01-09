package net.minecraft.world.biome.source;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import org.jetbrains.annotations.Nullable;

public abstract class BiomeSource implements BiomeSupplier {
   public static final Codec CODEC;
   private final Supplier biomes = Suppliers.memoize(() -> {
      return (Set)this.biomeStream().distinct().collect(ImmutableSet.toImmutableSet());
   });

   protected BiomeSource() {
   }

   protected abstract MapCodec getCodec();

   protected abstract Stream biomeStream();

   public Set getBiomes() {
      return (Set)this.biomes.get();
   }

   public Set getBiomesInArea(int x, int y, int z, int radius, MultiNoiseUtil.MultiNoiseSampler sampler) {
      int i = BiomeCoords.fromBlock(x - radius);
      int j = BiomeCoords.fromBlock(y - radius);
      int k = BiomeCoords.fromBlock(z - radius);
      int l = BiomeCoords.fromBlock(x + radius);
      int m = BiomeCoords.fromBlock(y + radius);
      int n = BiomeCoords.fromBlock(z + radius);
      int o = l - i + 1;
      int p = m - j + 1;
      int q = n - k + 1;
      Set set = Sets.newHashSet();

      for(int r = 0; r < q; ++r) {
         for(int s = 0; s < o; ++s) {
            for(int t = 0; t < p; ++t) {
               int u = i + s;
               int v = j + t;
               int w = k + r;
               set.add(this.getBiome(u, v, w, sampler));
            }
         }
      }

      return set;
   }

   @Nullable
   public Pair locateBiome(int x, int y, int z, int radius, Predicate predicate, Random random, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
      return this.locateBiome(x, y, z, radius, 1, predicate, random, false, noiseSampler);
   }

   @Nullable
   public Pair locateBiome(BlockPos origin, int radius, int horizontalBlockCheckInterval, int verticalBlockCheckInterval, Predicate predicate, MultiNoiseUtil.MultiNoiseSampler noiseSampler, WorldView world) {
      Set set = (Set)this.getBiomes().stream().filter(predicate).collect(Collectors.toUnmodifiableSet());
      if (set.isEmpty()) {
         return null;
      } else {
         int i = Math.floorDiv(radius, horizontalBlockCheckInterval);
         int[] is = MathHelper.stream(origin.getY(), world.getBottomY() + 1, world.getTopYInclusive() + 1, verticalBlockCheckInterval).toArray();
         Iterator var11 = BlockPos.iterateInSquare(BlockPos.ORIGIN, i, Direction.EAST, Direction.SOUTH).iterator();

         while(var11.hasNext()) {
            BlockPos.Mutable mutable = (BlockPos.Mutable)var11.next();
            int j = origin.getX() + mutable.getX() * horizontalBlockCheckInterval;
            int k = origin.getZ() + mutable.getZ() * horizontalBlockCheckInterval;
            int l = BiomeCoords.fromBlock(j);
            int m = BiomeCoords.fromBlock(k);
            int[] var17 = is;
            int var18 = is.length;

            for(int var19 = 0; var19 < var18; ++var19) {
               int n = var17[var19];
               int o = BiomeCoords.fromBlock(n);
               RegistryEntry registryEntry = this.getBiome(l, o, m, noiseSampler);
               if (set.contains(registryEntry)) {
                  return Pair.of(new BlockPos(j, n, k), registryEntry);
               }
            }
         }

         return null;
      }
   }

   @Nullable
   public Pair locateBiome(int x, int y, int z, int radius, int blockCheckInterval, Predicate predicate, Random random, boolean bl, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
      int i = BiomeCoords.fromBlock(x);
      int j = BiomeCoords.fromBlock(z);
      int k = BiomeCoords.fromBlock(radius);
      int l = BiomeCoords.fromBlock(y);
      Pair pair = null;
      int m = 0;
      int n = bl ? 0 : k;

      for(int o = n; o <= k; o += blockCheckInterval) {
         for(int p = SharedConstants.DEBUG_BIOME_SOURCE ? 0 : -o; p <= o; p += blockCheckInterval) {
            boolean bl2 = Math.abs(p) == o;

            for(int q = -o; q <= o; q += blockCheckInterval) {
               if (bl) {
                  boolean bl3 = Math.abs(q) == o;
                  if (!bl3 && !bl2) {
                     continue;
                  }
               }

               int r = i + q;
               int s = j + p;
               RegistryEntry registryEntry = this.getBiome(r, l, s, noiseSampler);
               if (predicate.test(registryEntry)) {
                  if (pair == null || random.nextInt(m + 1) == 0) {
                     BlockPos blockPos = new BlockPos(BiomeCoords.toBlock(r), y, BiomeCoords.toBlock(s));
                     if (bl) {
                        return Pair.of(blockPos, registryEntry);
                     }

                     pair = Pair.of(blockPos, registryEntry);
                  }

                  ++m;
               }
            }
         }
      }

      return pair;
   }

   public abstract RegistryEntry getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise);

   public void addDebugInfo(List info, BlockPos pos, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
   }

   static {
      CODEC = Registries.BIOME_SOURCE.getCodec().dispatchStable(BiomeSource::getCodec, Function.identity());
   }
}
