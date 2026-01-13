/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.storage;

import com.google.common.collect.AbstractIterator;
import java.util.ListIterator;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import org.jspecify.annotations.Nullable;

class NbtReadView.NbtListReadView.1
extends AbstractIterator<ReadView> {
    final /* synthetic */ ListIterator field_60386;

    NbtReadView.NbtListReadView.1(ListIterator listIterator) {
        this.field_60386 = listIterator;
    }

    protected @Nullable ReadView computeNext() {
        if (this.field_60386.hasNext()) {
            int i = this.field_60386.nextIndex();
            NbtCompound nbtCompound = (NbtCompound)this.field_60386.next();
            return NbtListReadView.this.createReadView(i, nbtCompound);
        }
        return (ReadView)this.endOfData();
    }

    protected /* synthetic */ @Nullable Object computeNext() {
        return this.computeNext();
    }
}
