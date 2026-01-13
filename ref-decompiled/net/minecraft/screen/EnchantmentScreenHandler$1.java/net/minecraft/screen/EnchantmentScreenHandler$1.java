/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.SimpleInventory;

class EnchantmentScreenHandler.1
extends SimpleInventory {
    EnchantmentScreenHandler.1(int i) {
        super(i);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        EnchantmentScreenHandler.this.onContentChanged(this);
    }
}
