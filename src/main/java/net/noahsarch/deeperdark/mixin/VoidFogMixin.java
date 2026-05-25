package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Ports void fog from Minecraft 1.3.1 (EntityRenderer.setupFog / updateFogColor).
 *
 * 1.3.1 had two void fog effects:
 *   1) Colour darkening   — already re-implemented in modern MC via voidDarknessOnsetRange
 *   2) Fog distance pull-in — NOT present in modern MC; added here
 *
 * Distance formula (direct port, Y made relative to world bottom so the
 * behaviour is identical to 1.3.1 relative to the void):
 *   d = skyLight / 16.0  +  (relativeY + 4) / 32.0
 * If d < 1.0:  fogEnd = max(5, 100 * d²)  blocks
 *
 * In 1.3.1 this condition fired whenever the player was not in creative mode
 * and the world had void particles (overworld).  We keep the creative-mode
 * guard and rely on the Y threshold to naturally silence the effect when the
 * player is not near the world floor.
 */
@Environment(EnvType.CLIENT)
@Mixin(FogRenderer.class)
public class VoidFogMixin {

    @Inject(method = "setupFog", at = @At("RETURN"))
    private void deeperdark$applyVoidFogDistance(
            Camera camera, int renderDistanceInChunks, DeltaTracker deltaTracker,
            float darkenWorldAmount, ClientLevel level,
            CallbackInfoReturnable<FogData> cir) {

        // Only atmospheric fog — water / lava / powder-snow have their own handling
        if (camera.getFluidInCamera() != FogType.NONE) return;

        // 1.3.1 skipped void fog in creative mode
        if (camera.entity() instanceof Player player && player.isCreative()) return;

        // Sky-light at the camera block (0–15, same range as 1.3.1's packed value >> 20)
        BlockPos blockPos = camera.blockPosition();
        int skyLight = level.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(blockPos);

        // Y relative to world bottom — matches 1.3.1's absolute Y when minY == 0
        double relativeY = camera.position().y - level.getMinY();

        // Direct port of the 1.3.1 EntityRenderer formula
        double d = skyLight / 16.0 + (relativeY + 4.0) / 32.0;

        if (d >= 1.0) return; // not near the void — nothing to do

        if (d < 0.0) d = 0.0;
        d *= d; // square for the same non-linear curve as the original
        float voidFogEnd = Math.max(5.0f, 100.0f * (float) d);

        FogData fog = cir.getReturnValue();

        // Pull environmental fog (spherical — affects blocks in all directions)
        if (fog.environmentalEnd > voidFogEnd) {
            fog.environmentalStart = Math.min(fog.environmentalStart, voidFogEnd * 0.25f);
            fog.environmentalEnd = voidFogEnd;
        }

        // Pull render-distance fog (cylindrical — affects chunk boundary)
        if (fog.renderDistanceEnd > voidFogEnd) {
            fog.renderDistanceStart = Math.min(fog.renderDistanceStart, voidFogEnd * 0.25f);
            fog.renderDistanceEnd = voidFogEnd;
        }

        // Pull sky and cloud fog so the void darkens the upper atmosphere too
        fog.skyEnd = Math.min(fog.skyEnd, voidFogEnd);
        fog.cloudEnd = Math.min(fog.cloudEnd, voidFogEnd);
    }
}
