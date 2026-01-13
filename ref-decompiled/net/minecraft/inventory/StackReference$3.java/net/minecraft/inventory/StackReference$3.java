/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import java.util.List;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;

static class StackReference.3
implements StackReference {
    final /* synthetic */ List field_64139;
    final /* synthetic */ int field_64140;

    StackReference.3() {
        this.field_64139 = list;
        this.field_64140 = i;
    }

    @Override
    public ItemStack get() {
        return (ItemStack)this.field_64139.get(this.field_64140);
    }

    @Override
    public boolean set(ItemStack stack) {
        this.field_64139.set(this.field_64140, stack);
        return true;
    }
}
