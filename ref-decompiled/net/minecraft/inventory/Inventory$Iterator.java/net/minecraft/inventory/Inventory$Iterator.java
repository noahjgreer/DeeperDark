/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import java.util.Iterator;
import java.util.NoSuchElementException;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public static class Inventory.Iterator
implements Iterator<ItemStack> {
    private final Inventory inventory;
    private int index;
    private final int size;

    public Inventory.Iterator(Inventory inventory) {
        this.inventory = inventory;
        this.size = inventory.size();
    }

    @Override
    public boolean hasNext() {
        return this.index < this.size;
    }

    @Override
    public ItemStack next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        return this.inventory.getStack(this.index++);
    }

    @Override
    public /* synthetic */ Object next() {
        return this.next();
    }
}
