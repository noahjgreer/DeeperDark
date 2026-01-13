/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.inventory.CraftingResultInventory;

class ForgingScreenHandler.1
extends CraftingResultInventory {
    ForgingScreenHandler.1() {
    }

    @Override
    public void markDirty() {
        ForgingScreenHandler.this.onContentChanged(this);
    }
}
