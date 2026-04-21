package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

/**
 * Handles middle-click pick block for custom blocks.
 * Note: This is a serverside approximation - uses sneak+right-click as a workaround.
 */
public class CustomBlockPickBlock {

    public static void register() {
        // This won't catch actual middle-click (that's client-only),
        // but we can provide a workaround using right-click while sneaking
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient() || hand != InteractionHand.MAIN_HAND) {
                return InteractionResult.PASS;
            }

            // Only trigger if player is sneaking and holding nothing or the same custom block
            if (!player.isSneaking()) {
                return InteractionResult.PASS;
            }

            BlockPos pos = hitResult.getBlockPos();
            ItemStack clickedBlock = getCustomBlockItem(world, pos);

            if (clickedBlock == null) {
                return InteractionResult.PASS;
            }

            // Check if player has permission (creative or has item in inventory)
            ServerPlayer serverPlayer = (ServerPlayer) player;
            if (!serverPlayer.isCreative() && !hasItemInInventory(serverPlayer, clickedBlock)) {
                return InteractionResult.PASS;
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
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        });
    }

    /**
     * Get the custom block item at the given position
     */
    private static ItemStack getCustomBlockItem(Level world, BlockPos pos) {
        AABB box = new AABB(pos);
        var displays = world.getEntitiesByClass(
            Display.ItemDisplay.class,
            box,
            entity -> {
                ItemStack stack = entity.getItemStack();
                return stack.contains(DataComponents.ITEM_MODEL);
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
    private static boolean hasItemInInventory(ServerPlayer player, ItemStack item) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (ItemStack.areItemsAndComponentsEqual(stack, item)) {
                return true;
            }
        }
        return false;
    }
}

