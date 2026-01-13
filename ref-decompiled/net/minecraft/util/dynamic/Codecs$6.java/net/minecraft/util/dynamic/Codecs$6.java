/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

static class Codecs.6
extends MapCodec<V> {
    final /* synthetic */ String field_47219;
    final /* synthetic */ String field_47220;
    final /* synthetic */ Codec field_47221;
    final /* synthetic */ Function field_47222;
    final /* synthetic */ Function field_47223;

    Codecs.6(String string, String string2, Codec codec, Function function, Function function2) {
        this.field_47219 = string;
        this.field_47220 = string2;
        this.field_47221 = codec;
        this.field_47222 = function;
        this.field_47223 = function2;
    }

    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return Stream.of(ops.createString(this.field_47219), ops.createString(this.field_47220));
    }

    public <T> DataResult<V> decode(DynamicOps<T> ops, MapLike<T> input) {
        Object object = input.get(this.field_47219);
        if (object == null) {
            return DataResult.error(() -> "Missing \"" + this.field_47219 + "\" in: " + String.valueOf(input));
        }
        return this.field_47221.decode(ops, object).flatMap(pair -> {
            Object object = Objects.requireNonNullElseGet(input.get(this.field_47220), () -> ((DynamicOps)ops).emptyMap());
            return ((Codec)this.field_47222.apply(pair.getFirst())).decode(ops, object).map(Pair::getFirst);
        });
    }

    public <T> RecordBuilder<T> encode(V input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        Object object = this.field_47223.apply(input);
        prefix.add(this.field_47219, this.field_47221.encodeStart(ops, object));
        DataResult<T> dataResult = this.encode((Codec)this.field_47222.apply(object), input, ops);
        if (dataResult.result().isEmpty() || !Objects.equals(dataResult.result().get(), ops.emptyMap())) {
            prefix.add(this.field_47220, dataResult);
        }
        return prefix;
    }

    private <T, V2 extends V> DataResult<T> encode(Codec<V2> codec, V value, DynamicOps<T> ops) {
        return codec.encodeStart(ops, value);
    }
}
