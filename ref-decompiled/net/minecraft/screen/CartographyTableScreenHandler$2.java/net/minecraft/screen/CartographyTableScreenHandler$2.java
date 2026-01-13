/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.CraftingResultInventory;

class CartographyTableScreenHandler.2
extends CraftingResultInventory {
    CartographyTableScreenHandler.2() {
    }

    @Override
    public void markDirty() {
        CartographyTableScreenHandler.this.onContentChanged(this);
        super.markDirty();
    }
}
