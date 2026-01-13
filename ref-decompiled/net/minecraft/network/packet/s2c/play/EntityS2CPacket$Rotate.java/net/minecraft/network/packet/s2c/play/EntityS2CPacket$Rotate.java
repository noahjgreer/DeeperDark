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

public static class EntityS2CPacket.Rotate
extends EntityS2CPacket {
    public static final PacketCodec<PacketByteBuf, EntityS2CPacket.Rotate> CODEC = Packet.createCodec(EntityS2CPacket.Rotate::write, EntityS2CPacket.Rotate::read);

    public EntityS2CPacket.Rotate(int entityId, byte yaw, byte pitch, boolean onGround) {
        super(entityId, (short)0, (short)0, (short)0, yaw, pitch, onGround, true, false);
    }

    private static EntityS2CPacket.Rotate read(PacketByteBuf buf) {
        int i = buf.readVarInt();
        byte b = buf.readByte();
        byte c = buf.readByte();
        boolean bl = buf.readBoolean();
        return new EntityS2CPacket.Rotate(i, b, c, bl);
    }

    private void write(PacketByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        buf.writeBoolean(this.onGround);
    }

    @Override
    public PacketType<EntityS2CPacket.Rotate> getPacketType() {
        return PlayPackets.MOVE_ENTITY_ROT;
    }
}
