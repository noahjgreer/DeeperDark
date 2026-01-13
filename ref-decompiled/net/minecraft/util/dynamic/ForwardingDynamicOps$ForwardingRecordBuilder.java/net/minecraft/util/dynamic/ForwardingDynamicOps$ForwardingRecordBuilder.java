/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Encoder
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.RecordBuilder
 */
package net.minecraft.util.dynamic;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.RecordBuilder;
import java.util.function.UnaryOperator;

protected class ForwardingDynamicOps.ForwardingRecordBuilder
implements RecordBuilder<T> {
    private final RecordBuilder<T> delegate;

    protected ForwardingDynamicOps.ForwardingRecordBuilder(RecordBuilder<T> delegate) {
        this.delegate = delegate;
    }

    public DynamicOps<T> ops() {
        return ForwardingDynamicOps.this;
    }

    public RecordBuilder<T> add(T key, T value) {
        this.delegate.add(key, value);
        return this;
    }

    public RecordBuilder<T> add(T key, DataResult<T> value) {
        this.delegate.add(key, value);
        return this;
    }

    public RecordBuilder<T> add(DataResult<T> key, DataResult<T> value) {
        this.delegate.add(key, value);
        return this;
    }

    public RecordBuilder<T> add(String key, T value) {
        this.delegate.add(key, value);
        return this;
    }

    public RecordBuilder<T> add(String key, DataResult<T> value) {
        this.delegate.add(key, value);
        return this;
    }

    public <E> RecordBuilder<T> add(String key, E value, Encoder<E> encoder) {
        return this.delegate.add(key, encoder.encodeStart(this.ops(), value));
    }

    public RecordBuilder<T> withErrorsFrom(DataResult<?> result) {
        this.delegate.withErrorsFrom(result);
        return this;
    }

    public RecordBuilder<T> setLifecycle(Lifecycle lifecycle) {
        this.delegate.setLifecycle(lifecycle);
        return this;
    }

    public RecordBuilder<T> mapError(UnaryOperator<String> onError) {
        this.delegate.mapError(onError);
        return this;
    }

    public DataResult<T> build(T prefix) {
        return this.delegate.build(prefix);
    }

    public DataResult<T> build(DataResult<T> prefix) {
        return this.delegate.build(prefix);
    }
}
