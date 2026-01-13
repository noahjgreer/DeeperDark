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
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.handler.PacketBundleHandler;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.state.NetworkState;
import org.jspecify.annotations.Nullable;

record NetworkStateBuilder.NetworkStateImpl<L extends PacketListener>(NetworkPhase id, NetworkSide side, PacketCodec<ByteBuf, Packet<? super L>> codec, @Nullable PacketBundleHandler bundleHandler) implements NetworkState<L>
{
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{NetworkStateBuilder.NetworkStateImpl.class, "id;flow;codec;bundlerInfo", "id", "side", "codec", "bundleHandler"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{NetworkStateBuilder.NetworkStateImpl.class, "id;flow;codec;bundlerInfo", "id", "side", "codec", "bundleHandler"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{NetworkStateBuilder.NetworkStateImpl.class, "id;flow;codec;bundlerInfo", "id", "side", "codec", "bundleHandler"}, this, object);
    }
}
