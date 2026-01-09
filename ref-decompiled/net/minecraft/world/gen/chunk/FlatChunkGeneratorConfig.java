package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.StructureSetKeys;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.MiscPlacedFeatures;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import org.slf4j.Logger;

public class FlatChunkGeneratorConfig {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(RegistryCodecs.entryList(RegistryKeys.STRUCTURE_SET).lenientOptionalFieldOf("structure_overrides").forGetter((config) -> {
         return config.structureOverrides;
      }), FlatChunkGeneratorLayer.CODEC.listOf().fieldOf("layers").forGetter(FlatChunkGeneratorConfig::getLayers), Codec.BOOL.fieldOf("lakes").orElse(false).forGetter((config) -> {
         return config.hasLakes;
      }), Codec.BOOL.fieldOf("features").orElse(false).forGetter((config) -> {
         return config.hasFeatures;
      }), Biome.REGISTRY_CODEC.lenientOptionalFieldOf("biome").orElseGet(Optional::empty).forGetter((config) -> {
         return Optional.of(config.biome);
      }), RegistryOps.getEntryCodec(BiomeKeys.PLAINS), RegistryOps.getEntryCodec(MiscPlacedFeatures.LAKE_LAVA_UNDERGROUND), RegistryOps.getEntryCodec(MiscPlacedFeatures.LAKE_LAVA_SURFACE)).apply(instance, FlatChunkGeneratorConfig::new);
   }).comapFlatMap(FlatChunkGeneratorConfig::checkHeight, Function.identity()).stable();
   private final Optional structureOverrides;
   private final List layers;
   private final RegistryEntry biome;
   private final List layerBlocks;
   private boolean hasNoTerrain;
   private boolean hasFeatures;
   private boolean hasLakes;
   private final List features;

   private static DataResult checkHeight(FlatChunkGeneratorConfig config) {
      int i = config.layers.stream().mapToInt(FlatChunkGeneratorLayer::getThickness).sum();
      return i > DimensionType.MAX_HEIGHT ? DataResult.error(() -> {
         return "Sum of layer heights is > " + DimensionType.MAX_HEIGHT;
      }, config) : DataResult.success(config);
   }

   private FlatChunkGeneratorConfig(Optional structureOverrides, List layers, boolean lakes, boolean features, Optional biome, RegistryEntry.Reference fallback, RegistryEntry undergroundLavaLakeFeature, RegistryEntry surfaceLavaLakeFeature) {
      this(structureOverrides, getBiome(biome, fallback), List.of(undergroundLavaLakeFeature, surfaceLavaLakeFeature));
      if (lakes) {
         this.enableLakes();
      }

      if (features) {
         this.enableFeatures();
      }

      this.layers.addAll(layers);
      this.updateLayerBlocks();
   }

   private static RegistryEntry getBiome(Optional biome, RegistryEntry fallback) {
      if (biome.isEmpty()) {
         LOGGER.error("Unknown biome, defaulting to plains");
         return fallback;
      } else {
         return (RegistryEntry)biome.get();
      }
   }

   public FlatChunkGeneratorConfig(Optional structureOverrides, RegistryEntry biome, List features) {
      this.layers = Lists.newArrayList();
      this.structureOverrides = structureOverrides;
      this.biome = biome;
      this.layerBlocks = Lists.newArrayList();
      this.features = features;
   }

   public FlatChunkGeneratorConfig with(List layers, Optional structureOverrides, RegistryEntry biome) {
      FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(structureOverrides, biome, this.features);
      Iterator var5 = layers.iterator();

      while(var5.hasNext()) {
         FlatChunkGeneratorLayer flatChunkGeneratorLayer = (FlatChunkGeneratorLayer)var5.next();
         flatChunkGeneratorConfig.layers.add(new FlatChunkGeneratorLayer(flatChunkGeneratorLayer.getThickness(), flatChunkGeneratorLayer.getBlockState().getBlock()));
         flatChunkGeneratorConfig.updateLayerBlocks();
      }

      if (this.hasFeatures) {
         flatChunkGeneratorConfig.enableFeatures();
      }

      if (this.hasLakes) {
         flatChunkGeneratorConfig.enableLakes();
      }

      return flatChunkGeneratorConfig;
   }

   public void enableFeatures() {
      this.hasFeatures = true;
   }

   public void enableLakes() {
      this.hasLakes = true;
   }

   public GenerationSettings createGenerationSettings(RegistryEntry biomeEntry) {
      if (!biomeEntry.equals(this.biome)) {
         return ((Biome)biomeEntry.value()).getGenerationSettings();
      } else {
         GenerationSettings generationSettings = ((Biome)this.getBiome().value()).getGenerationSettings();
         GenerationSettings.Builder builder = new GenerationSettings.Builder();
         if (this.hasLakes) {
            Iterator var4 = this.features.iterator();

            while(var4.hasNext()) {
               RegistryEntry registryEntry = (RegistryEntry)var4.next();
               builder.feature(GenerationStep.Feature.LAKES, registryEntry);
            }
         }

         boolean bl = (!this.hasNoTerrain || biomeEntry.matchesKey(BiomeKeys.THE_VOID)) && this.hasFeatures;
         int i;
         List list;
         if (bl) {
            list = generationSettings.getFeatures();

            for(i = 0; i < list.size(); ++i) {
               if (i != GenerationStep.Feature.UNDERGROUND_STRUCTURES.ordinal() && i != GenerationStep.Feature.SURFACE_STRUCTURES.ordinal() && (!this.hasLakes || i != GenerationStep.Feature.LAKES.ordinal())) {
                  RegistryEntryList registryEntryList = (RegistryEntryList)list.get(i);
                  Iterator var8 = registryEntryList.iterator();

                  while(var8.hasNext()) {
                     RegistryEntry registryEntry2 = (RegistryEntry)var8.next();
                     builder.addFeature(i, registryEntry2);
                  }
               }
            }
         }

         list = this.getLayerBlocks();

         for(i = 0; i < list.size(); ++i) {
            BlockState blockState = (BlockState)list.get(i);
            if (!Heightmap.Type.MOTION_BLOCKING.getBlockPredicate().test(blockState)) {
               list.set(i, (Object)null);
               builder.feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, PlacedFeatures.createEntry(Feature.FILL_LAYER, new FillLayerFeatureConfig(i, blockState), (PlacementModifier[])()));
            }
         }

         return builder.build();
      }
   }

   public Optional getStructureOverrides() {
      return this.structureOverrides;
   }

   public RegistryEntry getBiome() {
      return this.biome;
   }

   public List getLayers() {
      return this.layers;
   }

   public List getLayerBlocks() {
      return this.layerBlocks;
   }

   public void updateLayerBlocks() {
      this.layerBlocks.clear();
      Iterator var1 = this.layers.iterator();

      while(var1.hasNext()) {
         FlatChunkGeneratorLayer flatChunkGeneratorLayer = (FlatChunkGeneratorLayer)var1.next();

         for(int i = 0; i < flatChunkGeneratorLayer.getThickness(); ++i) {
            this.layerBlocks.add(flatChunkGeneratorLayer.getBlockState());
         }
      }

      this.hasNoTerrain = this.layerBlocks.stream().allMatch((state) -> {
         return state.isOf(Blocks.AIR);
      });
   }

   public static FlatChunkGeneratorConfig getDefaultConfig(RegistryEntryLookup biomeLookup, RegistryEntryLookup structureSetLookup, RegistryEntryLookup featureLookup) {
      RegistryEntryList registryEntryList = RegistryEntryList.of(structureSetLookup.getOrThrow(StructureSetKeys.STRONGHOLDS), structureSetLookup.getOrThrow(StructureSetKeys.VILLAGES));
      FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(Optional.of(registryEntryList), getPlains(biomeLookup), getLavaLakes(featureLookup));
      flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
      flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(2, Blocks.DIRT));
      flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK));
      flatChunkGeneratorConfig.updateLayerBlocks();
      return flatChunkGeneratorConfig;
   }

   public static RegistryEntry getPlains(RegistryEntryLookup biomeLookup) {
      return biomeLookup.getOrThrow(BiomeKeys.PLAINS);
   }

   public static List getLavaLakes(RegistryEntryLookup featureLookup) {
      return List.of(featureLookup.getOrThrow(MiscPlacedFeatures.LAKE_LAVA_UNDERGROUND), featureLookup.getOrThrow(MiscPlacedFeatures.LAKE_LAVA_SURFACE));
   }
}
