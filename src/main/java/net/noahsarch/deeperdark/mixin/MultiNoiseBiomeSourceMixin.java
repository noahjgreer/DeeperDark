package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.noahsarch.deeperdark.DeepDarkBiomeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin {
    @Inject(method = "getBiome", at = @At("RETURN"), cancellable = true)
    private void injectDeepDarkBiome(int biomeX, int biomeY, int biomeZ, Climate.Sampler noiseSampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        // Convert from biome coordinates to block coordinates
        int blockY = QuartPos.toBlock(biomeY);

        // If below Y-54, replace with Deep Dark biome
        if (blockY < -54) {
            Holder<Biome> currentBiome = cir.getReturnValue();

            // If already Deep Dark, no change needed
            if (currentBiome != null && currentBiome.matchesKey(DeepDarkBiomeModifier.DEEP_DARK)) {
                return;
            }

            // Find Deep Dark biome in available biomes
            MultiNoiseBiomeSource source = (MultiNoiseBiomeSource)(Object)this;
            for (Holder<Biome> entry : source.getBiomes()) {
                if (entry.matchesKey(DeepDarkBiomeModifier.DEEP_DARK)) {
                    cir.setReturnValue(entry);
                    return;
                }
            }
        }
    }
}
