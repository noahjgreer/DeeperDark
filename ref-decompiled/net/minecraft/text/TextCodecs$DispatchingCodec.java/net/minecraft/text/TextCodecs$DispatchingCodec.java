/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 */
package net.minecraft.text;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.stream.Stream;

static class TextCodecs.DispatchingCodec<T>
extends MapCodec<T> {
    private final String dispatchingKey;
    private final MapCodec<T> withKeyCodec;
    private final MapCodec<T> withoutKeyCodec;

    public TextCodecs.DispatchingCodec(String dispatchingKey, MapCodec<T> withKeyCodec, MapCodec<T> withoutKeyCodec) {
        this.dispatchingKey = dispatchingKey;
        this.withKeyCodec = withKeyCodec;
        this.withoutKeyCodec = withoutKeyCodec;
    }

    public <O> DataResult<T> decode(DynamicOps<O> ops, MapLike<O> input) {
        if (input.get(this.dispatchingKey) != null) {
            return this.withKeyCodec.decode(ops, input);
        }
        return this.withoutKeyCodec.decode(ops, input);
    }

    public <O> RecordBuilder<O> encode(T input, DynamicOps<O> ops, RecordBuilder<O> prefix) {
        return this.withoutKeyCodec.encode(input, ops, prefix);
    }

    public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
        return Stream.concat(this.withKeyCodec.keys(ops), this.withoutKeyCodec.keys(ops)).distinct();
    }
}
