/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.block.enums.TestBlockMode
 *  net.minecraft.network.codec.PacketCodec
 *  net.minecraft.network.codec.PacketCodecs
 *  net.minecraft.text.Text
 *  net.minecraft.util.StringIdentifiable
 *  net.minecraft.util.function.ValueLists
 *  net.minecraft.util.function.ValueLists$OutOfBoundsHandling
 */
package net.minecraft.block.enums;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

/*
 * Exception performing whole class analysis ignored.
 */
public final class TestBlockMode
extends Enum<TestBlockMode>
implements StringIdentifiable {
    public static final /* enum */ TestBlockMode START = new TestBlockMode("START", 0, 0, "start");
    public static final /* enum */ TestBlockMode LOG = new TestBlockMode("LOG", 1, 1, "log");
    public static final /* enum */ TestBlockMode FAIL = new TestBlockMode("FAIL", 2, 2, "fail");
    public static final /* enum */ TestBlockMode ACCEPT = new TestBlockMode("ACCEPT", 3, 3, "accept");
    private static final IntFunction<TestBlockMode> INDEX_MAPPER;
    public static final Codec<TestBlockMode> CODEC;
    public static final PacketCodec<ByteBuf, TestBlockMode> PACKET_CODEC;
    private final int index;
    private final String id;
    private final Text name;
    private final Text info;
    private static final /* synthetic */ TestBlockMode[] field_56035;

    public static TestBlockMode[] values() {
        return (TestBlockMode[])field_56035.clone();
    }

    public static TestBlockMode valueOf(String string) {
        return Enum.valueOf(TestBlockMode.class, string);
    }

    private TestBlockMode(int index, String id) {
        this.index = index;
        this.id = id;
        this.name = Text.translatable((String)("test_block.mode." + id));
        this.info = Text.translatable((String)("test_block.mode_info." + id));
    }

    public String asString() {
        return this.id;
    }

    public Text getName() {
        return this.name;
    }

    public Text getInfo() {
        return this.info;
    }

    private static /* synthetic */ TestBlockMode[] method_66785() {
        return new TestBlockMode[]{START, LOG, FAIL, ACCEPT};
    }

    static {
        field_56035 = TestBlockMode.method_66785();
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(mode -> mode.index, (Object[])TestBlockMode.values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
        CODEC = StringIdentifiable.createCodec(TestBlockMode::values);
        PACKET_CODEC = PacketCodecs.indexed((IntFunction)INDEX_MAPPER, mode -> mode.index);
    }
}

