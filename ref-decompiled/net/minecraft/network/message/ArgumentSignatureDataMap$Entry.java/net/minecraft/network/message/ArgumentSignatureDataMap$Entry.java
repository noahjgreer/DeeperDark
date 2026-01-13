/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.message;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.MessageSignatureData;

public record ArgumentSignatureDataMap.Entry(String name, MessageSignatureData signature) {
    public ArgumentSignatureDataMap.Entry(PacketByteBuf buf) {
        this(buf.readString(16), MessageSignatureData.fromBuf(buf));
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(this.name, 16);
        MessageSignatureData.write(buf, this.signature);
    }
}
