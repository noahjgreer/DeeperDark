/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

class LecternBlockEntity.1
implements Inventory {
    LecternBlockEntity.1() {
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return LecternBlockEntity.this.book.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot == 0 ? LecternBlockEntity.this.book : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot == 0) {
            ItemStack itemStack = LecternBlockEntity.this.book.split(amount);
            if (LecternBlockEntity.this.book.isEmpty()) {
                LecternBlockEntity.this.onBookRemoved();
            }
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot == 0) {
            ItemStack itemStack = LecternBlockEntity.this.book;
            LecternBlockEntity.this.book = ItemStack.EMPTY;
            LecternBlockEntity.this.onBookRemoved();
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public void markDirty() {
        LecternBlockEntity.this.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(LecternBlockEntity.this, player) && LecternBlockEntity.this.hasBook();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public void clear() {
    }
}
