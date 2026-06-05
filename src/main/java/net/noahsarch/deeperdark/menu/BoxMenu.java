package net.noahsarch.deeperdark.menu;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.inventory.ContainerItemUtil;

public class BoxMenu extends AbstractContainerMenu {
    private static final int COLUMNS = 3;
    private static final int PLAYER_INV_SLOT_COUNT = 36;
    private final Container container;
    private final int rows;
    private final int containerSlotCount;

    public BoxMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, Container container, int rows) {
        super(menuType, containerId);
        this.container = container;
        this.rows = rows;
        this.containerSlotCount = rows * COLUMNS;

        checkContainerSize(container, this.containerSlotCount);
        container.startOpen(playerInventory.player);

        // Center box slots in the space between the title area (y=18) and player inventory label (y=72).
        // Available height = 54; slots height = rows * 18.
        int slotTop = 17 + (54 - rows * 18) / 2;
        addBoxSlots(container, 62, slotTop);
        addStandardInventorySlots(playerInventory, 8, 84);
    }

    public int getRowCount() {
        return this.rows;
    }

    private void addBoxSlots(Container container, int left, int top) {
        for (int row = 0; row < this.rows; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int slotIndex = col + row * COLUMNS;
                this.addSlot(new Slot(container, slotIndex, left + col * 18, top + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        // Per spec: custom boxes block all shulker boxes and all custom boxes.
                        if (stack.is(ItemTags.SHULKER_BOXES)) return false;
                        if (ContainerItemUtil.getContainerSize(stack) >= 0) return false;
                        return this.container.canPlaceItem(this.getContainerSlot(), stack);
                    }
                });
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex < this.containerSlotCount) {
                if (!this.moveItemStackTo(stack, this.containerSlotCount, this.containerSlotCount + PLAYER_INV_SLOT_COUNT, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, this.containerSlotCount, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == clicked.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return clicked;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}
