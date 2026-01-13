/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;

class ScreenHandler.1
implements StackReference {
    ScreenHandler.1() {
    }

    @Override
    public ItemStack get() {
        return ScreenHandler.this.getCursorStack();
    }

    @Override
    public boolean set(ItemStack stack) {
        ScreenHandler.this.setCursorStack(stack);
        return true;
    }
}
