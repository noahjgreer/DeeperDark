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
        Holder<SoundEvent> soundEntry = Holder.of(SoundEvent.of(soundId));

        player.networkHandler.sendPacket(new ClientboundSoundPacket(
                soundEntry,
                SoundSource.AMBIENT,
                pos.x, pos.y, pos.z,
                volume,
                1.0f,
                player.getEntityWorld().getRandom().nextLong()
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
        Holder<SoundEvent> soundEntry = Holder.of(SoundEvent.of(HUSH_ID));

        player.networkHandler.sendPacket(new ClientboundSoundPacket(
                soundEntry,
                SoundSource.HOSTILE,
                pos.x, pos.y, pos.z,
                volume,
                1.0f,
                player.getEntityWorld().getRandom().nextLong()
        ));
    }

    /**
     * Plays a block-dependent footstep sound at the creature's position.
     * Uses the block below the creature to determine the sound.
     */
    public static void playFootstepSound(ServerPlayer player, Vec3 pos, net.minecraft.server.level.ServerLevel world) {
        net.minecraft.core.BlockPos blockBelow = net.minecraft.core.BlockPos.ofFloored(pos).down();
        net.minecraft.world.level.block.state.BlockState stateBelow = world.getBlockState(blockBelow);
        net.minecraft.world.level.block.SoundType soundGroup = stateBelow.getSoundGroup();

        Holder<SoundEvent> stepSound = Holder.of(soundGroup.getStepSound());

        // Bypass the mixin sound filter so creature footsteps reach the chased player
        bypassSoundFilter = true;
        try {
            player.networkHandler.sendPacket(new ClientboundSoundPacket(
                    stepSound,
                    SoundSource.HOSTILE,
                    pos.x, pos.y, pos.z,
                    2.0f,  // Loud volume
                    1.8f,  // Higher pitch for rapid, scary footsteps
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
        // Stop all ambience sounds
        for (int i = 0; i < 8; i++) {
            player.networkHandler.sendPacket(new ClientboundStopSoundPacket(AMBIENCE_IDS[i], SoundSource.AMBIENT));
        }
        // Stop hush sound
        player.networkHandler.sendPacket(new ClientboundStopSoundPacket(HUSH_ID, SoundSource.HOSTILE));
    }

    /**
     * Stops a specific ambience sound for a player.
     */
    public static void stopAmbienceSound(ServerPlayer player, int variant) {
        if (variant < 0 || variant > 7) return;
        player.networkHandler.sendPacket(new ClientboundStopSoundPacket(AMBIENCE_IDS[variant], SoundSource.AMBIENT));
    }

    /**
     * Stops the hush sound for a player.
     */
    public static void stopHushSound(ServerPlayer player) {
        player.networkHandler.sendPacket(new ClientboundStopSoundPacket(HUSH_ID, SoundSource.HOSTILE));
    }

    /**
     * Stops all non-essential sounds for a player during chase sequence.
     * Silences ambient, music, weather, hostile, and neutral categories so the player
     * only hears their own footsteps (PLAYERS) and the creature's footsteps (HOSTILE steps are re-played each tick).
     */
    public static void stopAllAmbientSoundsForChase(ServerPlayer player) {
        // Stop broad categories per spec: AMBIENT, MUSIC, WEATHER, HOSTILE, NEUTRAL, RECORDS
        // Creature footsteps are continuously re-sent so they won't stay muted
        player.networkHandler.sendPacket(new ClientboundStopSoundPacket(null, SoundSource.AMBIENT));
        player.networkHandler.sendPacket(new ClientboundStopSoundPacket(null, SoundSource.MUSIC));
        player.networkHandler.sendPacket(new ClientboundStopSoundPacket(null, SoundSource.WEATHER));
        player.networkHandler.sendPacket(new ClientboundStopSoundPacket(null, SoundSource.HOSTILE));
        player.networkHandler.sendPacket(new ClientboundStopSoundPacket(null, SoundSource.NEUTRAL));
        player.networkHandler.sendPacket(new ClientboundStopSoundPacket(null, SoundSource.RECORDS));
    }

    /**
     * Calculates the appropriate volume for the ambience sound based on distance.
     * The volume is set so the sound perfectly encompasses the distance from creature
     * to its minimum pathtracing range.
     *
     * @param distance  Current distance between player and creature
     * @param maxRange  Maximum range the sound should be heard from (min pathtrace distance)
     * @return Volume value for the sound packet
     */
    public static float calculateAmbienceVolume(double distance, int maxRange) {
        // MC's sound attenuation uses volume to scale the audible radius
        // Volume of 1.0 = audible at ~16 blocks; we want audible at maxRange blocks
        // So volume = maxRange / 16.0
        return Math.max(0.1f, (float) maxRange / 16.0f);
    }
}
