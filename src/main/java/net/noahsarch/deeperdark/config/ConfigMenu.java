package net.noahsarch.deeperdark.config;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.command.PlayerSoundsCommands;
import net.noahsarch.deeperdark.payload.AllIngredientsConsumableSyncPacket;
import net.noahsarch.deeperdark.payload.ConfigChangesPayload;
import net.noahsarch.deeperdark.payload.ConfigMenuPayload;
import net.noahsarch.deeperdark.payload.VoidFogSyncPacket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Server side of the config menu, modelled on vanilla's in-world game rules screen: "/dd" sends
 * the values a player may edit, and the client sends back what changed. Nothing from the client
 * is trusted: every key is re-checked against the sender's permissions and every value is parsed.
 *
 * Keys: a {@link ConfigOptions#SERVER} key (operators only), "self.KEY" for the sender's own
 * {@link ConfigOptions#LISTENER} settings, and "player.NAME.KEY" for a {@link ConfigOptions#PROFILE}
 * field (own name, or anyone's for operators).
 */
public final class ConfigMenu {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("DeeperDarkConfigMenu");
    public static final String SELF = "self.";
    public static final String PLAYER = "player.";

    private ConfigMenu() {}

    /** Returns false when the player's client doesn't have the mod and can't show the menu. */
    public static boolean open(ServerPlayer player) {
        if (!ServerPlayNetworking.canSend(player, ConfigMenuPayload.ID)) return false;

        boolean operator = isOperator(player);
        String self = player.getName().getString();
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        Map<String, String> values = new HashMap<>();

        ConfigOptions.LISTENER.forEach(option -> values.put(SELF + option.key(), option.get(self)));
        putProfile(values, self, findProfile(self));
        if (operator) {
            ConfigOptions.SERVER.values().forEach(option -> values.put(option.key(), option.get(config)));
            config.playerSounds.forEach((name, profile) -> {
                if (!name.equalsIgnoreCase(self)) putProfile(values, name, profile);
            });
        }

        ServerPlayNetworking.send(player, new ConfigMenuPayload(operator, values));
        return true;
    }

    public static void apply(ServerPlayer player, ConfigChangesPayload payload) {
        boolean operator = isOperator(player);
        String self = player.getName().getString();
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        Set<String> applied = new HashSet<>();
        payload.changes().forEach((key, value) -> {
            if (applyOne(key, value, operator, self, config)) applied.add(key);
        });
        int rejected = payload.changes().size() - applied.size();
        if (rejected > 0) LOGGER.warn("Rejected {} config change(s) from {}", rejected, self);
        if (applied.isEmpty()) return;

        DeeperDarkConfig.save();
        LOGGER.info("{} changed DeeperDark settings: {}", self, applied);
        if (applied.contains("void_fog_enabled")) {
            VoidFogSyncPacket packet = new VoidFogSyncPacket(config.voidFogEnabled);
            player.level().getServer().getPlayerList().getPlayers().forEach(p -> ServerPlayNetworking.send(p, packet));
        }
        if (applied.contains("all_ingredients_consumable")) {
            AllIngredientsConsumableSyncPacket packet = new AllIngredientsConsumableSyncPacket(config.allIngredientsConsumable);
            player.level().getServer().getPlayerList().getPlayers().forEach(p -> ServerPlayNetworking.send(p, packet));
        }
        player.sendSystemMessage(Component.translatable("deeperdark.config.saved", applied.size()).withStyle(ChatFormatting.GREEN));
    }

    private static boolean applyOne(String key, String value, boolean operator, String self, DeeperDarkConfig.ConfigInstance config) {
        if (key.startsWith(SELF)) {
            return ConfigOptions.find(ConfigOptions.LISTENER, key.substring(SELF.length()))
                .map(option -> option.set(self, value)).orElse(false);
        }
        if (key.startsWith(PLAYER)) {
            // Fields have no dots but names can (Floodgate prefixes Bedrock players with '.')
            String rest = key.substring(PLAYER.length());
            int dot = rest.lastIndexOf('.');
            if (dot < 1) return false;
            String name = rest.substring(0, dot);
            if (!(operator || name.equalsIgnoreCase(self))) return false;
            return ConfigOptions.find(ConfigOptions.PROFILE, rest.substring(dot + 1))
                .map(option -> option.set(PlayerSoundsCommands.getOrCreateProfile(name), value)).orElse(false);
        }
        ConfigOption<DeeperDarkConfig.ConfigInstance> option = ConfigOptions.SERVER.get(key);
        return operator && option != null && option.set(config, value);
    }

    private static void putProfile(Map<String, String> values, String name, DeeperDarkConfig.PlayerSoundProfile profile) {
        ConfigOptions.PROFILE.forEach(option -> values.put(PLAYER + name + "." + option.key(), option.get(profile)));
    }

    private static DeeperDarkConfig.PlayerSoundProfile findProfile(String name) {
        return DeeperDarkConfig.get().playerSounds.entrySet().stream()
            .filter(entry -> entry.getKey().equalsIgnoreCase(name))
            .map(Map.Entry::getValue).findFirst()
            .orElseGet(DeeperDarkConfig.PlayerSoundProfile::new);
    }

    private static boolean isOperator(ServerPlayer player) {
        return player.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS));
    }
}
