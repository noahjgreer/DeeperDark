/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.handler;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.network.codec.PacketCodec;

static final class PacketCodecDispatcher.PacketType<B, V, T>
extends Record {
    final PacketCodec<? super B, ? extends V> codec;
    final T id;

    PacketCodecDispatcher.PacketType(PacketCodec<? super B, ? extends V> codec, T id) {
        this.codec = codec;
        this.id = id;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PacketCodecDispatcher.PacketType.class, "serializer;type", "codec", "id"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PacketCodecDispatcher.PacketType.class, "serializer;type", "codec", "id"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PacketCodecDispatcher.PacketType.class, "serializer;type", "codec", "id"}, this, object);
    }

    public PacketCodec<? super B, ? extends V> codec() {
        return this.codec;
    }

    public T id() {
        return this.id;
    }
}
