package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.material.FogType;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Distant Horizons compatibility for void fog.
 *
 * DH's MixinFogRenderer uses @WrapOperation on FogData.renderDistanceEnd to intercept
 * setupFog() mid-execution. When cancelFog() returns true (DH is managing all fog), DH
 * sets environmentalEnd and renderDistanceEnd to sentinel values (~4e19) to disable
 * vanilla fog. Our VoidFogMixin runs at RETURN — after DH — and sees environmentalEnd=HUGE,
 * overwriting it to ~47, which corrupts DH's rendering pipeline.
 *
 * Fix: DH already exempts "special" fog states (fluid, blindness) from its cancelFog path
 * via isFogStateSpecial(). We extend that exemption to the void fog zone. When d < 1.0,
 * returning true here causes DH to leave FogData untouched, and VoidFogMixin applies
 * void fog cleanly to unmodified vanilla FogData.
 *
 * This mixin is in a separate config (deeperdark.compat.distanthorizons.mixins.json)
 * with required:false so it silently no-ops when DH is not installed.
 */
@Environment(EnvType.CLIENT)
@Mixin(targets = "com.seibel.distanthorizons.common.wrappers.minecraft.MinecraftRenderWrapper", remap = false)
public class VoidFogDHCompatMixin {

    @Inject(method = "isFogStateSpecial", at = @At("RETURN"), cancellable = true)
    private void deeperdark$voidFogIsSpecial(CallbackInfoReturnable<Boolean> cir) {
        // Already a special state (fluid, blindness) — nothing to add
        if (Boolean.TRUE.equals(cir.getReturnValue())) return;
        if (!DeeperDarkConfig.get().voidFogEnabled) return;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.player == null) return;

        Camera camera = mc.gameRenderer.getMainCamera();
        if (camera.getFluidInCamera() != FogType.NONE) return;
        if (camera.entity() instanceof Player player && player.isCreative()) return;

        // Same d formula as VoidFogMixin — only claim special state in the void fog zone
        BlockPos blockPos = camera.blockPosition();
        int skyLight = level.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(blockPos);
        double relativeY = camera.position().y - level.getMinY();
        double d = skyLight / 16.0 + (relativeY + 4.0) / 32.0;

        if (d < 1.0) {
            // Tell DH this is a special fog state → DH skips its sentinel override →
            // VoidFogMixin sees normal FogData and applies void fog cleanly.
            cir.setReturnValue(true);
        }
    }
}
