/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  com.google.common.collect.Streams
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.storage;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Streams;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadContext;
import net.minecraft.storage.ReadView;
import net.minecraft.util.ErrorReporter;
import org.jspecify.annotations.Nullable;

static class NbtReadView.NbtListReadView
implements ReadView.ListReadView {
    private final ErrorReporter reporter;
    private final ReadContext context;
    private final List<NbtCompound> nbts;

    public NbtReadView.NbtListReadView(ErrorReporter reporter, ReadContext context, List<NbtCompound> nbts) {
        this.reporter = reporter;
        this.context = context;
        this.nbts = nbts;
    }

    ReadView createReadView(int index, NbtCompound nbt) {
        return NbtReadView.createReadView(this.reporter.makeChild(new ErrorReporter.ListElementContext(index)), this.context, nbt);
    }

    @Override
    public boolean isEmpty() {
        return this.nbts.isEmpty();
    }

    @Override
    public Stream<ReadView> stream() {
        return Streams.mapWithIndex(this.nbts.stream(), (nbt, index) -> this.createReadView((int)index, (NbtCompound)nbt));
    }

    @Override
    public Iterator<ReadView> iterator() {
        final ListIterator<NbtCompound> listIterator = this.nbts.listIterator();
        return new AbstractIterator<ReadView>(){

            protected @Nullable ReadView computeNext() {
                if (listIterator.hasNext()) {
                    int i = listIterator.nextIndex();
                    NbtCompound nbtCompound = (NbtCompound)listIterator.next();
                    return this.createReadView(i, nbtCompound);
                }
                return (ReadView)this.endOfData();
            }

            protected /* synthetic */ @Nullable Object computeNext() {
                return this.computeNext();
            }
        };
    }
}
