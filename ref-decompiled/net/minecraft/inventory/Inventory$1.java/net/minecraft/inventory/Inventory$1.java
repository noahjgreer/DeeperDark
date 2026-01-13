/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;

class Inventory.1
implements StackReference {
    final /* synthetic */ int field_64116;

    Inventory.1() {
        this.field_64116 = i;
    }

    @Override
    public ItemStack get() {
        return Inventory.this.getStack(this.field_64116);
    }

    @Override
    public boolean set(ItemStack stack) {
        Inventory.this.setStack(this.field_64116, stack);
        return true;
    }
}
