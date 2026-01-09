package net.minecraft.data.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.tag.SimpleTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.WorldPresetTags;
import net.minecraft.world.gen.WorldPresets;

public class VanillaWorldPresetTagProvider extends SimpleTagProvider {
   public VanillaWorldPresetTagProvider(DataOutput output, CompletableFuture registriesFuture) {
      super(output, RegistryKeys.WORLD_PRESET, registriesFuture);
   }

   protected void configure(RegistryWrapper.WrapperLookup registries) {
      this.builder(WorldPresetTags.NORMAL).add((Object)WorldPresets.DEFAULT).add((Object)WorldPresets.FLAT).add((Object)WorldPresets.LARGE_BIOMES).add((Object)WorldPresets.AMPLIFIED).add((Object)WorldPresets.SINGLE_BIOME_SURFACE);
      this.builder(WorldPresetTags.EXTENDED).addTag(WorldPresetTags.NORMAL).add((Object)WorldPresets.DEBUG_ALL_BLOCK_STATES);
   }
}
