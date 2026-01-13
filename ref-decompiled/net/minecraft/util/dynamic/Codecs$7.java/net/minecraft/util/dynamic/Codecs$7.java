/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapLike
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import java.util.Optional;

static class Codecs.7
implements Codec<Optional<A>> {
    final /* synthetic */ Codec field_50110;

    Codecs.7(Codec codec) {
        this.field_50110 = codec;
    }

    public <T> DataResult<Pair<Optional<A>, T>> decode(DynamicOps<T> ops, T input) {
        if (Codecs.7.isEmpty(ops, input)) {
            return DataResult.success((Object)Pair.of(Optional.empty(), input));
        }
        return this.field_50110.decode(ops, input).map(pair -> pair.mapFirst(Optional::of));
    }

    private static <T> boolean isEmpty(DynamicOps<T> ops, T input) {
        Optional optional = ops.getMap(input).result();
        return optional.isPresent() && ((MapLike)optional.get()).entries().findAny().isEmpty();
    }

    public <T> DataResult<T> encode(Optional<A> optional, DynamicOps<T> dynamicOps, T object) {
        if (optional.isEmpty()) {
            return DataResult.success((Object)dynamicOps.emptyMap());
        }
        return this.field_50110.encode(optional.get(), dynamicOps, object);
    }

    public /* synthetic */ DataResult encode(Object input, DynamicOps ops, Object prefix) {
        return this.encode((Optional)input, ops, prefix);
    }
}
