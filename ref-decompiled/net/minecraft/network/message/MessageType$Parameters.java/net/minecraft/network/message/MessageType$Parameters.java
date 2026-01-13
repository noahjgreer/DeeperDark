/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.message;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record MessageType.Parameters(RegistryEntry<MessageType> type, Text name, Optional<Text> targetName) {
    public static final PacketCodec<RegistryByteBuf, MessageType.Parameters> CODEC = PacketCodec.tuple(ENTRY_PACKET_CODEC, MessageType.Parameters::type, TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, MessageType.Parameters::name, TextCodecs.OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC, MessageType.Parameters::targetName, MessageType.Parameters::new);

    MessageType.Parameters(RegistryEntry<MessageType> type, Text name) {
        this(type, name, Optional.empty());
    }

    public Text applyChatDecoration(Text content) {
        return this.type.value().chat().apply(content, this);
    }

    public Text applyNarrationDecoration(Text content) {
        return this.type.value().narration().apply(content, this);
    }

    public MessageType.Parameters withTargetName(Text targetName) {
        return new MessageType.Parameters(this.type, this.name, Optional.of(targetName));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MessageType.Parameters.class, "chatType;name;targetName", "type", "name", "targetName"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MessageType.Parameters.class, "chatType;name;targetName", "type", "name", "targetName"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MessageType.Parameters.class, "chatType;name;targetName", "type", "name", "targetName"}, this, object);
    }
}
