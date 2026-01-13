/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.message;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Instant;
import java.util.Optional;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageSignatureStorage;

public record MessageBody.Serialized(String content, Instant timestamp, long salt, LastSeenMessageList.Indexed lastSeen) {
    public MessageBody.Serialized(PacketByteBuf buf) {
        this(buf.readString(256), buf.readInstant(), buf.readLong(), new LastSeenMessageList.Indexed(buf));
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(this.content, 256);
        buf.writeInstant(this.timestamp);
        buf.writeLong(this.salt);
        this.lastSeen.write(buf);
    }

    public Optional<MessageBody> toBody(MessageSignatureStorage storage) {
        return this.lastSeen.unpack(storage).map(lastSeenMessages -> new MessageBody(this.content, this.timestamp, this.salt, (LastSeenMessageList)lastSeenMessages));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MessageBody.Serialized.class, "content;timeStamp;salt;lastSeen", "content", "timestamp", "salt", "lastSeen"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MessageBody.Serialized.class, "content;timeStamp;salt;lastSeen", "content", "timestamp", "salt", "lastSeen"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MessageBody.Serialized.class, "content;timeStamp;salt;lastSeen", "content", "timestamp", "salt", "lastSeen"}, this, object);
    }
}
