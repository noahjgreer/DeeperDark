/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapDecoder
 *  com.mojang.serialization.MapEncoder
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 */
package net.minecraft.text;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

static class TextCodecs.FuzzyCodec<T>
extends MapCodec<T> {
    private final Collection<MapCodec<? extends T>> codecs;
    private final Function<T, ? extends MapEncoder<? extends T>> codecGetter;

    public TextCodecs.FuzzyCodec(Collection<MapCodec<? extends T>> codecs, Function<T, ? extends MapEncoder<? extends T>> codecGetter) {
        this.codecs = codecs;
        this.codecGetter = codecGetter;
    }

    public <S> DataResult<T> decode(DynamicOps<S> ops, MapLike<S> input) {
        for (MapDecoder mapDecoder : this.codecs) {
            DataResult dataResult = mapDecoder.decode(ops, input);
            if (!dataResult.result().isPresent()) continue;
            return dataResult;
        }
        return DataResult.error(() -> "No matching codec found");
    }

    public <S> RecordBuilder<S> encode(T input, DynamicOps<S> ops, RecordBuilder<S> prefix) {
        MapEncoder<? extends T> mapEncoder = this.codecGetter.apply(input);
        return mapEncoder.encode(input, ops, prefix);
    }

    public <S> Stream<S> keys(DynamicOps<S> ops) {
        return this.codecs.stream().flatMap(codec -> codec.keys(ops)).distinct();
    }

    public String toString() {
        return "FuzzyCodec[" + String.valueOf(this.codecs) + "]";
    }
}
