package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelReader.class)
public interface WorldViewMixin {

    @Shadow BiomeManager getBiomeManager();

    /**
     * @author D4rkness_King
     * @reason Inject season-based biome temperature before callers read weather data,
     *   without mutating the immutable ClimateSettings record.
     */
    @Overwrite
    default Holder<Biome> getBiome(BlockPos pos) {
        Holder<Biome> biomeHolder = this.getBiomeManager().getBiome(pos);
        if (this instanceof Level world) {
            FabricSeasons.injectBiomeTemperature(biomeHolder, world);
        }
        return biomeHolder;
    }
}
