/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public static class CreativeInventoryScreen.CreativeScreenHandler
extends ScreenHandler {
    public final DefaultedList<ItemStack> itemList = DefaultedList.of();
    private final ScreenHandler parent;

    public CreativeInventoryScreen.CreativeScreenHandler(PlayerEntity player) {
        super(null, 0);
        this.parent = player.playerScreenHandler;
        PlayerInventory playerInventory = player.getInventory();
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new CreativeInventoryScreen.LockableSlot(INVENTORY, i * 9 + j, 9 + j * 18, 18 + i * 18));
            }
        }
        this.addPlayerHotbarSlots(playerInventory, 9, 112);
        this.scrollItems(0.0f);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    protected int getOverflowRows() {
        return MathHelper.ceilDiv(this.itemList.size(), 9) - 5;
    }

    protected int getRow(float scroll) {
        return Math.max((int)((double)(scroll * (float)this.getOverflowRows()) + 0.5), 0);
    }

    protected float getScrollPosition(int row) {
        return MathHelper.clamp((float)row / (float)this.getOverflowRows(), 0.0f, 1.0f);
    }

    protected float getScrollPosition(float current, double amount) {
        return MathHelper.clamp(current - (float)(amount / (double)this.getOverflowRows()), 0.0f, 1.0f);
    }

    public void scrollItems(float position) {
        int i = this.getRow(position);
        for (int j = 0; j < 5; ++j) {
            for (int k = 0; k < 9; ++k) {
                int l = k + (j + i) * 9;
                if (l >= 0 && l < this.itemList.size()) {
                    INVENTORY.setStack(k + j * 9, this.itemList.get(l));
                    continue;
                }
                INVENTORY.setStack(k + j * 9, ItemStack.EMPTY);
            }
        }
    }

    public boolean shouldShowScrollbar() {
        return this.itemList.size() > 45;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        Slot slot2;
        if (slot >= this.slots.size() - 9 && slot < this.slots.size() && (slot2 = (Slot)this.slots.get(slot)) != null && slot2.hasStack()) {
            slot2.setStack(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != INVENTORY;
    }

    @Override
    public boolean canInsertIntoSlot(Slot slot) {
        return slot.inventory != INVENTORY;
    }

    @Override
    public ItemStack getCursorStack() {
        return this.parent.getCursorStack();
    }

    @Override
    public void setCursorStack(ItemStack stack) {
        this.parent.setCursorStack(stack);
    }
}
