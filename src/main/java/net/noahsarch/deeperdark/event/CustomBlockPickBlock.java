package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

/**
 * Handles middle-click pick block for custom blocks.
 * Note: This is a serverside approximation - uses sneak+right-click as a workaround.
 */
public class CustomBlockPickBlock {

    public static void register() {
        // This won't catch actual middle-click (that's client-only),
        // but we can provide a workaround using right-click while sneaking
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient() || hand != Hand.MAIN_HAND) {
                return ActionResult.PASS;
            }

            // Only trigger if player is sneaking and holding nothing or the same custom block
            if (!player.isSneaking()) {
                return ActionResult.PASS;
            }

            BlockPos pos = hitResult.getBlockPos();
            ItemStack clickedBlock = getCustomBlockItem(world, pos);

            if (clickedBlock == null) {
                return ActionResult.PASS;
            }

            // Check if player has permission (creative or has item in inventory)
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            if (!serverPlayer.isCreative() && !hasItemInInventory(serverPlayer, clickedBlock)) {
                return ActionResult.PASS;
            }

            // Get the custom block item
            ItemStack pickStack = clickedBlock.copy();
            pickStack.setCount(1);

            // Try to put it in the selected slot
            int selectedSlot = serverPlayer.getInventory().getSelectedSlot();
            ItemStack currentStack = serverPlayer.getInventory().getStack(selectedSlot);

            // Only replace if slot is empty or contains the same item
            if (currentStack.isEmpty() || ItemStack.areItemsAndComponentsEqual(currentStack, pickStack)) {
                serverPlayer.getInventory().setStack(selectedSlot, pickStack);
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });
    }

    /**
     * Get the custom block item at the given position
     */
    private static ItemStack getCustomBlockItem(World world, BlockPos pos) {
        Box box = new Box(pos);
        var displays = world.getEntitiesByClass(
            DisplayEntity.ItemDisplayEntity.class,
            box,
            entity -> {
                ItemStack stack = entity.getItemStack();
                return stack.contains(DataComponentTypes.ITEM_MODEL);
            }
        );

        if (displays.isEmpty()) {
            return null;
        }

        // Return the first custom block found
        return displays.getFirst().getItemStack();
    }

    /**
     * Check if player has the item in their inventory
     */
    private static boolean hasItemInInventory(ServerPlayerEntity player, ItemStack item) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (ItemStack.areItemsAndComponentsEqual(stack, item)) {
                return true;
            }
        }
        return false;
    }
}

