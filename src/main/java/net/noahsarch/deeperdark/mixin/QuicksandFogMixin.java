package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.noahsarch.deeperdark.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(FogRenderer.class)
public class QuicksandFogMixin {

    @Inject(method = "setupFog", at = @At("RETURN"))
    private void deeperdark$applyQuicksandFog(
            Camera camera, int renderDistanceInChunks, DeltaTracker deltaTracker,
            float darkenWorldAmount, ClientLevel level,
            CallbackInfoReturnable<FogData> cir) {

        if (camera.entity().isSpectator()) return;

        BlockPos cameraPos = camera.blockPosition();
        BlockState cameraBlock = level.getBlockState(cameraPos);
        if (!cameraBlock.is(ModBlocks.QUICKSAND)) return;

        FogData fog = cir.getReturnValue();

        // Sandy brown colour
        fog.color.set(0.76F, 0.65F, 0.43F, 1.0F);

        // Very short visibility, same density as powder snow
        fog.environmentalStart = 0.0F;
        fog.environmentalEnd = 2.0F;
        fog.skyEnd = fog.environmentalEnd;
        fog.cloudEnd = fog.environmentalEnd;
    }
}
