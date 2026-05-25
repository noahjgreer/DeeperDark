package net.noahsarch.deeperdark.command;

import com.mojang.brigadier.CommandDispatcher;
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
import net.minecraft.ChatFormatting;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.payload.AllIngredientsConsumableSyncPacket;
import net.noahsarch.deeperdark.payload.VoidFogSyncPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;

import java.util.HashMap;
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
 *   /dd give <item> [amount]
 *   /dd reload
 *   /dd border origin/radius/force/info
 *   /dd border config allow_entity_spawning / push_survival_only
 *   /dd creature clear/list/summon/spawn/config ...
 *   /dd player_sounds [toggle] / volume / send / death / join / hurt / pitch / pitchdeviance
 *   /dd player_sounds set <player> ...   (op only)
 *   /dd config list / <snake_case_setting> [value]
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
                    .then(Commands.literal("allow_entity_spawning")
                        .executes(ctx -> executeConfigQuery(ctx, "allow_entity_spawning"))
                        .then(Commands.argument("value", BoolArgumentType.bool())
                            .executes(ctx -> executeConfigBoolean(ctx, "allow_entity_spawning"))))
                    .then(Commands.literal("push_survival_only")
                        .executes(ctx -> executeConfigQuery(ctx, "push_survival_only"))
                        .then(Commands.argument("value", BoolArgumentType.bool())
                            .executes(ctx -> executeConfigBoolean(ctx, "push_survival_only"))))))

            // dd creature (delegated to CreatureCommands)
            .then(net.noahsarch.deeperdark.creature.CreatureCommands.buildCreatureCommand()
                .requires(IS_OP))

            // dd player_sounds (any player; set <player> requires op)
            .then(PlayerSoundsCommands.buildPlayerSoundsCommand())

            // dd config <snake_case_setting> [value]
            .then(Commands.literal("config")
                .requires(IS_OP)
                .then(Commands.literal("list")
                    .executes(DeeperDarkCommands::executeConfigList))

                // Nether
                .then(Commands.literal("nether_coordinate_multiplier")
                    .executes(ctx -> executeConfigQuery(ctx, "nether_coordinate_multiplier"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0))
                        .executes(ctx -> executeConfigDouble(ctx, "nether_coordinate_multiplier"))))

                // Piston
                .then(Commands.literal("piston_push_limit")
                    .executes(ctx -> executeConfigQuery(ctx, "piston_push_limit"))
                    .then(Commands.argument("value", IntegerArgumentType.integer(1, 1000))
                        .executes(ctx -> executeConfigInt(ctx, "piston_push_limit"))))

                // Enderman
                .then(Commands.literal("enderman_pick_up_all_blocks")
                    .executes(ctx -> executeConfigQuery(ctx, "enderman_pick_up_all_blocks"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "enderman_pick_up_all_blocks"))))

                // Baby mobs
                .then(Commands.literal("baby_skeletons_enabled")
                    .executes(ctx -> executeConfigQuery(ctx, "baby_skeletons_enabled"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "baby_skeletons_enabled"))))
                .then(Commands.literal("baby_creepers_enabled")
                    .executes(ctx -> executeConfigQuery(ctx, "baby_creepers_enabled"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "baby_creepers_enabled"))))
                .then(Commands.literal("baby_spiders_enabled")
                    .executes(ctx -> executeConfigQuery(ctx, "baby_spiders_enabled"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "baby_spiders_enabled"))))

                // Creeper effects
                .then(Commands.literal("creeper_effect_min_seconds")
                    .executes(ctx -> executeConfigQuery(ctx, "creeper_effect_min_seconds"))
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeConfigInt(ctx, "creeper_effect_min_seconds"))))
                .then(Commands.literal("creeper_effect_max_seconds")
                    .executes(ctx -> executeConfigQuery(ctx, "creeper_effect_max_seconds"))
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeConfigInt(ctx, "creeper_effect_max_seconds"))))

                // Fortune
                .then(Commands.literal("custom_fortune_enabled")
                    .executes(ctx -> executeConfigQuery(ctx, "custom_fortune_enabled"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "custom_fortune_enabled"))))
                .then(Commands.literal("fortune_1_drop_chance")
                    .executes(ctx -> executeConfigQuery(ctx, "fortune_1_drop_chance"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "fortune_1_drop_chance"))))
                .then(Commands.literal("fortune_2_drop_chance")
                    .executes(ctx -> executeConfigQuery(ctx, "fortune_2_drop_chance"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "fortune_2_drop_chance"))))
                .then(Commands.literal("fortune_3_drop_chance")
                    .executes(ctx -> executeConfigQuery(ctx, "fortune_3_drop_chance"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "fortune_3_drop_chance"))))
                .then(Commands.literal("fortune_max_drops")
                    .executes(ctx -> executeConfigQuery(ctx, "fortune_max_drops"))
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeConfigInt(ctx, "fortune_max_drops"))))

                // Explosion item knockback
                .then(Commands.literal("explosion_item_knockback_enabled")
                    .executes(ctx -> executeConfigQuery(ctx, "explosion_item_knockback_enabled"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "explosion_item_knockback_enabled"))))
                .then(Commands.literal("explosion_item_knockback_multiplier")
                    .executes(ctx -> executeConfigQuery(ctx, "explosion_item_knockback_multiplier"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "explosion_item_knockback_multiplier"))))

                // Fishing
                .then(Commands.literal("fishing_charged_creeper_chance")
                    .executes(ctx -> executeConfigQuery(ctx, "fishing_charged_creeper_chance"))
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeConfigInt(ctx, "fishing_charged_creeper_chance"))))

                // Moss growth
                .then(Commands.literal("moss_growth_enabled")
                    .executes(ctx -> executeConfigQuery(ctx, "moss_growth_enabled"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "moss_growth_enabled"))))
                .then(Commands.literal("moss_tick_check_frequency")
                    .executes(ctx -> executeConfigQuery(ctx, "moss_tick_check_frequency"))
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeConfigInt(ctx, "moss_tick_check_frequency"))))
                .then(Commands.literal("moss_base_chance")
                    .executes(ctx -> executeConfigQuery(ctx, "moss_base_chance"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "moss_base_chance"))))
                .then(Commands.literal("moss_nearby_bonus")
                    .executes(ctx -> executeConfigQuery(ctx, "moss_nearby_bonus"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "moss_nearby_bonus"))))
                .then(Commands.literal("moss_underwater_multiplier")
                    .executes(ctx -> executeConfigQuery(ctx, "moss_underwater_multiplier"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.1))
                        .executes(ctx -> executeConfigDouble(ctx, "moss_underwater_multiplier"))))
                .then(Commands.literal("stone_brick_moss_multiplier")
                    .executes(ctx -> executeConfigQuery(ctx, "stone_brick_moss_multiplier"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0))
                        .executes(ctx -> executeConfigDouble(ctx, "stone_brick_moss_multiplier"))))

                // Zombie
                .then(Commands.literal("zombie_follow_range")
                    .executes(ctx -> executeConfigQuery(ctx, "zombie_follow_range"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(1.0, 128.0))
                        .executes(ctx -> executeConfigDouble(ctx, "zombie_follow_range"))))

                // Void fog (client rendering; synced to all players via packet)
                .then(Commands.literal("void_fog_enabled")
                    .executes(ctx -> executeConfigQuery(ctx, "void_fog_enabled"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeVoidFogEnabled(ctx))))

                // All ingredients consumable (synced to all players via packet)
                .then(Commands.literal("all_ingredients_consumable")
                    .executes(ctx -> executeConfigQuery(ctx, "all_ingredients_consumable"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeAllIngredientsConsumable(ctx)))))

            // dd reload
            .then(Commands.literal("reload")
                .requires(IS_OP)
                .executes(DeeperDarkCommands::executeReload)));
    }

    // ===== Suggestions =====

    private static CompletableFuture<Suggestions> suggestItems(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(REGISTRY.keySet(), builder);
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
                player.drop(giveStack, false);
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

    // ===== Config list =====

    private static int executeConfigList(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        source.sendSuccess(() -> Component.literal("===== DeeperDark General Configuration =====").withStyle(ChatFormatting.GOLD)
            .append(Component.literal("\n(Border settings: /dd border config)").withStyle(ChatFormatting.GRAY))
            .append(Component.literal("\n(Creature settings: /dd creature config)").withStyle(ChatFormatting.GRAY))
            .append(Component.literal("\n\n--- Nether ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("nether_coordinate_multiplier", config.netherCoordinateMultiplier))
            .append(Component.literal("\n\n--- Creeper ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("creeper_effect_min_seconds", config.creeperEffectMinSeconds))
            .append(formatConfigLine("creeper_effect_max_seconds", config.creeperEffectMaxSeconds))
            .append(Component.literal("\n\n--- Fortune ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("custom_fortune_enabled", config.customFortuneEnabled))
            .append(formatConfigLine("fortune_1_drop_chance", config.fortune1DropChance))
            .append(formatConfigLine("fortune_2_drop_chance", config.fortune2DropChance))
            .append(formatConfigLine("fortune_3_drop_chance", config.fortune3DropChance))
            .append(formatConfigLine("fortune_max_drops", config.fortuneMaxDrops))
            .append(Component.literal("\n\n--- Explosion ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("explosion_item_knockback_enabled", config.explosionItemKnockbackEnabled))
            .append(formatConfigLine("explosion_item_knockback_multiplier", config.explosionItemKnockbackMultiplier))
            .append(Component.literal("\n\n--- Piston ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("piston_push_limit", config.pistonPushLimit))
            .append(Component.literal("\n\n--- Enderman ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("enderman_pick_up_all_blocks", config.endermanPickUpAllBlocks))
            .append(Component.literal("\n\n--- Baby Mobs ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("baby_skeletons_enabled", config.babySkeletonsEnabled))
            .append(formatConfigLine("baby_creepers_enabled", config.babyCreepersEnabled))
            .append(formatConfigLine("baby_spiders_enabled", config.babySpidersEnabled))
            .append(Component.literal("\n\n--- Fishing ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("fishing_charged_creeper_chance", config.fishingChargedCreeperChance))
            .append(Component.literal("\n\n--- Moss Growth ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("moss_growth_enabled", config.mossGrowthEnabled))
            .append(formatConfigLine("moss_tick_check_frequency", config.mossTickCheckFrequency))
            .append(formatConfigLine("moss_base_chance", config.mossBaseChance))
            .append(formatConfigLine("moss_nearby_bonus", config.mossNearbyBonus))
            .append(formatConfigLine("moss_underwater_multiplier", config.mossUnderwaterMultiplier))
            .append(formatConfigLine("stone_brick_moss_multiplier", config.stoneBrickMossMultiplier))
            .append(Component.literal("\n\n--- Zombie ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("zombie_follow_range", config.zombieFollowRange))
            .append(Component.literal("\n\n--- Void Fog ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("void_fog_enabled", config.voidFogEnabled))
            .append(Component.literal("\n\n--- Ingredients ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("all_ingredients_consumable", config.allIngredientsConsumable))
            .append(Component.literal("\n\nUse /dd config <setting> [value] to change or query").withStyle(ChatFormatting.GRAY)), false);
        return 1;
    }

    private static Component formatConfigLine(String key, Object value) {
        return Component.literal("\n  " + key + ": ").withStyle(ChatFormatting.WHITE)
            .append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.AQUA));
    }

    // ===== Config query/set =====

    private static int executeConfigQuery(CommandContext<CommandSourceStack> context, String key) {
        CommandSourceStack source = context.getSource();
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        Object value = switch (key) {
            // Border (via dd border config)
            case "allow_entity_spawning"              -> config.allowEntitySpawning;
            case "push_survival_only"                 -> config.pushSurvivalModeOnly;
            // General
            case "nether_coordinate_multiplier"       -> config.netherCoordinateMultiplier;
            case "creeper_effect_min_seconds"         -> config.creeperEffectMinSeconds;
            case "creeper_effect_max_seconds"         -> config.creeperEffectMaxSeconds;
            case "custom_fortune_enabled"             -> config.customFortuneEnabled;
            case "fortune_1_drop_chance"              -> config.fortune1DropChance;
            case "fortune_2_drop_chance"              -> config.fortune2DropChance;
            case "fortune_3_drop_chance"              -> config.fortune3DropChance;
            case "fortune_max_drops"                  -> config.fortuneMaxDrops;
            case "explosion_item_knockback_enabled"   -> config.explosionItemKnockbackEnabled;
            case "explosion_item_knockback_multiplier"-> config.explosionItemKnockbackMultiplier;
            case "piston_push_limit"                  -> config.pistonPushLimit;
            case "enderman_pick_up_all_blocks"        -> config.endermanPickUpAllBlocks;
            case "baby_skeletons_enabled"             -> config.babySkeletonsEnabled;
            case "baby_creepers_enabled"              -> config.babyCreepersEnabled;
            case "baby_spiders_enabled"               -> config.babySpidersEnabled;
            case "fishing_charged_creeper_chance"     -> config.fishingChargedCreeperChance;
            case "moss_growth_enabled"                -> config.mossGrowthEnabled;
            case "moss_tick_check_frequency"          -> config.mossTickCheckFrequency;
            case "moss_base_chance"                   -> config.mossBaseChance;
            case "moss_nearby_bonus"                  -> config.mossNearbyBonus;
            case "moss_underwater_multiplier"         -> config.mossUnderwaterMultiplier;
            case "stone_brick_moss_multiplier"        -> config.stoneBrickMossMultiplier;
            case "zombie_follow_range"                -> config.zombieFollowRange;
            case "void_fog_enabled"                   -> config.voidFogEnabled;
            case "all_ingredients_consumable"         -> config.allIngredientsConsumable;
            default -> {
                source.sendFailure(Component.literal("Unknown config key: " + key).withStyle(ChatFormatting.RED));
                yield null;
            }
        };

        if (value == null) return 0;

        source.sendSuccess(() -> Component.literal(key + ": ").withStyle(ChatFormatting.WHITE)
            .append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.AQUA)), false);
        return 1;
    }

    private static int executeConfigBoolean(CommandContext<CommandSourceStack> context, String key) {
        CommandSourceStack source = context.getSource();
        boolean value = BoolArgumentType.getBool(context, "value");
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        switch (key) {
            case "allow_entity_spawning"            -> config.allowEntitySpawning = value;
            case "push_survival_only"               -> config.pushSurvivalModeOnly = value;
            case "custom_fortune_enabled"           -> config.customFortuneEnabled = value;
            case "explosion_item_knockback_enabled" -> config.explosionItemKnockbackEnabled = value;
            case "enderman_pick_up_all_blocks"      -> config.endermanPickUpAllBlocks = value;
            case "baby_skeletons_enabled"           -> config.babySkeletonsEnabled = value;
            case "baby_creepers_enabled"            -> config.babyCreepersEnabled = value;
            case "baby_spiders_enabled"             -> config.babySpidersEnabled = value;
            case "moss_growth_enabled"              -> config.mossGrowthEnabled = value;
            default -> {
                source.sendFailure(Component.literal("Unknown config key: " + key).withStyle(ChatFormatting.RED));
                return 0;
            }
        }

        DeeperDarkConfig.save();
        source.sendSuccess(() -> Component.literal("Set ")
            .append(Component.literal(key).withStyle(ChatFormatting.AQUA))
            .append(Component.literal(" to "))
            .append(Component.literal(String.valueOf(value)).withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED)), true);
        return 1;
    }

    private static int executeConfigInt(CommandContext<CommandSourceStack> context, String key) {
        CommandSourceStack source = context.getSource();
        int value = IntegerArgumentType.getInteger(context, "value");
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        switch (key) {
            case "creeper_effect_min_seconds"     -> config.creeperEffectMinSeconds = value;
            case "creeper_effect_max_seconds"     -> config.creeperEffectMaxSeconds = value;
            case "fortune_max_drops"              -> config.fortuneMaxDrops = value;
            case "piston_push_limit"              -> config.pistonPushLimit = value;
            case "fishing_charged_creeper_chance" -> config.fishingChargedCreeperChance = value;
            case "moss_tick_check_frequency"      -> config.mossTickCheckFrequency = value;
            default -> {
                source.sendFailure(Component.literal("Unknown config key: " + key).withStyle(ChatFormatting.RED));
                return 0;
            }
        }

        DeeperDarkConfig.save();
        source.sendSuccess(() -> Component.literal("Set ")
            .append(Component.literal(key).withStyle(ChatFormatting.AQUA))
            .append(Component.literal(" to "))
            .append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.GOLD)), true);
        return 1;
    }

    private static int executeConfigDouble(CommandContext<CommandSourceStack> context, String key) {
        CommandSourceStack source = context.getSource();
        double value = DoubleArgumentType.getDouble(context, "value");
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        switch (key) {
            case "nether_coordinate_multiplier"        -> config.netherCoordinateMultiplier = value;
            case "fortune_1_drop_chance"               -> config.fortune1DropChance = value;
            case "fortune_2_drop_chance"               -> config.fortune2DropChance = value;
            case "fortune_3_drop_chance"               -> config.fortune3DropChance = value;
            case "explosion_item_knockback_multiplier" -> config.explosionItemKnockbackMultiplier = value;
            case "moss_base_chance"                    -> config.mossBaseChance = value;
            case "moss_nearby_bonus"                   -> config.mossNearbyBonus = value;
            case "moss_underwater_multiplier"          -> config.mossUnderwaterMultiplier = value;
            case "stone_brick_moss_multiplier"         -> config.stoneBrickMossMultiplier = value;
            case "zombie_follow_range"                 -> config.zombieFollowRange = value;
            default -> {
                source.sendFailure(Component.literal("Unknown config key: " + key).withStyle(ChatFormatting.RED));
                return 0;
            }
        }

        DeeperDarkConfig.save();
        source.sendSuccess(() -> Component.literal("Set ")
            .append(Component.literal(key).withStyle(ChatFormatting.AQUA))
            .append(Component.literal(" to "))
            .append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.GOLD)), true);
        return 1;
    }

    // ===== Void fog toggle (broadcasts packet so all clients update immediately) =====

    private static int executeVoidFogEnabled(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        boolean value = BoolArgumentType.getBool(context, "value");
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        config.voidFogEnabled = value;
        DeeperDarkConfig.save();

        net.minecraft.server.MinecraftServer server = source.getServer();
        if (server != null) {
            VoidFogSyncPacket packet = new VoidFogSyncPacket(value);
            server.getPlayerList().getPlayers().forEach(p -> ServerPlayNetworking.send(p, packet));
        }

        source.sendSuccess(() -> Component.literal("Set ")
            .append(Component.literal("void_fog_enabled").withStyle(ChatFormatting.AQUA))
            .append(Component.literal(" to "))
            .append(Component.literal(String.valueOf(value)).withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED)), true);
        return 1;
    }

    // ===== All ingredients consumable toggle (broadcasts packet so all clients update immediately) =====

    private static int executeAllIngredientsConsumable(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        boolean value = BoolArgumentType.getBool(context, "value");
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        config.allIngredientsConsumable = value;
        DeeperDarkConfig.save();

        net.minecraft.server.MinecraftServer server = source.getServer();
        if (server != null) {
            AllIngredientsConsumableSyncPacket packet = new AllIngredientsConsumableSyncPacket(value);
            server.getPlayerList().getPlayers().forEach(p -> ServerPlayNetworking.send(p, packet));
        }

        source.sendSuccess(() -> Component.literal("Set ")
            .append(Component.literal("all_ingredients_consumable").withStyle(ChatFormatting.AQUA))
            .append(Component.literal(" to "))
            .append(Component.literal(String.valueOf(value)).withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED)), true);
        return 1;
    }

    // ===== Reload =====

    private static int executeReload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            DeeperDarkConfig.load();
            source.sendSuccess(() -> Component.literal("Successfully reloaded DeeperDark configuration from disk").withStyle(ChatFormatting.GREEN), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to reload configuration: " + e.getMessage()).withStyle(ChatFormatting.RED));
            return 0;
        }
    }
}
