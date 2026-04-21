package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.noahsarch.deeperdark.DeepDarkBiomeModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeManager.class)
public class BiomeAccessMixin {
    @Shadow @Final private BiomeManager.NoiseBiomeSource storage;

    @Inject(method = "getBiome", at = @At("RETURN"), cancellable = true)
    private void injectDeepDarkBiome(BlockPos pos, CallbackInfoReturnable<Holder<Biome>> cir) {
        // Check if we're below Y-54
        if (pos.getY() < -54) {
            // Get the current biome from the return value
            Holder<Biome> currentBiome = cir.getReturnValue();

            // If it's already Deep Dark, don't need to do anything
            if (currentBiome != null && currentBiome.matchesKey(DeepDarkBiomeModifier.DEEP_DARK)) {
                return;
            }

            // The actual replacement will be handled by MultiNoiseBiomeSourceMixin
            // This is just a secondary check that doesn't try to use storage directly
        }
    }
}
