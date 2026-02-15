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
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.event.GoldenCauldronEvents;
import net.noahsarch.deeperdark.event.GunpowderBlockEvents;
import net.noahsarch.deeperdark.event.SiphonEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Consolidated command system for DeeperDark mod.
 * All commands are under the "dd" root command and require operator permissions (level 2).
 */
public class DeeperDarkCommands {

    private static final Map<String, ItemStack> REGISTRY = new HashMap<>();

    public static void register() {
        // Register items to simpler names
        registerItems();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));
    }

    private static void registerItems() {
        // Gunpowder Block (Sugar base)
        ItemStack gunpowderBlock = new ItemStack(Items.SUGAR);
        gunpowderBlock.set(DataComponentTypes.ITEM_MODEL, GunpowderBlockEvents.GUNPOWDER_BLOCK_MODEL_ID);
        // User requested localized name fix via recipe, but here we give default logic or let the client handle translation key if set
        // Adding custom name for clarity
        gunpowderBlock.set(DataComponentTypes.ITEM_NAME, Text.translatable("item.deeperdark.gunpowder_block"));
        REGISTRY.put("gunpowder_block", gunpowderBlock);

        // Leather Block (Brown Wool base)
        ItemStack leatherBlock = new ItemStack(Items.SUGAR);
        leatherBlock.set(DataComponentTypes.ITEM_MODEL, net.noahsarch.deeperdark.event.LeatherBlockEvents.LEATHER_BLOCK_MODEL_ID);
        leatherBlock.set(DataComponentTypes.ITEM_NAME, Text.translatable("item.deeperdark.leather_block"));
        REGISTRY.put("leather_block", leatherBlock);

        // Flint Block (Cobbled Deepslate base)
        ItemStack flintBlock = new ItemStack(Items.SUGAR);
        flintBlock.set(DataComponentTypes.ITEM_MODEL, net.noahsarch.deeperdark.event.FlintBlockEvents.FLINT_BLOCK_MODEL_ID);
        flintBlock.set(DataComponentTypes.ITEM_NAME, Text.translatable("item.deeperdark.flint_block"));
        REGISTRY.put("flint_block", flintBlock);

        // Rotten Flesh Block (Nether Wart Block base)
        ItemStack rottenFleshBlock = new ItemStack(Items.SUGAR);
        rottenFleshBlock.set(DataComponentTypes.ITEM_MODEL, net.noahsarch.deeperdark.event.RottenFleshBlockEvents.ROTTEN_FLESH_BLOCK_MODEL_ID);
        rottenFleshBlock.set(DataComponentTypes.ITEM_NAME, Text.translatable("item.deeperdark.rotten_flesh_block"));
        REGISTRY.put("rotten_flesh_block", rottenFleshBlock);

        // Golden Cauldron (Cauldron base, item model)
        ItemStack goldenCauldron = new ItemStack(Items.SUGAR);
        goldenCauldron.set(DataComponentTypes.ITEM_MODEL, GoldenCauldronEvents.GOLDEN_CAULDRON_ITEM_MODEL_ID);
        goldenCauldron.set(DataComponentTypes.ITEM_NAME, Text.translatable("item.deeperdark.golden_cauldron"));
        REGISTRY.put("golden_cauldron", goldenCauldron);

        // Siphon (Hopper base)
        ItemStack siphon = new ItemStack(Items.SUGAR);
        siphon.set(DataComponentTypes.ITEM_MODEL, SiphonEvents.SIPHON_MODEL_ID);
        siphon.set(DataComponentTypes.ITEM_NAME, Text.translatable("item.deeperdark.siphon"));
        REGISTRY.put("siphon", siphon);

        // Leather Scrap (Sugar base)
        ItemStack leatherScrap = new ItemStack(Items.SUGAR);
        leatherScrap.set(DataComponentTypes.ITEM_MODEL, net.minecraft.util.Identifier.of("leather_scrap"));
        leatherScrap.set(DataComponentTypes.ITEM_NAME, Text.translatable("item.deeperdark.leather_scrap"));
        REGISTRY.put("leather_scrap", leatherScrap);
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Main "dd" command with all subcommands
        dispatcher.register(CommandManager.literal("dd")
            .requires(source -> {
                // Check if source has operator permissions (level 2)
                try {
                    net.minecraft.server.network.ServerPlayerEntity player = source.getPlayer();
                    if (player != null) {
                        return source.getServer().getPlayerManager().isOperator(player.getPlayerConfigEntry());
                    }
                    // Allow console/command blocks
                    return true;
                } catch (Exception e) {
                    return false;
                }
            })

            // dd give <item> [amount]
            .then(CommandManager.literal("give")
                .then(CommandManager.argument("item", StringArgumentType.word())
                    .suggests(DeeperDarkCommands::suggestItems)
                    .executes(DeeperDarkCommands::executeGive) // Default 1 item
                    .then(CommandManager.argument("amount", IntegerArgumentType.integer(1, 64))
                        .executes(DeeperDarkCommands::executeGiveWithAmount))))

            // dd border origin <x> <z>
            // dd border radius <radius>
            // dd border force <multiplier>
            // dd border info
            .then(CommandManager.literal("border")
                .then(CommandManager.literal("origin")
                    .then(CommandManager.argument("x", IntegerArgumentType.integer())
                        .then(CommandManager.argument("z", IntegerArgumentType.integer())
                            .executes(DeeperDarkCommands::executeSetOrigin))))
                .then(CommandManager.literal("radius")
                    .then(CommandManager.argument("radius", DoubleArgumentType.doubleArg(1.0))
                        .executes(DeeperDarkCommands::executeSetRadius)))
                .then(CommandManager.literal("force")
                    .then(CommandManager.argument("multiplier", DoubleArgumentType.doubleArg(0.0))
                        .executes(DeeperDarkCommands::executeSetForce)))
                .then(CommandManager.literal("info")
                    .executes(DeeperDarkCommands::executeBorderInfo)))

            // dd config <setting> <value>
            // dd config <setting> (query current value)
            // dd config list
            .then(CommandManager.literal("config")
                .then(CommandManager.literal("list")
                    .executes(DeeperDarkCommands::executeConfigList))

                // Border settings
                .then(CommandManager.literal("allowEntitySpawningOutsideBorder")
                    .executes(ctx -> executeConfigQuery(ctx, "allowEntitySpawning"))
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "allowEntitySpawning"))))
                .then(CommandManager.literal("pushSurvivalModeOnly")
                    .executes(ctx -> executeConfigQuery(ctx, "pushSurvivalModeOnly"))
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "pushSurvivalModeOnly"))))

                // Nether settings
                .then(CommandManager.literal("netherCoordinateMultiplier")
                    .executes(ctx -> executeConfigQuery(ctx, "netherCoordinateMultiplier"))
                    .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0.0))
                        .executes(ctx -> executeConfigDouble(ctx, "netherCoordinateMultiplier"))))

                // Creeper settings
                .then(CommandManager.literal("creeperEffectMinSeconds")
                    .executes(ctx -> executeConfigQuery(ctx, "creeperEffectMinSeconds"))
                    .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeConfigInt(ctx, "creeperEffectMinSeconds"))))
                .then(CommandManager.literal("creeperEffectMaxSeconds")
                    .executes(ctx -> executeConfigQuery(ctx, "creeperEffectMaxSeconds"))
                    .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeConfigInt(ctx, "creeperEffectMaxSeconds"))))

                // Fortune settings
                .then(CommandManager.literal("customFortuneEnabled")
                    .executes(ctx -> executeConfigQuery(ctx, "customFortuneEnabled"))
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "customFortuneEnabled"))))
                .then(CommandManager.literal("fortune1DropChance")
                    .executes(ctx -> executeConfigQuery(ctx, "fortune1DropChance"))
                    .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "fortune1DropChance"))))
                .then(CommandManager.literal("fortune2DropChance")
                    .executes(ctx -> executeConfigQuery(ctx, "fortune2DropChance"))
                    .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "fortune2DropChance"))))
                .then(CommandManager.literal("fortune3DropChance")
                    .executes(ctx -> executeConfigQuery(ctx, "fortune3DropChance"))
                    .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "fortune3DropChance"))))
                .then(CommandManager.literal("fortuneMaxDrops")
                    .executes(ctx -> executeConfigQuery(ctx, "fortuneMaxDrops"))
                    .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeConfigInt(ctx, "fortuneMaxDrops"))))

                // Explosion settings
                .then(CommandManager.literal("explosionItemKnockbackEnabled")
                    .executes(ctx -> executeConfigQuery(ctx, "explosionItemKnockbackEnabled"))
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "explosionItemKnockbackEnabled"))))
                .then(CommandManager.literal("explosionItemKnockbackMultiplier")
                    .executes(ctx -> executeConfigQuery(ctx, "explosionItemKnockbackMultiplier"))
                    .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "explosionItemKnockbackMultiplier"))))

                // Piston settings
                .then(CommandManager.literal("pistonPushLimit")
                    .executes(ctx -> executeConfigQuery(ctx, "pistonPushLimit"))
                    .then(CommandManager.argument("value", IntegerArgumentType.integer(1, 1000))
                        .executes(ctx -> executeConfigInt(ctx, "pistonPushLimit"))))

                // Enderman settings
                .then(CommandManager.literal("endermanPickUpAllBlocks")
                    .executes(ctx -> executeConfigQuery(ctx, "endermanPickUpAllBlocks"))
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "endermanPickUpAllBlocks"))))

                // Baby mob settings
                .then(CommandManager.literal("babySkeletonsEnabled")
                    .executes(ctx -> executeConfigQuery(ctx, "babySkeletonsEnabled"))
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "babySkeletonsEnabled"))))
                .then(CommandManager.literal("babyCreepersEnabled")
                    .executes(ctx -> executeConfigQuery(ctx, "babyCreepersEnabled"))
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "babyCreepersEnabled"))))

                // Fishing settings
                .then(CommandManager.literal("fishingChargedCreeperChance")
                    .executes(ctx -> executeConfigQuery(ctx, "fishingChargedCreeperChance"))
                    .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeConfigInt(ctx, "fishingChargedCreeperChance"))))

                // Moss growth settings
                .then(CommandManager.literal("mossGrowthEnabled")
                    .executes(ctx -> executeConfigQuery(ctx, "mossGrowthEnabled"))
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "mossGrowthEnabled"))))
                .then(CommandManager.literal("mossTickCheckFrequency")
                    .executes(ctx -> executeConfigQuery(ctx, "mossTickCheckFrequency"))
                    .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeConfigInt(ctx, "mossTickCheckFrequency"))))
                .then(CommandManager.literal("mossBaseChance")
                    .executes(ctx -> executeConfigQuery(ctx, "mossBaseChance"))
                    .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "mossBaseChance"))))
                .then(CommandManager.literal("mossNearbyBonus")
                    .executes(ctx -> executeConfigQuery(ctx, "mossNearbyBonus"))
                    .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "mossNearbyBonus"))))
                .then(CommandManager.literal("mossUnderwaterMultiplier")
                    .executes(ctx -> executeConfigQuery(ctx, "mossUnderwaterMultiplier"))
                    .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0.1))
                        .executes(ctx -> executeConfigDouble(ctx, "mossUnderwaterMultiplier"))))
                .then(CommandManager.literal("stoneBrickMossMultiplier")
                    .executes(ctx -> executeConfigQuery(ctx, "stoneBrickMossMultiplier"))
                    .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(0.0))
                        .executes(ctx -> executeConfigDouble(ctx, "stoneBrickMossMultiplier"))))

                // Zombie settings
                .then(CommandManager.literal("zombieFollowRange")
                    .executes(ctx -> executeConfigQuery(ctx, "zombieFollowRange"))
                    .then(CommandManager.argument("value", DoubleArgumentType.doubleArg(1.0, 128.0))
                        .executes(ctx -> executeConfigDouble(ctx, "zombieFollowRange")))))

            // dd reload - Reload configuration from disk
            .then(CommandManager.literal("reload")
                .executes(DeeperDarkCommands::executeReload)));
    }

    // ===== Suggestions =====

    private static CompletableFuture<Suggestions> suggestItems(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(REGISTRY.keySet(), builder);
    }

    // ===== Command Executors =====

    // ----- Give command -----
    private static int executeGive(CommandContext<ServerCommandSource> context) {
        return executeGiveInternal(context, 1);
    }

    private static int executeGiveWithAmount(CommandContext<ServerCommandSource> context) {
        int amount = IntegerArgumentType.getInteger(context, "amount");
        return executeGiveInternal(context, amount);
    }

    private static int executeGiveInternal(CommandContext<ServerCommandSource> context, int amount) {
        ServerCommandSource source = context.getSource();
        String itemName = StringArgumentType.getString(context, "item");

        ItemStack stack = REGISTRY.get(itemName);
        if (stack == null) {
            source.sendError(Text.literal("Unknown item: " + itemName).formatted(Formatting.RED));
            return 0;
        }

        try {
            net.minecraft.server.network.ServerPlayerEntity player = source.getPlayerOrThrow();
            ItemStack giveStack = stack.copy();
            giveStack.setCount(amount);

            boolean inserted = player.getInventory().insertStack(giveStack);
            if (!inserted) {
                player.dropItem(giveStack, false);
            }

            source.sendFeedback(() -> Text.literal("Gave ")
                .append(Text.literal(String.valueOf(amount)).formatted(Formatting.GOLD))
                .append(Text.literal(" ["))
                .append(Text.literal(itemName).formatted(Formatting.AQUA))
                .append(Text.literal("] to "))
                .append(Text.literal(player.getName().getString()).formatted(Formatting.YELLOW)), true);
            return 1;
        } catch (Exception e) {
            source.sendError(Text.literal("Failed to give item: " + e.getMessage()).formatted(Formatting.RED));
            return 0;
        }
    }

    // ----- Border commands -----
    private static int executeSetOrigin(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        int x = IntegerArgumentType.getInteger(context, "x");
        int z = IntegerArgumentType.getInteger(context, "z");

        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        config.originX = x;
        config.originZ = z;
        DeeperDarkConfig.save();

        source.sendFeedback(() -> Text.literal("Border origin set to ")
            .append(Text.literal("(" + x + ", " + z + ")").formatted(Formatting.AQUA)), true);
        return 1;
    }

    private static int executeSetRadius(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        double radius = DoubleArgumentType.getDouble(context, "radius");

        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        config.safeRadius = radius;
        DeeperDarkConfig.save();

        source.sendFeedback(() -> Text.literal("Border radius set to ")
            .append(Text.literal(String.valueOf(radius)).formatted(Formatting.AQUA))
            .append(Text.literal(" blocks")), true);
        return 1;
    }

    private static int executeSetForce(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        double multiplier = DoubleArgumentType.getDouble(context, "multiplier");

        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        config.forceMultiplier = multiplier;
        DeeperDarkConfig.save();

        source.sendFeedback(() -> Text.literal("Border force multiplier set to ")
            .append(Text.literal(String.valueOf(multiplier)).formatted(Formatting.AQUA)), true);
        return 1;
    }

    private static int executeBorderInfo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        source.sendFeedback(() -> Text.literal("===== Border Configuration =====").formatted(Formatting.GOLD)
            .append(Text.literal("\nOrigin: ").formatted(Formatting.WHITE))
            .append(Text.literal("(" + config.originX + ", " + config.originZ + ")").formatted(Formatting.AQUA))
            .append(Text.literal("\nRadius: ").formatted(Formatting.WHITE))
            .append(Text.literal(String.valueOf(config.safeRadius)).formatted(Formatting.AQUA))
            .append(Text.literal(" blocks").formatted(Formatting.WHITE))
            .append(Text.literal("\nForce Multiplier: ").formatted(Formatting.WHITE))
            .append(Text.literal(String.valueOf(config.forceMultiplier)).formatted(Formatting.AQUA))
            .append(Text.literal("\nAllow Entity Spawning Outside: ").formatted(Formatting.WHITE))
            .append(Text.literal(String.valueOf(config.allowEntitySpawning)).formatted(config.allowEntitySpawning ? Formatting.GREEN : Formatting.RED)), false);
        return 1;
    }

    // ----- Config commands -----
    private static int executeConfigList(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        source.sendFeedback(() -> Text.literal("===== DeeperDark Configuration =====").formatted(Formatting.GOLD)
            .append(Text.literal("\n\n--- Border Settings ---").formatted(Formatting.YELLOW))
            .append(formatConfigLine("allowEntitySpawningOutsideBorder", config.allowEntitySpawning))
            .append(formatConfigLine("pushSurvivalModeOnly", config.pushSurvivalModeOnly))
            .append(Text.literal("\n\n--- Nether Settings ---").formatted(Formatting.YELLOW))
            .append(formatConfigLine("netherCoordinateMultiplier", config.netherCoordinateMultiplier))
            .append(Text.literal("\n\n--- Creeper Settings ---").formatted(Formatting.YELLOW))
            .append(formatConfigLine("creeperEffectMinSeconds", config.creeperEffectMinSeconds))
            .append(formatConfigLine("creeperEffectMaxSeconds", config.creeperEffectMaxSeconds))
            .append(Text.literal("\n\n--- Fortune Settings ---").formatted(Formatting.YELLOW))
            .append(formatConfigLine("customFortuneEnabled", config.customFortuneEnabled))
            .append(formatConfigLine("fortune1DropChance", config.fortune1DropChance))
            .append(formatConfigLine("fortune2DropChance", config.fortune2DropChance))
            .append(formatConfigLine("fortune3DropChance", config.fortune3DropChance))
            .append(formatConfigLine("fortuneMaxDrops", config.fortuneMaxDrops))
            .append(Text.literal("\n\n--- Explosion Settings ---").formatted(Formatting.YELLOW))
            .append(formatConfigLine("explosionItemKnockbackEnabled", config.explosionItemKnockbackEnabled))
            .append(formatConfigLine("explosionItemKnockbackMultiplier", config.explosionItemKnockbackMultiplier))
            .append(Text.literal("\n\n--- Piston Settings ---").formatted(Formatting.YELLOW))
            .append(formatConfigLine("pistonPushLimit", config.pistonPushLimit))
            .append(Text.literal("\n\n--- Enderman Settings ---").formatted(Formatting.YELLOW))
            .append(formatConfigLine("endermanPickUpAllBlocks", config.endermanPickUpAllBlocks))
            .append(Text.literal("\n\n--- Baby Mob Settings ---").formatted(Formatting.YELLOW))
            .append(formatConfigLine("babySkeletonsEnabled", config.babySkeletonsEnabled))
            .append(formatConfigLine("babyCreepersEnabled", config.babyCreepersEnabled))
            .append(Text.literal("\n\n--- Fishing Settings ---").formatted(Formatting.YELLOW))
            .append(formatConfigLine("fishingChargedCreeperChance", config.fishingChargedCreeperChance))
            .append(Text.literal("\n\n--- Moss Growth Settings ---").formatted(Formatting.YELLOW))
            .append(formatConfigLine("mossGrowthEnabled", config.mossGrowthEnabled))
            .append(formatConfigLine("mossTickCheckFrequency", config.mossTickCheckFrequency))
            .append(formatConfigLine("mossBaseChance", config.mossBaseChance))
            .append(formatConfigLine("mossNearbyBonus", config.mossNearbyBonus))
            .append(formatConfigLine("mossUnderwaterMultiplier", config.mossUnderwaterMultiplier))
            .append(formatConfigLine("stoneBrickMossMultiplier", config.stoneBrickMossMultiplier))
            .append(Text.literal("\n\n--- Zombie Settings ---").formatted(Formatting.YELLOW))
            .append(formatConfigLine("zombieFollowRange", config.zombieFollowRange))
            .append(Text.literal("\n\nUse /dd config <setting> [value] to change or query settings").formatted(Formatting.GRAY)), false);
        return 1;
    }

    private static Text formatConfigLine(String key, Object value) {
        return Text.literal("\n  " + key + ": ").formatted(Formatting.WHITE)
            .append(Text.literal(String.valueOf(value)).formatted(Formatting.AQUA));
    }

    private static int executeConfigQuery(CommandContext<ServerCommandSource> context, String configKey) {
        ServerCommandSource source = context.getSource();
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        Object value = switch (configKey) {
            case "allowEntitySpawning" -> config.allowEntitySpawning;
            case "pushSurvivalModeOnly" -> config.pushSurvivalModeOnly;
            case "customFortuneEnabled" -> config.customFortuneEnabled;
            case "explosionItemKnockbackEnabled" -> config.explosionItemKnockbackEnabled;
            case "endermanPickUpAllBlocks" -> config.endermanPickUpAllBlocks;
            case "babySkeletonsEnabled" -> config.babySkeletonsEnabled;
            case "babyCreepersEnabled" -> config.babyCreepersEnabled;
            case "mossGrowthEnabled" -> config.mossGrowthEnabled;
            case "creeperEffectMinSeconds" -> config.creeperEffectMinSeconds;
            case "creeperEffectMaxSeconds" -> config.creeperEffectMaxSeconds;
            case "fortuneMaxDrops" -> config.fortuneMaxDrops;
            case "pistonPushLimit" -> config.pistonPushLimit;
            case "fishingChargedCreeperChance" -> config.fishingChargedCreeperChance;
            case "mossTickCheckFrequency" -> config.mossTickCheckFrequency;
            case "fortune1DropChance" -> config.fortune1DropChance;
            case "fortune2DropChance" -> config.fortune2DropChance;
            case "fortune3DropChance" -> config.fortune3DropChance;
            case "explosionItemKnockbackMultiplier" -> config.explosionItemKnockbackMultiplier;
            case "netherCoordinateMultiplier" -> config.netherCoordinateMultiplier;
            case "mossBaseChance" -> config.mossBaseChance;
            case "mossNearbyBonus" -> config.mossNearbyBonus;
            case "mossUnderwaterMultiplier" -> config.mossUnderwaterMultiplier;
            case "stoneBrickMossMultiplier" -> config.stoneBrickMossMultiplier;
            case "zombieFollowRange" -> config.zombieFollowRange;
            default -> {
                source.sendError(Text.literal("Unknown config key: " + configKey).formatted(Formatting.RED));
                yield null;
            }
        };

        if (value == null) return 0;

        source.sendFeedback(() -> Text.literal(configKey + ": ").formatted(Formatting.WHITE)
            .append(Text.literal(String.valueOf(value)).formatted(Formatting.AQUA)), false);
        return 1;
    }

    private static int executeConfigBoolean(CommandContext<ServerCommandSource> context, String configKey) {
        ServerCommandSource source = context.getSource();
        boolean value = BoolArgumentType.getBool(context, "value");
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        switch (configKey) {
            case "allowEntitySpawning" -> config.allowEntitySpawning = value;
            case "pushSurvivalModeOnly" -> config.pushSurvivalModeOnly = value;
            case "customFortuneEnabled" -> config.customFortuneEnabled = value;
            case "explosionItemKnockbackEnabled" -> config.explosionItemKnockbackEnabled = value;
            case "endermanPickUpAllBlocks" -> config.endermanPickUpAllBlocks = value;
            case "babySkeletonsEnabled" -> config.babySkeletonsEnabled = value;
            case "babyCreepersEnabled" -> config.babyCreepersEnabled = value;
            case "mossGrowthEnabled" -> config.mossGrowthEnabled = value;
            default -> {
                source.sendError(Text.literal("Unknown config key: " + configKey).formatted(Formatting.RED));
                return 0;
            }
        }

        DeeperDarkConfig.save();
        source.sendFeedback(() -> Text.literal("Set ")
            .append(Text.literal(configKey).formatted(Formatting.AQUA))
            .append(Text.literal(" to "))
            .append(Text.literal(String.valueOf(value)).formatted(value ? Formatting.GREEN : Formatting.RED)), true);
        return 1;
    }

    private static int executeConfigInt(CommandContext<ServerCommandSource> context, String configKey) {
        ServerCommandSource source = context.getSource();
        int value = IntegerArgumentType.getInteger(context, "value");
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        switch (configKey) {
            case "creeperEffectMinSeconds" -> config.creeperEffectMinSeconds = value;
            case "creeperEffectMaxSeconds" -> config.creeperEffectMaxSeconds = value;
            case "fortuneMaxDrops" -> config.fortuneMaxDrops = value;
            case "pistonPushLimit" -> config.pistonPushLimit = value;
            case "fishingChargedCreeperChance" -> config.fishingChargedCreeperChance = value;
            case "mossTickCheckFrequency" -> config.mossTickCheckFrequency = value;
            default -> {
                source.sendError(Text.literal("Unknown config key: " + configKey).formatted(Formatting.RED));
                return 0;
            }
        }

        DeeperDarkConfig.save();
        source.sendFeedback(() -> Text.literal("Set ")
            .append(Text.literal(configKey).formatted(Formatting.AQUA))
            .append(Text.literal(" to "))
            .append(Text.literal(String.valueOf(value)).formatted(Formatting.GOLD)), true);
        return 1;
    }

    private static int executeConfigDouble(CommandContext<ServerCommandSource> context, String configKey) {
        ServerCommandSource source = context.getSource();
        double value = DoubleArgumentType.getDouble(context, "value");
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        switch (configKey) {
            case "fortune1DropChance" -> config.fortune1DropChance = value;
            case "fortune2DropChance" -> config.fortune2DropChance = value;
            case "fortune3DropChance" -> config.fortune3DropChance = value;
            case "explosionItemKnockbackMultiplier" -> config.explosionItemKnockbackMultiplier = value;
            case "netherCoordinateMultiplier" -> config.netherCoordinateMultiplier = value;
            case "mossBaseChance" -> config.mossBaseChance = value;
            case "mossNearbyBonus" -> config.mossNearbyBonus = value;
            case "mossUnderwaterMultiplier" -> config.mossUnderwaterMultiplier = value;
            case "stoneBrickMossMultiplier" -> config.stoneBrickMossMultiplier = value;
            case "zombieFollowRange" -> config.zombieFollowRange = value;
            default -> {
                source.sendError(Text.literal("Unknown config key: " + configKey).formatted(Formatting.RED));
                return 0;
            }
        }

        DeeperDarkConfig.save();
        source.sendFeedback(() -> Text.literal("Set ")
            .append(Text.literal(configKey).formatted(Formatting.AQUA))
            .append(Text.literal(" to "))
            .append(Text.literal(String.valueOf(value)).formatted(Formatting.GOLD)), true);
        return 1;
    }

    // ----- Reload command -----
    private static int executeReload(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        try {
            DeeperDarkConfig.load();
            source.sendFeedback(() -> Text.literal("Successfully reloaded DeeperDark configuration from disk").formatted(Formatting.GREEN), true);
            return 1;
        } catch (Exception e) {
            source.sendError(Text.literal("Failed to reload configuration: " + e.getMessage()).formatted(Formatting.RED));
            return 0;
        }
    }
}
