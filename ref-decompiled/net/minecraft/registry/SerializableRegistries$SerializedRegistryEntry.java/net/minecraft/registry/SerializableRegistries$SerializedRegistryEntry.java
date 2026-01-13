/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.registry;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

public record SerializableRegistries.SerializedRegistryEntry(Identifier id, Optional<NbtElement> data) {
    public static final PacketCodec<ByteBuf, SerializableRegistries.SerializedRegistryEntry> PACKET_CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, SerializableRegistries.SerializedRegistryEntry::id, PacketCodecs.NBT_ELEMENT.collect(PacketCodecs::optional), SerializableRegistries.SerializedRegistryEntry::data, SerializableRegistries.SerializedRegistryEntry::new);
}
