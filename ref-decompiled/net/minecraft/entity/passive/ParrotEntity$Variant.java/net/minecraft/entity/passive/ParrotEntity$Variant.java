/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public static final class ParrotEntity.Variant
extends Enum<ParrotEntity.Variant>
implements StringIdentifiable {
    public static final /* enum */ ParrotEntity.Variant RED_BLUE = new ParrotEntity.Variant(0, "red_blue");
    public static final /* enum */ ParrotEntity.Variant BLUE = new ParrotEntity.Variant(1, "blue");
    public static final /* enum */ ParrotEntity.Variant GREEN = new ParrotEntity.Variant(2, "green");
    public static final /* enum */ ParrotEntity.Variant YELLOW_BLUE = new ParrotEntity.Variant(3, "yellow_blue");
    public static final /* enum */ ParrotEntity.Variant GRAY = new ParrotEntity.Variant(4, "gray");
    public static final ParrotEntity.Variant DEFAULT;
    private static final IntFunction<ParrotEntity.Variant> INDEX_MAPPER;
    public static final Codec<ParrotEntity.Variant> CODEC;
    @Deprecated
    public static final Codec<ParrotEntity.Variant> INDEX_CODEC;
    public static final PacketCodec<ByteBuf, ParrotEntity.Variant> PACKET_CODEC;
    final int index;
    private final String id;
    private static final /* synthetic */ ParrotEntity.Variant[] field_41559;

    public static ParrotEntity.Variant[] values() {
        return (ParrotEntity.Variant[])field_41559.clone();
    }

    public static ParrotEntity.Variant valueOf(String string) {
        return Enum.valueOf(ParrotEntity.Variant.class, string);
    }

    private ParrotEntity.Variant(int index, String id) {
        this.index = index;
        this.id = id;
    }

    public int getIndex() {
        return this.index;
    }

    public static ParrotEntity.Variant byIndex(int index) {
        return INDEX_MAPPER.apply(index);
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ ParrotEntity.Variant[] method_47851() {
        return new ParrotEntity.Variant[]{RED_BLUE, BLUE, GREEN, YELLOW_BLUE, GRAY};
    }

    static {
        field_41559 = ParrotEntity.Variant.method_47851();
        DEFAULT = RED_BLUE;
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(ParrotEntity.Variant::getIndex, ParrotEntity.Variant.values(), ValueLists.OutOfBoundsHandling.CLAMP);
        CODEC = StringIdentifiable.createCodec(ParrotEntity.Variant::values);
        INDEX_CODEC = Codec.INT.xmap(INDEX_MAPPER::apply, ParrotEntity.Variant::getIndex);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, ParrotEntity.Variant::getIndex);
    }
}
