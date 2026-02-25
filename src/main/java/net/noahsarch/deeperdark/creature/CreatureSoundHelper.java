package net.noahsarch.deeperdark.creature;

import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

/**
 * Helper for playing creature sounds to specific players using direct packets.
 * Uses the Direct RegistryEntry approach to bypass registry sync:
 * clients with the resource pack hear the sound, clients without hear nothing.
 */
public class CreatureSoundHelper {

    private static final String MOD_ID = "deeperdark";

    /**
     * When true, the SoundFilterMixin will allow the current PlaySoundS2CPacket through
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
            AMBIENCE_IDS[i] = Identifier.of(MOD_ID, "ambient.creature.ambience" + i);
        }
        HUSH_ID = Identifier.of(MOD_ID, "entity.creature.hush0");
    }

    /**
     * Plays one of the creature ambience sounds to a specific player.
     *
     * @param player  The player to send the sound to
     * @param variant Which ambience variant to play (0-6)
     * @param pos     Position to play the sound from
     * @param volume  Volume of the sound (distance-dependent)
     */
    public static void playAmbienceSound(ServerPlayerEntity player, int variant, Vec3d pos, float volume) {
        if (variant < 0 || variant > 7) variant = 0;
        Identifier soundId = AMBIENCE_IDS[variant];
        RegistryEntry<SoundEvent> soundEntry = RegistryEntry.of(SoundEvent.of(soundId));

        player.networkHandler.sendPacket(new PlaySoundS2CPacket(
                soundEntry,
                SoundCategory.AMBIENT,
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
    public static void playHushSound(ServerPlayerEntity player, Vec3d pos, float volume) {
        RegistryEntry<SoundEvent> soundEntry = RegistryEntry.of(SoundEvent.of(HUSH_ID));

        player.networkHandler.sendPacket(new PlaySoundS2CPacket(
                soundEntry,
                SoundCategory.HOSTILE,
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
    public static void playFootstepSound(ServerPlayerEntity player, Vec3d pos, net.minecraft.server.world.ServerWorld world) {
        net.minecraft.util.math.BlockPos blockBelow = net.minecraft.util.math.BlockPos.ofFloored(pos).down();
        net.minecraft.block.BlockState stateBelow = world.getBlockState(blockBelow);
        net.minecraft.sound.BlockSoundGroup soundGroup = stateBelow.getSoundGroup();

        RegistryEntry<SoundEvent> stepSound = RegistryEntry.of(soundGroup.getStepSound());

        // Bypass the mixin sound filter so creature footsteps reach the chased player
        bypassSoundFilter = true;
        try {
            player.networkHandler.sendPacket(new PlaySoundS2CPacket(
                    stepSound,
                    SoundCategory.HOSTILE,
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
    public static void stopAllCreatureSounds(ServerPlayerEntity player) {
        // Stop all ambience sounds
        for (int i = 0; i < 8; i++) {
            player.networkHandler.sendPacket(new StopSoundS2CPacket(AMBIENCE_IDS[i], SoundCategory.AMBIENT));
        }
        // Stop hush sound
        player.networkHandler.sendPacket(new StopSoundS2CPacket(HUSH_ID, SoundCategory.HOSTILE));
    }

    /**
     * Stops a specific ambience sound for a player.
     */
    public static void stopAmbienceSound(ServerPlayerEntity player, int variant) {
        if (variant < 0 || variant > 7) return;
        player.networkHandler.sendPacket(new StopSoundS2CPacket(AMBIENCE_IDS[variant], SoundCategory.AMBIENT));
    }

    /**
     * Stops the hush sound for a player.
     */
    public static void stopHushSound(ServerPlayerEntity player) {
        player.networkHandler.sendPacket(new StopSoundS2CPacket(HUSH_ID, SoundCategory.HOSTILE));
    }

    /**
     * Stops all non-essential sounds for a player during chase sequence.
     * Silences ambient, music, weather, hostile, and neutral categories so the player
     * only hears their own footsteps (PLAYERS) and the creature's footsteps (HOSTILE steps are re-played each tick).
     */
    public static void stopAllAmbientSoundsForChase(ServerPlayerEntity player) {
        // Stop broad categories per spec: AMBIENT, MUSIC, WEATHER, HOSTILE, NEUTRAL, RECORDS
        // Creature footsteps are continuously re-sent so they won't stay muted
        player.networkHandler.sendPacket(new StopSoundS2CPacket(null, SoundCategory.AMBIENT));
        player.networkHandler.sendPacket(new StopSoundS2CPacket(null, SoundCategory.MUSIC));
        player.networkHandler.sendPacket(new StopSoundS2CPacket(null, SoundCategory.WEATHER));
        player.networkHandler.sendPacket(new StopSoundS2CPacket(null, SoundCategory.HOSTILE));
        player.networkHandler.sendPacket(new StopSoundS2CPacket(null, SoundCategory.NEUTRAL));
        player.networkHandler.sendPacket(new StopSoundS2CPacket(null, SoundCategory.RECORDS));
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
