/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.state;

import net.minecraft.network.packet.PacketType;

@FunctionalInterface
public static interface NetworkState.Unbound.PacketTypeConsumer {
    public void accept(PacketType<?> var1, int var2);
}
