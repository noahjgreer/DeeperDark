/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.storage;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import org.jspecify.annotations.Nullable;

class NbtReadView.ChildListReadView.1
extends AbstractIterator<ReadView> {
    private int index;
    final /* synthetic */ Iterator field_60392;

    NbtReadView.ChildListReadView.1(Iterator iterator) {
        this.field_60392 = iterator;
    }

    protected @Nullable ReadView computeNext() {
        while (this.field_60392.hasNext()) {
            int i;
            NbtElement nbtElement = (NbtElement)this.field_60392.next();
            ++this.index;
            if (nbtElement instanceof NbtCompound) {
                NbtCompound nbtCompound = (NbtCompound)nbtElement;
                return NbtReadView.createReadView(ChildListReadView.this.createErrorReporter(i), ChildListReadView.this.context, nbtCompound);
            }
            ChildListReadView.this.reportExpectedTypeAtIndexError(i, nbtElement);
        }
        return (ReadView)this.endOfData();
    }

    protected /* synthetic */ @Nullable Object computeNext() {
        return this.computeNext();
    }
}
