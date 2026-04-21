package net.noahsarch.deeperdark.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
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
                dispatcher.register(Commands.literal("ddclient")
                        .requires(source -> source.getEntity() instanceof ServerPlayer)
                        .then(Commands.literal("chat_sounds")
                                .executes(DeeperDarkClientCommands::executeChatSoundsQuery)
                                .then(Commands.argument("enabled", BoolArgumentType.bool())
                                        .executes(DeeperDarkClientCommands::executeChatSoundsSet))))
        );
    }

    private static int executeChatSoundsQuery(CommandContext<CommandSourceStack> context) {
        ServerPlayer player;
        try {
            player = context.getSource().getPlayerOrException();
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("This command can only be used by players.").withStyle(ChatFormatting.RED));
            return 0;
        }

        boolean enabled = isChatSoundsEnabledFor(player);
        context.getSource().sendSuccess(() -> Component.literal("Chat sounds are currently ")
                .withStyle(ChatFormatting.WHITE)
                .append(Component.literal(enabled ? "enabled" : "disabled")
                        .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED)), false);
        return 1;
    }

    private static int executeChatSoundsSet(CommandContext<CommandSourceStack> context) {
        ServerPlayer player;
        try {
            player = context.getSource().getPlayerOrException();
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("This command can only be used by players.").withStyle(ChatFormatting.RED));
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

        context.getSource().sendSuccess(() -> Component.literal("Chat sounds ")
                .withStyle(ChatFormatting.WHITE)
                .append(Component.literal(enabled ? "enabled" : "disabled")
                        .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED))
                .append(Component.literal(" for you.").withStyle(ChatFormatting.WHITE)), false);
        return 1;
    }

    private static boolean isChatSoundsEnabledFor(ServerPlayer player) {
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
