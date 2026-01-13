/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public static class PlayerMoveC2SPacket.LookAndOnGround
extends PlayerMoveC2SPacket {
    public static final PacketCodec<PacketByteBuf, PlayerMoveC2SPacket.LookAndOnGround> CODEC = Packet.createCodec(PlayerMoveC2SPacket.LookAndOnGround::write, PlayerMoveC2SPacket.LookAndOnGround::read);

    public PlayerMoveC2SPacket.LookAndOnGround(float yaw, float pitch, boolean onGround, boolean horizontalCollision) {
        super(0.0, 0.0, 0.0, yaw, pitch, onGround, horizontalCollision, false, true);
    }

    private static PlayerMoveC2SPacket.LookAndOnGround read(PacketByteBuf buf) {
        float f = buf.readFloat();
        float g = buf.readFloat();
        short s = buf.readUnsignedByte();
        boolean bl = PlayerMoveC2SPacket.changePosition(s);
        boolean bl2 = PlayerMoveC2SPacket.changeLook(s);
        return new PlayerMoveC2SPacket.LookAndOnGround(f, g, bl, bl2);
    }

    private void write(PacketByteBuf buf) {
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
        buf.writeByte(PlayerMoveC2SPacket.toFlag(this.onGround, this.horizontalCollision));
    }

    @Override
    public PacketType<PlayerMoveC2SPacket.LookAndOnGround> getPacketType() {
        return PlayPackets.MOVE_PLAYER_ROT;
    }
}
