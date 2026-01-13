/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

static class PlayerInteractEntityC2SPacket.InteractAtHandler
implements PlayerInteractEntityC2SPacket.InteractTypeHandler {
    private final Hand hand;
    private final Vec3d pos;

    PlayerInteractEntityC2SPacket.InteractAtHandler(Hand hand, Vec3d pos) {
        this.hand = hand;
        this.pos = pos;
    }

    private PlayerInteractEntityC2SPacket.InteractAtHandler(PacketByteBuf buf) {
        this.pos = new Vec3d(buf.readFloat(), buf.readFloat(), buf.readFloat());
        this.hand = buf.readEnumConstant(Hand.class);
    }

    @Override
    public PlayerInteractEntityC2SPacket.InteractType getType() {
        return PlayerInteractEntityC2SPacket.InteractType.INTERACT_AT;
    }

    @Override
    public void handle(PlayerInteractEntityC2SPacket.Handler handler) {
        handler.interactAt(this.hand, this.pos);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeFloat((float)this.pos.x);
        buf.writeFloat((float)this.pos.y);
        buf.writeFloat((float)this.pos.z);
        buf.writeEnumConstant(this.hand);
    }
}
