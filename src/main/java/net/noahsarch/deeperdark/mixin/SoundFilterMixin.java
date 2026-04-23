package net.noahsarch.deeperdark.mixin;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundSource;
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
 * The mixin targets ServerCommonPacketListenerImpl (parent of ServerGamePacketListenerImpl)
 * where sendPacket is defined. When a ClientboundSoundPacket is being sent to a player
 * whose sounds are suppressed, and the packet's category is one of the suppressed
 * categories (AMBIENT, MUSIC, WEATHER, HOSTILE, NEUTRAL, RECORDS), the packet is
 * cancelled entirely — it never leaves the server.
 *
 * Creature footstep sounds bypass this filter via CreatureSoundHelper.bypassSoundFilter.
 */
@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class SoundFilterMixin {

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void deeperdark$filterSoundPackets(Packet<?> packet, CallbackInfo ci) {
        // Only filter PlaySoundS2CPackets
        if (!(packet instanceof ClientboundSoundPacket soundPacket)) return;

        // Allow creature footstep sounds through (bypass flag set by CreatureSoundHelper)
        if (CreatureSoundHelper.bypassSoundFilter) return;

        // Only applies when the handler is a play-phase handler with a player reference
        if (!((Object) this instanceof ServerGamePacketListenerImpl sph)) return;

        // Check if this player's sounds should be suppressed
        if (!CreatureManager.isPlayerSoundSuppressed(sph.player.getUUID())) return;

        // Suppress the specified categories per the creature spec
        SoundSource category = soundPacket.getSource();
        if (category == SoundSource.AMBIENT
                || category == SoundSource.MUSIC
                || category == SoundSource.WEATHER
                || category == SoundSource.HOSTILE
                || category == SoundSource.NEUTRAL
                || category == SoundSource.RECORDS) {
            ci.cancel();
        }
    }
}
