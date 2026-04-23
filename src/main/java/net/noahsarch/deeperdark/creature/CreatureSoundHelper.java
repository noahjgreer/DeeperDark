package net.noahsarch.deeperdark.creature;

import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

/**
 * Helper for playing creature sounds to specific players using direct packets.
 * Uses the Direct Holder approach to bypass registry sync:
 * clients with the resource pack hear the sound, clients without hear nothing.
 */
public class CreatureSoundHelper {

    private static final String MOD_ID = "deeperdark";

    /**
     * When true, the SoundFilterMixin will allow the current ClientboundSoundPacket through
     * even for chase-suppressed players. Set before sending creature footstep sounds,
     * cleared immediately after. Safe because game logic runs on a single thread.
     */
    public static boolean bypassSoundFilter = false;

    /** Ambience sound identifiers (ambience0 through ambience7) */
    private static final Identifier[] AMBIENCE_IDS = new Identifier[8];
    /** Hush sound identifier */
    private static final Identifier HUSH_ID;

    static {
        for (int i = 0; i < 8; i++) {
            AMBIENCE_IDS[i] = Identifier.fromNamespaceAndPath(MOD_ID, "ambient.creature.ambience" + i);
        }
        HUSH_ID = Identifier.fromNamespaceAndPath(MOD_ID, "entity.creature.hush0");
    }

    /**
     * Plays one of the creature ambience sounds to a specific player.
     *
     * @param player  The player to send the sound to
     * @param variant Which ambience variant to play (0-6)
     * @param pos     Position to play the sound from
     * @param volume  Volume of the sound (distance-dependent)
     */
    public static void playAmbienceSound(ServerPlayer player, int variant, Vec3 pos, float volume) {
        if (variant < 0 || variant > 7) variant = 0;
        Identifier soundId = AMBIENCE_IDS[variant];
        Holder<SoundEvent> soundEntry = Holder.direct(SoundEvent.createVariableRangeEvent(soundId));

        player.connection.send(new ClientboundSoundPacket(
                soundEntry,
                SoundSource.AMBIENT,
                pos.x, pos.y, pos.z,
                volume,
                1.0f,
                player.level().getRandom().nextLong()
        ));
    }

    /**
     * Plays the hush sound to a specific player.
     *
     * @param player The player to send the sound to
     * @param pos    Position to play the sound from
     * @param volume Volume of the sound
     */
    public static void playHushSound(ServerPlayer player, Vec3 pos, float volume) {
        Holder<SoundEvent> soundEntry = Holder.direct(SoundEvent.createVariableRangeEvent(HUSH_ID));

        player.connection.send(new ClientboundSoundPacket(
                soundEntry,
                SoundSource.HOSTILE,
                pos.x, pos.y, pos.z,
                volume,
                1.0f,
                player.level().getRandom().nextLong()
        ));
    }

    /**
     * Plays a block-dependent footstep sound at the creature's position.
     * Uses the block below the creature to determine the sound.
     */
    public static void playFootstepSound(ServerPlayer player, Vec3 pos, net.minecraft.server.level.ServerLevel world) {
        net.minecraft.core.BlockPos blockBelow = net.minecraft.core.BlockPos.containing(pos).below();
        net.minecraft.world.level.block.state.BlockState stateBelow = world.getBlockState(blockBelow);
        net.minecraft.world.level.block.SoundType soundGroup = stateBelow.getSoundType();

        Holder<SoundEvent> stepSound = Holder.direct(soundGroup.getStepSound());

        // Bypass the mixin sound filter so creature footsteps reach the chased player
        bypassSoundFilter = true;
        try {
            player.connection.send(new ClientboundSoundPacket(
                    stepSound,
                    SoundSource.HOSTILE,
                    pos.x, pos.y, pos.z,
                    2.0f,
                    1.8f,
                    world.getRandom().nextLong()
            ));
        } finally {
            bypassSoundFilter = false;
        }
    }

    /**
     * Stops all creature sounds for a player (both ambience and hush).
     */
    public static void stopAllCreatureSounds(ServerPlayer player) {
        for (int i = 0; i < 8; i++) {
            player.connection.send(new ClientboundStopSoundPacket(AMBIENCE_IDS[i], SoundSource.AMBIENT));
        }
        player.connection.send(new ClientboundStopSoundPacket(HUSH_ID, SoundSource.HOSTILE));
    }

    /**
     * Stops a specific ambience sound for a player.
     */
    public static void stopAmbienceSound(ServerPlayer player, int variant) {
        if (variant < 0 || variant > 7) return;
        player.connection.send(new ClientboundStopSoundPacket(AMBIENCE_IDS[variant], SoundSource.AMBIENT));
    }

    /**
     * Stops the hush sound for a player.
     */
    public static void stopHushSound(ServerPlayer player) {
        player.connection.send(new ClientboundStopSoundPacket(HUSH_ID, SoundSource.HOSTILE));
    }

    /**
     * Stops all non-essential sounds for a player during chase sequence.
     */
    public static void stopAllAmbientSoundsForChase(ServerPlayer player) {
        player.connection.send(new ClientboundStopSoundPacket(null, SoundSource.AMBIENT));
        player.connection.send(new ClientboundStopSoundPacket(null, SoundSource.MUSIC));
        player.connection.send(new ClientboundStopSoundPacket(null, SoundSource.WEATHER));
        player.connection.send(new ClientboundStopSoundPacket(null, SoundSource.HOSTILE));
        player.connection.send(new ClientboundStopSoundPacket(null, SoundSource.NEUTRAL));
        player.connection.send(new ClientboundStopSoundPacket(null, SoundSource.RECORDS));
    }

    /**
     * Calculates the appropriate volume for the ambience sound based on distance.
     */
    public static float calculateAmbienceVolume(double distance, int maxRange) {
        return Math.max(0.1f, (float) maxRange / 16.0f);
    }
}
