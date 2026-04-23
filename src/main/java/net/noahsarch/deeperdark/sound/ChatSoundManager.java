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

/**
 * Plays configurable chat-related sounds (message, join, death) to all players.
 * Uses direct PlaySound packets so sounds can come from resource packs without requiring
 * client-side registry sync.
 */
public class ChatSoundManager {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("ChatSoundManager");

    public static void playSendMessageSound(ServerPlayer sourcePlayer) {
        DeeperDarkConfig.ChatSoundProfile profile = getProfileForPlayer(sourcePlayer);
        if (profile == null) return;
        playGlobalSound(sourcePlayer, profile.sendMessageSound, profile.pitch, profile.pitchDeviance);
    }

    public static void playDeathMessageSound(ServerPlayer sourcePlayer) {
        DeeperDarkConfig.ChatSoundProfile profile = getProfileForPlayer(sourcePlayer);
        if (profile == null) return;
        playGlobalSound(sourcePlayer, profile.deathMessageSound, profile.pitch, profile.pitchDeviance);
    }

    public static void playJoinMessageSound(ServerPlayer sourcePlayer) {
        DeeperDarkConfig.ChatSoundProfile profile = getProfileForPlayer(sourcePlayer);
        if (profile == null) return;
        playGlobalSound(sourcePlayer, profile.joinMessageSound, profile.pitch, profile.pitchDeviance);
    }

    private static DeeperDarkConfig.ChatSoundProfile getProfileForPlayer(ServerPlayer player) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        if (config.chatSounds == null || config.chatSounds.isEmpty()) return null;

        String playerName = player.getName().getString();

        // Fast exact lookup first
        DeeperDarkConfig.ChatSoundProfile exact = config.chatSounds.get(playerName);
        if (exact != null) return exact;

        // Case-insensitive fallback
        for (Map.Entry<String, DeeperDarkConfig.ChatSoundProfile> entry : config.chatSounds.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(playerName)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private static void playGlobalSound(ServerPlayer sourcePlayer, String rawSoundId, double basePitch, double pitchDeviance) {
        if (rawSoundId == null || rawSoundId.isBlank()) return;

        MinecraftServer server = sourcePlayer.level().getServer();
        if (server == null) return;

        Identifier soundId = parseSoundId(rawSoundId.trim());
        if (soundId == null) return;

        Holder<SoundEvent> soundEntry = Holder.direct(SoundEvent.createVariableRangeEvent(soundId));
        double deviance = Math.max(0.0, pitchDeviance);
        double randomOffset = (sourcePlayer.level().getRandom().nextDouble() * 2.0 - 1.0) * deviance;
        float finalPitch = (float) Math.max(0.01, basePitch + randomOffset);

        List<String> exclusions = DeeperDarkConfig.get().chatSoundExclusions;

        for (ServerPlayer target : server.getPlayerList().getPlayers()) {
            String targetName = target.getName().getString().toLowerCase(Locale.ROOT);
            if (exclusions != null && exclusions.contains(targetName)) {
                continue;
            }

            // Play at the receiver's location so chat sounds are globally audible regardless of distance.
            target.connection.send(new ClientboundSoundPacket(
                    soundEntry,
                    SoundSource.PLAYERS,
                    target.getX(), target.getY(), target.getZ(),
                    1.0f,
                    finalPitch,
                    sourcePlayer.level().getRandom().nextLong()
            ));
        }
    }

    private static Identifier parseSoundId(String rawSoundId) {
        try {
            // Explicit namespace always wins
            if (rawSoundId.contains(":")) {
                return Identifier.withDefaultNamespace(rawSoundId);
            }

            // Convenience for this mod's custom snake ids used in config examples
            if (rawSoundId.startsWith("entity.snake.")) {
                return Identifier.fromNamespaceAndPath("deeperdark", rawSoundId);
            }

            // Default to vanilla ids for shorthand values like entity.cat.ambient
            return Identifier.withDefaultNamespace(rawSoundId);
        } catch (Exception e) {
            LOGGER.warn("[ChatSounds] Invalid sound id '{}': {}", rawSoundId, e.getMessage());
            return null;
        }
    }
}
