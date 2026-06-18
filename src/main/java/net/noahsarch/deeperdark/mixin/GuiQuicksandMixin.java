package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.noahsarch.deeperdark.block.ModBlocks;
import net.noahsarch.deeperdark.client.QuicksandClientTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public class GuiQuicksandMixin {

    @Shadow private float vignetteBrightness;

    /** Keep the tracker up-to-date every frame so the redirect & vignette stay in sync. */
    @Inject(method = "extractCameraOverlays", at = @At("HEAD"))
    private void deeperdark$updateQuicksandTracker(
            GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.level != null
                && mc.player.getInBlockState().is(ModBlocks.QUICKSAND)) {
            QuicksandClientTracker.lastInQuicksandTick = mc.level.getGameTime();
        }
    }

    /**
     * Suppress the powder-snow texture overlay while the player is, or was recently, in
     * quicksand.  "Recently" covers the window while freeze ticks drain back to zero.
     */
    @Redirect(
            method = "extractCameraOverlays",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getTicksFrozen()I"))
    private int deeperdark$suppressFreezeOverlay(LocalPlayer player) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && QuicksandClientTracker.isActive(mc.level.getGameTime())) {
            return 0;
        }
        return player.getTicksFrozen();
    }

    /**
     * While inside quicksand, replace the freeze overlay with the world-border redness
     * vignette, scaled by how far along the sinking progress the player is.
     */
    @Inject(method = "extractCameraOverlays", at = @At("RETURN"))
    private void deeperdark$drawQuicksandRedness(
            GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) return;
        // Keep drawing (and fading) while the tracker is active, even after the player
        // has left quicksand — getPercentFrozen() decreases naturally as ticks drain.
        if (!QuicksandClientTracker.isActive(level.getGameTime())) return;

        float strength = Mth.clamp(player.getPercentFrozen(), 0.0F, 1.0F);
        if (strength <= 0.0F) return;

        // Same colour formula as the world-border vignette in Gui.extractVignette
        float brightness = Mth.clamp(this.vignetteBrightness, 0.0F, 1.0F);
        float red = brightness * (1.0F - strength);
        float greenBlue = brightness + (1.0F - brightness) * strength;
        int color = ARGB.colorFromFloat(1.0F, red, greenBlue, greenBlue);

        graphics.blit(
                RenderPipelines.VIGNETTE,
                Identifier.withDefaultNamespace("textures/misc/vignette.png"),
                0, 0, 0.0F, 0.0F,
                graphics.guiWidth(), graphics.guiHeight(),
                graphics.guiWidth(), graphics.guiHeight(),
                color);
    }
}
