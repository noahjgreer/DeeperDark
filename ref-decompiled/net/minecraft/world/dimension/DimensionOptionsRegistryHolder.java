package net.minecraft.world.dimension;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.level.LevelProperties;

public record DimensionOptionsRegistryHolder(Map dimensions) {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.unboundedMap(RegistryKey.createCodec(RegistryKeys.DIMENSION), DimensionOptions.CODEC).fieldOf("dimensions").forGetter(DimensionOptionsRegistryHolder::dimensions)).apply(instance, instance.stable(DimensionOptionsRegistryHolder::new));
   });
   private static final Set VANILLA_KEYS;
   private static final int VANILLA_KEY_COUNT;

   public DimensionOptionsRegistryHolder(Map map) {
      DimensionOptions dimensionOptions = (DimensionOptions)map.get(DimensionOptions.OVERWORLD);
      if (dimensionOptions == null) {
         throw new IllegalStateException("Overworld settings missing");
      } else {
         this.dimensions = map;
      }
   }

   public DimensionOptionsRegistryHolder(Registry dimensionOptionsRegistry) {
      this((Map)dimensionOptionsRegistry.streamEntries().collect(Collectors.toMap(RegistryEntry.Reference::registryKey, RegistryEntry.Reference::value)));
   }

   public static Stream streamAll(Stream otherKeys) {
      return Stream.concat(VANILLA_KEYS.stream(), otherKeys.filter((key) -> {
         return !VANILLA_KEYS.contains(key);
      }));
   }

   public DimensionOptionsRegistryHolder with(RegistryWrapper.WrapperLookup registries, ChunkGenerator chunkGenerator) {
      RegistryWrapper registryWrapper = registries.getOrThrow(RegistryKeys.DIMENSION_TYPE);
      Map map = createRegistry((RegistryWrapper)registryWrapper, (Map)this.dimensions, chunkGenerator);
      return new DimensionOptionsRegistryHolder(map);
   }

   public static Map createRegistry(RegistryWrapper dimensionTypeRegistry, Map dimensionOptions, ChunkGenerator chunkGenerator) {
      DimensionOptions dimensionOptions2 = (DimensionOptions)dimensionOptions.get(DimensionOptions.OVERWORLD);
      RegistryEntry registryEntry = dimensionOptions2 == null ? dimensionTypeRegistry.getOrThrow(DimensionTypes.OVERWORLD) : dimensionOptions2.dimensionTypeEntry();
      return createRegistry((Map)dimensionOptions, (RegistryEntry)registryEntry, chunkGenerator);
   }

   public static Map createRegistry(Map dimensionOptions, RegistryEntry overworld, ChunkGenerator chunkGenerator) {
      ImmutableMap.Builder builder = ImmutableMap.builder();
      builder.putAll(dimensionOptions);
      builder.put(DimensionOptions.OVERWORLD, new DimensionOptions(overworld, chunkGenerator));
      return builder.buildKeepingLast();
   }

   public ChunkGenerator getChunkGenerator() {
      DimensionOptions dimensionOptions = (DimensionOptions)this.dimensions.get(DimensionOptions.OVERWORLD);
      if (dimensionOptions == null) {
         throw new IllegalStateException("Overworld settings missing");
      } else {
         return dimensionOptions.chunkGenerator();
      }
   }

   public Optional getOrEmpty(RegistryKey key) {
      return Optional.ofNullable((DimensionOptions)this.dimensions.get(key));
   }

   public ImmutableSet getWorldKeys() {
      return (ImmutableSet)this.dimensions().keySet().stream().map(RegistryKeys::toWorldKey).collect(ImmutableSet.toImmutableSet());
   }

   public boolean isDebug() {
      return this.getChunkGenerator() instanceof DebugChunkGenerator;
   }

   private static LevelProperties.SpecialProperty getSpecialProperty(Registry dimensionOptionsRegistry) {
      return (LevelProperties.SpecialProperty)dimensionOptionsRegistry.getOptionalValue(DimensionOptions.OVERWORLD).map((overworldEntry) -> {
         ChunkGenerator chunkGenerator = overworldEntry.chunkGenerator();
         if (chunkGenerator instanceof DebugChunkGenerator) {
            return LevelProperties.SpecialProperty.DEBUG;
         } else {
            return chunkGenerator instanceof FlatChunkGenerator ? LevelProperties.SpecialProperty.FLAT : LevelProperties.SpecialProperty.NONE;
         }
      }).orElse(LevelProperties.SpecialProperty.NONE);
   }

   static Lifecycle getLifecycle(RegistryKey key, DimensionOptions dimensionOptions) {
      return isVanilla(key, dimensionOptions) ? Lifecycle.stable() : Lifecycle.experimental();
   }

   private static boolean isVanilla(RegistryKey key, DimensionOptions dimensionOptions) {
      if (key == DimensionOptions.OVERWORLD) {
         return isOverworldVanilla(dimensionOptions);
      } else if (key == DimensionOptions.NETHER) {
         return isNetherVanilla(dimensionOptions);
      } else {
         return key == DimensionOptions.END ? isTheEndVanilla(dimensionOptions) : false;
      }
   }

   private static boolean isOverworldVanilla(DimensionOptions dimensionOptions) {
      RegistryEntry registryEntry = dimensionOptions.dimensionTypeEntry();
      if (!registryEntry.matchesKey(DimensionTypes.OVERWORLD) && !registryEntry.matchesKey(DimensionTypes.OVERWORLD_CAVES)) {
         return false;
      } else {
         BiomeSource var3 = dimensionOptions.chunkGenerator().getBiomeSource();
         if (var3 instanceof MultiNoiseBiomeSource) {
            MultiNoiseBiomeSource multiNoiseBiomeSource = (MultiNoiseBiomeSource)var3;
            if (!multiNoiseBiomeSource.matchesInstance(MultiNoiseBiomeSourceParameterLists.OVERWORLD)) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean isNetherVanilla(DimensionOptions dimensionOptions) {
      boolean var10000;
      if (dimensionOptions.dimensionTypeEntry().matchesKey(DimensionTypes.THE_NETHER)) {
         ChunkGenerator var3 = dimensionOptions.chunkGenerator();
         if (var3 instanceof NoiseChunkGenerator) {
            NoiseChunkGenerator noiseChunkGenerator = (NoiseChunkGenerator)var3;
            if (noiseChunkGenerator.matchesSettings(ChunkGeneratorSettings.NETHER)) {
               BiomeSource var4 = noiseChunkGenerator.getBiomeSource();
               if (var4 instanceof MultiNoiseBiomeSource) {
                  MultiNoiseBiomeSource multiNoiseBiomeSource = (MultiNoiseBiomeSource)var4;
                  if (multiNoiseBiomeSource.matchesInstance(MultiNoiseBiomeSourceParameterLists.NETHER)) {
                     var10000 = true;
                     return var10000;
                  }
               }
            }
         }
      }

      var10000 = false;
      return var10000;
   }

   private static boolean isTheEndVanilla(DimensionOptions dimensionOptions) {
      boolean var10000;
      if (dimensionOptions.dimensionTypeEntry().matchesKey(DimensionTypes.THE_END)) {
         ChunkGenerator var2 = dimensionOptions.chunkGenerator();
         if (var2 instanceof NoiseChunkGenerator) {
            NoiseChunkGenerator noiseChunkGenerator = (NoiseChunkGenerator)var2;
            if (noiseChunkGenerator.matchesSettings(ChunkGeneratorSettings.END) && noiseChunkGenerator.getBiomeSource() instanceof TheEndBiomeSource) {
               var10000 = true;
               return var10000;
            }
         }
      }

      var10000 = false;
      return var10000;
   }

   public DimensionsConfig toConfig(Registry existingRegistry) {
      Stream stream = Stream.concat(existingRegistry.getKeys().stream(), this.dimensions.keySet().stream()).distinct();
      List list = new ArrayList();
      streamAll(stream).forEach((key) -> {
         existingRegistry.getOptionalValue(key).or(() -> {
            return Optional.ofNullable((DimensionOptions)this.dimensions.get(key));
         }).ifPresent((dimensionOptions) -> {
            record Entry(RegistryKey key, DimensionOptions value) {
               final RegistryKey key;
               final DimensionOptions value;

               Entry(RegistryKey registryKey, DimensionOptions dimensionOptions) {
                  this.key = registryKey;
                  this.value = dimensionOptions;
               }

               RegistryEntryInfo toEntryInfo() {
                  return new RegistryEntryInfo(Optional.empty(), DimensionOptionsRegistryHolder.getLifecycle(this.key, this.value));
               }

               public RegistryKey key() {
                  return this.key;
               }

               public DimensionOptions value() {
                  return this.value;
               }
            }

            list.add(new Entry(key, dimensionOptions));
         });
      });
      Lifecycle lifecycle = list.size() == VANILLA_KEY_COUNT ? Lifecycle.stable() : Lifecycle.experimental();
      MutableRegistry mutableRegistry = new SimpleRegistry(RegistryKeys.DIMENSION, lifecycle);
      list.forEach((entry) -> {
         mutableRegistry.add(entry.key, entry.value, entry.toEntryInfo());
      });
      Registry registry = mutableRegistry.freeze();
      LevelProperties.SpecialProperty specialProperty = getSpecialProperty(registry);
      return new DimensionsConfig(registry.freeze(), specialProperty);
   }

   public Map dimensions() {
      return this.dimensions;
   }

   static {
      VANILLA_KEYS = ImmutableSet.of(DimensionOptions.OVERWORLD, DimensionOptions.NETHER, DimensionOptions.END);
      VANILLA_KEY_COUNT = VANILLA_KEYS.size();
   }

   public static record DimensionsConfig(Registry dimensions, LevelProperties.SpecialProperty specialWorldProperty) {
      public DimensionsConfig(Registry registry, LevelProperties.SpecialProperty specialProperty) {
         this.dimensions = registry;
         this.specialWorldProperty = specialProperty;
      }

      public Lifecycle getLifecycle() {
         return this.dimensions.getLifecycle();
      }

      public DynamicRegistryManager.Immutable toDynamicRegistryManager() {
         return (new DynamicRegistryManager.ImmutableImpl(List.of(this.dimensions))).toImmutable();
      }

      public Registry dimensions() {
         return this.dimensions;
      }

      public LevelProperties.SpecialProperty specialWorldProperty() {
         return this.specialWorldProperty;
      }
   }
}
