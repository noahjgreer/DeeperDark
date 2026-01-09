package net.minecraft.world.gen.structure;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.alias.StructurePoolAliasBinding;
import net.minecraft.structure.pool.alias.StructurePoolAliasLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public final class JigsawStructure extends Structure {
   public static final DimensionPadding DEFAULT_DIMENSION_PADDING;
   public static final StructureLiquidSettings DEFAULT_LIQUID_SETTINGS;
   public static final int MAX_SIZE = 128;
   public static final int field_49155 = 0;
   public static final int MAX_GENERATION_DEPTH = 20;
   public static final MapCodec CODEC;
   private final RegistryEntry startPool;
   private final Optional startJigsawName;
   private final int size;
   private final HeightProvider startHeight;
   private final boolean useExpansionHack;
   private final Optional projectStartToHeightmap;
   private final int maxDistanceFromCenter;
   private final List poolAliasBindings;
   private final DimensionPadding dimensionPadding;
   private final StructureLiquidSettings liquidSettings;

   private static DataResult validate(JigsawStructure structure) {
      byte var10000;
      switch (structure.getTerrainAdaptation()) {
         case NONE:
            var10000 = 0;
            break;
         case BURY:
         case BEARD_THIN:
         case BEARD_BOX:
         case ENCAPSULATE:
            var10000 = 12;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      int i = var10000;
      return structure.maxDistanceFromCenter + i > 128 ? DataResult.error(() -> {
         return "Structure size including terrain adaptation must not exceed 128";
      }) : DataResult.success(structure);
   }

   public JigsawStructure(Structure.Config config, RegistryEntry startPool, Optional startJigsawName, int size, HeightProvider startHeight, boolean useExpansionHack, Optional projectStartToHeightmap, int maxDistanceFromCenter, List poolAliasBindings, DimensionPadding dimensionPadding, StructureLiquidSettings liquidSettings) {
      super(config);
      this.startPool = startPool;
      this.startJigsawName = startJigsawName;
      this.size = size;
      this.startHeight = startHeight;
      this.useExpansionHack = useExpansionHack;
      this.projectStartToHeightmap = projectStartToHeightmap;
      this.maxDistanceFromCenter = maxDistanceFromCenter;
      this.poolAliasBindings = poolAliasBindings;
      this.dimensionPadding = dimensionPadding;
      this.liquidSettings = liquidSettings;
   }

   public JigsawStructure(Structure.Config config, RegistryEntry startPool, int size, HeightProvider startHeight, boolean useExpansionHack, Heightmap.Type projectStartToHeightmap) {
      this(config, startPool, Optional.empty(), size, startHeight, useExpansionHack, Optional.of(projectStartToHeightmap), 80, List.of(), DEFAULT_DIMENSION_PADDING, DEFAULT_LIQUID_SETTINGS);
   }

   public JigsawStructure(Structure.Config config, RegistryEntry startPool, int size, HeightProvider startHeight, boolean useExpansionHack) {
      this(config, startPool, Optional.empty(), size, startHeight, useExpansionHack, Optional.empty(), 80, List.of(), DEFAULT_DIMENSION_PADDING, DEFAULT_LIQUID_SETTINGS);
   }

   public Optional getStructurePosition(Structure.Context context) {
      ChunkPos chunkPos = context.chunkPos();
      int i = this.startHeight.get(context.random(), new HeightContext(context.chunkGenerator(), context.world()));
      BlockPos blockPos = new BlockPos(chunkPos.getStartX(), i, chunkPos.getStartZ());
      return StructurePoolBasedGenerator.generate(context, this.startPool, this.startJigsawName, this.size, blockPos, this.useExpansionHack, this.projectStartToHeightmap, this.maxDistanceFromCenter, StructurePoolAliasLookup.create(this.poolAliasBindings, blockPos, context.seed()), this.dimensionPadding, this.liquidSettings);
   }

   public StructureType getType() {
      return StructureType.JIGSAW;
   }

   @VisibleForTesting
   public RegistryEntry getStartPool() {
      return this.startPool;
   }

   @VisibleForTesting
   public List getPoolAliasBindings() {
      return this.poolAliasBindings;
   }

   static {
      DEFAULT_DIMENSION_PADDING = DimensionPadding.NONE;
      DEFAULT_LIQUID_SETTINGS = StructureLiquidSettings.APPLY_WATERLOGGING;
      CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(configCodecBuilder(instance), StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter((structure) -> {
            return structure.startPool;
         }), Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter((structure) -> {
            return structure.startJigsawName;
         }), Codec.intRange(0, 20).fieldOf("size").forGetter((structure) -> {
            return structure.size;
         }), HeightProvider.CODEC.fieldOf("start_height").forGetter((structure) -> {
            return structure.startHeight;
         }), Codec.BOOL.fieldOf("use_expansion_hack").forGetter((structure) -> {
            return structure.useExpansionHack;
         }), Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter((structure) -> {
            return structure.projectStartToHeightmap;
         }), Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter((structure) -> {
            return structure.maxDistanceFromCenter;
         }), Codec.list(StructurePoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter((structure) -> {
            return structure.poolAliasBindings;
         }), DimensionPadding.CODEC.optionalFieldOf("dimension_padding", DEFAULT_DIMENSION_PADDING).forGetter((structure) -> {
            return structure.dimensionPadding;
         }), StructureLiquidSettings.codec.optionalFieldOf("liquid_settings", DEFAULT_LIQUID_SETTINGS).forGetter((jigsawStructure) -> {
            return jigsawStructure.liquidSettings;
         })).apply(instance, JigsawStructure::new);
      }).validate(JigsawStructure::validate);
   }
}
