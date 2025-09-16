package net.noahsarch.deeperdark;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public class DeepDarkBiomeModifier {
    // We're keeping this constant for use in our mixin
    public static final RegistryKey<Biome> DEEP_DARK = BiomeKeys.DEEP_DARK;

    // Initialize the biome modification system
    public static void init() {
        // Nothing needed here anymore as we're using the mixin approach
    }
}
