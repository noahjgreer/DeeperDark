package net.noahsarch.deeperdark.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModWorldgen {

    public static final ResourceKey<PlacedFeature> QUICKSAND_PATCH = ResourceKey.create(
            Registries.PLACED_FEATURE,
            Identifier.fromNamespaceAndPath("deeperdark", "quicksand_patch")
    );

    public static void initialize() {
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(Biomes.DESERT),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                QUICKSAND_PATCH
        );
    }
}
