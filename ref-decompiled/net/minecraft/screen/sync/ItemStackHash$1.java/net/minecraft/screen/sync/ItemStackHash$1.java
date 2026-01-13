/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen.sync;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.sync.ComponentChangesHash;
import net.minecraft.screen.sync.ItemStackHash;

class ItemStackHash.1
implements ItemStackHash {
    ItemStackHash.1() {
    }

    public String toString() {
        return "<empty>";
    }

    @Override
    public boolean hashEquals(ItemStack stack, ComponentChangesHash.ComponentHasher hasher) {
        return stack.isEmpty();
    }
}
