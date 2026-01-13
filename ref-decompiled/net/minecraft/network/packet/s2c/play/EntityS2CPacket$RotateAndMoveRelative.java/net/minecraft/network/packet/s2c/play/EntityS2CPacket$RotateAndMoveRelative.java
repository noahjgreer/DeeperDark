/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;

public static class EntityS2CPacket.RotateAndMoveRelative
extends EntityS2CPacket {
    public static final PacketCodec<PacketByteBuf, EntityS2CPacket.RotateAndMoveRelative> CODEC = Packet.createCodec(EntityS2CPacket.RotateAndMoveRelative::write, EntityS2CPacket.RotateAndMoveRelative::read);

    public EntityS2CPacket.RotateAndMoveRelative(int entityId, short deltaX, short deltaY, short deltaZ, byte yaw, byte pitch, boolean onGround) {
        super(entityId, deltaX, deltaY, deltaZ, yaw, pitch, onGround, true, true);
    }

    private static EntityS2CPacket.RotateAndMoveRelative read(PacketByteBuf buf) {
        int i = buf.readVarInt();
        short s = buf.readShort();
        short t = buf.readShort();
        short u = buf.readShort();
        byte b = buf.readByte();
        byte c = buf.readByte();
        boolean bl = buf.readBoolean();
        return new EntityS2CPacket.RotateAndMoveRelative(i, s, t, u, b, c, bl);
    }

    private void write(PacketByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeShort(this.deltaX);
        buf.writeShort(this.deltaY);
        buf.writeShort(this.deltaZ);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        buf.writeBoolean(this.onGround);
    }

    @Override
    public PacketType<EntityS2CPacket.RotateAndMoveRelative> getPacketType() {
        return PlayPackets.MOVE_ENTITY_POS_ROT;
    }
}
