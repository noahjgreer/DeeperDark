package net.noahsarch.deeperdark.sound;

import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.Identifier;
import net.noahsarch.deeperdark.DeeperDarkConfig;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PlayerSoundManager {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("PlayerSoundManager");
    private static final double HURT_SOUND_RADIUS = 16.0;

    public static void playSendMessageSound(ServerPlayer sourcePlayer) {
        DeeperDarkConfig.PlayerSoundProfile profile = getProfileForPlayer(sourcePlayer);
        if (profile == null) return;
        playGlobalSound(sourcePlayer, profile.sendMessageSound, profile.pitch, profile.pitchDeviance);
    }

    public static void playDeathMessageSound(ServerPlayer sourcePlayer) {
        DeeperDarkConfig.PlayerSoundProfile profile = getProfileForPlayer(sourcePlayer);
        if (profile == null) return;
        playGlobalSound(sourcePlayer, profile.deathMessageSound, profile.pitch, profile.pitchDeviance);
    }

    public static void playJoinMessageSound(ServerPlayer sourcePlayer) {
        DeeperDarkConfig.PlayerSoundProfile profile = getProfileForPlayer(sourcePlayer);
        if (profile == null) return;
        playGlobalSound(sourcePlayer, profile.joinMessageSound, profile.pitch, profile.pitchDeviance);
    }

    /**
     * Plays the custom hurt sound to nearby players only.
     * Returns true if a custom sound was played (caller should cancel the vanilla hurt sound).
     */
    public static boolean playHurtSound(ServerPlayer sourcePlayer) {
        DeeperDarkConfig.PlayerSoundProfile profile = getProfileForPlayer(sourcePlayer);
        if (profile == null || profile.hurtSound == null || profile.hurtSound.isBlank()) return false;

        MinecraftServer server = sourcePlayer.level().getServer();
        if (server == null) return false;

        Identifier soundId = parseSoundId(profile.hurtSound.trim());
        if (soundId == null) return false;

        Holder<SoundEvent> soundEntry = Holder.direct(SoundEvent.createVariableRangeEvent(soundId));
        double deviance = Math.max(0.0, profile.pitchDeviance);
        double randomOffset = (sourcePlayer.level().getRandom().nextDouble() * 2.0 - 1.0) * deviance;
        float finalPitch = (float) Math.max(0.01, profile.pitch + randomOffset);

        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        List<String> exclusions = config.playerSoundExclusions;

        double radiusSq = HURT_SOUND_RADIUS * HURT_SOUND_RADIUS;

        for (ServerPlayer target : server.getPlayerList().getPlayers()) {
            if (!target.level().dimension().equals(sourcePlayer.level().dimension())) continue;
            if (target.distanceToSqr(sourcePlayer.getX(), sourcePlayer.getY(), sourcePlayer.getZ()) > radiusSq) continue;

            String targetName = target.getName().getString().toLowerCase(Locale.ROOT);
            if (exclusions != null && exclusions.contains(targetName)) continue;

            float volume = getVolumeFor(target, config);

            target.connection.send(new ClientboundSoundPacket(
                    soundEntry,
                    SoundSource.PLAYERS,
                    sourcePlayer.getX(), sourcePlayer.getY(), sourcePlayer.getZ(),
                    volume,
                    finalPitch,
                    sourcePlayer.level().getRandom().nextLong()
            ));
        }

        return true;
    }

    private static DeeperDarkConfig.PlayerSoundProfile getProfileForPlayer(ServerPlayer player) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        if (config.playerSounds == null || config.playerSounds.isEmpty()) return null;

        String playerName = player.getName().getString();

        DeeperDarkConfig.PlayerSoundProfile exact = config.playerSounds.get(playerName);
        if (exact != null) return exact;

        for (Map.Entry<String, DeeperDarkConfig.PlayerSoundProfile> entry : config.playerSounds.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(playerName)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private static void playGlobalSound(ServerPlayer sourcePlayer, String rawSoundId, double basePitch, double pitchDeviance) {
        if (rawSoundId == null || rawSoundId.isBlank()) return;

        MinecraftServer server = sourcePlayer.level().getServer();

        Identifier soundId = parseSoundId(rawSoundId.trim());
        if (soundId == null) return;

        Holder<SoundEvent> soundEntry = Holder.direct(SoundEvent.createVariableRangeEvent(soundId));
        double deviance = Math.max(0.0, pitchDeviance);
        double randomOffset = (sourcePlayer.level().getRandom().nextDouble() * 2.0 - 1.0) * deviance;
        float finalPitch = (float) Math.max(0.01, basePitch + randomOffset);

        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        List<String> exclusions = config.playerSoundExclusions;

        for (ServerPlayer target : server.getPlayerList().getPlayers()) {
            String targetName = target.getName().getString().toLowerCase(Locale.ROOT);
            if (exclusions != null && exclusions.contains(targetName)) {
                continue;
            }

            float volume = getVolumeFor(target, config);

            // Play at the receiver's own location so the sound is globally audible regardless of distance.
            target.connection.send(new ClientboundSoundPacket(
                    soundEntry,
                    SoundSource.PLAYERS,
                    target.getX(), target.getY(), target.getZ(),
                    volume,
                    finalPitch,
                    sourcePlayer.level().getRandom().nextLong()
            ));
        }
    }

    private static float getVolumeFor(ServerPlayer target, DeeperDarkConfig.ConfigInstance config) {
        if (config.playerSoundVolumes == null || config.playerSoundVolumes.isEmpty()) return 0.8f;
        String name = target.getName().getString();
        Double vol = config.playerSoundVolumes.get(name);
        if (vol != null) return (float) Math.max(0.0, Math.min(1.0, vol));
        for (Map.Entry<String, Double> entry : config.playerSoundVolumes.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return (float) Math.max(0.0, Math.min(1.0, entry.getValue()));
            }
        }
        return 0.8f;
    }

    private static Identifier parseSoundId(String rawSoundId) {
        try {
            if (rawSoundId.contains(":")) {
                int colon = rawSoundId.indexOf(':');
                return Identifier.fromNamespaceAndPath(rawSoundId.substring(0, colon), rawSoundId.substring(colon + 1));
            }
            if (rawSoundId.startsWith("entity.snake.")) {
                return Identifier.fromNamespaceAndPath("deeperdark", rawSoundId);
            }
            return Identifier.fromNamespaceAndPath("minecraft", rawSoundId);
        } catch (Exception e) {
            LOGGER.warn("[PlayerSounds] Invalid sound id '{}': {}", rawSoundId, e.getMessage());
            return null;
        }
    }
}
