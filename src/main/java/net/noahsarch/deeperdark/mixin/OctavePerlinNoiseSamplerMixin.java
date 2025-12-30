package net.noahsarch.deeperdark.mixin;

import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OctavePerlinNoiseSampler.class)
public class OctavePerlinNoiseSamplerMixin {
    @Inject(method = "maintainPrecision", at = @At("HEAD"), cancellable = true)
    private static void onMaintainPrecision(double value, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(value);
    }
}

