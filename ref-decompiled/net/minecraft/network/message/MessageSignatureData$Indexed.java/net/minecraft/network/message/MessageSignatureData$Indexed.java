/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.message;

import java.util.Optional;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageSignatureStorage;
import org.jspecify.annotations.Nullable;

public record MessageSignatureData.Indexed(int id, @Nullable MessageSignatureData fullSignature) {
    public static final int MISSING_ID = -1;

    public MessageSignatureData.Indexed(MessageSignatureData signature) {
        this(-1, signature);
    }

    public MessageSignatureData.Indexed(int id) {
        this(id, null);
    }

    public static MessageSignatureData.Indexed fromBuf(PacketByteBuf buf) {
        int i = buf.readVarInt() - 1;
        if (i == -1) {
            return new MessageSignatureData.Indexed(MessageSignatureData.fromBuf(buf));
        }
        return new MessageSignatureData.Indexed(i);
    }

    public static void write(PacketByteBuf buf, MessageSignatureData.Indexed indexed) {
        buf.writeVarInt(indexed.id() + 1);
        if (indexed.fullSignature() != null) {
            MessageSignatureData.write(buf, indexed.fullSignature());
        }
    }

    public Optional<MessageSignatureData> getSignature(MessageSignatureStorage storage) {
        if (this.fullSignature != null) {
            return Optional.of(this.fullSignature);
        }
        return Optional.ofNullable(storage.get(this.id));
    }
}
