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
import net.minecraft.util.math.Vec3d;

public static class PlayerMoveC2SPacket.Full
extends PlayerMoveC2SPacket {
    public static final PacketCodec<PacketByteBuf, PlayerMoveC2SPacket.Full> CODEC = Packet.createCodec(PlayerMoveC2SPacket.Full::write, PlayerMoveC2SPacket.Full::read);

    public PlayerMoveC2SPacket.Full(Vec3d pos, float yaw, float pitch, boolean onGround, boolean horizontalCollision) {
        super(pos.x, pos.y, pos.z, yaw, pitch, onGround, horizontalCollision, true, true);
    }

    public PlayerMoveC2SPacket.Full(double x, double y, double z, float yaw, float pitch, boolean onGround, boolean horizontalCollision) {
        super(x, y, z, yaw, pitch, onGround, horizontalCollision, true, true);
    }

    private static PlayerMoveC2SPacket.Full read(PacketByteBuf buf) {
        double d = buf.readDouble();
        double e = buf.readDouble();
        double f = buf.readDouble();
        float g = buf.readFloat();
        float h = buf.readFloat();
        short s = buf.readUnsignedByte();
        boolean bl = PlayerMoveC2SPacket.changePosition(s);
        boolean bl2 = PlayerMoveC2SPacket.changeLook(s);
        return new PlayerMoveC2SPacket.Full(d, e, f, g, h, bl, bl2);
    }

    private void write(PacketByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
        buf.writeByte(PlayerMoveC2SPacket.toFlag(this.onGround, this.horizontalCollision));
    }

    @Override
    public PacketType<PlayerMoveC2SPacket.Full> getPacketType() {
        return PlayPackets.MOVE_PLAYER_POS_ROT;
    }
}
