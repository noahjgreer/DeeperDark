/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.SimpleInventory;

class StonecutterScreenHandler.1
extends SimpleInventory {
    StonecutterScreenHandler.1(int i) {
        super(i);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        StonecutterScreenHandler.this.onContentChanged(this);
        StonecutterScreenHandler.this.contentsChangedListener.run();
    }
}
