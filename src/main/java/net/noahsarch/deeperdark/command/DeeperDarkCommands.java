package net.noahsarch.deeperdark.command;

import com.mojang.brigadier.CommandDispatcher;
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
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("ddgive")
            //.requires(source -> source.hasPermissionLevel(2)) // Operator only
            .then(CommandManager.argument("item", StringArgumentType.word())
                .suggests(DeeperDarkCommands::suggestItems)
                .executes(DeeperDarkCommands::executeGive)));
    }

    private static CompletableFuture<Suggestions> suggestItems(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(REGISTRY.keySet(), builder);
    }

    private static int executeGive(CommandContext<ServerCommandSource> context) {
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
            giveStack.setCount(1); // Default count 1 for now

            boolean inserted = player.getInventory().insertStack(giveStack);
            if (!inserted) {
                player.dropItem(giveStack, false);
            }

            source.sendFeedback(() -> Text.literal("Gave 1 [" + itemName + "] to " + player.getName().getString()), true);
            return 1;
        } catch (Exception e) {
            source.sendError(Text.literal("Failed to give item: " + e.getMessage()));
            return 0;
        }
    }
}
