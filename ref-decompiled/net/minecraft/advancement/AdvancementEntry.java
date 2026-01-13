/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancement.Advancement
 *  net.minecraft.advancement.AdvancementEntry
 *  net.minecraft.network.RegistryByteBuf
 *  net.minecraft.network.codec.PacketCodec
 *  net.minecraft.network.codec.PacketCodecs
 *  net.minecraft.util.Identifier
 */
package net.minecraft.advancement;

import java.util.List;
import net.minecraft.advancement.Advancement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

public record AdvancementEntry(Identifier id, Advancement value) {
    private final Identifier id;
    private final Advancement value;
    public static final PacketCodec<RegistryByteBuf, AdvancementEntry> PACKET_CODEC = PacketCodec.tuple((PacketCodec)Identifier.PACKET_CODEC, AdvancementEntry::id, (PacketCodec)Advancement.PACKET_CODEC, AdvancementEntry::value, AdvancementEntry::new);
    public static final PacketCodec<RegistryByteBuf, List<AdvancementEntry>> LIST_PACKET_CODEC = PACKET_CODEC.collect(PacketCodecs.toList());

    public AdvancementEntry(Identifier id, Advancement value) {
        this.id = id;
        this.value = value;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AdvancementEntry)) return false;
        AdvancementEntry advancementEntry = (AdvancementEntry)o;
        if (!this.id.equals((Object)advancementEntry.id)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return this.id.toString();
    }

    public Identifier id() {
        return this.id;
    }

    public Advancement value() {
        return this.value;
    }
}

