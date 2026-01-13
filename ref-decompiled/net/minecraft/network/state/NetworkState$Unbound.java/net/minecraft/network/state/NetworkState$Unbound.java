/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.state;

import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.annotation.Debug;

public static interface NetworkState.Unbound {
    public NetworkPhase phase();

    public NetworkSide side();

    @Debug
    public void forEachPacketType(PacketTypeConsumer var1);

    @FunctionalInterface
    public static interface PacketTypeConsumer {
        public void accept(PacketType<?> var1, int var2);
    }
}
