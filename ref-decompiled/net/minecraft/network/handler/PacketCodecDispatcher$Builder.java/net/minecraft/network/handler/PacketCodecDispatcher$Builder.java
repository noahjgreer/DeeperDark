/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.handler.PacketCodecDispatcher;

public static class PacketCodecDispatcher.Builder<B extends ByteBuf, V, T> {
    private final List<PacketCodecDispatcher.PacketType<B, V, T>> packetTypes = new ArrayList<PacketCodecDispatcher.PacketType<B, V, T>>();
    private final Function<V, ? extends T> packetIdGetter;

    PacketCodecDispatcher.Builder(Function<V, ? extends T> packetIdGetter) {
        this.packetIdGetter = packetIdGetter;
    }

    public PacketCodecDispatcher.Builder<B, V, T> add(T id, PacketCodec<? super B, ? extends V> codec) {
        this.packetTypes.add(new PacketCodecDispatcher.PacketType<B, V, T>(codec, id));
        return this;
    }

    public PacketCodecDispatcher<B, V, T> build() {
        Object2IntOpenHashMap object2IntOpenHashMap = new Object2IntOpenHashMap();
        object2IntOpenHashMap.defaultReturnValue(-2);
        for (PacketCodecDispatcher.PacketType<B, V, T> packetType : this.packetTypes) {
            int i = object2IntOpenHashMap.size();
            int j = object2IntOpenHashMap.putIfAbsent(packetType.id, i);
            if (j == -2) continue;
            throw new IllegalStateException("Duplicate registration for type " + String.valueOf(packetType.id));
        }
        return new PacketCodecDispatcher<B, V, T>(this.packetIdGetter, List.copyOf(this.packetTypes), object2IntOpenHashMap);
    }
}
