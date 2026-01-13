/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.message;

import java.util.BitSet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.LastSeenMessageList;

public record LastSeenMessageList.Acknowledgment(int offset, BitSet acknowledged, byte checksum) {
    public static final byte NO_CHECKSUM = 0;

    public LastSeenMessageList.Acknowledgment(PacketByteBuf buf) {
        this(buf.readVarInt(), buf.readBitSet(20), buf.readByte());
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.offset);
        buf.writeBitSet(this.acknowledged, 20);
        buf.writeByte(this.checksum);
    }

    public boolean checksumEquals(LastSeenMessageList lastSeenMessages) {
        return this.checksum == 0 || this.checksum == lastSeenMessages.calculateChecksum();
    }
}
