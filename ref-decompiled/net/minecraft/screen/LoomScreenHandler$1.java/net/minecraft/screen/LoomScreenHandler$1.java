/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.SimpleInventory;

class LoomScreenHandler.1
extends SimpleInventory {
    LoomScreenHandler.1(int i) {
        super(i);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        LoomScreenHandler.this.onContentChanged(this);
        LoomScreenHandler.this.inventoryChangeListener.run();
    }
}
