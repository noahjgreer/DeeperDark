/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;

static class PlayerInteractEntityC2SPacket.InteractHandler
implements PlayerInteractEntityC2SPacket.InteractTypeHandler {
    private final Hand hand;

    PlayerInteractEntityC2SPacket.InteractHandler(Hand hand) {
        this.hand = hand;
    }

    private PlayerInteractEntityC2SPacket.InteractHandler(PacketByteBuf buf) {
        this.hand = buf.readEnumConstant(Hand.class);
    }

    @Override
    public PlayerInteractEntityC2SPacket.InteractType getType() {
        return PlayerInteractEntityC2SPacket.InteractType.INTERACT;
    }

    @Override
    public void handle(PlayerInteractEntityC2SPacket.Handler handler) {
        handler.interact(this.hand);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.hand);
    }
}
