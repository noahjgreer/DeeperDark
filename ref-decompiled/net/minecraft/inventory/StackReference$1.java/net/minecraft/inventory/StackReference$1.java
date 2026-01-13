/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;

static class StackReference.1
implements StackReference {
    final /* synthetic */ Supplier field_64134;
    final /* synthetic */ Consumer field_64135;

    StackReference.1() {
        this.field_64134 = supplier;
        this.field_64135 = consumer;
    }

    @Override
    public ItemStack get() {
        return (ItemStack)this.field_64134.get();
    }

    @Override
    public boolean set(ItemStack stack) {
        this.field_64135.accept(stack);
        return true;
    }
}
