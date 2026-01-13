/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.state;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.Function;
import net.minecraft.network.handler.PacketBundleHandler;
import net.minecraft.network.state.ContextAwareNetworkStateFactory;
import net.minecraft.network.state.NetworkState;
import net.minecraft.network.state.NetworkStateBuilder;

class NetworkStateBuilder.3
implements ContextAwareNetworkStateFactory<T, B, C> {
    final /* synthetic */ List field_58194;
    final /* synthetic */ PacketBundleHandler field_58195;
    final /* synthetic */ NetworkState.Unbound field_58196;

    NetworkStateBuilder.3(List list, PacketBundleHandler packetBundleHandler, NetworkState.Unbound unbound) {
        this.field_58194 = list;
        this.field_58195 = packetBundleHandler;
        this.field_58196 = unbound;
    }

    @Override
    public NetworkState<T> bind(Function<ByteBuf, B> registryBinder, C context) {
        return new NetworkStateBuilder.NetworkStateImpl(NetworkStateBuilder.this.phase, NetworkStateBuilder.this.side, NetworkStateBuilder.this.createCodec(registryBinder, this.field_58194, context), this.field_58195);
    }

    @Override
    public NetworkState.Unbound buildUnbound() {
        return this.field_58196;
    }
}
