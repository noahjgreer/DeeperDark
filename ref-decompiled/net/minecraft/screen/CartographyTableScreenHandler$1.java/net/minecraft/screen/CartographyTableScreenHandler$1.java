/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.SimpleInventory;

class CartographyTableScreenHandler.1
extends SimpleInventory {
    CartographyTableScreenHandler.1(int i) {
        super(i);
    }

    @Override
    public void markDirty() {
        CartographyTableScreenHandler.this.onContentChanged(this);
        super.markDirty();
    }
}
