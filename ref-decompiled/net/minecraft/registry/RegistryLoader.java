package net.minecraft.registry;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.block.spawner.TrialSpawnerConfig;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.ChickenVariant;
import net.minecraft.entity.passive.CowVariant;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.passive.PigVariant;
import net.minecraft.entity.passive.WolfSoundVariant;
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.item.Instrument;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.test.TestEnvironmentDefinition;
import net.minecraft.test.TestInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.structure.Structure;
import org.slf4j.Logger;

public class RegistryLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Comparator KEY_COMPARATOR = Comparator.comparing(RegistryKey::getRegistry).thenComparing(RegistryKey::getValue);
   private static final RegistryEntryInfo EXPERIMENTAL_ENTRY_INFO = new RegistryEntryInfo(Optional.empty(), Lifecycle.experimental());
   private static final Function RESOURCE_ENTRY_INFO_GETTER = Util.memoize((knownPacks) -> {
      Lifecycle lifecycle = (Lifecycle)knownPacks.map(VersionedIdentifier::isVanilla).map((vanilla) -> {
         return Lifecycle.stable();
      }).orElse(Lifecycle.experimental());
      return new RegistryEntryInfo(knownPacks, lifecycle);
   });
   public static final List DYNAMIC_REGISTRIES;
   public static final List DIMENSION_REGISTRIES;
   public static final List SYNCED_REGISTRIES;

   public static DynamicRegistryManager.Immutable loadFromResource(ResourceManager resourceManager, List registries, List entries) {
      return load((loader, infoGetter) -> {
         loader.loadFromResource(resourceManager, infoGetter);
      }, registries, entries);
   }

   public static DynamicRegistryManager.Immutable loadFromNetwork(Map data, ResourceFactory factory, List registries, List entries) {
      return load((loader, infoGetter) -> {
         loader.loadFromNetwork(data, factory, infoGetter);
      }, registries, entries);
   }

   private static DynamicRegistryManager.Immutable load(RegistryLoadable loadable, List registries, List entries) {
      Map map = new HashMap();
      List list = (List)entries.stream().map((entry) -> {
         return entry.getLoader(Lifecycle.stable(), map);
      }).collect(Collectors.toUnmodifiableList());
      RegistryOps.RegistryInfoGetter registryInfoGetter = createInfoGetter(registries, list);
      list.forEach((loader) -> {
         loadable.apply(loader, registryInfoGetter);
      });
      list.forEach((loader) -> {
         Registry registry = loader.registry();

         try {
            registry.freeze();
         } catch (Exception var4) {
            map.put(registry.getKey(), var4);
         }

         if (loader.data.requiredNonEmpty && registry.size() == 0) {
            map.put(registry.getKey(), new IllegalStateException("Registry must be non-empty: " + String.valueOf(registry.getKey().getValue())));
         }

      });
      if (!map.isEmpty()) {
         throw writeAndCreateLoadingException(map);
      } else {
         return (new DynamicRegistryManager.ImmutableImpl(list.stream().map(Loader::registry).toList())).toImmutable();
      }
   }

   private static RegistryOps.RegistryInfoGetter createInfoGetter(List registries, List additionalRegistries) {
      final Map map = new HashMap();
      registries.forEach((registry) -> {
         map.put(registry.getKey(), createInfo(registry));
      });
      additionalRegistries.forEach((loader) -> {
         map.put(loader.registry.getKey(), createInfo(loader.registry));
      });
      return new RegistryOps.RegistryInfoGetter() {
         public Optional getRegistryInfo(RegistryKey registryRef) {
            return Optional.ofNullable((RegistryOps.RegistryInfo)map.get(registryRef));
         }
      };
   }

   private static RegistryOps.RegistryInfo createInfo(MutableRegistry registry) {
      return new RegistryOps.RegistryInfo(registry, registry.createMutableRegistryLookup(), registry.getLifecycle());
   }

   private static RegistryOps.RegistryInfo createInfo(RegistryWrapper.Impl registry) {
      return new RegistryOps.RegistryInfo(registry, registry, registry.getLifecycle());
   }

   private static CrashException writeAndCreateLoadingException(Map exceptions) {
      writeLoadingError(exceptions);
      return createLoadingException(exceptions);
   }

   private static void writeLoadingError(Map exceptions) {
      StringWriter stringWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(stringWriter);
      Map map = (Map)exceptions.entrySet().stream().collect(Collectors.groupingBy((entry) -> {
         return ((RegistryKey)entry.getKey()).getRegistry();
      }, Collectors.toMap((entry) -> {
         return ((RegistryKey)entry.getKey()).getValue();
      }, Map.Entry::getValue)));
      map.entrySet().stream().sorted(java.util.Map.Entry.comparingByKey()).forEach((entry) -> {
         printWriter.printf("> Errors in registry %s:%n", entry.getKey());
         ((Map)entry.getValue()).entrySet().stream().sorted(java.util.Map.Entry.comparingByKey()).forEach((element) -> {
            printWriter.printf(">> Errors in element %s:%n", element.getKey());
            ((Exception)element.getValue()).printStackTrace(printWriter);
         });
      });
      printWriter.flush();
      LOGGER.error("Registry loading errors:\n{}", stringWriter);
   }

   private static CrashException createLoadingException(Map exceptions) {
      CrashReport crashReport = CrashReport.create(new IllegalStateException("Failed to load registries due to errors"), "Registry Loading");
      CrashReportSection crashReportSection = crashReport.addElement("Loading info");
      crashReportSection.add("Errors", () -> {
         StringBuilder stringBuilder = new StringBuilder();
         exceptions.entrySet().stream().sorted(java.util.Map.Entry.comparingByKey(KEY_COMPARATOR)).forEach((entry) -> {
            stringBuilder.append("\n\t\t").append(((RegistryKey)entry.getKey()).getRegistry()).append("/").append(((RegistryKey)entry.getKey()).getValue()).append(": ").append(((Exception)entry.getValue()).getMessage());
         });
         return stringBuilder.toString();
      });
      return new CrashException(crashReport);
   }

   private static void parseAndAdd(MutableRegistry registry, Decoder decoder, RegistryOps ops, RegistryKey key, Resource resource, RegistryEntryInfo entryInfo) throws IOException {
      Reader reader = resource.getReader();

      try {
         JsonElement jsonElement = StrictJsonParser.parse((Reader)reader);
         DataResult dataResult = decoder.parse(ops, jsonElement);
         Object object = dataResult.getOrThrow();
         registry.add(key, object, entryInfo);
      } catch (Throwable var11) {
         if (reader != null) {
            try {
               reader.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (reader != null) {
         reader.close();
      }

   }

   static void loadFromResource(ResourceManager resourceManager, RegistryOps.RegistryInfoGetter infoGetter, MutableRegistry registry, Decoder elementDecoder, Map errors) {
      ResourceFinder resourceFinder = ResourceFinder.json(registry.getKey());
      RegistryOps registryOps = RegistryOps.of(JsonOps.INSTANCE, (RegistryOps.RegistryInfoGetter)infoGetter);
      Iterator var7 = resourceFinder.findResources(resourceManager).entrySet().iterator();

      while(var7.hasNext()) {
         Map.Entry entry = (Map.Entry)var7.next();
         Identifier identifier = (Identifier)entry.getKey();
         RegistryKey registryKey = RegistryKey.of(registry.getKey(), resourceFinder.toResourceId(identifier));
         Resource resource = (Resource)entry.getValue();
         RegistryEntryInfo registryEntryInfo = (RegistryEntryInfo)RESOURCE_ENTRY_INFO_GETTER.apply(resource.getKnownPackInfo());

         try {
            parseAndAdd(registry, elementDecoder, registryOps, registryKey, resource, registryEntryInfo);
         } catch (Exception var14) {
            errors.put(registryKey, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse %s from pack %s", identifier, resource.getPackId()), var14));
         }
      }

      TagGroupLoader.loadInitial(resourceManager, registry);
   }

   static void loadFromNetwork(Map data, ResourceFactory factory, RegistryOps.RegistryInfoGetter infoGetter, MutableRegistry registry, Decoder decoder, Map loadingErrors) {
      ElementsAndTags elementsAndTags = (ElementsAndTags)data.get(registry.getKey());
      if (elementsAndTags != null) {
         RegistryOps registryOps = RegistryOps.of(NbtOps.INSTANCE, (RegistryOps.RegistryInfoGetter)infoGetter);
         RegistryOps registryOps2 = RegistryOps.of(JsonOps.INSTANCE, (RegistryOps.RegistryInfoGetter)infoGetter);
         ResourceFinder resourceFinder = ResourceFinder.json(registry.getKey());
         Iterator var10 = elementsAndTags.elements.iterator();

         while(var10.hasNext()) {
            SerializableRegistries.SerializedRegistryEntry serializedRegistryEntry = (SerializableRegistries.SerializedRegistryEntry)var10.next();
            RegistryKey registryKey = RegistryKey.of(registry.getKey(), serializedRegistryEntry.id());
            Optional optional = serializedRegistryEntry.data();
            if (optional.isPresent()) {
               try {
                  DataResult dataResult = decoder.parse(registryOps, (NbtElement)optional.get());
                  Object object = dataResult.getOrThrow();
                  registry.add(registryKey, object, EXPERIMENTAL_ENTRY_INFO);
               } catch (Exception var16) {
                  loadingErrors.put(registryKey, new IllegalStateException(String.format(Locale.ROOT, "Failed to parse value %s from server", optional.get()), var16));
               }
            } else {
               Identifier identifier = resourceFinder.toResourcePath(serializedRegistryEntry.id());

               try {
                  Resource resource = factory.getResourceOrThrow(identifier);
                  parseAndAdd(registry, decoder, registryOps2, registryKey, resource, EXPERIMENTAL_ENTRY_INFO);
               } catch (Exception var17) {
                  loadingErrors.put(registryKey, new IllegalStateException("Failed to parse local data", var17));
               }
            }
         }

         TagGroupLoader.loadFromNetwork(elementsAndTags.tags, registry);
      }
   }

   static {
      DYNAMIC_REGISTRIES = List.of(new Entry(RegistryKeys.DIMENSION_TYPE, DimensionType.CODEC), new Entry(RegistryKeys.BIOME, Biome.CODEC), new Entry(RegistryKeys.MESSAGE_TYPE, MessageType.CODEC), new Entry(RegistryKeys.CONFIGURED_CARVER, ConfiguredCarver.CODEC), new Entry(RegistryKeys.CONFIGURED_FEATURE, ConfiguredFeature.CODEC), new Entry(RegistryKeys.PLACED_FEATURE, PlacedFeature.CODEC), new Entry(RegistryKeys.STRUCTURE, Structure.STRUCTURE_CODEC), new Entry(RegistryKeys.STRUCTURE_SET, StructureSet.CODEC), new Entry(RegistryKeys.PROCESSOR_LIST, StructureProcessorType.PROCESSORS_CODEC), new Entry(RegistryKeys.TEMPLATE_POOL, StructurePool.CODEC), new Entry(RegistryKeys.CHUNK_GENERATOR_SETTINGS, ChunkGeneratorSettings.CODEC), new Entry(RegistryKeys.NOISE_PARAMETERS, DoublePerlinNoiseSampler.NoiseParameters.CODEC), new Entry(RegistryKeys.DENSITY_FUNCTION, DensityFunction.CODEC), new Entry(RegistryKeys.WORLD_PRESET, WorldPreset.CODEC), new Entry(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPreset.CODEC), new Entry(RegistryKeys.TRIM_PATTERN, ArmorTrimPattern.CODEC), new Entry(RegistryKeys.TRIM_MATERIAL, ArmorTrimMaterial.CODEC), new Entry(RegistryKeys.TRIAL_SPAWNER, TrialSpawnerConfig.CODEC), new Entry(RegistryKeys.WOLF_VARIANT, WolfVariant.CODEC, true), new Entry(RegistryKeys.WOLF_SOUND_VARIANT, WolfSoundVariant.CODEC, true), new Entry(RegistryKeys.PIG_VARIANT, PigVariant.CODEC, true), new Entry(RegistryKeys.FROG_VARIANT, FrogVariant.CODEC, true), new Entry(RegistryKeys.CAT_VARIANT, CatVariant.CODEC, true), new Entry(RegistryKeys.COW_VARIANT, CowVariant.CODEC, true), new Entry(RegistryKeys.CHICKEN_VARIANT, ChickenVariant.CODEC, true), new Entry(RegistryKeys.PAINTING_VARIANT, PaintingVariant.CODEC, true), new Entry(RegistryKeys.DAMAGE_TYPE, DamageType.CODEC), new Entry(RegistryKeys.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterList.CODEC), new Entry(RegistryKeys.BANNER_PATTERN, BannerPattern.CODEC), new Entry(RegistryKeys.ENCHANTMENT, Enchantment.CODEC), new Entry(RegistryKeys.ENCHANTMENT_PROVIDER, EnchantmentProvider.CODEC), new Entry(RegistryKeys.JUKEBOX_SONG, JukeboxSong.CODEC), new Entry(RegistryKeys.INSTRUMENT, Instrument.CODEC), new Entry(RegistryKeys.TEST_ENVIRONMENT, TestEnvironmentDefinition.CODEC), new Entry(RegistryKeys.TEST_INSTANCE, TestInstance.CODEC), new Entry(RegistryKeys.DIALOG, Dialog.CODEC));
      DIMENSION_REGISTRIES = List.of(new Entry(RegistryKeys.DIMENSION, DimensionOptions.CODEC));
      SYNCED_REGISTRIES = List.of(new Entry(RegistryKeys.BIOME, Biome.NETWORK_CODEC), new Entry(RegistryKeys.MESSAGE_TYPE, MessageType.CODEC), new Entry(RegistryKeys.TRIM_PATTERN, ArmorTrimPattern.CODEC), new Entry(RegistryKeys.TRIM_MATERIAL, ArmorTrimMaterial.CODEC), new Entry(RegistryKeys.WOLF_VARIANT, WolfVariant.NETWORK_CODEC, true), new Entry(RegistryKeys.WOLF_SOUND_VARIANT, WolfSoundVariant.NETWORK_CODEC, true), new Entry(RegistryKeys.PIG_VARIANT, PigVariant.NETWORK_CODEC, true), new Entry(RegistryKeys.FROG_VARIANT, FrogVariant.NETWORK_CODEC, true), new Entry(RegistryKeys.CAT_VARIANT, CatVariant.NETWORK_CODEC, true), new Entry(RegistryKeys.COW_VARIANT, CowVariant.NETWORK_CODEC, true), new Entry(RegistryKeys.CHICKEN_VARIANT, ChickenVariant.NETWORK_CODEC, true), new Entry(RegistryKeys.PAINTING_VARIANT, PaintingVariant.CODEC, true), new Entry(RegistryKeys.DIMENSION_TYPE, DimensionType.CODEC), new Entry(RegistryKeys.DAMAGE_TYPE, DamageType.CODEC), new Entry(RegistryKeys.BANNER_PATTERN, BannerPattern.CODEC), new Entry(RegistryKeys.ENCHANTMENT, Enchantment.CODEC), new Entry(RegistryKeys.JUKEBOX_SONG, JukeboxSong.CODEC), new Entry(RegistryKeys.INSTRUMENT, Instrument.CODEC), new Entry(RegistryKeys.TEST_ENVIRONMENT, TestEnvironmentDefinition.CODEC), new Entry(RegistryKeys.TEST_INSTANCE, TestInstance.CODEC), new Entry(RegistryKeys.DIALOG, Dialog.CODEC));
   }

   @FunctionalInterface
   private interface RegistryLoadable {
      void apply(Loader loader, RegistryOps.RegistryInfoGetter infoGetter);
   }

   public static record ElementsAndTags(List elements, TagPacketSerializer.Serialized tags) {
      final List elements;
      final TagPacketSerializer.Serialized tags;

      public ElementsAndTags(List list, TagPacketSerializer.Serialized serialized) {
         this.elements = list;
         this.tags = serialized;
      }

      public List elements() {
         return this.elements;
      }

      public TagPacketSerializer.Serialized tags() {
         return this.tags;
      }
   }

   private static record Loader(Entry data, MutableRegistry registry, Map loadingErrors) {
      final Entry data;
      final MutableRegistry registry;

      Loader(Entry entry, MutableRegistry mutableRegistry, Map map) {
         this.data = entry;
         this.registry = mutableRegistry;
         this.loadingErrors = map;
      }

      public void loadFromResource(ResourceManager resourceManager, RegistryOps.RegistryInfoGetter infoGetter) {
         RegistryLoader.loadFromResource(resourceManager, infoGetter, this.registry, this.data.elementCodec, this.loadingErrors);
      }

      public void loadFromNetwork(Map data, ResourceFactory factory, RegistryOps.RegistryInfoGetter infoGetter) {
         RegistryLoader.loadFromNetwork(data, factory, infoGetter, this.registry, this.data.elementCodec, this.loadingErrors);
      }

      public Entry data() {
         return this.data;
      }

      public MutableRegistry registry() {
         return this.registry;
      }

      public Map loadingErrors() {
         return this.loadingErrors;
      }
   }

   public static record Entry(RegistryKey key, Codec elementCodec, boolean requiredNonEmpty) {
      final Codec elementCodec;
      final boolean requiredNonEmpty;

      Entry(RegistryKey key, Codec codec) {
         this(key, codec, false);
      }

      public Entry(RegistryKey registryKey, Codec codec, boolean bl) {
         this.key = registryKey;
         this.elementCodec = codec;
         this.requiredNonEmpty = bl;
      }

      Loader getLoader(Lifecycle lifecycle, Map errors) {
         MutableRegistry mutableRegistry = new SimpleRegistry(this.key, lifecycle);
         return new Loader(this, mutableRegistry, errors);
      }

      public void addToCloner(BiConsumer callback) {
         callback.accept(this.key, this.elementCodec);
      }

      public RegistryKey key() {
         return this.key;
      }

      public Codec elementCodec() {
         return this.elementCodec;
      }

      public boolean requiredNonEmpty() {
         return this.requiredNonEmpty;
      }
   }
}
