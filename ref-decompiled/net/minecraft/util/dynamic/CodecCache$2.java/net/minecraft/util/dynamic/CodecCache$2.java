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
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.dynamic.CodecCache;

class CodecCache.2
implements Codec<A> {
    final /* synthetic */ Codec field_51505;

    CodecCache.2(Codec codec) {
        this.field_51505 = codec;
    }

    public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
        return this.field_51505.decode(ops, input);
    }

    public <T> DataResult<T> encode(A value, DynamicOps<T> ops, T prefix) {
        return ((DataResult)CodecCache.this.cache.getUnchecked(new CodecCache.Key(this.field_51505, value, ops))).map(object -> {
            if (object instanceof NbtElement) {
                NbtElement nbtElement = (NbtElement)object;
                return nbtElement.copy();
            }
            return object;
        });
    }
}
