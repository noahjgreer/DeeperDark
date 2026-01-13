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

public static class EntityS2CPacket.MoveRelative
extends EntityS2CPacket {
    public static final PacketCodec<PacketByteBuf, EntityS2CPacket.MoveRelative> CODEC = Packet.createCodec(EntityS2CPacket.MoveRelative::write, EntityS2CPacket.MoveRelative::read);

    public EntityS2CPacket.MoveRelative(int entityId, short deltaX, short deltaY, short deltaZ, boolean onGround) {
        super(entityId, deltaX, deltaY, deltaZ, (byte)0, (byte)0, onGround, false, true);
    }

    private static EntityS2CPacket.MoveRelative read(PacketByteBuf buf) {
        int i = buf.readVarInt();
        short s = buf.readShort();
        short t = buf.readShort();
        short u = buf.readShort();
        boolean bl = buf.readBoolean();
        return new EntityS2CPacket.MoveRelative(i, s, t, u, bl);
    }

    private void write(PacketByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeShort(this.deltaX);
        buf.writeShort(this.deltaY);
        buf.writeShort(this.deltaZ);
        buf.writeBoolean(this.onGround);
    }

    @Override
    public PacketType<EntityS2CPacket.MoveRelative> getPacketType() {
        return PlayPackets.MOVE_ENTITY_POS;
    }
}
