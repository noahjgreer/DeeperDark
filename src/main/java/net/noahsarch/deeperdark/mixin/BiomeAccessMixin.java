package net.noahsarch.deeperdark.mixin;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.noahsarch.deeperdark.DeepDarkBiomeModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeAccess.class)
public class BiomeAccessMixin {
    @Shadow @Final private BiomeAccess.Storage storage;

    @Inject(method = "getBiome", at = @At("RETURN"), cancellable = true)
    private void injectDeepDarkBiome(BlockPos pos, CallbackInfoReturnable<RegistryEntry<Biome>> cir) {
        // Check if we're below Y-54
        if (pos.getY() < -54) {
            // Get the current biome from the return value
            RegistryEntry<Biome> currentBiome = cir.getReturnValue();

            // If it's already Deep Dark, don't need to do anything
            if (currentBiome != null && currentBiome.matchesKey(DeepDarkBiomeModifier.DEEP_DARK)) {
                return;
            }

            // The actual replacement will be handled by MultiNoiseBiomeSourceMixin
            // This is just a secondary check that doesn't try to use storage directly
        }
    }
}
