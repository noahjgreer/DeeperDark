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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public static final class MooshroomEntity.Variant
extends Enum<MooshroomEntity.Variant>
implements StringIdentifiable {
    public static final /* enum */ MooshroomEntity.Variant RED = new MooshroomEntity.Variant("red", 0, Blocks.RED_MUSHROOM.getDefaultState());
    public static final /* enum */ MooshroomEntity.Variant BROWN = new MooshroomEntity.Variant("brown", 1, Blocks.BROWN_MUSHROOM.getDefaultState());
    public static final MooshroomEntity.Variant DEFAULT;
    public static final Codec<MooshroomEntity.Variant> CODEC;
    private static final IntFunction<MooshroomEntity.Variant> INDEX_MAPPER;
    public static final PacketCodec<ByteBuf, MooshroomEntity.Variant> PACKET_CODEC;
    private final String name;
    final int index;
    private final BlockState mushroom;
    private static final /* synthetic */ MooshroomEntity.Variant[] field_18113;

    public static MooshroomEntity.Variant[] values() {
        return (MooshroomEntity.Variant[])field_18113.clone();
    }

    public static MooshroomEntity.Variant valueOf(String string) {
        return Enum.valueOf(MooshroomEntity.Variant.class, string);
    }

    private MooshroomEntity.Variant(String name, int index, BlockState mushroom) {
        this.name = name;
        this.index = index;
        this.mushroom = mushroom;
    }

    public BlockState getMushroomState() {
        return this.mushroom;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private int getIndex() {
        return this.index;
    }

    static MooshroomEntity.Variant fromIndex(int index) {
        return INDEX_MAPPER.apply(index);
    }

    private static /* synthetic */ MooshroomEntity.Variant[] method_36639() {
        return new MooshroomEntity.Variant[]{RED, BROWN};
    }

    static {
        field_18113 = MooshroomEntity.Variant.method_36639();
        DEFAULT = RED;
        CODEC = StringIdentifiable.createCodec(MooshroomEntity.Variant::values);
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(MooshroomEntity.Variant::getIndex, MooshroomEntity.Variant.values(), ValueLists.OutOfBoundsHandling.CLAMP);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, MooshroomEntity.Variant::getIndex);
    }
}
