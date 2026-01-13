/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Encoder
 *  com.mojang.serialization.ListBuilder
 */
package net.minecraft.util.dynamic;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.ListBuilder;
import java.util.function.UnaryOperator;

protected class ForwardingDynamicOps.ForwardingListBuilder
implements ListBuilder<T> {
    private final ListBuilder<T> delegate;

    protected ForwardingDynamicOps.ForwardingListBuilder(ListBuilder<T> delegate) {
        this.delegate = delegate;
    }

    public DynamicOps<T> ops() {
        return ForwardingDynamicOps.this;
    }

    public DataResult<T> build(T prefix) {
        return this.delegate.build(prefix);
    }

    public ListBuilder<T> add(T value) {
        this.delegate.add(value);
        return this;
    }

    public ListBuilder<T> add(DataResult<T> value) {
        this.delegate.add(value);
        return this;
    }

    public <E> ListBuilder<T> add(E value, Encoder<E> encoder) {
        this.delegate.add(encoder.encodeStart(this.ops(), value));
        return this;
    }

    public <E> ListBuilder<T> addAll(Iterable<E> values, Encoder<E> encoder) {
        values.forEach(value -> this.delegate.add(encoder.encode(value, this.ops(), this.ops().empty())));
        return this;
    }

    public ListBuilder<T> withErrorsFrom(DataResult<?> result) {
        this.delegate.withErrorsFrom(result);
        return this;
    }

    public ListBuilder<T> mapError(UnaryOperator<String> onError) {
        this.delegate.mapError(onError);
        return this;
    }

    public DataResult<T> build(DataResult<T> prefix) {
        return this.delegate.build(prefix);
    }
}
