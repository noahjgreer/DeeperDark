package net.noahsarch.deeperdark.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.noahsarch.deeperdark.DeeperDarkConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Client-preference commands that players can run for personal settings.
 * Operators can additionally target other players via /ddclient chat_sounds set <player> ...
 */
public class DeeperDarkClientCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(Commands.literal("ddclient")
                .requires(source -> source.getEntity() instanceof ServerPlayer)
                .then(Commands.literal("chat_sounds")
                    .executes(DeeperDarkClientCommands::executeChatSoundsQuery)
                    .then(Commands.argument("enabled", BoolArgumentType.bool())
                        .executes(DeeperDarkClientCommands::executeChatSoundsSet))

                    // Volume (self only)
                    .then(Commands.literal("volume")
                        .executes(DeeperDarkClientCommands::executeVolumeQuery)
                        .then(Commands.argument("volume", DoubleArgumentType.doubleArg(0.0, 1.0))
                            .executes(DeeperDarkClientCommands::executeVolumeSet)))

                    // Per-field self-config
                    .then(Commands.literal("send")
                        .then(Commands.argument("sound", StringArgumentType.word())
                            .suggests(DeeperDarkClientCommands::suggestSounds)
                            .executes(ctx -> executeSetSelfProfileField(ctx, "send"))))
                    .then(Commands.literal("death")
                        .then(Commands.argument("sound", StringArgumentType.word())
                            .suggests(DeeperDarkClientCommands::suggestSounds)
                            .executes(ctx -> executeSetSelfProfileField(ctx, "death"))))
                    .then(Commands.literal("join")
                        .then(Commands.argument("sound", StringArgumentType.word())
                            .suggests(DeeperDarkClientCommands::suggestSounds)
                            .executes(ctx -> executeSetSelfProfileField(ctx, "join"))))
                    .then(Commands.literal("pitch")
                        .then(Commands.argument("pitch", DoubleArgumentType.doubleArg(0.01, 4.0))
                            .executes(ctx -> executeSetSelfProfileField(ctx, "pitch"))))
                    .then(Commands.literal("pitchdeviance")
                        .then(Commands.argument("pitchdeviance", DoubleArgumentType.doubleArg(0.0, 2.0))
                            .executes(ctx -> executeSetSelfProfileField(ctx, "pitchdeviance"))))

                    // Operator-only: set <player> <field> <value>
                    .then(Commands.literal("set")
                        .requires(src -> src.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)))
                        .then(Commands.argument("player", StringArgumentType.word())
                            .suggests(DeeperDarkClientCommands::suggestOnlinePlayers)
                            .then(Commands.literal("send")
                                .then(Commands.argument("sound", StringArgumentType.word())
                                    .suggests(DeeperDarkClientCommands::suggestSounds)
                                    .executes(ctx -> executeSetOpProfileField(ctx, "send"))))
                            .then(Commands.literal("death")
                                .then(Commands.argument("sound", StringArgumentType.word())
                                    .suggests(DeeperDarkClientCommands::suggestSounds)
                                    .executes(ctx -> executeSetOpProfileField(ctx, "death"))))
                            .then(Commands.literal("join")
                                .then(Commands.argument("sound", StringArgumentType.word())
                                    .suggests(DeeperDarkClientCommands::suggestSounds)
                                    .executes(ctx -> executeSetOpProfileField(ctx, "join"))))
                            .then(Commands.literal("pitch")
                                .then(Commands.argument("pitch", DoubleArgumentType.doubleArg(0.01, 4.0))
                                    .executes(ctx -> executeSetOpProfileField(ctx, "pitch"))))
                            .then(Commands.literal("pitchdeviance")
                                .then(Commands.argument("pitchdeviance", DoubleArgumentType.doubleArg(0.0, 2.0))
                                    .executes(ctx -> executeSetOpProfileField(ctx, "pitchdeviance"))))))))
        );
    }

    // ===== Suggestions =====

    private static CompletableFuture<Suggestions> suggestSounds(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(
            BuiltInRegistries.SOUND_EVENT.keySet().stream().map(Object::toString),
            builder
        );
    }

    private static CompletableFuture<Suggestions> suggestOnlinePlayers(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(
            ctx.getSource().getServer().getPlayerList().getPlayers().stream()
                .map(p -> p.getName().getString()),
            builder
        );
    }

    // ===== Chat sounds toggle =====

    private static int executeChatSoundsQuery(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = requirePlayer(context);
        if (player == null) return 0;

        boolean enabled = isChatSoundsEnabledFor(player);
        context.getSource().sendSuccess(() -> Component.literal("Chat sounds are currently ")
            .withStyle(ChatFormatting.WHITE)
            .append(Component.literal(enabled ? "enabled" : "disabled")
                .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED)), false);
        return 1;
    }

    private static int executeChatSoundsSet(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = requirePlayer(context);
        if (player == null) return 0;

        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        if (config.chatSoundExclusions == null) config.chatSoundExclusions = new ArrayList<>();

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

    // ===== Volume =====

    private static int executeVolumeQuery(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = requirePlayer(context);
        if (player == null) return 0;

        double volume = getVolumeFor(player);
        context.getSource().sendSuccess(() -> Component.literal("Chat sound volume: ")
            .withStyle(ChatFormatting.WHITE)
            .append(Component.literal(String.format("%.2f", volume)).withStyle(ChatFormatting.AQUA))
            .append(Component.literal(" (default: 0.80)").withStyle(ChatFormatting.GRAY)), false);
        return 1;
    }

    private static int executeVolumeSet(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = requirePlayer(context);
        if (player == null) return 0;

        double volume = DoubleArgumentType.getDouble(context, "volume");
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        if (config.chatSoundVolumes == null) config.chatSoundVolumes = new HashMap<>();

        config.chatSoundVolumes.put(player.getName().getString(), volume);
        DeeperDarkConfig.save();

        context.getSource().sendSuccess(() -> Component.literal("Chat sound volume set to ")
            .withStyle(ChatFormatting.WHITE)
            .append(Component.literal(String.format("%.2f", volume)).withStyle(ChatFormatting.AQUA))
            .append(Component.literal(".").withStyle(ChatFormatting.WHITE)), false);
        return 1;
    }

    // ===== Profile field commands =====

    private static int executeSetSelfProfileField(CommandContext<CommandSourceStack> context, String field) {
        ServerPlayer player = requirePlayer(context);
        if (player == null) return 0;
        return applyProfileField(context, player.getName().getString(), field);
    }

    private static int executeSetOpProfileField(CommandContext<CommandSourceStack> context, String field) {
        String targetName = StringArgumentType.getString(context, "player");
        return applyProfileField(context, targetName, field);
    }

    private static int applyProfileField(CommandContext<CommandSourceStack> context, String targetName, String field) {
        DeeperDarkConfig.ChatSoundProfile profile = getOrCreateProfile(targetName);

        String displayValue;
        switch (field) {
            case "send" -> {
                String sound = StringArgumentType.getString(context, "sound");
                profile.sendMessageSound = sound;
                displayValue = sound;
            }
            case "death" -> {
                String sound = StringArgumentType.getString(context, "sound");
                profile.deathMessageSound = sound;
                displayValue = sound;
            }
            case "join" -> {
                String sound = StringArgumentType.getString(context, "sound");
                profile.joinMessageSound = sound;
                displayValue = sound;
            }
            case "pitch" -> {
                double v = DoubleArgumentType.getDouble(context, "pitch");
                profile.pitch = v;
                displayValue = String.format("%.3f", v);
            }
            case "pitchdeviance" -> {
                double v = DoubleArgumentType.getDouble(context, "pitchdeviance");
                profile.pitchDeviance = v;
                displayValue = String.format("%.3f", v);
            }
            default -> {
                context.getSource().sendFailure(Component.literal("Unknown field: " + field).withStyle(ChatFormatting.RED));
                return 0;
            }
        }

        DeeperDarkConfig.save();

        String finalDisplay = displayValue;
        context.getSource().sendSuccess(() -> Component.literal("Set ")
            .withStyle(ChatFormatting.WHITE)
            .append(Component.literal(field).withStyle(ChatFormatting.AQUA))
            .append(Component.literal(" for ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal(targetName).withStyle(ChatFormatting.YELLOW))
            .append(Component.literal(" to ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal(finalDisplay).withStyle(ChatFormatting.GREEN)), false);
        return 1;
    }

    // ===== Helpers =====

    private static DeeperDarkConfig.ChatSoundProfile getOrCreateProfile(String playerName) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        if (config.chatSounds == null) config.chatSounds = new HashMap<>();
        DeeperDarkConfig.ChatSoundProfile profile = config.chatSounds.get(playerName);
        if (profile != null) return profile;
        for (Map.Entry<String, DeeperDarkConfig.ChatSoundProfile> entry : config.chatSounds.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(playerName)) return entry.getValue();
        }
        profile = new DeeperDarkConfig.ChatSoundProfile();
        config.chatSounds.put(playerName, profile);
        return profile;
    }

    private static double getVolumeFor(ServerPlayer player) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        if (config.chatSoundVolumes == null || config.chatSoundVolumes.isEmpty()) return 0.8;
        String name = player.getName().getString();
        Double vol = config.chatSoundVolumes.get(name);
        if (vol != null) return vol;
        for (Map.Entry<String, Double> entry : config.chatSoundVolumes.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) return entry.getValue();
        }
        return 0.8;
    }

    private static boolean isChatSoundsEnabledFor(ServerPlayer player) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        if (config.chatSoundExclusions == null) return true;
        String playerNameLower = player.getName().getString().toLowerCase(Locale.ROOT);
        return !containsIgnoreCase(config.chatSoundExclusions, playerNameLower);
    }

    private static ServerPlayer requirePlayer(CommandContext<CommandSourceStack> context) {
        try {
            return context.getSource().getPlayerOrException();
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("This command can only be used by players.").withStyle(ChatFormatting.RED));
            return null;
        }
    }

    private static boolean containsIgnoreCase(List<String> values, String target) {
        for (String value : values) {
            if (value != null && value.equalsIgnoreCase(target)) return true;
        }
        return false;
    }

    private static void removeIgnoreCase(List<String> values, String target) {
        values.removeIf(value -> value != null && value.equalsIgnoreCase(target));
    }
}
