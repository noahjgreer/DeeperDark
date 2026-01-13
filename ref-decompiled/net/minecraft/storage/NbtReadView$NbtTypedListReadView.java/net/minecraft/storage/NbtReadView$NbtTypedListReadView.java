/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  com.google.common.collect.Streams
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$Error
 *  com.mojang.serialization.DataResult$Success
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.storage;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Streams;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.lang.runtime.SwitchBootstraps;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadContext;
import net.minecraft.storage.ReadView;
import net.minecraft.util.ErrorReporter;
import org.jspecify.annotations.Nullable;

static class NbtReadView.NbtTypedListReadView<T>
implements ReadView.TypedListReadView<T> {
    private final ErrorReporter reporter;
    private final String name;
    final ReadContext context;
    final Codec<T> typeCodec;
    private final NbtList list;

    NbtReadView.NbtTypedListReadView(ErrorReporter reporter, String name, ReadContext context, Codec<T> typeCodec, NbtList list) {
        this.reporter = reporter;
        this.name = name;
        this.context = context;
        this.typeCodec = typeCodec;
        this.list = list;
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    void reportDecodeAtIndexError(int index, NbtElement element, DataResult.Error<?> error) {
        this.reporter.report(new NbtReadView.DecodeAtIndexError(this.name, index, element, error));
    }

    @Override
    public Stream<T> stream() {
        return Streams.mapWithIndex(this.list.stream(), (element, index) -> {
            DataResult dataResult = this.typeCodec.parse(this.context.getOps(), element);
            Objects.requireNonNull(dataResult);
            DataResult dataResult2 = dataResult;
            int i = 0;
            return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{DataResult.Success.class, DataResult.Error.class}, (Object)dataResult2, i)) {
                default -> throw new MatchException(null, null);
                case 0 -> {
                    DataResult.Success success = (DataResult.Success)dataResult2;
                    yield success.value();
                }
                case 1 -> {
                    DataResult.Error error = (DataResult.Error)dataResult2;
                    this.reportDecodeAtIndexError((int)index, (NbtElement)element, (DataResult.Error<?>)error);
                    yield error.partialValue().orElse(null);
                }
            };
        }).filter(Objects::nonNull);
    }

    @Override
    public Iterator<T> iterator() {
        final ListIterator listIterator = this.list.listIterator();
        return new AbstractIterator<T>(){

            protected @Nullable T computeNext() {
                while (listIterator.hasNext()) {
                    DataResult dataResult;
                    int i = listIterator.nextIndex();
                    NbtElement nbtElement = (NbtElement)listIterator.next();
                    Objects.requireNonNull(typeCodec.parse(context.getOps(), (Object)nbtElement));
                    int n = 0;
                    switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{DataResult.Success.class, DataResult.Error.class}, (Object)dataResult, n)) {
                        default: {
                            throw new MatchException(null, null);
                        }
                        case 0: {
                            DataResult.Success success = (DataResult.Success)dataResult;
                            return success.value();
                        }
                        case 1: 
                    }
                    DataResult.Error error = (DataResult.Error)dataResult;
                    this.reportDecodeAtIndexError(i, nbtElement, error);
                    if (!error.partialValue().isPresent()) continue;
                    return error.partialValue().get();
                }
                return this.endOfData();
            }
        };
    }
}
