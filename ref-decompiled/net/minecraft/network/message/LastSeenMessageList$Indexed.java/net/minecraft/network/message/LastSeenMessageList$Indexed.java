/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.message;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageSignatureStorage;

public record LastSeenMessageList.Indexed(List<MessageSignatureData.Indexed> buf) {
    public static final LastSeenMessageList.Indexed EMPTY = new LastSeenMessageList.Indexed(List.of());

    public LastSeenMessageList.Indexed(PacketByteBuf buf) {
        this(buf.readCollection(PacketByteBuf.getMaxValidator(ArrayList::new, 20), MessageSignatureData.Indexed::fromBuf));
    }

    public void write(PacketByteBuf buf) {
        buf.writeCollection(this.buf, MessageSignatureData.Indexed::write);
    }

    public Optional<LastSeenMessageList> unpack(MessageSignatureStorage storage) {
        ArrayList<MessageSignatureData> list = new ArrayList<MessageSignatureData>(this.buf.size());
        for (MessageSignatureData.Indexed indexed : this.buf) {
            Optional<MessageSignatureData> optional = indexed.getSignature(storage);
            if (optional.isEmpty()) {
                return Optional.empty();
            }
            list.add(optional.get());
        }
        return Optional.of(new LastSeenMessageList(list));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{LastSeenMessageList.Indexed.class, "entries", "buf"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LastSeenMessageList.Indexed.class, "entries", "buf"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LastSeenMessageList.Indexed.class, "entries", "buf"}, this, object);
    }
}
