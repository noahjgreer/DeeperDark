/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.SimpleInventory;

class GrindstoneScreenHandler.1
extends SimpleInventory {
    GrindstoneScreenHandler.1(int i) {
        super(i);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        GrindstoneScreenHandler.this.onContentChanged(this);
    }
}
