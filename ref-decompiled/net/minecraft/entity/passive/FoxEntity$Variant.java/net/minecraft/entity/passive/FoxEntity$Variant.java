/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.entity.passive;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.world.biome.Biome;

public static final class FoxEntity.Variant
extends Enum<FoxEntity.Variant>
implements StringIdentifiable {
    public static final /* enum */ FoxEntity.Variant RED = new FoxEntity.Variant(0, "red");
    public static final /* enum */ FoxEntity.Variant SNOW = new FoxEntity.Variant(1, "snow");
    public static final FoxEntity.Variant DEFAULT;
    public static final StringIdentifiable.EnumCodec<FoxEntity.Variant> CODEC;
    private static final IntFunction<FoxEntity.Variant> INDEX_MAPPER;
    public static final PacketCodec<ByteBuf, FoxEntity.Variant> PACKET_CODEC;
    private final int index;
    private final String id;
    private static final /* synthetic */ FoxEntity.Variant[] field_18003;

    public static FoxEntity.Variant[] values() {
        return (FoxEntity.Variant[])field_18003.clone();
    }

    public static FoxEntity.Variant valueOf(String string) {
        return Enum.valueOf(FoxEntity.Variant.class, string);
    }

    private FoxEntity.Variant(int index, String id) {
        this.index = index;
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public int getIndex() {
        return this.index;
    }

    public static FoxEntity.Variant byIndex(int index) {
        return INDEX_MAPPER.apply(index);
    }

    public static FoxEntity.Variant fromBiome(RegistryEntry<Biome> biome) {
        return biome.isIn(BiomeTags.SPAWNS_SNOW_FOXES) ? SNOW : RED;
    }

    private static /* synthetic */ FoxEntity.Variant[] method_36637() {
        return new FoxEntity.Variant[]{RED, SNOW};
    }

    static {
        field_18003 = FoxEntity.Variant.method_36637();
        DEFAULT = RED;
        CODEC = StringIdentifiable.createCodec(FoxEntity.Variant::values);
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(FoxEntity.Variant::getIndex, FoxEntity.Variant.values(), ValueLists.OutOfBoundsHandling.ZERO);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, FoxEntity.Variant::getIndex);
    }
}
