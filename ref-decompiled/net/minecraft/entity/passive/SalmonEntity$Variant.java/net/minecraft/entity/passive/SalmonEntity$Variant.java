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
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public static final class SalmonEntity.Variant
extends Enum<SalmonEntity.Variant>
implements StringIdentifiable {
    public static final /* enum */ SalmonEntity.Variant SMALL = new SalmonEntity.Variant("small", 0, 0.5f);
    public static final /* enum */ SalmonEntity.Variant MEDIUM = new SalmonEntity.Variant("medium", 1, 1.0f);
    public static final /* enum */ SalmonEntity.Variant LARGE = new SalmonEntity.Variant("large", 2, 1.5f);
    public static final SalmonEntity.Variant DEFAULT;
    public static final StringIdentifiable.EnumCodec<SalmonEntity.Variant> CODEC;
    static final IntFunction<SalmonEntity.Variant> FROM_INDEX;
    public static final PacketCodec<ByteBuf, SalmonEntity.Variant> PACKET_CODEC;
    private final String id;
    final int index;
    final float scale;
    private static final /* synthetic */ SalmonEntity.Variant[] field_52475;

    public static SalmonEntity.Variant[] values() {
        return (SalmonEntity.Variant[])field_52475.clone();
    }

    public static SalmonEntity.Variant valueOf(String string) {
        return Enum.valueOf(SalmonEntity.Variant.class, string);
    }

    private SalmonEntity.Variant(String id, int index, float scale) {
        this.id = id;
        this.index = index;
        this.scale = scale;
    }

    @Override
    public String asString() {
        return this.id;
    }

    int getIndex() {
        return this.index;
    }

    private static /* synthetic */ SalmonEntity.Variant[] method_61473() {
        return new SalmonEntity.Variant[]{SMALL, MEDIUM, LARGE};
    }

    static {
        field_52475 = SalmonEntity.Variant.method_61473();
        DEFAULT = MEDIUM;
        CODEC = StringIdentifiable.createCodec(SalmonEntity.Variant::values);
        FROM_INDEX = ValueLists.createIndexToValueFunction(SalmonEntity.Variant::getIndex, SalmonEntity.Variant.values(), ValueLists.OutOfBoundsHandling.CLAMP);
        PACKET_CODEC = PacketCodecs.indexed(FROM_INDEX, SalmonEntity.Variant::getIndex);
    }
}
