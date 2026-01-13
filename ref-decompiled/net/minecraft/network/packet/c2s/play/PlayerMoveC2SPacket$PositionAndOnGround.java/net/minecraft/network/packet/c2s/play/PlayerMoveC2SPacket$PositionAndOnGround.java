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

public static class PlayerMoveC2SPacket.PositionAndOnGround
extends PlayerMoveC2SPacket {
    public static final PacketCodec<PacketByteBuf, PlayerMoveC2SPacket.PositionAndOnGround> CODEC = Packet.createCodec(PlayerMoveC2SPacket.PositionAndOnGround::write, PlayerMoveC2SPacket.PositionAndOnGround::read);

    public PlayerMoveC2SPacket.PositionAndOnGround(Vec3d pos, boolean onGround, boolean horizontalCollision) {
        super(pos.x, pos.y, pos.z, 0.0f, 0.0f, onGround, horizontalCollision, true, false);
    }

    public PlayerMoveC2SPacket.PositionAndOnGround(double x, double y, double z, boolean onGround, boolean horizontalCollision) {
        super(x, y, z, 0.0f, 0.0f, onGround, horizontalCollision, true, false);
    }

    private static PlayerMoveC2SPacket.PositionAndOnGround read(PacketByteBuf buf) {
        double d = buf.readDouble();
        double e = buf.readDouble();
        double f = buf.readDouble();
        short s = buf.readUnsignedByte();
        boolean bl = PlayerMoveC2SPacket.changePosition(s);
        boolean bl2 = PlayerMoveC2SPacket.changeLook(s);
        return new PlayerMoveC2SPacket.PositionAndOnGround(d, e, f, bl, bl2);
    }

    private void write(PacketByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(PlayerMoveC2SPacket.toFlag(this.onGround, this.horizontalCollision));
    }

    @Override
    public PacketType<PlayerMoveC2SPacket.PositionAndOnGround> getPacketType() {
        return PlayPackets.MOVE_PLAYER_POS;
    }
}
