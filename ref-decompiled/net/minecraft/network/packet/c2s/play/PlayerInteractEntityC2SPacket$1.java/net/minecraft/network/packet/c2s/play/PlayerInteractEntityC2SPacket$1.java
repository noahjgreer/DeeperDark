/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

class PlayerInteractEntityC2SPacket.1
implements PlayerInteractEntityC2SPacket.InteractTypeHandler {
    PlayerInteractEntityC2SPacket.1() {
    }

    @Override
    public PlayerInteractEntityC2SPacket.InteractType getType() {
        return PlayerInteractEntityC2SPacket.InteractType.ATTACK;
    }

    @Override
    public void handle(PlayerInteractEntityC2SPacket.Handler handler) {
        handler.attack();
    }

    @Override
    public void write(PacketByteBuf buf) {
    }
}
