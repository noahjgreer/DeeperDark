/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.codec;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.IndexedIterable;

static class PacketCodecs.24
implements PacketCodec<RegistryByteBuf, RegistryEntry<T>> {
    private static final int field_61045 = 0;
    final /* synthetic */ RegistryKey field_60506;
    final /* synthetic */ PacketCodec field_61044;

    PacketCodecs.24(RegistryKey registryKey, PacketCodec packetCodec) {
        this.field_60506 = registryKey;
        this.field_61044 = packetCodec;
    }

    private IndexedIterable<RegistryEntry<T>> getIndexedEntries(RegistryByteBuf buf) {
        return buf.getRegistryManager().getOrThrow(this.field_60506).getIndexedEntries();
    }

    @Override
    public RegistryEntry<T> decode(RegistryByteBuf registryByteBuf) {
        int i = VarInts.read(registryByteBuf);
        if (i == 0) {
            return RegistryEntry.of(this.field_61044.decode(registryByteBuf));
        }
        return this.getIndexedEntries(registryByteBuf).getOrThrow(i - 1);
    }

    @Override
    public void encode(RegistryByteBuf registryByteBuf, RegistryEntry<T> registryEntry) {
        switch (registryEntry.getType()) {
            case REFERENCE: {
                int i = this.getIndexedEntries(registryByteBuf).getRawIdOrThrow(registryEntry);
                VarInts.write(registryByteBuf, i + 1);
                break;
            }
            case DIRECT: {
                VarInts.write(registryByteBuf, 0);
                this.field_61044.encode(registryByteBuf, registryEntry.value());
            }
        }
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((RegistryByteBuf)((Object)object), (RegistryEntry)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((RegistryByteBuf)((Object)object));
    }
}
