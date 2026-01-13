/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.state;

import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.handler.SideValidatingDispatchingCodecBuilder;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketCodecModifier;
import net.minecraft.network.packet.PacketType;
import org.jspecify.annotations.Nullable;

static final class NetworkStateBuilder.PacketType<T extends PacketListener, P extends Packet<? super T>, B extends ByteBuf, C>
extends Record {
    final PacketType<P> type;
    private final PacketCodec<? super B, P> codec;
    private final @Nullable PacketCodecModifier<B, P, C> modifier;

    NetworkStateBuilder.PacketType(PacketType<P> type, PacketCodec<? super B, P> codec, @Nullable PacketCodecModifier<B, P, C> modifier) {
        this.type = type;
        this.codec = codec;
        this.modifier = modifier;
    }

    public void add(SideValidatingDispatchingCodecBuilder<ByteBuf, T> builder, Function<ByteBuf, B> bufUpgrader, C context) {
        PacketCodec<Object, P> packetCodec = this.modifier != null ? this.modifier.apply(this.codec, context) : this.codec;
        PacketCodec<ByteBuf, P> packetCodec2 = packetCodec.mapBuf(bufUpgrader);
        builder.add(this.type, packetCodec2);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{NetworkStateBuilder.PacketType.class, "type;serializer;modifier", "type", "codec", "modifier"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{NetworkStateBuilder.PacketType.class, "type;serializer;modifier", "type", "codec", "modifier"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{NetworkStateBuilder.PacketType.class, "type;serializer;modifier", "type", "codec", "modifier"}, this, object);
    }

    public PacketType<P> type() {
        return this.type;
    }

    public PacketCodec<? super B, P> codec() {
        return this.codec;
    }

    public @Nullable PacketCodecModifier<B, P, C> modifier() {
        return this.modifier;
    }
}
