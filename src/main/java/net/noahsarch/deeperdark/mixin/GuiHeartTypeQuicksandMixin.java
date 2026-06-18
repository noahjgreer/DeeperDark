package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.noahsarch.deeperdark.client.QuicksandClientTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(targets = "net.minecraft.client.gui.Gui$HeartType")
public class GuiHeartTypeQuicksandMixin {

    /**
     * Prevent the heart display from switching to the "frozen" style when the player
     * is sinking in quicksand.  The freeze ticks are still accumulating for the
     * gameplay damage mechanic, but we don't want the hearts to turn blue.
     */
    @Redirect(
            method = "forPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isFullyFrozen()Z"))
    private static boolean deeperdark$suppressFrozenHearts(Player player) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && QuicksandClientTracker.isActive(mc.level.getGameTime())) {
            return false;
        }
        return player.isFullyFrozen();
    }
}
