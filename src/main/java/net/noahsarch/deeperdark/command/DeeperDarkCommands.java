package net.noahsarch.deeperdark.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.config.ConfigMenu;
import net.noahsarch.deeperdark.config.ConfigOption;
import net.noahsarch.deeperdark.config.ConfigOptions;
import net.noahsarch.deeperdark.config.ConfigSync;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
//? if >=26.3 {
/*import net.minecraft.util.Prediction;
*///?}

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Consolidated command system for DeeperDark mod.
 *
 * All commands live under the "/dd" root. Operator-level subcommands require
 * permission level 2. The "player_sounds" subtree is open to all players for
 * self-targeting; "set <player>" within it requires operator.
 *
 * Tree overview:
 *   /dd                       (opens the config menu, see ConfigMenu)
 *   /dd give <item> [amount]
 *   /dd reload
 *   /dd border origin/radius/force/info
 *   /dd border config allow_entity_spawning / push_survival_only [value]
 *   /dd creature clear/list/summon/spawn/config ...
 *   /dd creepiness [config <setting> [value] | trigger <haunt> [player]]
 *   /dd player_sounds [toggle] / volume / send / death / join / hurt / pitch / pitchdeviance
 *   /dd player_sounds set <player> ...   (op only)
 *   /dd config list / <setting> [value]   (generated from ConfigOptions: every setting in the menu)
 */
public class DeeperDarkCommands {

    private static final Map<String, Supplier<ItemStack>> REGISTRY = new HashMap<>();

    private static final Predicate<CommandSourceStack> IS_OP =
        source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS));

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dd")
            .executes(DeeperDarkCommands::executeOpenMenu)

            // dd give <item> [amount]
            .then(Commands.literal("give")
                .requires(IS_OP)
                .then(Commands.argument("item", StringArgumentType.word())
                    .suggests(DeeperDarkCommands::suggestItems)
                    .executes(DeeperDarkCommands::executeGive)
                    .then(Commands.argument("amount", IntegerArgumentType.integer(1, 64))
                        .executes(DeeperDarkCommands::executeGiveWithAmount))))

            // dd border
            .then(Commands.literal("border")
                .requires(IS_OP)
                .then(Commands.literal("origin")
                    .then(Commands.argument("x", IntegerArgumentType.integer())
                        .then(Commands.argument("z", IntegerArgumentType.integer())
                            .executes(DeeperDarkCommands::executeSetOrigin))))
                .then(Commands.literal("radius")
                    .then(Commands.argument("radius", DoubleArgumentType.doubleArg(1.0))
                        .executes(DeeperDarkCommands::executeSetRadius)))
                .then(Commands.literal("force")
                    .then(Commands.argument("multiplier", DoubleArgumentType.doubleArg(0.0))
                        .executes(DeeperDarkCommands::executeSetForce)))
                .then(Commands.literal("info")
                    .executes(DeeperDarkCommands::executeBorderInfo))
                .then(Commands.literal("config")
                    .then(optionNode(ConfigOptions.SERVER.get("allow_entity_spawning")))
                    .then(optionNode(ConfigOptions.SERVER.get("push_survival_only")))))

            // dd creature (delegated to CreatureCommands)
            .then(net.noahsarch.deeperdark.creature.CreatureCommands.buildCreatureCommand()
                .requires(IS_OP))

            // dd creepiness (delegated to CreepinessCommands)
            .then(net.noahsarch.deeperdark.creepiness.CreepinessCommands.buildCreepinessCommand()
                .requires(IS_OP))

            // dd player_sounds (any player; set <player> requires op)
            .then(PlayerSoundsCommands.buildPlayerSoundsCommand())

            // dd config list / <setting> [value]: every setting in the /dd menu
            .then(configCommand())

            // dd reload
            .then(Commands.literal("reload")
                .requires(IS_OP)
                .executes(DeeperDarkCommands::executeReload)));
    }

    // ===== Suggestions =====

    private static CompletableFuture<Suggestions> suggestItems(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(REGISTRY.keySet(), builder);
    }

    // ===== Config menu =====

    private static int executeOpenMenu(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (ConfigMenu.open(context.getSource().getPlayerOrException())) return 1;
        context.getSource().sendFailure(Component.translatable("deeperdark.config.no_client_mod").withStyle(ChatFormatting.RED));
        return 0;
    }

    // ===== Give command =====

    private static int executeGive(CommandContext<CommandSourceStack> context) {
        return executeGiveInternal(context, 1);
    }

    private static int executeGiveWithAmount(CommandContext<CommandSourceStack> context) {
        int amount = IntegerArgumentType.getInteger(context, "amount");
        return executeGiveInternal(context, amount);
    }

    private static int executeGiveInternal(CommandContext<CommandSourceStack> context, int amount) {
        CommandSourceStack source = context.getSource();
        String itemName = StringArgumentType.getString(context, "item");

        Supplier<ItemStack> factory = REGISTRY.get(itemName);
        if (factory == null) {
            source.sendFailure(Component.literal("Unknown item: " + itemName).withStyle(ChatFormatting.RED));
            return 0;
        }

        try {
            net.minecraft.server.level.ServerPlayer player = source.getPlayerOrException();
            ItemStack giveStack = factory.get();
            giveStack.setCount(amount);

            boolean inserted = player.getInventory().add(giveStack);
            if (!inserted) {
                //? if >=26.3 {
                /*player.drop(giveStack, false, Prediction.SERVER_ONLY);
                *///?} else {
                player.drop(giveStack, false);
                //?}
            }

            source.sendSuccess(() -> Component.literal("Gave ")
                .append(Component.literal(String.valueOf(amount)).withStyle(ChatFormatting.GOLD))
                .append(Component.literal(" ["))
                .append(Component.literal(itemName).withStyle(ChatFormatting.AQUA))
                .append(Component.literal("] to "))
                .append(Component.literal(player.getName().getString()).withStyle(ChatFormatting.YELLOW)), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to give item: " + e.getMessage()).withStyle(ChatFormatting.RED));
            return 0;
        }
    }

    // ===== Border shortcut commands =====

    private static int executeSetOrigin(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        int x = IntegerArgumentType.getInteger(context, "x");
        int z = IntegerArgumentType.getInteger(context, "z");

        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        config.originX = x;
        config.originZ = z;
        DeeperDarkConfig.save();

        source.sendSuccess(() -> Component.literal("Border origin set to ")
            .append(Component.literal("(" + x + ", " + z + ")").withStyle(ChatFormatting.AQUA)), true);
        return 1;
    }

    private static int executeSetRadius(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        double radius = DoubleArgumentType.getDouble(context, "radius");

        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        config.safeRadius = radius;
        DeeperDarkConfig.save();

        source.sendSuccess(() -> Component.literal("Border radius set to ")
            .append(Component.literal(String.valueOf(radius)).withStyle(ChatFormatting.AQUA))
            .append(Component.literal(" blocks")), true);
        return 1;
    }

    private static int executeSetForce(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        double multiplier = DoubleArgumentType.getDouble(context, "multiplier");

        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        config.forceMultiplier = multiplier;
        DeeperDarkConfig.save();

        source.sendSuccess(() -> Component.literal("Border force multiplier set to ")
            .append(Component.literal(String.valueOf(multiplier)).withStyle(ChatFormatting.AQUA)), true);
        return 1;
    }

    private static int executeBorderInfo(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        source.sendSuccess(() -> Component.literal("===== Border Configuration =====").withStyle(ChatFormatting.GOLD)
            .append(Component.literal("\nOrigin: ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("(" + config.originX + ", " + config.originZ + ")").withStyle(ChatFormatting.AQUA))
            .append(Component.literal("\nRadius: ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal(String.valueOf(config.safeRadius)).withStyle(ChatFormatting.AQUA))
            .append(Component.literal(" blocks").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("\nForce Multiplier: ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal(String.valueOf(config.forceMultiplier)).withStyle(ChatFormatting.AQUA))
            .append(Component.literal("\nAllow Entity Spawning Outside: ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal(String.valueOf(config.allowEntitySpawning)).withStyle(config.allowEntitySpawning ? ChatFormatting.GREEN : ChatFormatting.RED))
            .append(Component.literal("\nPush Survival Mode Only: ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal(String.valueOf(config.pushSurvivalModeOnly)).withStyle(config.pushSurvivalModeOnly ? ChatFormatting.GREEN : ChatFormatting.RED)), false);
        return 1;
    }

    // ===== Config (generated from ConfigOptions) =====

    private static LiteralArgumentBuilder<CommandSourceStack> configCommand() {
        LiteralArgumentBuilder<CommandSourceStack> config = Commands.literal("config")
            .requires(IS_OP)
            .then(Commands.literal("list").executes(DeeperDarkCommands::executeConfigList));
        ConfigOptions.SERVER.values().forEach(option -> config.then(optionNode(option)));
        return config;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> optionNode(ConfigOption<DeeperDarkConfig.ConfigInstance> option) {
        ArgumentType<?> type = switch (option.kind()) {
            case BOOL -> BoolArgumentType.bool();
            case INT -> IntegerArgumentType.integer((int) option.min(), (int) option.max());
            case DOUBLE -> DoubleArgumentType.doubleArg(option.min(), option.max());
            case SOUND -> StringArgumentType.string();
        };
        return Commands.literal(option.key())
            .executes(ctx -> executeConfigQuery(ctx, option))
            .then(Commands.argument("value", type).executes(ctx -> executeConfigSet(ctx, option)));
    }

    private static int executeConfigList(CommandContext<CommandSourceStack> context) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        MutableComponent message = Component.literal("===== DeeperDark Configuration =====").withStyle(ChatFormatting.GOLD);
        for (ConfigOptions.Category category : ConfigOptions.CATEGORIES) {
            message.append(Component.literal("\n\n--- ").withStyle(ChatFormatting.YELLOW))
                .append(Component.translatable("deeperdark.config.category." + category.id()).withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(" ---").withStyle(ChatFormatting.YELLOW));
            category.options().forEach(option -> message.append(formatConfigLine(option.key(), option.get(config))));
        }
        message.append(Component.literal("\n\nUse /dd config <setting> [value] to change or query").withStyle(ChatFormatting.GRAY));
        context.getSource().sendSuccess(() -> message, false);
        return 1;
    }

    private static Component formatConfigLine(String key, Object value) {
        return Component.literal("\n  " + key + ": ").withStyle(ChatFormatting.WHITE)
            .append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.AQUA));
    }

    private static int executeConfigQuery(CommandContext<CommandSourceStack> context, ConfigOption<DeeperDarkConfig.ConfigInstance> option) {
        context.getSource().sendSuccess(() -> formatConfigLine(option.key(), option.get(DeeperDarkConfig.get())), false);
        return 1;
    }

    private static int executeConfigSet(CommandContext<CommandSourceStack> context, ConfigOption<DeeperDarkConfig.ConfigInstance> option) {
        CommandSourceStack source = context.getSource();
        Object value = context.getArgument("value", Object.class);
        if (!option.set(DeeperDarkConfig.get(), String.valueOf(value))) {
            source.sendFailure(Component.literal("Invalid value for " + option.key() + ": " + value).withStyle(ChatFormatting.RED));
            return 0;
        }
        DeeperDarkConfig.save();
        ConfigSync.broadcastIfSynced(source.getServer(), List.of(option.key()));

        ChatFormatting color = value instanceof Boolean b ? (b ? ChatFormatting.GREEN : ChatFormatting.RED) : ChatFormatting.GOLD;
        source.sendSuccess(() -> Component.literal("Set ")
            .append(Component.literal(option.key()).withStyle(ChatFormatting.AQUA))
            .append(Component.literal(" to "))
            .append(Component.literal(String.valueOf(value)).withStyle(color)), true);
        return 1;
    }

    // ===== Reload =====

    private static int executeReload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            DeeperDarkConfig.load();
            ConfigSync.broadcastIfSynced(source.getServer(), ConfigOptions.SYNCED);
            source.sendSuccess(() -> Component.literal("Successfully reloaded DeeperDark configuration from disk").withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to reload configuration: " + e.getMessage()).withStyle(ChatFormatting.RED));
            return 0;
        }
    }
}
