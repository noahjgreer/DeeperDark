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

public static final class RabbitEntity.Variant
extends Enum<RabbitEntity.Variant>
implements StringIdentifiable {
    public static final /* enum */ RabbitEntity.Variant BROWN = new RabbitEntity.Variant(0, "brown");
    public static final /* enum */ RabbitEntity.Variant WHITE = new RabbitEntity.Variant(1, "white");
    public static final /* enum */ RabbitEntity.Variant BLACK = new RabbitEntity.Variant(2, "black");
    public static final /* enum */ RabbitEntity.Variant WHITE_SPLOTCHED = new RabbitEntity.Variant(3, "white_splotched");
    public static final /* enum */ RabbitEntity.Variant GOLD = new RabbitEntity.Variant(4, "gold");
    public static final /* enum */ RabbitEntity.Variant SALT = new RabbitEntity.Variant(5, "salt");
    public static final /* enum */ RabbitEntity.Variant EVIL = new RabbitEntity.Variant(99, "evil");
    public static final RabbitEntity.Variant DEFAULT;
    private static final IntFunction<RabbitEntity.Variant> INDEX_MAPPER;
    public static final Codec<RabbitEntity.Variant> CODEC;
    @Deprecated
    public static final Codec<RabbitEntity.Variant> INDEX_CODEC;
    public static final PacketCodec<ByteBuf, RabbitEntity.Variant> PACKET_CODEC;
    final int index;
    private final String id;
    private static final /* synthetic */ RabbitEntity.Variant[] field_41572;

    public static RabbitEntity.Variant[] values() {
        return (RabbitEntity.Variant[])field_41572.clone();
    }

    public static RabbitEntity.Variant valueOf(String string) {
        return Enum.valueOf(RabbitEntity.Variant.class, string);
    }

    private RabbitEntity.Variant(int index, String id) {
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

    public static RabbitEntity.Variant byIndex(int index) {
        return INDEX_MAPPER.apply(index);
    }

    private static /* synthetic */ RabbitEntity.Variant[] method_47859() {
        return new RabbitEntity.Variant[]{BROWN, WHITE, BLACK, WHITE_SPLOTCHED, GOLD, SALT, EVIL};
    }

    static {
        field_41572 = RabbitEntity.Variant.method_47859();
        DEFAULT = BROWN;
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(RabbitEntity.Variant::getIndex, RabbitEntity.Variant.values(), DEFAULT);
        CODEC = StringIdentifiable.createCodec(RabbitEntity.Variant::values);
        INDEX_CODEC = Codec.INT.xmap(INDEX_MAPPER::apply, RabbitEntity.Variant::getIndex);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, RabbitEntity.Variant::getIndex);
    }
}
