package net.minecraft.world.gen.chunk;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;

public class ChunkGenerators {
   public static MapCodec registerAndGetDefault(Registry registry) {
      Registry.register(registry, (String)"noise", NoiseChunkGenerator.CODEC);
      Registry.register(registry, (String)"flat", FlatChunkGenerator.CODEC);
      return (MapCodec)Registry.register(registry, (String)"debug", DebugChunkGenerator.CODEC);
   }
}
