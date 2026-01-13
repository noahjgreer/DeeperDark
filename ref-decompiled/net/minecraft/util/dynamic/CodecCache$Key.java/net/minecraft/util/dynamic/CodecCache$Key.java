/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.dynamic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

record CodecCache.Key<A, T>(Codec<A> codec, A value, DynamicOps<T> ops) {
    public DataResult<T> encode() {
        return this.codec.encodeStart(this.ops, this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CodecCache.Key) {
            CodecCache.Key key = (CodecCache.Key)o;
            return this.codec == key.codec && this.value.equals(key.value) && this.ops.equals(key.ops);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int i = System.identityHashCode(this.codec);
        i = 31 * i + this.value.hashCode();
        i = 31 * i + this.ops.hashCode();
        return i;
    }
}
