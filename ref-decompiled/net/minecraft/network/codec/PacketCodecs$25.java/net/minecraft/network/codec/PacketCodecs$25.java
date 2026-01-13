/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.codec;

import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

static class PacketCodecs.25
implements PacketCodec<RegistryByteBuf, RegistryEntryList<T>> {
    private static final int field_61046 = -1;
    private final PacketCodec<RegistryByteBuf, RegistryEntry<T>> entryCodec;
    final /* synthetic */ RegistryKey field_54511;

    PacketCodecs.25(RegistryKey registryKey) {
        this.field_54511 = registryKey;
        this.entryCodec = PacketCodecs.registryEntry(this.field_54511);
    }

    @Override
    public RegistryEntryList<T> decode(RegistryByteBuf registryByteBuf) {
        int i = VarInts.read(registryByteBuf) - 1;
        if (i == -1) {
            RegistryWrapper.Impl registry = registryByteBuf.getRegistryManager().getOrThrow(this.field_54511);
            return (RegistryEntryList)registry.getOptional(TagKey.of(this.field_54511, (Identifier)Identifier.PACKET_CODEC.decode(registryByteBuf))).orElseThrow();
        }
        ArrayList<RegistryEntry> list = new ArrayList<RegistryEntry>(Math.min(i, 65536));
        for (int j = 0; j < i; ++j) {
            list.add((RegistryEntry)this.entryCodec.decode(registryByteBuf));
        }
        return RegistryEntryList.of(list);
    }

    @Override
    public void encode(RegistryByteBuf registryByteBuf, RegistryEntryList<T> registryEntryList) {
        Optional optional = registryEntryList.getTagKey();
        if (optional.isPresent()) {
            VarInts.write(registryByteBuf, 0);
            Identifier.PACKET_CODEC.encode(registryByteBuf, optional.get().id());
        } else {
            VarInts.write(registryByteBuf, registryEntryList.size() + 1);
            for (RegistryEntry registryEntry : registryEntryList) {
                this.entryCodec.encode(registryByteBuf, registryEntry);
            }
        }
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((RegistryByteBuf)((Object)object), (RegistryEntryList)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((RegistryByteBuf)((Object)object));
    }
}
