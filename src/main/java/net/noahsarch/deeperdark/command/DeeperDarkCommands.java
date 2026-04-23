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
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.event.GoldenCauldronEvents;
import net.noahsarch.deeperdark.event.GunpowderBlockEvents;
import net.noahsarch.deeperdark.event.SiphonEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.level.Explosion;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;

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
        gunpowderBlock.set(DataComponents.ITEM_MODEL, GunpowderBlockEvents.GUNPOWDER_BLOCK_MODEL_ID);
        // User requested localized name fix via recipe, but here we give default logic or let the client handle translation key if set
        // Adding custom name for clarity
        gunpowderBlock.set(DataComponents.ITEM_NAME, Component.translatable("item.deeperdark.gunpowder_block"));
        REGISTRY.put("gunpowder_block", gunpowderBlock);

        // Leather Block (Brown Wool base)
        ItemStack leatherBlock = new ItemStack(Items.SUGAR);
        leatherBlock.set(DataComponents.ITEM_MODEL, net.noahsarch.deeperdark.event.LeatherBlockEvents.LEATHER_BLOCK_MODEL_ID);
        leatherBlock.set(DataComponents.ITEM_NAME, Component.translatable("item.deeperdark.leather_block"));
        REGISTRY.put("leather_block", leatherBlock);

        // Flint Block (Cobbled Deepslate base)
        ItemStack flintBlock = new ItemStack(Items.SUGAR);
        flintBlock.set(DataComponents.ITEM_MODEL, net.noahsarch.deeperdark.event.FlintBlockEvents.FLINT_BLOCK_MODEL_ID);
        flintBlock.set(DataComponents.ITEM_NAME, Component.translatable("item.deeperdark.flint_block"));
        REGISTRY.put("flint_block", flintBlock);

        // Rotten Flesh Block (Nether Wart Block base)
        ItemStack rottenFleshBlock = new ItemStack(Items.SUGAR);
        rottenFleshBlock.set(DataComponents.ITEM_MODEL, net.noahsarch.deeperdark.event.RottenFleshBlockEvents.ROTTEN_FLESH_BLOCK_MODEL_ID);
        rottenFleshBlock.set(DataComponents.ITEM_NAME, Component.translatable("item.deeperdark.rotten_flesh_block"));
        REGISTRY.put("rotten_flesh_block", rottenFleshBlock);

        // Golden Cauldron (Cauldron base, item model)
        ItemStack goldenCauldron = new ItemStack(Items.SUGAR);
        goldenCauldron.set(DataComponents.ITEM_MODEL, GoldenCauldronEvents.GOLDEN_CAULDRON_ITEM_MODEL_ID);
        goldenCauldron.set(DataComponents.ITEM_NAME, Component.translatable("item.deeperdark.golden_cauldron"));
        REGISTRY.put("golden_cauldron", goldenCauldron);

        // Siphon (Hopper base)
        ItemStack siphon = new ItemStack(Items.SUGAR);
        siphon.set(DataComponents.ITEM_MODEL, SiphonEvents.SIPHON_MODEL_ID);
        siphon.set(DataComponents.ITEM_NAME, Component.translatable("item.deeperdark.siphon"));
        REGISTRY.put("siphon", siphon);

        // Leather Scrap (Sugar base)
        ItemStack leatherScrap = new ItemStack(Items.SUGAR);
        leatherScrap.set(DataComponents.ITEM_MODEL, net.minecraft.resources.Identifier.withDefaultNamespace("leather_scrap"));
        leatherScrap.set(DataComponents.ITEM_NAME, Component.translatable("item.deeperdark.leather_scrap"));
        REGISTRY.put("leather_scrap", leatherScrap);
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Main "dd" command with all subcommands
        dispatcher.register(Commands.literal("dd")
            .requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)))

            // dd give <item> [amount]
            .then(Commands.literal("give")
                .then(Commands.argument("item", StringArgumentType.word())
                    .suggests(DeeperDarkCommands::suggestItems)
                    .executes(DeeperDarkCommands::executeGive) // Default 1 item
                    .then(Commands.argument("amount", IntegerArgumentType.integer(1, 64))
                        .executes(DeeperDarkCommands::executeGiveWithAmount))))

            // dd border origin <x> <z>
            // dd border radius <radius>
            // dd border force <multiplier>
            // dd border info
            .then(Commands.literal("border")
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
                    .executes(DeeperDarkCommands::executeBorderInfo)))

            // dd config <setting> <value>
            // dd config <setting> (query current value)
            // dd config list
            .then(Commands.literal("config")
                .then(Commands.literal("list")
                    .executes(DeeperDarkCommands::executeConfigList))

                // Border settings
                .then(Commands.literal("allowEntitySpawningOutsideBorder")
                    .executes(ctx -> executeConfigQuery(ctx, "allowEntitySpawning"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "allowEntitySpawning"))))
                .then(Commands.literal("pushSurvivalModeOnly")
                    .executes(ctx -> executeConfigQuery(ctx, "pushSurvivalModeOnly"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "pushSurvivalModeOnly"))))

                // Nether settings
                .then(Commands.literal("netherCoordinateMultiplier")
                    .executes(ctx -> executeConfigQuery(ctx, "netherCoordinateMultiplier"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0))
                        .executes(ctx -> executeConfigDouble(ctx, "netherCoordinateMultiplier"))))

                // Creeper settings
                .then(Commands.literal("creeperEffectMinSeconds")
                    .executes(ctx -> executeConfigQuery(ctx, "creeperEffectMinSeconds"))
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeConfigInt(ctx, "creeperEffectMinSeconds"))))
                .then(Commands.literal("creeperEffectMaxSeconds")
                    .executes(ctx -> executeConfigQuery(ctx, "creeperEffectMaxSeconds"))
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeConfigInt(ctx, "creeperEffectMaxSeconds"))))

                // Fortune settings
                .then(Commands.literal("customFortuneEnabled")
                    .executes(ctx -> executeConfigQuery(ctx, "customFortuneEnabled"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "customFortuneEnabled"))))
                .then(Commands.literal("fortune1DropChance")
                    .executes(ctx -> executeConfigQuery(ctx, "fortune1DropChance"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "fortune1DropChance"))))
                .then(Commands.literal("fortune2DropChance")
                    .executes(ctx -> executeConfigQuery(ctx, "fortune2DropChance"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "fortune2DropChance"))))
                .then(Commands.literal("fortune3DropChance")
                    .executes(ctx -> executeConfigQuery(ctx, "fortune3DropChance"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "fortune3DropChance"))))
                .then(Commands.literal("fortuneMaxDrops")
                    .executes(ctx -> executeConfigQuery(ctx, "fortuneMaxDrops"))
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeConfigInt(ctx, "fortuneMaxDrops"))))

                // Explosion settings
                .then(Commands.literal("explosionItemKnockbackEnabled")
                    .executes(ctx -> executeConfigQuery(ctx, "explosionItemKnockbackEnabled"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "explosionItemKnockbackEnabled"))))
                .then(Commands.literal("explosionItemKnockbackMultiplier")
                    .executes(ctx -> executeConfigQuery(ctx, "explosionItemKnockbackMultiplier"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "explosionItemKnockbackMultiplier"))))

                // Piston settings
                .then(Commands.literal("pistonPushLimit")
                    .executes(ctx -> executeConfigQuery(ctx, "pistonPushLimit"))
                    .then(Commands.argument("value", IntegerArgumentType.integer(1, 1000))
                        .executes(ctx -> executeConfigInt(ctx, "pistonPushLimit"))))

                // Enderman settings
                .then(Commands.literal("endermanPickUpAllBlocks")
                    .executes(ctx -> executeConfigQuery(ctx, "endermanPickUpAllBlocks"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "endermanPickUpAllBlocks"))))

                // Baby mob settings
                .then(Commands.literal("babySkeletonsEnabled")
                    .executes(ctx -> executeConfigQuery(ctx, "babySkeletonsEnabled"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "babySkeletonsEnabled"))))
                .then(Commands.literal("babyCreepersEnabled")
                    .executes(ctx -> executeConfigQuery(ctx, "babyCreepersEnabled"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "babyCreepersEnabled"))))

                // Fishing settings
                .then(Commands.literal("fishingChargedCreeperChance")
                    .executes(ctx -> executeConfigQuery(ctx, "fishingChargedCreeperChance"))
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeConfigInt(ctx, "fishingChargedCreeperChance"))))

                // Moss growth settings
                .then(Commands.literal("mossGrowthEnabled")
                    .executes(ctx -> executeConfigQuery(ctx, "mossGrowthEnabled"))
                    .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(ctx -> executeConfigBoolean(ctx, "mossGrowthEnabled"))))
                .then(Commands.literal("mossTickCheckFrequency")
                    .executes(ctx -> executeConfigQuery(ctx, "mossTickCheckFrequency"))
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeConfigInt(ctx, "mossTickCheckFrequency"))))
                .then(Commands.literal("mossBaseChance")
                    .executes(ctx -> executeConfigQuery(ctx, "mossBaseChance"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "mossBaseChance"))))
                .then(Commands.literal("mossNearbyBonus")
                    .executes(ctx -> executeConfigQuery(ctx, "mossNearbyBonus"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                        .executes(ctx -> executeConfigDouble(ctx, "mossNearbyBonus"))))
                .then(Commands.literal("mossUnderwaterMultiplier")
                    .executes(ctx -> executeConfigQuery(ctx, "mossUnderwaterMultiplier"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.1))
                        .executes(ctx -> executeConfigDouble(ctx, "mossUnderwaterMultiplier"))))
                .then(Commands.literal("stoneBrickMossMultiplier")
                    .executes(ctx -> executeConfigQuery(ctx, "stoneBrickMossMultiplier"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0))
                        .executes(ctx -> executeConfigDouble(ctx, "stoneBrickMossMultiplier"))))

                // Zombie settings
                .then(Commands.literal("zombieFollowRange")
                    .executes(ctx -> executeConfigQuery(ctx, "zombieFollowRange"))
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg(1.0, 128.0))
                        .executes(ctx -> executeConfigDouble(ctx, "zombieFollowRange")))))

            // dd reload - Reload configuration from disk
            .then(Commands.literal("reload")
                .executes(DeeperDarkCommands::executeReload))

            // dd creature - Creature system commands
            .then(net.noahsarch.deeperdark.creature.CreatureCommands.buildCreatureCommand()));
    }

    // ===== Suggestions =====

    private static CompletableFuture<Suggestions> suggestItems(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(REGISTRY.keySet(), builder);
    }

    // ===== Command Executors =====

    // ----- Give command -----
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

        ItemStack stack = REGISTRY.get(itemName);
        if (stack == null) {
            source.sendFailure(Component.literal("Unknown item: " + itemName).withStyle(ChatFormatting.RED));
            return 0;
        }

        try {
            net.minecraft.server.level.ServerPlayer player = source.getPlayerOrException();
            ItemStack giveStack = stack.copy();
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

    // ----- Border commands -----
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
            .append(Component.literal(String.valueOf(config.allowEntitySpawning)).withStyle(config.allowEntitySpawning ? ChatFormatting.GREEN : ChatFormatting.RED)), false);
        return 1;
    }

    // ----- Config commands -----
    private static int executeConfigList(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        source.sendSuccess(() -> Component.literal("===== DeeperDark Configuration =====").withStyle(ChatFormatting.GOLD)
            .append(Component.literal("\n\n--- Border Settings ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("allowEntitySpawningOutsideBorder", config.allowEntitySpawning))
            .append(formatConfigLine("pushSurvivalModeOnly", config.pushSurvivalModeOnly))
            .append(Component.literal("\n\n--- Nether Settings ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("netherCoordinateMultiplier", config.netherCoordinateMultiplier))
            .append(Component.literal("\n\n--- Creeper Settings ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("creeperEffectMinSeconds", config.creeperEffectMinSeconds))
            .append(formatConfigLine("creeperEffectMaxSeconds", config.creeperEffectMaxSeconds))
            .append(Component.literal("\n\n--- Fortune Settings ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("customFortuneEnabled", config.customFortuneEnabled))
            .append(formatConfigLine("fortune1DropChance", config.fortune1DropChance))
            .append(formatConfigLine("fortune2DropChance", config.fortune2DropChance))
            .append(formatConfigLine("fortune3DropChance", config.fortune3DropChance))
            .append(formatConfigLine("fortuneMaxDrops", config.fortuneMaxDrops))
            .append(Component.literal("\n\n--- Explosion Settings ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("explosionItemKnockbackEnabled", config.explosionItemKnockbackEnabled))
            .append(formatConfigLine("explosionItemKnockbackMultiplier", config.explosionItemKnockbackMultiplier))
            .append(Component.literal("\n\n--- Piston Settings ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("pistonPushLimit", config.pistonPushLimit))
            .append(Component.literal("\n\n--- Enderman Settings ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("endermanPickUpAllBlocks", config.endermanPickUpAllBlocks))
            .append(Component.literal("\n\n--- Baby Mob Settings ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("babySkeletonsEnabled", config.babySkeletonsEnabled))
            .append(formatConfigLine("babyCreepersEnabled", config.babyCreepersEnabled))
            .append(Component.literal("\n\n--- Fishing Settings ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("fishingChargedCreeperChance", config.fishingChargedCreeperChance))
            .append(Component.literal("\n\n--- Moss Growth Settings ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("mossGrowthEnabled", config.mossGrowthEnabled))
            .append(formatConfigLine("mossTickCheckFrequency", config.mossTickCheckFrequency))
            .append(formatConfigLine("mossBaseChance", config.mossBaseChance))
            .append(formatConfigLine("mossNearbyBonus", config.mossNearbyBonus))
            .append(formatConfigLine("mossUnderwaterMultiplier", config.mossUnderwaterMultiplier))
            .append(formatConfigLine("stoneBrickMossMultiplier", config.stoneBrickMossMultiplier))
            .append(Component.literal("\n\n--- Zombie Settings ---").withStyle(ChatFormatting.YELLOW))
            .append(formatConfigLine("zombieFollowRange", config.zombieFollowRange))
            .append(Component.literal("\n\nUse /dd config <setting> [value] to change or query settings").withStyle(ChatFormatting.GRAY)), false);
        return 1;
    }

    private static Component formatConfigLine(String key, Object value) {
        return Component.literal("\n  " + key + ": ").withStyle(ChatFormatting.WHITE)
            .append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.AQUA));
    }

    private static int executeConfigQuery(CommandContext<CommandSourceStack> context, String configKey) {
        CommandSourceStack source = context.getSource();
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
                source.sendFailure(Component.literal("Unknown config key: " + configKey).withStyle(ChatFormatting.RED));
                yield null;
            }
        };

        if (value == null) return 0;

        source.sendSuccess(() -> Component.literal(configKey + ": ").withStyle(ChatFormatting.WHITE)
            .append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.AQUA)), false);
        return 1;
    }

    private static int executeConfigBoolean(CommandContext<CommandSourceStack> context, String configKey) {
        CommandSourceStack source = context.getSource();
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
                source.sendFailure(Component.literal("Unknown config key: " + configKey).withStyle(ChatFormatting.RED));
                return 0;
            }
        }

        DeeperDarkConfig.save();
        source.sendSuccess(() -> Component.literal("Set ")
            .append(Component.literal(configKey).withStyle(ChatFormatting.AQUA))
            .append(Component.literal(" to "))
            .append(Component.literal(String.valueOf(value)).withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED)), true);
        return 1;
    }

    private static int executeConfigInt(CommandContext<CommandSourceStack> context, String configKey) {
        CommandSourceStack source = context.getSource();
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
                source.sendFailure(Component.literal("Unknown config key: " + configKey).withStyle(ChatFormatting.RED));
                return 0;
            }
        }

        DeeperDarkConfig.save();
        source.sendSuccess(() -> Component.literal("Set ")
            .append(Component.literal(configKey).withStyle(ChatFormatting.AQUA))
            .append(Component.literal(" to "))
            .append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.GOLD)), true);
        return 1;
    }

    private static int executeConfigDouble(CommandContext<CommandSourceStack> context, String configKey) {
        CommandSourceStack source = context.getSource();
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
                source.sendFailure(Component.literal("Unknown config key: " + configKey).withStyle(ChatFormatting.RED));
                return 0;
            }
        }

        DeeperDarkConfig.save();
        source.sendSuccess(() -> Component.literal("Set ")
            .append(Component.literal(configKey).withStyle(ChatFormatting.AQUA))
            .append(Component.literal(" to "))
            .append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.GOLD)), true);
        return 1;
    }

    // ----- Reload command -----
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
