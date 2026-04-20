package net.noahsarch.deeperdark.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.noahsarch.deeperdark.DeeperDarkConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Client-preference commands that players can run for personal settings.
 */
public class DeeperDarkClientCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("ddclient")
                        .requires(source -> source.getEntity() instanceof ServerPlayerEntity)
                        .then(CommandManager.literal("chat_sounds")
                                .executes(DeeperDarkClientCommands::executeChatSoundsQuery)
                                .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                        .executes(DeeperDarkClientCommands::executeChatSoundsSet))))
        );
    }

    private static int executeChatSoundsQuery(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player;
        try {
            player = context.getSource().getPlayerOrThrow();
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("This command can only be used by players.").formatted(Formatting.RED));
            return 0;
        }

        boolean enabled = isChatSoundsEnabledFor(player);
        context.getSource().sendFeedback(() -> Text.literal("Chat sounds are currently ")
                .formatted(Formatting.WHITE)
                .append(Text.literal(enabled ? "enabled" : "disabled")
                        .formatted(enabled ? Formatting.GREEN : Formatting.RED)), false);
        return 1;
    }

    private static int executeChatSoundsSet(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player;
        try {
            player = context.getSource().getPlayerOrThrow();
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("This command can only be used by players.").formatted(Formatting.RED));
            return 0;
        }

        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        if (config.chatSoundExclusions == null) {
            config.chatSoundExclusions = new ArrayList<>();
        }

        String playerNameLower = player.getName().getString().toLowerCase(Locale.ROOT);

        if (enabled) {
            removeIgnoreCase(config.chatSoundExclusions, playerNameLower);
        } else if (!containsIgnoreCase(config.chatSoundExclusions, playerNameLower)) {
            config.chatSoundExclusions.add(playerNameLower);
        }

        DeeperDarkConfig.save();

        context.getSource().sendFeedback(() -> Text.literal("Chat sounds ")
                .formatted(Formatting.WHITE)
                .append(Text.literal(enabled ? "enabled" : "disabled")
                        .formatted(enabled ? Formatting.GREEN : Formatting.RED))
                .append(Text.literal(" for you.").formatted(Formatting.WHITE)), false);
        return 1;
    }

    private static boolean isChatSoundsEnabledFor(ServerPlayerEntity player) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        if (config.chatSoundExclusions == null) return true;
        String playerNameLower = player.getName().getString().toLowerCase(Locale.ROOT);
        return !containsIgnoreCase(config.chatSoundExclusions, playerNameLower);
    }

    private static boolean containsIgnoreCase(List<String> values, String target) {
        for (String value : values) {
            if (value != null && value.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    private static void removeIgnoreCase(List<String> values, String target) {
        values.removeIf(value -> value != null && value.equalsIgnoreCase(target));
    }
}
