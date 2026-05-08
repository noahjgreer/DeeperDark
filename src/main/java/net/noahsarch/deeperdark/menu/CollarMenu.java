package net.noahsarch.deeperdark.menu;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.noahsarch.deeperdark.item.ItemMagnetItem;

import java.util.List;

public class CollarMenu extends AbstractContainerMenu {

    private static final int TRINKET_SLOTS = 5;
    private static final int PLAYER_INV_SLOT_COUNT = 36;

    private final Container container;
    private final ItemStack collarStack;

    public CollarMenu(MenuType<?> type, int containerId, Inventory playerInventory, ItemStack collarStack) {
        super(type, containerId);
        this.collarStack = collarStack;

        SimpleContainer inner = new SimpleContainer(TRINKET_SLOTS);
        ItemContainerContents contents = collarStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> trinkets = NonNullList.withSize(TRINKET_SLOTS, ItemStack.EMPTY);
        contents.copyInto(trinkets);
        for (int i = 0; i < TRINKET_SLOTS; i++) {
            inner.setItem(i, trinkets.get(i));
        }
        this.container = inner;

        container.startOpen(playerInventory.player);

        // 5 trinket slots in a row (hopper layout: x=44,62,80,98,116  y=20), max 1 item per slot
        for (int i = 0; i < TRINKET_SLOTS; i++) {
            final int slotIndex = i;
            this.addSlot(new Slot(container, slotIndex, 44 + slotIndex * 18, 20) {
                @Override
                public int getMaxStackSize() { return 1; }
                @Override
                public int getMaxStackSize(ItemStack stack) { return 1; }
            });
        }

        // Player inventory (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 51 + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 109));
        }
    }

    private void saveToCollar() {
        NonNullList<ItemStack> list = NonNullList.withSize(TRINKET_SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < TRINKET_SLOTS; i++) {
            ItemStack s = container.getItem(i);
            // Pre-initialize MAX_DAMAGE so ItemContainerContents serializes it correctly
            // before tickCollarEffects has a chance to run.
            if (s.is(Items.BLAZE_ROD) && !s.has(DataComponents.MAX_DAMAGE)) {
                s.set(DataComponents.MAX_STACK_SIZE, 1);
                s.set(DataComponents.MAX_DAMAGE, 1200);
            } else if ((s.is(Items.SPONGE) || s.is(Items.WET_SPONGE)) && !s.has(DataComponents.MAX_DAMAGE)) {
                s.set(DataComponents.MAX_STACK_SIZE, 1);
                s.set(DataComponents.MAX_DAMAGE, 2400);
            }
            list.set(i, s);
        }
        collarStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(list));

        boolean hasSponge = false, hasGold = false, hasBell = false;
        boolean hasMagnet = false, hasBlazeRod = false, hasGlowBerries = false;
        for (ItemStack s : list) {
            if (s.isEmpty()) continue;
            if (s.is(Items.SPONGE) || s.is(Items.WET_SPONGE)) hasSponge = true;
            if (s.is(Items.GOLD_INGOT)) hasGold = true;
            if (s.is(Items.BELL)) hasBell = true;
            if (s.getItem() instanceof ItemMagnetItem) hasMagnet = true;
            if (s.is(Items.BLAZE_ROD)) hasBlazeRod = true;
            if (s.is(Items.GLOW_BERRIES)) hasGlowBerries = true;
        }
        List<Boolean> flags = List.of(hasSponge, hasGold, hasBell, hasMagnet, hasBlazeRod, hasGlowBerries);
        CustomModelData existing = collarStack.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.EMPTY);
        collarStack.set(DataComponents.CUSTOM_MODEL_DATA,
            new CustomModelData(existing.floats(), flags, existing.strings(), existing.colors()));
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == this.container) {
            saveToCollar();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (slotIndex < TRINKET_SLOTS) {
                if (!this.moveItemStackTo(stack, TRINKET_SLOTS, TRINKET_SLOTS + PLAYER_INV_SLOT_COUNT, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, TRINKET_SLOTS, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        saveToCollar();
        container.stopOpen(player);
    }
}
