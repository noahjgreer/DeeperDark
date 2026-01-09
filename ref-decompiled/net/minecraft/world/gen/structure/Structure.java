package net.minecraft.world.gen.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.Finishable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;

public abstract class Structure {
   public static final Codec STRUCTURE_CODEC;
   public static final Codec ENTRY_CODEC;
   protected final Config config;

   public static RecordCodecBuilder configCodecBuilder(RecordCodecBuilder.Instance instance) {
      return Structure.Config.CODEC.forGetter((feature) -> {
         return feature.config;
      });
   }

   public static MapCodec createCodec(Function featureCreator) {
      return RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(configCodecBuilder(instance)).apply(instance, featureCreator);
      });
   }

   protected Structure(Config config) {
      this.config = config;
   }

   public RegistryEntryList getValidBiomes() {
      return this.config.biomes;
   }

   public Map getStructureSpawns() {
      return this.config.spawnOverrides;
   }

   public GenerationStep.Feature getFeatureGenerationStep() {
      return this.config.step;
   }

   public StructureTerrainAdaptation getTerrainAdaptation() {
      return this.config.terrainAdaptation;
   }

   public BlockBox expandBoxIfShouldAdaptNoise(BlockBox box) {
      return this.getTerrainAdaptation() != StructureTerrainAdaptation.NONE ? box.expand(12) : box;
   }

   public StructureStart createStructureStart(RegistryEntry structure, RegistryKey dimension, DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, BiomeSource biomeSource, NoiseConfig noiseConfig, StructureTemplateManager structureTemplateManager, long seed, ChunkPos chunkPos, int references, HeightLimitView world, Predicate validBiomes) {
      Finishable finishable = FlightProfiler.INSTANCE.startStructureGenerationProfiling(chunkPos, dimension, structure);
      Context context = new Context(dynamicRegistryManager, chunkGenerator, biomeSource, noiseConfig, structureTemplateManager, seed, chunkPos, world, validBiomes);
      Optional optional = this.getValidStructurePosition(context);
      if (optional.isPresent()) {
         StructurePiecesCollector structurePiecesCollector = ((StructurePosition)optional.get()).generate();
         StructureStart structureStart = new StructureStart(this, chunkPos, references, structurePiecesCollector.toList());
         if (structureStart.hasChildren()) {
            if (finishable != null) {
               finishable.finish(true);
            }

            return structureStart;
         }
      }

      if (finishable != null) {
         finishable.finish(false);
      }

      return StructureStart.DEFAULT;
   }

   protected static Optional getStructurePosition(Context context, Heightmap.Type heightmap, Consumer generator) {
      ChunkPos chunkPos = context.chunkPos();
      int i = chunkPos.getCenterX();
      int j = chunkPos.getCenterZ();
      int k = context.chunkGenerator().getHeightInGround(i, j, heightmap, context.world(), context.noiseConfig());
      return Optional.of(new StructurePosition(new BlockPos(i, k, j), generator));
   }

   private static boolean isBiomeValid(StructurePosition result, Context context) {
      BlockPos blockPos = result.position();
      return context.biomePredicate.test(context.chunkGenerator.getBiomeSource().getBiome(BiomeCoords.fromBlock(blockPos.getX()), BiomeCoords.fromBlock(blockPos.getY()), BiomeCoords.fromBlock(blockPos.getZ()), context.noiseConfig.getMultiNoiseSampler()));
   }

   public void postPlace(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox box, ChunkPos chunkPos, StructurePiecesList pieces) {
   }

   private static int[] getCornerHeights(Context context, int x, int width, int z, int height) {
      ChunkGenerator chunkGenerator = context.chunkGenerator();
      HeightLimitView heightLimitView = context.world();
      NoiseConfig noiseConfig = context.noiseConfig();
      return new int[]{chunkGenerator.getHeightInGround(x, z, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig), chunkGenerator.getHeightInGround(x, z + height, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig), chunkGenerator.getHeightInGround(x + width, z, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig), chunkGenerator.getHeightInGround(x + width, z + height, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig)};
   }

   public static int getAverageCornerHeights(Context context, int x, int width, int z, int height) {
      int[] is = getCornerHeights(context, x, width, z, height);
      return (is[0] + is[1] + is[2] + is[3]) / 4;
   }

   protected static int getMinCornerHeight(Context context, int width, int height) {
      ChunkPos chunkPos = context.chunkPos();
      int i = chunkPos.getStartX();
      int j = chunkPos.getStartZ();
      return getMinCornerHeight(context, i, j, width, height);
   }

   protected static int getMinCornerHeight(Context context, int x, int z, int width, int height) {
      int[] is = getCornerHeights(context, x, width, z, height);
      return Math.min(Math.min(is[0], is[1]), Math.min(is[2], is[3]));
   }

   /** @deprecated */
   @Deprecated
   protected BlockPos getShiftedPos(Context context, BlockRotation rotation) {
      int i = 5;
      int j = 5;
      if (rotation == BlockRotation.CLOCKWISE_90) {
         i = -5;
      } else if (rotation == BlockRotation.CLOCKWISE_180) {
         i = -5;
         j = -5;
      } else if (rotation == BlockRotation.COUNTERCLOCKWISE_90) {
         j = -5;
      }

      ChunkPos chunkPos = context.chunkPos();
      int k = chunkPos.getOffsetX(7);
      int l = chunkPos.getOffsetZ(7);
      return new BlockPos(k, getMinCornerHeight(context, k, l, i, j), l);
   }

   protected abstract Optional getStructurePosition(Context context);

   public Optional getValidStructurePosition(Context context) {
      return this.getStructurePosition(context).filter((position) -> {
         return isBiomeValid(position, context);
      });
   }

   public abstract StructureType getType();

   static {
      STRUCTURE_CODEC = Registries.STRUCTURE_TYPE.getCodec().dispatch(Structure::getType, StructureType::codec);
      ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.STRUCTURE, STRUCTURE_CODEC);
   }

   public static record Config(RegistryEntryList biomes, Map spawnOverrides, GenerationStep.Feature step, StructureTerrainAdaptation terrainAdaptation) {
      final RegistryEntryList biomes;
      final Map spawnOverrides;
      final GenerationStep.Feature step;
      final StructureTerrainAdaptation terrainAdaptation;
      static final Config DEFAULT;
      public static final MapCodec CODEC;

      public Config(RegistryEntryList biomes) {
         this(biomes, DEFAULT.spawnOverrides, DEFAULT.step, DEFAULT.terrainAdaptation);
      }

      public Config(RegistryEntryList registryEntryList, Map map, GenerationStep.Feature feature, StructureTerrainAdaptation structureTerrainAdaptation) {
         this.biomes = registryEntryList;
         this.spawnOverrides = map;
         this.step = feature;
         this.terrainAdaptation = structureTerrainAdaptation;
      }

      public RegistryEntryList biomes() {
         return this.biomes;
      }

      public Map spawnOverrides() {
         return this.spawnOverrides;
      }

      public GenerationStep.Feature step() {
         return this.step;
      }

      public StructureTerrainAdaptation terrainAdaptation() {
         return this.terrainAdaptation;
      }

      static {
         DEFAULT = new Config(RegistryEntryList.of(), Map.of(), GenerationStep.Feature.SURFACE_STRUCTURES, StructureTerrainAdaptation.NONE);
         CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(RegistryCodecs.entryList(RegistryKeys.BIOME).fieldOf("biomes").forGetter(Config::biomes), Codec.simpleMap(SpawnGroup.CODEC, StructureSpawns.CODEC, StringIdentifiable.toKeyable(SpawnGroup.values())).fieldOf("spawn_overrides").forGetter(Config::spawnOverrides), GenerationStep.Feature.CODEC.fieldOf("step").forGetter(Config::step), StructureTerrainAdaptation.CODEC.optionalFieldOf("terrain_adaptation", DEFAULT.terrainAdaptation).forGetter(Config::terrainAdaptation)).apply(instance, Config::new);
         });
      }

      public static class Builder {
         private final RegistryEntryList biomes;
         private Map spawnOverrides;
         private GenerationStep.Feature step;
         private StructureTerrainAdaptation terrainAdaptation;

         public Builder(RegistryEntryList biomes) {
            this.spawnOverrides = Structure.Config.DEFAULT.spawnOverrides;
            this.step = Structure.Config.DEFAULT.step;
            this.terrainAdaptation = Structure.Config.DEFAULT.terrainAdaptation;
            this.biomes = biomes;
         }

         public Builder spawnOverrides(Map spawnOverrides) {
            this.spawnOverrides = spawnOverrides;
            return this;
         }

         public Builder step(GenerationStep.Feature step) {
            this.step = step;
            return this;
         }

         public Builder terrainAdaptation(StructureTerrainAdaptation terrainAdaptation) {
            this.terrainAdaptation = terrainAdaptation;
            return this;
         }

         public Config build() {
            return new Config(this.biomes, this.spawnOverrides, this.step, this.terrainAdaptation);
         }
      }
   }

   public static record Context(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, BiomeSource biomeSource, NoiseConfig noiseConfig, StructureTemplateManager structureTemplateManager, ChunkRandom random, long seed, ChunkPos chunkPos, HeightLimitView world, Predicate biomePredicate) {
      final ChunkGenerator chunkGenerator;
      final NoiseConfig noiseConfig;
      final Predicate biomePredicate;

      public Context(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, BiomeSource biomeSource, NoiseConfig noiseConfig, StructureTemplateManager structureTemplateManager, long seed, ChunkPos chunkPos, HeightLimitView world, Predicate biomePredicate) {
         this(dynamicRegistryManager, chunkGenerator, biomeSource, noiseConfig, structureTemplateManager, createChunkRandom(seed, chunkPos), seed, chunkPos, world, biomePredicate);
      }

      public Context(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, BiomeSource biomeSource, NoiseConfig noiseConfig, StructureTemplateManager structureTemplateManager, ChunkRandom chunkRandom, long l, ChunkPos chunkPos, HeightLimitView heightLimitView, Predicate predicate) {
         this.dynamicRegistryManager = dynamicRegistryManager;
         this.chunkGenerator = chunkGenerator;
         this.biomeSource = biomeSource;
         this.noiseConfig = noiseConfig;
         this.structureTemplateManager = structureTemplateManager;
         this.random = chunkRandom;
         this.seed = l;
         this.chunkPos = chunkPos;
         this.world = heightLimitView;
         this.biomePredicate = predicate;
      }

      private static ChunkRandom createChunkRandom(long seed, ChunkPos chunkPos) {
         ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
         chunkRandom.setCarverSeed(seed, chunkPos.x, chunkPos.z);
         return chunkRandom;
      }

      public DynamicRegistryManager dynamicRegistryManager() {
         return this.dynamicRegistryManager;
      }

      public ChunkGenerator chunkGenerator() {
         return this.chunkGenerator;
      }

      public BiomeSource biomeSource() {
         return this.biomeSource;
      }

      public NoiseConfig noiseConfig() {
         return this.noiseConfig;
      }

      public StructureTemplateManager structureTemplateManager() {
         return this.structureTemplateManager;
      }

      public ChunkRandom random() {
         return this.random;
      }

      public long seed() {
         return this.seed;
      }

      public ChunkPos chunkPos() {
         return this.chunkPos;
      }

      public HeightLimitView world() {
         return this.world;
      }

      public Predicate biomePredicate() {
         return this.biomePredicate;
      }
   }

   public static record StructurePosition(BlockPos position, Either generator) {
      public StructurePosition(BlockPos pos, Consumer generator) {
         this(pos, Either.left(generator));
      }

      public StructurePosition(BlockPos blockPos, Either either) {
         this.position = blockPos;
         this.generator = either;
      }

      public StructurePiecesCollector generate() {
         return (StructurePiecesCollector)this.generator.map((generator) -> {
            StructurePiecesCollector structurePiecesCollector = new StructurePiecesCollector();
            generator.accept(structurePiecesCollector);
            return structurePiecesCollector;
         }, (collector) -> {
            return collector;
         });
      }

      public BlockPos position() {
         return this.position;
      }

      public Either generator() {
         return this.generator;
      }
   }
}
