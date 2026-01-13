/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.player;

import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;

class PlayerEntity.1
implements StackReference {
    PlayerEntity.1() {
    }

    @Override
    public ItemStack get() {
        return PlayerEntity.this.currentScreenHandler.getCursorStack();
    }

    @Override
    public boolean set(ItemStack stack) {
        PlayerEntity.this.currentScreenHandler.setCursorStack(stack);
        return true;
    }
}
