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
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadContext;
import net.minecraft.storage.ReadView;
import net.minecraft.util.ErrorReporter;
import org.jspecify.annotations.Nullable;

static class NbtReadView.ChildListReadView
implements ReadView.ListReadView {
    private final ErrorReporter reporter;
    private final String name;
    final ReadContext context;
    private final NbtList list;

    NbtReadView.ChildListReadView(ErrorReporter reporter, String name, ReadContext context, NbtList list) {
        this.reporter = reporter;
        this.name = name;
        this.context = context;
        this.list = list;
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    ErrorReporter createErrorReporter(int index) {
        return this.reporter.makeChild(new ErrorReporter.NamedListElementContext(this.name, index));
    }

    void reportExpectedTypeAtIndexError(int index, NbtElement element) {
        this.reporter.report(new NbtReadView.ExpectedTypeAtIndexError(this.name, index, NbtCompound.TYPE, element.getNbtType()));
    }

    @Override
    public Stream<ReadView> stream() {
        return Streams.mapWithIndex(this.list.stream(), (element, index) -> {
            if (element instanceof NbtCompound) {
                NbtCompound nbtCompound = (NbtCompound)element;
                return NbtReadView.createReadView(this.createErrorReporter((int)index), this.context, nbtCompound);
            }
            this.reportExpectedTypeAtIndexError((int)index, (NbtElement)element);
            return null;
        }).filter(Objects::nonNull);
    }

    @Override
    public Iterator<ReadView> iterator() {
        final Iterator iterator = this.list.iterator();
        return new AbstractIterator<ReadView>(){
            private int index;

            protected @Nullable ReadView computeNext() {
                while (iterator.hasNext()) {
                    int i;
                    NbtElement nbtElement = (NbtElement)iterator.next();
                    ++this.index;
                    if (nbtElement instanceof NbtCompound) {
                        NbtCompound nbtCompound = (NbtCompound)nbtElement;
                        return NbtReadView.createReadView(this.createErrorReporter(i), context, nbtCompound);
                    }
                    this.reportExpectedTypeAtIndexError(i, nbtElement);
                }
                return (ReadView)this.endOfData();
            }

            protected /* synthetic */ @Nullable Object computeNext() {
                return this.computeNext();
            }
        };
    }
}
