package net.noahsarch.deeperdark.config;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.payload.ConfigSyncPayload;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** Sends the {@link ConfigOptions#SYNCED} settings to clients. */
public final class ConfigSync {

    private ConfigSync() {}

    public static void send(ServerPlayer player) {
        send(player, payload());
    }

    /** Resends to everyone if any of {@code changedKeys} is synced. */
    public static void broadcastIfSynced(MinecraftServer server, Collection<String> changedKeys) {
        if (changedKeys.stream().noneMatch(ConfigOptions.SYNCED::contains)) return;
        ConfigSyncPayload payload = payload();
        server.getPlayerList().getPlayers().forEach(player -> send(player, payload));
    }

    private static void send(ServerPlayer player, ConfigSyncPayload payload) {
        if (ServerPlayNetworking.canSend(player, ConfigSyncPayload.ID)) ServerPlayNetworking.send(player, payload);
    }

    private static ConfigSyncPayload payload() {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        Map<String, String> values = new HashMap<>();
        ConfigOptions.SYNCED.forEach(key -> values.put(key, ConfigOptions.SERVER.get(key).get(config)));
        return new ConfigSyncPayload(values);
    }
}
