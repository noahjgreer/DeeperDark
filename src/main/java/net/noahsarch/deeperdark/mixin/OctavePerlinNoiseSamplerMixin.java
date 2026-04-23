package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PerlinNoise.class)
public class OctavePerlinNoiseSamplerMixin {
    @Inject(method = "wrap", at = @At("HEAD"), cancellable = true)
    private static void onMaintainPrecision(double value, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(value);
    }
}

