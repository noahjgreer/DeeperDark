package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.noahsarch.deeperdark.DeepDarkBiomeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.stream.Stream;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MultiNoiseBiomeSourceMixin {

    @Shadow protected abstract Stream<Holder<Biome>> collectPossibleBiomes();
    @Inject(method = "getNoiseBiome", at = @At("RETURN"), cancellable = true)
    private void injectDeepDarkBiome(int biomeX, int biomeY, int biomeZ, Climate.Sampler noiseSampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        int blockY = QuartPos.toBlock(biomeY);

        if (blockY < -54) {
            Holder<Biome> currentBiome = cir.getReturnValue();

            if (currentBiome != null && currentBiome.is(DeepDarkBiomeModifier.DEEP_DARK)) {
                return;
            }

            this.collectPossibleBiomes().forEach(entry -> {
                if (entry.is(DeepDarkBiomeModifier.DEEP_DARK)) {
                    cir.setReturnValue(entry);
                }
            });
        }
    }
}
