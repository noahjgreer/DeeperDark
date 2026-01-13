/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$Error
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.codecs.BaseMapCodec
 */
package net.minecraft.util.dynamic;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.BaseMapCodec;
import java.util.Map;
import java.util.Optional;

public record Codecs.StrictUnboundedMapCodec<K, V>(Codec<K> keyCodec, Codec<V> elementCodec) implements Codec<Map<K, V>>,
BaseMapCodec<K, V>
{
    public <T> DataResult<Map<K, V>> decode(DynamicOps<T> ops, MapLike<T> input) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Pair pair : input.entries().toList()) {
            DataResult dataResult2;
            DataResult dataResult = this.keyCodec().parse(ops, pair.getFirst());
            DataResult dataResult3 = dataResult.apply2stable(Pair::of, dataResult2 = this.elementCodec().parse(ops, pair.getSecond()));
            Optional optional = dataResult3.error();
            if (optional.isPresent()) {
                String string = ((DataResult.Error)optional.get()).message();
                return DataResult.error(() -> {
                    if (dataResult.result().isPresent()) {
                        return "Map entry '" + String.valueOf(dataResult.result().get()) + "' : " + string;
                    }
                    return string;
                });
            }
            if (dataResult3.result().isPresent()) {
                Pair pair2 = (Pair)dataResult3.result().get();
                builder.put(pair2.getFirst(), pair2.getSecond());
                continue;
            }
            return DataResult.error(() -> "Empty or invalid map contents are not allowed");
        }
        ImmutableMap map = builder.build();
        return DataResult.success((Object)map);
    }

    public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(map -> this.decode(ops, (Object)map)).map(map -> Pair.of((Object)map, (Object)input));
    }

    public <T> DataResult<T> encode(Map<K, V> map, DynamicOps<T> dynamicOps, T object) {
        return this.encode(map, dynamicOps, dynamicOps.mapBuilder()).build(object);
    }

    @Override
    public String toString() {
        return "StrictUnboundedMapCodec[" + String.valueOf(this.keyCodec) + " -> " + String.valueOf(this.elementCodec) + "]";
    }

    public /* synthetic */ DataResult encode(Object input, DynamicOps ops, Object prefix) {
        return this.encode((Map)input, ops, prefix);
    }
}
