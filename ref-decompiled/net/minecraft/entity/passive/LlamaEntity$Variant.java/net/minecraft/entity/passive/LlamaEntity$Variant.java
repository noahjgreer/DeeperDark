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

public static final class LlamaEntity.Variant
extends Enum<LlamaEntity.Variant>
implements StringIdentifiable {
    public static final /* enum */ LlamaEntity.Variant CREAMY = new LlamaEntity.Variant(0, "creamy");
    public static final /* enum */ LlamaEntity.Variant WHITE = new LlamaEntity.Variant(1, "white");
    public static final /* enum */ LlamaEntity.Variant BROWN = new LlamaEntity.Variant(2, "brown");
    public static final /* enum */ LlamaEntity.Variant GRAY = new LlamaEntity.Variant(3, "gray");
    public static final LlamaEntity.Variant DEFAULT;
    private static final IntFunction<LlamaEntity.Variant> INDEX_MAPPER;
    public static final Codec<LlamaEntity.Variant> CODEC;
    @Deprecated
    public static final Codec<LlamaEntity.Variant> INDEX_CODEC;
    public static final PacketCodec<ByteBuf, LlamaEntity.Variant> PACKET_CODEC;
    final int index;
    private final String id;
    private static final /* synthetic */ LlamaEntity.Variant[] field_41594;

    public static LlamaEntity.Variant[] values() {
        return (LlamaEntity.Variant[])field_41594.clone();
    }

    public static LlamaEntity.Variant valueOf(String string) {
        return Enum.valueOf(LlamaEntity.Variant.class, string);
    }

    private LlamaEntity.Variant(int index, String id) {
        this.index = index;
        this.id = id;
    }

    public int getIndex() {
        return this.index;
    }

    public static LlamaEntity.Variant byIndex(int index) {
        return INDEX_MAPPER.apply(index);
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ LlamaEntity.Variant[] method_47877() {
        return new LlamaEntity.Variant[]{CREAMY, WHITE, BROWN, GRAY};
    }

    static {
        field_41594 = LlamaEntity.Variant.method_47877();
        DEFAULT = CREAMY;
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(LlamaEntity.Variant::getIndex, LlamaEntity.Variant.values(), ValueLists.OutOfBoundsHandling.CLAMP);
        CODEC = StringIdentifiable.createCodec(LlamaEntity.Variant::values);
        INDEX_CODEC = Codec.INT.xmap(INDEX_MAPPER::apply, LlamaEntity.Variant::getIndex);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, LlamaEntity.Variant::getIndex);
    }
}
