package net.minecraft.world.gen.chunk;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.noise.NoiseConfig;

public class FlatChunkGenerator extends ChunkGenerator {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(FlatChunkGeneratorConfig.CODEC.fieldOf("settings").forGetter(FlatChunkGenerator::getConfig)).apply(instance, instance.stable(FlatChunkGenerator::new));
   });
   private final FlatChunkGeneratorConfig config;

   public FlatChunkGenerator(FlatChunkGeneratorConfig config) {
      FixedBiomeSource var10001 = new FixedBiomeSource(config.getBiome());
      Objects.requireNonNull(config);
      super(var10001, Util.memoize(config::createGenerationSettings));
      this.config = config;
   }

   public StructurePlacementCalculator createStructurePlacementCalculator(RegistryWrapper structureSetRegistry, NoiseConfig noiseConfig, long seed) {
      Stream stream = (Stream)this.config.getStructureOverrides().map(RegistryEntryList::stream).orElseGet(() -> {
         return structureSetRegistry.streamEntries().map((structureEntry) -> {
            return structureEntry;
         });
      });
      return StructurePlacementCalculator.create(noiseConfig, seed, this.biomeSource, stream);
   }

   protected MapCodec getCodec() {
      return CODEC;
   }

   public FlatChunkGeneratorConfig getConfig() {
      return this.config;
   }

   public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
   }

   public int getSpawnHeight(HeightLimitView world) {
      return world.getBottomY() + Math.min(world.getHeight(), this.config.getLayerBlocks().size());
   }

   public CompletableFuture populateNoise(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
      List list = this.config.getLayerBlocks();
      BlockPos.Mutable mutable = new BlockPos.Mutable();
      Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
      Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

      for(int i = 0; i < Math.min(chunk.getHeight(), list.size()); ++i) {
         BlockState blockState = (BlockState)list.get(i);
         if (blockState != null) {
            int j = chunk.getBottomY() + i;

            for(int k = 0; k < 16; ++k) {
               for(int l = 0; l < 16; ++l) {
                  chunk.setBlockState(mutable.set(k, j, l), blockState);
                  heightmap.trackUpdate(k, j, l, blockState);
                  heightmap2.trackUpdate(k, j, l, blockState);
               }
            }
         }
      }

      return CompletableFuture.completedFuture(chunk);
   }

   public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
      List list = this.config.getLayerBlocks();

      for(int i = Math.min(list.size() - 1, world.getTopYInclusive()); i >= 0; --i) {
         BlockState blockState = (BlockState)list.get(i);
         if (blockState != null && heightmap.getBlockPredicate().test(blockState)) {
            return world.getBottomY() + i + 1;
         }
      }

      return world.getBottomY();
   }

   public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
      return new VerticalBlockSample(world.getBottomY(), (BlockState[])this.config.getLayerBlocks().stream().limit((long)world.getHeight()).map((state) -> {
         return state == null ? Blocks.AIR.getDefaultState() : state;
      }).toArray((i) -> {
         return new BlockState[i];
      }));
   }

   public void appendDebugHudText(List text, NoiseConfig noiseConfig, BlockPos pos) {
   }

   public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk) {
   }

   public void populateEntities(ChunkRegion region) {
   }

   public int getMinimumY() {
      return 0;
   }

   public int getWorldHeight() {
      return 384;
   }

   public int getSeaLevel() {
      return -63;
   }
}
