/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerPacketListener;
import net.minecraft.network.packet.Packet;

@Environment(value=EnvType.CLIENT)
static final class ClientCommonNetworkHandler.QueuedPacket
extends Record {
    final Packet<? extends ServerPacketListener> packet;
    private final BooleanSupplier sendCondition;
    private final long expirationTime;

    ClientCommonNetworkHandler.QueuedPacket(Packet<? extends ServerPacketListener> packet, BooleanSupplier sendCondition, long expirationTime) {
        this.packet = packet;
        this.sendCondition = sendCondition;
        this.expirationTime = expirationTime;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ClientCommonNetworkHandler.QueuedPacket.class, "packet;sendCondition;expirationTime", "packet", "sendCondition", "expirationTime"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ClientCommonNetworkHandler.QueuedPacket.class, "packet;sendCondition;expirationTime", "packet", "sendCondition", "expirationTime"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ClientCommonNetworkHandler.QueuedPacket.class, "packet;sendCondition;expirationTime", "packet", "sendCondition", "expirationTime"}, this, object);
    }

    public Packet<? extends ServerPacketListener> packet() {
        return this.packet;
    }

    public BooleanSupplier sendCondition() {
        return this.sendCondition;
    }

    public long expirationTime() {
        return this.expirationTime;
    }
}
