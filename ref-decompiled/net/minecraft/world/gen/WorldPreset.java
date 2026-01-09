package net.minecraft.world.gen;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;

public class WorldPreset {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codec.unboundedMap(RegistryKey.createCodec(RegistryKeys.DIMENSION), DimensionOptions.CODEC).fieldOf("dimensions").forGetter((preset) -> {
         return preset.dimensions;
      })).apply(instance, WorldPreset::new);
   }).validate(WorldPreset::validate);
   public static final Codec ENTRY_CODEC;
   private final Map dimensions;

   public WorldPreset(Map dimensions) {
      this.dimensions = dimensions;
   }

   private ImmutableMap collectDimensions() {
      ImmutableMap.Builder builder = ImmutableMap.builder();
      DimensionOptionsRegistryHolder.streamAll(this.dimensions.keySet().stream()).forEach((dimensionKey) -> {
         DimensionOptions dimensionOptions = (DimensionOptions)this.dimensions.get(dimensionKey);
         if (dimensionOptions != null) {
            builder.put(dimensionKey, dimensionOptions);
         }

      });
      return builder.build();
   }

   public DimensionOptionsRegistryHolder createDimensionsRegistryHolder() {
      return new DimensionOptionsRegistryHolder(this.collectDimensions());
   }

   public Optional getOverworld() {
      return Optional.ofNullable((DimensionOptions)this.dimensions.get(DimensionOptions.OVERWORLD));
   }

   private static DataResult validate(WorldPreset preset) {
      return preset.getOverworld().isEmpty() ? DataResult.error(() -> {
         return "Missing overworld dimension";
      }) : DataResult.success(preset, Lifecycle.stable());
   }

   static {
      ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.WORLD_PRESET, CODEC);
   }
}
