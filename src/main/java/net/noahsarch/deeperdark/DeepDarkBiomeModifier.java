package net.noahsarch.deeperdark;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class DeepDarkBiomeModifier {
    // We're keeping this constant for use in our mixin
    public static final ResourceKey<Biome> DEEP_DARK = Biomes.DEEP_DARK;

    // Initialize the biome modification system
    public static void init() {
        // Nothing needed here anymore as we're using the mixin approach
    }
}
