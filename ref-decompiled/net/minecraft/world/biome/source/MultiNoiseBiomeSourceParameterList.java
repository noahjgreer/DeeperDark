package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;

public class MultiNoiseBiomeSourceParameterList {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(MultiNoiseBiomeSourceParameterList.Preset.CODEC.fieldOf("preset").forGetter((multiNoiseBiomeSourceParameterList) -> {
         return multiNoiseBiomeSourceParameterList.preset;
      }), RegistryOps.getEntryLookupCodec(RegistryKeys.BIOME)).apply(instance, MultiNoiseBiomeSourceParameterList::new);
   });
   public static final Codec REGISTRY_CODEC;
   private final Preset preset;
   private final MultiNoiseUtil.Entries entries;

   public MultiNoiseBiomeSourceParameterList(Preset preset, RegistryEntryLookup biomeLookup) {
      this.preset = preset;
      Preset.BiomeSourceFunction var10001 = preset.biomeSourceFunction;
      Objects.requireNonNull(biomeLookup);
      this.entries = var10001.apply(biomeLookup::getOrThrow);
   }

   public MultiNoiseUtil.Entries getEntries() {
      return this.entries;
   }

   public static Map getPresetToEntriesMap() {
      return (Map)MultiNoiseBiomeSourceParameterList.Preset.BY_IDENTIFIER.values().stream().collect(Collectors.toMap((preset) -> {
         return preset;
      }, (preset) -> {
         return preset.biomeSourceFunction().apply((biomeKey) -> {
            return biomeKey;
         });
      }));
   }

   static {
      REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, CODEC);
   }

   public static record Preset(Identifier id, BiomeSourceFunction biomeSourceFunction) {
      final BiomeSourceFunction biomeSourceFunction;
      public static final Preset NETHER = new Preset(Identifier.ofVanilla("nether"), new BiomeSourceFunction() {
         public MultiNoiseUtil.Entries apply(Function function) {
            return new MultiNoiseUtil.Entries(List.of(Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), function.apply(BiomeKeys.NETHER_WASTES)), Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), function.apply(BiomeKeys.SOUL_SAND_VALLEY)), Pair.of(MultiNoiseUtil.createNoiseHypercube(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), function.apply(BiomeKeys.CRIMSON_FOREST)), Pair.of(MultiNoiseUtil.createNoiseHypercube(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), function.apply(BiomeKeys.WARPED_FOREST)), Pair.of(MultiNoiseUtil.createNoiseHypercube(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), function.apply(BiomeKeys.BASALT_DELTAS))));
         }
      });
      public static final Preset OVERWORLD = new Preset(Identifier.ofVanilla("overworld"), new BiomeSourceFunction() {
         public MultiNoiseUtil.Entries apply(Function function) {
            return MultiNoiseBiomeSourceParameterList.Preset.getOverworldEntries(function);
         }
      });
      static final Map BY_IDENTIFIER;
      public static final Codec CODEC;

      public Preset(Identifier id, BiomeSourceFunction biomeSourceFunction) {
         this.id = id;
         this.biomeSourceFunction = biomeSourceFunction;
      }

      static MultiNoiseUtil.Entries getOverworldEntries(Function biomeEntryGetter) {
         ImmutableList.Builder builder = ImmutableList.builder();
         (new VanillaBiomeParameters()).writeOverworldBiomeParameters((pair) -> {
            builder.add(pair.mapSecond(biomeEntryGetter));
         });
         return new MultiNoiseUtil.Entries(builder.build());
      }

      public Stream biomeStream() {
         return this.biomeSourceFunction.apply((biomeKey) -> {
            return biomeKey;
         }).getEntries().stream().map(Pair::getSecond).distinct();
      }

      public Identifier id() {
         return this.id;
      }

      public BiomeSourceFunction biomeSourceFunction() {
         return this.biomeSourceFunction;
      }

      static {
         BY_IDENTIFIER = (Map)Stream.of(NETHER, OVERWORLD).collect(Collectors.toMap(Preset::id, (preset) -> {
            return preset;
         }));
         CODEC = Identifier.CODEC.flatXmap((id) -> {
            return (DataResult)Optional.ofNullable((Preset)BY_IDENTIFIER.get(id)).map(DataResult::success).orElseGet(() -> {
               return DataResult.error(() -> {
                  return "Unknown preset: " + String.valueOf(id);
               });
            });
         }, (preset) -> {
            return DataResult.success(preset.id);
         });
      }

      @FunctionalInterface
      private interface BiomeSourceFunction {
         MultiNoiseUtil.Entries apply(Function biomeEntryGetter);
      }
   }
}
