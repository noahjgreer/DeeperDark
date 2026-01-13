/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import java.util.Collection;
import java.util.Set;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;
import net.minecraft.resource.featuretoggle.FeatureSet;

static class ItemGroup.EntriesImpl
implements ItemGroup.Entries {
    public final Collection<ItemStack> parentTabStacks = ItemStackSet.create();
    public final Set<ItemStack> searchTabStacks = ItemStackSet.create();
    private final ItemGroup group;
    private final FeatureSet enabledFeatures;

    public ItemGroup.EntriesImpl(ItemGroup group, FeatureSet enabledFeatures) {
        this.group = group;
        this.enabledFeatures = enabledFeatures;
    }

    @Override
    public void add(ItemStack stack, ItemGroup.StackVisibility visibility) {
        boolean bl;
        if (stack.getCount() != 1) {
            throw new IllegalArgumentException("Stack size must be exactly 1");
        }
        boolean bl2 = bl = this.parentTabStacks.contains(stack) && visibility != ItemGroup.StackVisibility.SEARCH_TAB_ONLY;
        if (bl) {
            throw new IllegalStateException("Accidentally adding the same item stack twice " + stack.toHoverableText().getString() + " to a Creative Mode Tab: " + this.group.getDisplayName().getString());
        }
        if (stack.getItem().isEnabled(this.enabledFeatures)) {
            switch (visibility.ordinal()) {
                case 0: {
                    this.parentTabStacks.add(stack);
                    this.searchTabStacks.add(stack);
                    break;
                }
                case 1: {
                    this.parentTabStacks.add(stack);
                    break;
                }
                case 2: {
                    this.searchTabStacks.add(stack);
                }
            }
        }
    }
}
