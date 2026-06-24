package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.environment.WaterFogEnvironment;
import net.minecraft.world.level.material.FluidState;
import net.noahsarch.deeperdark.fluid.MilkFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(WaterFogEnvironment.class)
public class MilkFogMixin {

    @Inject(method = "getBaseColor", at = @At("HEAD"), cancellable = true)
    private void milkFogColor(ClientLevel level, Camera camera, int renderDistance, float partialTicks, CallbackInfoReturnable<Integer> cir) {
        FluidState fs = level.getFluidState(camera.blockPosition());
        if (fs.getType() instanceof MilkFluid) {
            cir.setReturnValue(0xFFF5F5);
        }
    }
}
