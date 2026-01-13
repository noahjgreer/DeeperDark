/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.SimpleInventory;

class ForgingScreenHandler.4
extends SimpleInventory {
    ForgingScreenHandler.4(int i) {
        super(i);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        ForgingScreenHandler.this.onContentChanged(this);
    }
}
