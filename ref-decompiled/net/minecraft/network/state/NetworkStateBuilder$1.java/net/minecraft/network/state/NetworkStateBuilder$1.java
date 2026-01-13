/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.state;

import java.util.List;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.state.NetworkState;
import net.minecraft.network.state.NetworkStateBuilder;

static class NetworkStateBuilder.1
implements NetworkState.Unbound {
    final /* synthetic */ NetworkPhase field_58187;
    final /* synthetic */ NetworkSide field_58188;
    final /* synthetic */ List field_52285;

    NetworkStateBuilder.1() {
        this.field_58187 = networkPhase;
        this.field_58188 = networkSide;
        this.field_52285 = list;
    }

    @Override
    public NetworkPhase phase() {
        return this.field_58187;
    }

    @Override
    public NetworkSide side() {
        return this.field_58188;
    }

    @Override
    public void forEachPacketType(NetworkState.Unbound.PacketTypeConsumer callback) {
        for (int i = 0; i < this.field_52285.size(); ++i) {
            NetworkStateBuilder.PacketType packetType = (NetworkStateBuilder.PacketType)this.field_52285.get(i);
            callback.accept(packetType.type, i);
        }
    }
}
