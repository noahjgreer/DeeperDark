package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.mixed.BiomeMixed;
import io.github.lucaargolo.seasons.resources.FoliageSeasonColors;
import io.github.lucaargolo.seasons.resources.GrassSeasonColors;
import io.github.lucaargolo.seasons.utils.ColorsCache;
import io.github.lucaargolo.seasons.utils.Season;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Biome.class)
public abstract class BiomeMixin implements BiomeMixed {

    @Shadow @Final private Biome.ClimateSettings climateSettings;
    @Shadow @Final private BiomeSpecialEffects specialEffects;

    // Season weather override fields
    @Unique private float seasons$originalTemperature;
    @Unique private float seasons$originalDownfall;
    @Unique private boolean seasons$originalHasPrecipitation;
    @Unique private boolean seasons$hasOriginalStored = false;

    @Unique private float seasons$seasonTemperature;
    @Unique private boolean seasons$seasonHasPrecipitation;
    @Unique private boolean seasons$hasSeasonWeather = false;

    // Cached biome ID (looked up once per biome instance)
    @Unique private @Nullable Identifier seasons$cachedBiomeId = null;
    @Unique private boolean seasons$biomeIdResolved = false;

    // -------------------------------------------------------------------------
    // BiomeMixed implementation
    // -------------------------------------------------------------------------

    @Override public float   seasons_getOriginalTemperature()    { return seasons$originalTemperature; }
    @Override public float   seasons_getOriginalDownfall()       { return seasons$originalDownfall; }
    @Override public boolean seasons_getOriginalHasPrecipitation(){ return seasons$originalHasPrecipitation; }

    @Override
    public void seasons_storeOriginal(float temperature, float downfall, boolean hasPrecipitation) {
        seasons$originalTemperature = temperature;
        seasons$originalDownfall = downfall;
        seasons$originalHasPrecipitation = hasPrecipitation;
        seasons$hasOriginalStored = true;
    }

    @Override public boolean seasons_hasOriginalStored()         { return seasons$hasOriginalStored; }
    @Override public float   seasons_getBaseDownfall()           { return climateSettings.downfall(); }

    @Override
    public void seasons_applySeasonWeather(float temperature, boolean hasPrecipitation) {
        seasons$seasonTemperature = temperature;
        seasons$seasonHasPrecipitation = hasPrecipitation;
        seasons$hasSeasonWeather = true;
    }

    @Override public void    seasons_clearSeasonWeather()        { seasons$hasSeasonWeather = false; }
    @Override public boolean seasons_hasSeasonWeather()          { return seasons$hasSeasonWeather; }
    @Override public float   seasons_getSeasonTemperature()      { return seasons$seasonTemperature; }
    @Override public boolean seasons_getSeasonHasPrecipitation() { return seasons$seasonHasPrecipitation; }

    // -------------------------------------------------------------------------
    // Temperature / precipitation season overrides
    // -------------------------------------------------------------------------

    @Inject(at = @At("HEAD"), method = "getBaseTemperature()F", cancellable = true)
    private void seasons$getBaseTemperature(CallbackInfoReturnable<Float> cir) {
        if (seasons$hasSeasonWeather) {
            cir.setReturnValue(seasons$seasonTemperature);
        }
    }

    @Inject(at = @At("HEAD"), method = "hasPrecipitation()Z", cancellable = true)
    private void seasons$hasPrecipitation(CallbackInfoReturnable<Boolean> cir) {
        if (seasons$hasSeasonWeather) {
            cir.setReturnValue(seasons$seasonHasPrecipitation);
        }
    }

    // -------------------------------------------------------------------------
    // Season colormap overrides (replaces vanilla temperature/downfall lookup)
    // -------------------------------------------------------------------------

    @Environment(EnvType.CLIENT)
    @Inject(at = @At("HEAD"), method = "getGrassColorFromTexture()I", cancellable = true)
    private void seasons$getGrassColorFromTexture(CallbackInfoReturnable<Integer> cir) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;
        if (!FabricSeasons.CONFIG.isValidInDimension(client.level.dimension())) return;
        Season current = FabricSeasons.getCurrentSeason(client.level);
        float t = FabricSeasons.getSeasonTransitionFraction(client.level);
        cir.setReturnValue(GrassSeasonColors.getBlendedColor(current, current.getNext(), t,
                Mth.clamp(climateSettings.temperature(), 0.0F, 1.0F),
                Mth.clamp(climateSettings.downfall(), 0.0F, 1.0F)));
    }

    @Environment(EnvType.CLIENT)
    @Inject(at = @At("HEAD"), method = "getFoliageColorFromTexture()I", cancellable = true)
    private void seasons$getFoliageColorFromTexture(CallbackInfoReturnable<Integer> cir) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;
        if (!FabricSeasons.CONFIG.isValidInDimension(client.level.dimension())) return;
        Season current = FabricSeasons.getCurrentSeason(client.level);
        float t = FabricSeasons.getSeasonTransitionFraction(client.level);
        cir.setReturnValue(FoliageSeasonColors.getBlendedColor(current, current.getNext(), t,
                Mth.clamp(climateSettings.temperature(), 0.0F, 1.0F),
                Mth.clamp(climateSettings.downfall(), 0.0F, 1.0F)));
    }

    // -------------------------------------------------------------------------
    // Season grass color (biome-specific overrides and swamp handling)
    // -------------------------------------------------------------------------

    @Environment(EnvType.CLIENT)
    @Inject(at = @At("TAIL"), method = "getGrassColor(DD)I", cancellable = true)
    private void seasons$getGrassColor(double x, double z, CallbackInfoReturnable<Integer> cir) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;
        if (!FabricSeasons.CONFIG.isValidInDimension(client.level.dimension())) return;
        Season current = FabricSeasons.getCurrentSeason(client.level);
        float t = FabricSeasons.getSeasonTransitionFraction(client.level);
        Biome self = (Biome) (Object) this;

        // Swamp uses per-position noise — never cache
        if (specialEffects.grassColorModifier() == BiomeSpecialEffects.GrassColorModifier.SWAMP) {
            double noise = Biome.BIOME_INFO_NOISE.getValue(x * 0.0225, z * 0.0225, false);
            cir.setReturnValue(noise < -0.1
                    ? GrassSeasonColors.getBlendedSwampColor1(current, current.getNext(), t)
                    : GrassSeasonColors.getBlendedSwampColor2(current, current.getNext(), t));
            return;
        }

        Optional<Integer> grassCached = ColorsCache.getGrassCache(self);
        if (grassCached != null) {
            grassCached.ifPresent(cir::setReturnValue);
            return;
        }

        Identifier biomeId = seasons$resolveBiomeId(client);
        if (biomeId != null) {
            Optional<Integer> seasonColor = GrassSeasonColors.getBlendedSeasonGrassColor(self, biomeId, current, current.getNext(), t);
            ColorsCache.createGrassCache(self, seasonColor);
            seasonColor.ifPresent(cir::setReturnValue);
        }
    }

    // -------------------------------------------------------------------------
    // Season foliage color (biome-specific overrides)
    // -------------------------------------------------------------------------

    @Environment(EnvType.CLIENT)
    @Inject(at = @At("TAIL"), method = "getFoliageColor()I", cancellable = true)
    private void seasons$getFoliageColor(CallbackInfoReturnable<Integer> cir) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;
        if (!FabricSeasons.CONFIG.isValidInDimension(client.level.dimension())) return;
        Season current = FabricSeasons.getCurrentSeason(client.level);
        float t = FabricSeasons.getSeasonTransitionFraction(client.level);
        Biome self = (Biome) (Object) this;

        Optional<Integer> foliageCached = ColorsCache.getFoliageCache(self);
        if (foliageCached != null) {
            foliageCached.ifPresent(cir::setReturnValue);
            return;
        }

        Identifier biomeId = seasons$resolveBiomeId(client);

        if (biomeId != null) {
            Optional<Integer> seasonColor = FoliageSeasonColors.getBlendedSeasonFoliageColor(self, biomeId, current, current.getNext(), t);
            if (seasonColor.isPresent()) {
                ColorsCache.createFoliageCache(self, seasonColor);
                cir.setReturnValue(seasonColor.get());
                return;
            }
        }

        // Biomes with a solid foliage color override use the default season foliage palette
        if (specialEffects.foliageColorOverride().isPresent()) {
            int color = FoliageSeasonColors.getBlendedDefaultColor(current, current.getNext(), t);
            ColorsCache.createFoliageCache(self, Optional.of(color));
            cir.setReturnValue(color);
            return;
        }

        if (biomeId != null) {
            ColorsCache.createFoliageCache(self, Optional.empty());
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Resolves and caches this biome's registry identifier; O(n) only on first call per instance. */
    @Unique
    private @Nullable Identifier seasons$resolveBiomeId(Minecraft client) {
        if (!seasons$biomeIdResolved) {
            seasons$biomeIdResolved = true;
            if (client.level != null) {
                Biome self = (Biome) (Object) this;
                var found = client.level.registryAccess()
                        .lookupOrThrow(Registries.BIOME)
                        .listElements()
                        .filter(ref -> ref.value() == self)
                        .findFirst();
                if (found.isPresent()) {
                    seasons$cachedBiomeId = found.get().key().identifier();
                }
            }
        }
        return seasons$cachedBiomeId;
    }
}
