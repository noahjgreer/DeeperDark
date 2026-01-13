/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.mojang.serialization.Codec
 */
package net.minecraft.util.dynamic;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import net.minecraft.util.dynamic.Codecs;

public static class Codecs.IdMapper<I, V> {
    private final BiMap<I, V> values = HashBiMap.create();

    public Codec<V> getCodec(Codec<I> idCodec) {
        BiMap biMap = this.values.inverse();
        return Codecs.idChecked(idCodec, arg_0 -> this.values.get(arg_0), arg_0 -> biMap.get(arg_0));
    }

    public Codecs.IdMapper<I, V> put(I id, V value) {
        Objects.requireNonNull(value, () -> "Value for " + String.valueOf(id) + " is null");
        this.values.put(id, value);
        return this;
    }

    public Set<V> values() {
        return Collections.unmodifiableSet(this.values.values());
    }
}
