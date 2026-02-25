package net.noahsarch.deeperdark.mixin;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.sound.SoundCategory;
import net.noahsarch.deeperdark.creature.CreatureManager;
import net.noahsarch.deeperdark.creature.CreatureSoundHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts outgoing sound packets to suppress non-essential sounds for players
 * during a creature chase sequence. This prevents mob sounds from briefly playing
 * before being stopped, providing a seamless silent experience.
 *
 * The mixin targets ServerCommonNetworkHandler (parent of ServerPlayNetworkHandler)
 * where sendPacket is defined. When a PlaySoundS2CPacket is being sent to a player
 * whose sounds are suppressed, and the packet's category is one of the suppressed
 * categories (AMBIENT, MUSIC, WEATHER, HOSTILE, NEUTRAL, RECORDS), the packet is
 * cancelled entirely — it never leaves the server.
 *
 * Creature footstep sounds bypass this filter via CreatureSoundHelper.bypassSoundFilter.
 */
@Mixin(ServerCommonNetworkHandler.class)
public abstract class SoundFilterMixin {

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void deeperdark$filterSoundPackets(Packet<?> packet, CallbackInfo ci) {
        // Only filter PlaySoundS2CPackets
        if (!(packet instanceof PlaySoundS2CPacket soundPacket)) return;

        // Allow creature footstep sounds through (bypass flag set by CreatureSoundHelper)
        if (CreatureSoundHelper.bypassSoundFilter) return;

        // Only applies when the handler is a play-phase handler with a player reference
        if (!((Object) this instanceof ServerPlayNetworkHandler sph)) return;

        // Check if this player's sounds should be suppressed
        if (!CreatureManager.isPlayerSoundSuppressed(sph.player.getUuid())) return;

        // Suppress the specified categories per the creature spec
        SoundCategory category = soundPacket.getCategory();
        if (category == SoundCategory.AMBIENT
                || category == SoundCategory.MUSIC
                || category == SoundCategory.WEATHER
                || category == SoundCategory.HOSTILE
                || category == SoundCategory.NEUTRAL
                || category == SoundCategory.RECORDS) {
            ci.cancel();
        }
    }
}
