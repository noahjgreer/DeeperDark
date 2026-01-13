/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

class SortedArraySet.SetIterator
implements Iterator<T> {
    private int nextIndex;
    private int lastIndex = -1;

    SortedArraySet.SetIterator() {
    }

    @Override
    public boolean hasNext() {
        return this.nextIndex < SortedArraySet.this.size;
    }

    @Override
    public T next() {
        if (this.nextIndex >= SortedArraySet.this.size) {
            throw new NoSuchElementException();
        }
        this.lastIndex = this.nextIndex++;
        return SortedArraySet.this.elements[this.lastIndex];
    }

    @Override
    public void remove() {
        if (this.lastIndex == -1) {
            throw new IllegalStateException();
        }
        SortedArraySet.this.remove(this.lastIndex);
        --this.nextIndex;
        this.lastIndex = -1;
    }
}
