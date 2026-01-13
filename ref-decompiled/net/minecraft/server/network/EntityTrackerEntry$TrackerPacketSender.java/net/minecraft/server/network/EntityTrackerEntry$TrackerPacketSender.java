/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.network;

import java.util.function.Predicate;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;

public static interface EntityTrackerEntry.TrackerPacketSender {
    public void sendToListeners(Packet<? super ClientPlayPacketListener> var1);

    public void sendToSelfAndListeners(Packet<? super ClientPlayPacketListener> var1);

    public void sendToListenersIf(Packet<? super ClientPlayPacketListener> var1, Predicate<ServerPlayerEntity> var2);
}
