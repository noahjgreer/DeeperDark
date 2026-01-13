/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

static interface PlayerInteractEntityC2SPacket.InteractTypeHandler {
    public PlayerInteractEntityC2SPacket.InteractType getType();

    public void handle(PlayerInteractEntityC2SPacket.Handler var1);

    public void write(PacketByteBuf var1);
}
