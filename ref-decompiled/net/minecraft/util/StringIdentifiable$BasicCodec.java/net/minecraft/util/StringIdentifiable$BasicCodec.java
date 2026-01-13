/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import org.jspecify.annotations.Nullable;

public static class StringIdentifiable.BasicCodec<S extends StringIdentifiable>
implements Codec<S> {
    private final Codec<S> codec;

    public StringIdentifiable.BasicCodec(S[] values, Function<String, @Nullable S> idToIdentifiable, ToIntFunction<S> identifiableToOrdinal) {
        this.codec = Codecs.orCompressed(Codec.stringResolver(StringIdentifiable::asString, idToIdentifiable), Codecs.rawIdChecked(identifiableToOrdinal, ordinal -> ordinal >= 0 && ordinal < values.length ? values[ordinal] : null, -1));
    }

    public <T> DataResult<Pair<S, T>> decode(DynamicOps<T> ops, T input) {
        return this.codec.decode(ops, input);
    }

    public <T> DataResult<T> encode(S stringIdentifiable, DynamicOps<T> dynamicOps, T object) {
        return this.codec.encode(stringIdentifiable, dynamicOps, object);
    }

    public /* synthetic */ DataResult encode(Object input, DynamicOps ops, Object prefix) {
        return this.encode((S)((StringIdentifiable)input), (DynamicOps<T>)ops, (T)prefix);
    }
}
