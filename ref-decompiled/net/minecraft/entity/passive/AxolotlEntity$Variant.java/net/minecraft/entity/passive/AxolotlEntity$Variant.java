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
import java.util.Arrays;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.random.Random;

public static final class AxolotlEntity.Variant
extends Enum<AxolotlEntity.Variant>
implements StringIdentifiable {
    public static final /* enum */ AxolotlEntity.Variant LUCY = new AxolotlEntity.Variant(0, "lucy", true);
    public static final /* enum */ AxolotlEntity.Variant WILD = new AxolotlEntity.Variant(1, "wild", true);
    public static final /* enum */ AxolotlEntity.Variant GOLD = new AxolotlEntity.Variant(2, "gold", true);
    public static final /* enum */ AxolotlEntity.Variant CYAN = new AxolotlEntity.Variant(3, "cyan", true);
    public static final /* enum */ AxolotlEntity.Variant BLUE = new AxolotlEntity.Variant(4, "blue", false);
    public static final AxolotlEntity.Variant DEFAULT;
    private static final IntFunction<AxolotlEntity.Variant> INDEX_MAPPER;
    public static final PacketCodec<ByteBuf, AxolotlEntity.Variant> PACKET_CODEC;
    public static final Codec<AxolotlEntity.Variant> CODEC;
    @Deprecated
    public static final Codec<AxolotlEntity.Variant> INDEX_CODEC;
    private final int index;
    private final String id;
    private final boolean natural;
    private static final /* synthetic */ AxolotlEntity.Variant[] field_28350;

    public static AxolotlEntity.Variant[] values() {
        return (AxolotlEntity.Variant[])field_28350.clone();
    }

    public static AxolotlEntity.Variant valueOf(String string) {
        return Enum.valueOf(AxolotlEntity.Variant.class, string);
    }

    private AxolotlEntity.Variant(int index, String id, boolean natural) {
        this.index = index;
        this.id = id;
        this.natural = natural;
    }

    public int getIndex() {
        return this.index;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public static AxolotlEntity.Variant byIndex(int index) {
        return INDEX_MAPPER.apply(index);
    }

    public static AxolotlEntity.Variant getRandomNatural(Random random) {
        return AxolotlEntity.Variant.getRandom(random, true);
    }

    public static AxolotlEntity.Variant getRandomUnnatural(Random random) {
        return AxolotlEntity.Variant.getRandom(random, false);
    }

    private static AxolotlEntity.Variant getRandom(Random random, boolean natural) {
        AxolotlEntity.Variant[] variants = (AxolotlEntity.Variant[])Arrays.stream(AxolotlEntity.Variant.values()).filter(variant -> variant.natural == natural).toArray(AxolotlEntity.Variant[]::new);
        return Util.getRandom(variants, random);
    }

    private static /* synthetic */ AxolotlEntity.Variant[] method_36644() {
        return new AxolotlEntity.Variant[]{LUCY, WILD, GOLD, CYAN, BLUE};
    }

    static {
        field_28350 = AxolotlEntity.Variant.method_36644();
        DEFAULT = LUCY;
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(AxolotlEntity.Variant::getIndex, AxolotlEntity.Variant.values(), ValueLists.OutOfBoundsHandling.ZERO);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, AxolotlEntity.Variant::getIndex);
        CODEC = StringIdentifiable.createCodec(AxolotlEntity.Variant::values);
        INDEX_CODEC = Codec.INT.xmap(INDEX_MAPPER::apply, AxolotlEntity.Variant::getIndex);
    }
}
