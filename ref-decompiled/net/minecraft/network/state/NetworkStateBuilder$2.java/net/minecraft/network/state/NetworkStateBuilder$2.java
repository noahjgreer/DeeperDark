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
import net.minecraft.network.state.NetworkState;
import net.minecraft.network.state.NetworkStateBuilder;
import net.minecraft.network.state.NetworkStateFactory;

class NetworkStateBuilder.2
implements NetworkStateFactory<T, B> {
    final /* synthetic */ List field_58189;
    final /* synthetic */ Object field_58190;
    final /* synthetic */ PacketBundleHandler field_58191;
    final /* synthetic */ NetworkState.Unbound field_58192;

    NetworkStateBuilder.2(List list, Object object, PacketBundleHandler packetBundleHandler, NetworkState.Unbound unbound) {
        this.field_58189 = list;
        this.field_58190 = object;
        this.field_58191 = packetBundleHandler;
        this.field_58192 = unbound;
    }

    @Override
    public NetworkState<T> bind(Function<ByteBuf, B> registryBinder) {
        return new NetworkStateBuilder.NetworkStateImpl(NetworkStateBuilder.this.phase, NetworkStateBuilder.this.side, NetworkStateBuilder.this.createCodec(registryBinder, this.field_58189, this.field_58190), this.field_58191);
    }

    @Override
    public NetworkState.Unbound buildUnbound() {
        return this.field_58192;
    }
}
