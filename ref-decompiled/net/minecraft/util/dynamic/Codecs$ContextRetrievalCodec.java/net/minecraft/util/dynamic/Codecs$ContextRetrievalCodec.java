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
package net.minecraft.util.dynamic;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.function.Function;
import java.util.stream.Stream;

static class Codecs.ContextRetrievalCodec
extends MapCodec<E> {
    final /* synthetic */ Function retriever;

    Codecs.ContextRetrievalCodec(Function retriever) {
        this.retriever = retriever;
    }

    public <T> RecordBuilder<T> encode(E input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        return prefix;
    }

    public <T> DataResult<E> decode(DynamicOps<T> ops, MapLike<T> input) {
        return (DataResult)this.retriever.apply(ops);
    }

    public String toString() {
        return "ContextRetrievalCodec[" + String.valueOf(this.retriever) + "]";
    }

    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return Stream.empty();
    }
}
