package net.minecraft.entity.spawn;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;

public class SpawnConditions {
   public static MapCodec registerAndGetDefault(Registry registry) {
      Registry.register(registry, (String)"structure", StructureSpawnCondition.CODEC);
      Registry.register(registry, (String)"moon_brightness", MoonBrightnessSpawnCondition.CODEC);
      return (MapCodec)Registry.register(registry, (String)"biome", BiomeSpawnCondition.CODEC);
   }
}
