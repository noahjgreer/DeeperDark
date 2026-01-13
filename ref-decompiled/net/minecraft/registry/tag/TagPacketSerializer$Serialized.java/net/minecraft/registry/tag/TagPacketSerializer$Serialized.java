/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package net.minecraft.registry.tag;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Map;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.util.Identifier;

public static final class TagPacketSerializer.Serialized {
    public static final TagPacketSerializer.Serialized NONE = new TagPacketSerializer.Serialized(Map.of());
    final Map<Identifier, IntList> contents;

    TagPacketSerializer.Serialized(Map<Identifier, IntList> contents) {
        this.contents = contents;
    }

    public void writeBuf(PacketByteBuf buf) {
        buf.writeMap(this.contents, PacketByteBuf::writeIdentifier, PacketByteBuf::writeIntList);
    }

    public static TagPacketSerializer.Serialized fromBuf(PacketByteBuf buf) {
        return new TagPacketSerializer.Serialized(buf.readMap(PacketByteBuf::readIdentifier, PacketByteBuf::readIntList));
    }

    public boolean isEmpty() {
        return this.contents.isEmpty();
    }

    public int size() {
        return this.contents.size();
    }

    public <T> TagGroupLoader.RegistryTags<T> toRegistryTags(Registry<T> registry) {
        return TagPacketSerializer.toRegistryTags(registry, this);
    }
}
