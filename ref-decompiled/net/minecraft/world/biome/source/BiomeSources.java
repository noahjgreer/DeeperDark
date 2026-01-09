package net.minecraft.world.biome.source;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;

public class BiomeSources {
   public static MapCodec registerAndGetDefault(Registry registry) {
      Registry.register(registry, (String)"fixed", FixedBiomeSource.CODEC);
      Registry.register(registry, (String)"multi_noise", MultiNoiseBiomeSource.CODEC);
      Registry.register(registry, (String)"checkerboard", CheckerboardBiomeSource.CODEC);
      return (MapCodec)Registry.register(registry, (String)"the_end", TheEndBiomeSource.CODEC);
   }
}
