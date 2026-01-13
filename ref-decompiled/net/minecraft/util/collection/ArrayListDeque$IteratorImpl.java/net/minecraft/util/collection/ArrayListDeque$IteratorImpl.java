/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.collection;

import java.util.Iterator;

class ArrayListDeque.IteratorImpl
implements Iterator<T> {
    private int currentIndex;

    public ArrayListDeque.IteratorImpl() {
        this.currentIndex = ArrayListDeque.this.size() - 1;
    }

    @Override
    public boolean hasNext() {
        return this.currentIndex >= 0;
    }

    @Override
    public T next() {
        return ArrayListDeque.this.get(this.currentIndex--);
    }

    @Override
    public void remove() {
        ArrayListDeque.this.remove(this.currentIndex + 1);
    }
}
