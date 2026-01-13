/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;

static class Codecs.5
implements Decoder<A> {
    final /* synthetic */ Codec field_38082;

    Codecs.5(Codec codec) {
        this.field_38082 = codec;
    }

    public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
        try {
            return this.field_38082.decode(ops, input);
        }
        catch (Exception exception) {
            return DataResult.error(() -> "Caught exception decoding " + String.valueOf(input) + ": " + exception.getMessage());
        }
    }
}
