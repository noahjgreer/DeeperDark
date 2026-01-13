/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import java.util.Collection;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public static interface ItemGroup.Entries {
    public void add(ItemStack var1, ItemGroup.StackVisibility var2);

    default public void add(ItemStack stack) {
        this.add(stack, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
    }

    default public void add(ItemConvertible item, ItemGroup.StackVisibility visibility) {
        this.add(new ItemStack(item), visibility);
    }

    default public void add(ItemConvertible item) {
        this.add(new ItemStack(item), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
    }

    default public void addAll(Collection<ItemStack> stacks, ItemGroup.StackVisibility visibility) {
        stacks.forEach(stack -> this.add((ItemStack)stack, visibility));
    }

    default public void addAll(Collection<ItemStack> stacks) {
        this.addAll(stacks, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
    }
}
