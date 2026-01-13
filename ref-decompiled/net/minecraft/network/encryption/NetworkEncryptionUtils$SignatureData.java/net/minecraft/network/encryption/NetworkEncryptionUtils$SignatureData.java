/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Longs
 *  it.unimi.dsi.fastutil.bytes.ByteArrays
 */
package net.minecraft.network.encryption;

import com.google.common.primitives.Longs;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import net.minecraft.network.PacketByteBuf;

public record NetworkEncryptionUtils.SignatureData(long salt, byte[] signature) {
    public static final NetworkEncryptionUtils.SignatureData NONE = new NetworkEncryptionUtils.SignatureData(0L, ByteArrays.EMPTY_ARRAY);

    public NetworkEncryptionUtils.SignatureData(PacketByteBuf buf) {
        this(buf.readLong(), buf.readByteArray());
    }

    public boolean isSignaturePresent() {
        return this.signature.length > 0;
    }

    public static void write(PacketByteBuf buf, NetworkEncryptionUtils.SignatureData signatureData) {
        buf.writeLong(signatureData.salt);
        buf.writeByteArray(signatureData.signature);
    }

    public byte[] getSalt() {
        return Longs.toByteArray((long)this.salt);
    }
}
