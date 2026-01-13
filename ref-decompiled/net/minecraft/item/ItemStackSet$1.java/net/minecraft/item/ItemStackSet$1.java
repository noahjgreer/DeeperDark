/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.Hash$Strategy
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.item;

import it.unimi.dsi.fastutil.Hash;
import net.minecraft.item.ItemStack;
import org.jspecify.annotations.Nullable;

class ItemStackSet.1
implements Hash.Strategy<ItemStack> {
    ItemStackSet.1() {
    }

    public int hashCode(@Nullable ItemStack itemStack) {
        return ItemStack.hashCode(itemStack);
    }

    public boolean equals(@Nullable ItemStack itemStack, @Nullable ItemStack itemStack2) {
        return itemStack == itemStack2 || itemStack != null && itemStack2 != null && itemStack.isEmpty() == itemStack2.isEmpty() && ItemStack.areItemsAndComponentsEqual(itemStack, itemStack2);
    }

    public /* synthetic */ boolean equals(@Nullable Object first, @Nullable Object second) {
        return this.equals((ItemStack)first, (ItemStack)second);
    }

    public /* synthetic */ int hashCode(@Nullable Object stack) {
        return this.hashCode((ItemStack)stack);
    }
}
