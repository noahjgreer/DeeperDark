package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;

public record WorldGenSettings(GeneratorOptions generatorOptions, DimensionOptionsRegistryHolder dimensionOptionsRegistryHolder) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(GeneratorOptions.CODEC.forGetter(WorldGenSettings::generatorOptions), DimensionOptionsRegistryHolder.CODEC.forGetter(WorldGenSettings::dimensionOptionsRegistryHolder)).apply(instance, instance.stable(WorldGenSettings::new));
   });

   public WorldGenSettings(GeneratorOptions generatorOptions, DimensionOptionsRegistryHolder dimensionOptionsRegistryHolder) {
      this.generatorOptions = generatorOptions;
      this.dimensionOptionsRegistryHolder = dimensionOptionsRegistryHolder;
   }

   public static DataResult encode(DynamicOps registryOps, GeneratorOptions generatorOptions, DimensionOptionsRegistryHolder dimensionOptionsRegistryHolder) {
      return CODEC.encodeStart(registryOps, new WorldGenSettings(generatorOptions, dimensionOptionsRegistryHolder));
   }

   public static DataResult encode(DynamicOps registryOps, GeneratorOptions generatorOptions, DynamicRegistryManager dynamicRegistryManager) {
      return encode(registryOps, generatorOptions, new DimensionOptionsRegistryHolder(dynamicRegistryManager.getOrThrow(RegistryKeys.DIMENSION)));
   }

   public GeneratorOptions generatorOptions() {
      return this.generatorOptions;
   }

   public DimensionOptionsRegistryHolder dimensionOptionsRegistryHolder() {
      return this.dimensionOptionsRegistryHolder;
   }
}
