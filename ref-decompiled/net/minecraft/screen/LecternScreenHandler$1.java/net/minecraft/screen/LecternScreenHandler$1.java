/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

class LecternScreenHandler.1
extends Slot {
    LecternScreenHandler.1(Inventory inventory, int i, int j, int k) {
        super(inventory, i, j, k);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        LecternScreenHandler.this.onContentChanged(this.inventory);
    }
}
