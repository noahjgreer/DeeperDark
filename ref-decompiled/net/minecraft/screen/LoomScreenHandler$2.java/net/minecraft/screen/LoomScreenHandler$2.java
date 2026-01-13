/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.SimpleInventory;

class LoomScreenHandler.2
extends SimpleInventory {
    LoomScreenHandler.2(int i) {
        super(i);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        LoomScreenHandler.this.inventoryChangeListener.run();
    }
}
