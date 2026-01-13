/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

static class Codecs.2
implements Codec<E> {
    final /* synthetic */ Codec field_35662;
    final /* synthetic */ Codec field_35663;

    Codecs.2(Codec codec, Codec codec2) {
        this.field_35662 = codec;
        this.field_35663 = codec2;
    }

    public <T> DataResult<T> encode(E input, DynamicOps<T> ops, T prefix) {
        if (ops.compressMaps()) {
            return this.field_35662.encode(input, ops, prefix);
        }
        return this.field_35663.encode(input, ops, prefix);
    }

    public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input) {
        if (ops.compressMaps()) {
            return this.field_35662.decode(ops, input);
        }
        return this.field_35663.decode(ops, input);
    }

    public String toString() {
        return String.valueOf(this.field_35663) + " orCompressed " + String.valueOf(this.field_35662);
    }
}
