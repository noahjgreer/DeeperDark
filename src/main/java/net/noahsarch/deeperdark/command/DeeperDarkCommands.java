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
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.event.GoldenCauldronEvents;
import net.noahsarch.deeperdark.event.GunpowderBlockEvents;
import net.noahsarch.deeperdark.event.SiphonEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
        // ddgive command with optional amount parameter
        dispatcher.register(CommandManager.literal("ddgive")
            .then(CommandManager.argument("item", StringArgumentType.word())
                .suggests(DeeperDarkCommands::suggestItems)
                .executes(DeeperDarkCommands::executeGive) // Default 1 item
                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1, 64))
                    .executes(DeeperDarkCommands::executeGiveWithAmount))));

        // ddborder command for configuring world border
        dispatcher.register(CommandManager.literal("ddborder")
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
            .then(CommandManager.literal("allowEntitySpawning")
                .then(CommandManager.argument("allow", BoolArgumentType.bool())
                    .executes(DeeperDarkCommands::executeSetAllowEntitySpawning)))
            .then(CommandManager.literal("info")
                .executes(DeeperDarkCommands::executeInfo)));
    }

    private static CompletableFuture<Suggestions> suggestItems(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(REGISTRY.keySet(), builder);
    }

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
            source.sendError(Text.literal("Unknown item: " + itemName));
            return 0;
        }

        try {
            // Give to player
            net.minecraft.server.network.ServerPlayerEntity player = source.getPlayerOrThrow();
            ItemStack giveStack = stack.copy();
            giveStack.setCount(amount);

            boolean inserted = player.getInventory().insertStack(giveStack);
            if (!inserted) {
                player.dropItem(giveStack, false);
            }

            source.sendFeedback(() -> Text.literal("Gave " + amount + " [" + itemName + "] to " + player.getName().getString()), true);
            return 1;
        } catch (Exception e) {
            source.sendError(Text.literal("Failed to give item: " + e.getMessage()));
            return 0;
        }
    }

    // ddborder command handlers

    private static int executeSetOrigin(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        int x = IntegerArgumentType.getInteger(context, "x");
        int z = IntegerArgumentType.getInteger(context, "z");

        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        config.originX = x;
        config.originZ = z;
        DeeperDarkConfig.save();

        source.sendFeedback(() -> Text.literal("Border origin set to (" + x + ", " + z + ")"), true);
        return 1;
    }

    private static int executeSetRadius(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        double radius = DoubleArgumentType.getDouble(context, "radius");

        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        config.safeRadius = radius;
        DeeperDarkConfig.save();

        source.sendFeedback(() -> Text.literal("Border radius set to " + radius + " blocks"), true);
        return 1;
    }

    private static int executeSetForce(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        double multiplier = DoubleArgumentType.getDouble(context, "multiplier");

        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        config.forceMultiplier = multiplier;
        DeeperDarkConfig.save();

        source.sendFeedback(() -> Text.literal("Border force multiplier set to " + multiplier), true);
        return 1;
    }

    private static int executeSetAllowEntitySpawning(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        boolean allow = BoolArgumentType.getBool(context, "allow");

        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        config.allowEntitySpawning = allow;
        DeeperDarkConfig.save();

        String status = allow ? "enabled" : "disabled";
        source.sendFeedback(() -> Text.literal("Entity spawning outside border " + status +
            (allow ? " (mobs will not be pushed)" : " (mobs will be pushed back)")), true);
        return 1;
    }

    private static int executeInfo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        source.sendFeedback(() -> Text.literal("=== Border Configuration ===\n" +
            "Origin: (" + config.originX + ", " + config.originZ + ")\n" +
            "Radius: " + config.safeRadius + " blocks\n" +
            "Force Multiplier: " + config.forceMultiplier + "\n" +
            "Allow Entity Spawning: " + config.allowEntitySpawning), false);
        return 1;
    }
}
