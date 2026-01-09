package net.minecraft.data.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.tag.SimpleTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.FlatLevelGeneratorPresetTags;
import net.minecraft.world.gen.FlatLevelGeneratorPresets;

public class VanillaFlatLevelGeneratorPresetTagProvider extends SimpleTagProvider {
   public VanillaFlatLevelGeneratorPresetTagProvider(DataOutput output, CompletableFuture registriesFuture) {
      super(output, RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET, registriesFuture);
   }

   protected void configure(RegistryWrapper.WrapperLookup registries) {
      this.builder(FlatLevelGeneratorPresetTags.VISIBLE).add((Object)FlatLevelGeneratorPresets.CLASSIC_FLAT).add((Object)FlatLevelGeneratorPresets.TUNNELERS_DREAM).add((Object)FlatLevelGeneratorPresets.WATER_WORLD).add((Object)FlatLevelGeneratorPresets.OVERWORLD).add((Object)FlatLevelGeneratorPresets.SNOWY_KINGDOM).add((Object)FlatLevelGeneratorPresets.BOTTOMLESS_PIT).add((Object)FlatLevelGeneratorPresets.DESERT).add((Object)FlatLevelGeneratorPresets.REDSTONE_READY).add((Object)FlatLevelGeneratorPresets.THE_VOID);
   }
}
