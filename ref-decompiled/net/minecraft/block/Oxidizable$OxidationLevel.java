/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.block;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public static final class Oxidizable.OxidationLevel
extends Enum<Oxidizable.OxidationLevel>
implements StringIdentifiable {
    public static final /* enum */ Oxidizable.OxidationLevel UNAFFECTED = new Oxidizable.OxidationLevel("unaffected");
    public static final /* enum */ Oxidizable.OxidationLevel EXPOSED = new Oxidizable.OxidationLevel("exposed");
    public static final /* enum */ Oxidizable.OxidationLevel WEATHERED = new Oxidizable.OxidationLevel("weathered");
    public static final /* enum */ Oxidizable.OxidationLevel OXIDIZED = new Oxidizable.OxidationLevel("oxidized");
    public static final IntFunction<Oxidizable.OxidationLevel> indexMapper;
    public static final Codec<Oxidizable.OxidationLevel> CODEC;
    public static final PacketCodec<ByteBuf, Oxidizable.OxidationLevel> PACKET_CODEC;
    private final String id;
    private static final /* synthetic */ Oxidizable.OxidationLevel[] field_28708;

    public static Oxidizable.OxidationLevel[] values() {
        return (Oxidizable.OxidationLevel[])field_28708.clone();
    }

    public static Oxidizable.OxidationLevel valueOf(String string) {
        return Enum.valueOf(Oxidizable.OxidationLevel.class, string);
    }

    private Oxidizable.OxidationLevel(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public Oxidizable.OxidationLevel getIncreased() {
        return indexMapper.apply(this.ordinal() + 1);
    }

    public Oxidizable.OxidationLevel getDecreased() {
        return indexMapper.apply(this.ordinal() - 1);
    }

    private static /* synthetic */ Oxidizable.OxidationLevel[] method_36712() {
        return new Oxidizable.OxidationLevel[]{UNAFFECTED, EXPOSED, WEATHERED, OXIDIZED};
    }

    static {
        field_28708 = Oxidizable.OxidationLevel.method_36712();
        indexMapper = ValueLists.createIndexToValueFunction(Enum::ordinal, Oxidizable.OxidationLevel.values(), ValueLists.OutOfBoundsHandling.CLAMP);
        CODEC = StringIdentifiable.createCodec(Oxidizable.OxidationLevel::values);
        PACKET_CODEC = PacketCodecs.indexed(indexMapper, Enum::ordinal);
    }
}
