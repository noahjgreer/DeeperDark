/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.util.Iterator;
import java.util.NoSuchElementException;
import net.minecraft.nbt.NbtElement;

class AbstractNbtList.1
implements Iterator<NbtElement> {
    private int current;

    AbstractNbtList.1() {
    }

    @Override
    public boolean hasNext() {
        return this.current < AbstractNbtList.this.size();
    }

    @Override
    public NbtElement next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        return AbstractNbtList.this.method_10534(this.current++);
    }

    @Override
    public /* synthetic */ Object next() {
        return this.next();
    }
}
