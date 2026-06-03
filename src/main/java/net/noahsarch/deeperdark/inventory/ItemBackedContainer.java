package net.noahsarch.deeperdark.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.List;

/**
 * A SimpleContainer backed by the CONTAINER data component on a player's inventory item.
 * Changes are written back to the source item when the menu is closed via stopOpen().
 */
public class ItemBackedContainer extends SimpleContainer {
    private final ServerPlayer player;
    private final ItemStack sourceStack;

    private ItemBackedContainer(ServerPlayer player, ItemStack sourceStack, int size) {
        super(size);
        this.player = player;
        this.sourceStack = sourceStack;
        NonNullList<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);
        sourceStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
        for (int i = 0; i < size; i++) {
            this.setItem(i, items.get(i));
        }
    }

    /**
     * Creates a container backed by the item at the given inventory slot.
     * The source item reference is held; the container is valid as long as
     * that exact ItemStack object remains in the player's inventory.
     */
    public static ItemBackedContainer of(ServerPlayer player, int inventorySlot, int size) {
        ItemStack stack = player.getInventory().getItem(inventorySlot);
        return new ItemBackedContainer(player, stack, size);
    }

    public ItemStack getSourceStack() {
        return sourceStack;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (stack == sourceStack) return false;
        // Block all container items (shulker boxes, our boxes) from being nested.
        if (ContainerItemUtil.getContainerSize(stack) >= 0) return false;
        return super.canPlaceItem(slot, stack);
    }

    @Override
    public boolean stillValid(Player p) {
        return p == this.player && findSourceStackInInventory() != -1;
    }

    @Override
    public void stopOpen(ContainerUser user) {
        super.stopOpen(user);
        saveBack();
    }

    private void saveBack() {
        if (findSourceStackInInventory() == -1) return;
        List<ItemStack> items = new ArrayList<>(getContainerSize());
        for (int i = 0; i < getContainerSize(); i++) {
            items.add(getItem(i).copy());
        }
        sourceStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        player.inventoryMenu.broadcastChanges();
    }

    /**
     * Finds the slot where sourceStack (by object reference) currently lives.
     * Returns -1 if the item is no longer in the player's inventory.
     */
    private int findSourceStackInInventory() {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i) == sourceStack) {
                return i;
            }
        }
        return -1;
    }
}
