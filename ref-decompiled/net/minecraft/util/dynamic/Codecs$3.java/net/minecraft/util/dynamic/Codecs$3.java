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
import java.util.stream.Stream;

static class Codecs.3
extends MapCodec<E> {
    final /* synthetic */ MapCodec field_46237;
    final /* synthetic */ MapCodec field_46238;

    Codecs.3(MapCodec mapCodec, MapCodec mapCodec2) {
        this.field_46237 = mapCodec;
        this.field_46238 = mapCodec2;
    }

    public <T> RecordBuilder<T> encode(E input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        if (ops.compressMaps()) {
            return this.field_46237.encode(input, ops, prefix);
        }
        return this.field_46238.encode(input, ops, prefix);
    }

    public <T> DataResult<E> decode(DynamicOps<T> ops, MapLike<T> input) {
        if (ops.compressMaps()) {
            return this.field_46237.decode(ops, input);
        }
        return this.field_46238.decode(ops, input);
    }

    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return this.field_46237.keys(ops);
    }

    public String toString() {
        return String.valueOf(this.field_46238) + " orCompressed " + String.valueOf(this.field_46237);
    }
}
