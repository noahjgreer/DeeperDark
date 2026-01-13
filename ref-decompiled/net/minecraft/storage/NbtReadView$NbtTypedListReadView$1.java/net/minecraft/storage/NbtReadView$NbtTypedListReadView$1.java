/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$Error
 *  com.mojang.serialization.DataResult$Success
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.storage;

import com.google.common.collect.AbstractIterator;
import com.mojang.serialization.DataResult;
import java.lang.runtime.SwitchBootstraps;
import java.util.ListIterator;
import java.util.Objects;
import net.minecraft.nbt.NbtElement;
import org.jspecify.annotations.Nullable;

class NbtReadView.NbtTypedListReadView.1
extends AbstractIterator<T> {
    final /* synthetic */ ListIterator field_60400;

    NbtReadView.NbtTypedListReadView.1(ListIterator listIterator) {
        this.field_60400 = listIterator;
    }

    protected @Nullable T computeNext() {
        while (this.field_60400.hasNext()) {
            DataResult dataResult;
            int i = this.field_60400.nextIndex();
            NbtElement nbtElement = (NbtElement)this.field_60400.next();
            Objects.requireNonNull(NbtTypedListReadView.this.typeCodec.parse(NbtTypedListReadView.this.context.getOps(), (Object)nbtElement));
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
            NbtTypedListReadView.this.reportDecodeAtIndexError(i, nbtElement, error);
            if (!error.partialValue().isPresent()) continue;
            return error.partialValue().get();
        }
        return this.endOfData();
    }
}
