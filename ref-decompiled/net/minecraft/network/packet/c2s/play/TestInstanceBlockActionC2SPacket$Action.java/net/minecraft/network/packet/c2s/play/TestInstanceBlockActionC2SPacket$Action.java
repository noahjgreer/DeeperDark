/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.packet.c2s.play;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.function.ValueLists;

public static final class TestInstanceBlockActionC2SPacket.Action
extends Enum<TestInstanceBlockActionC2SPacket.Action> {
    public static final /* enum */ TestInstanceBlockActionC2SPacket.Action INIT = new TestInstanceBlockActionC2SPacket.Action(0);
    public static final /* enum */ TestInstanceBlockActionC2SPacket.Action QUERY = new TestInstanceBlockActionC2SPacket.Action(1);
    public static final /* enum */ TestInstanceBlockActionC2SPacket.Action SET = new TestInstanceBlockActionC2SPacket.Action(2);
    public static final /* enum */ TestInstanceBlockActionC2SPacket.Action RESET = new TestInstanceBlockActionC2SPacket.Action(3);
    public static final /* enum */ TestInstanceBlockActionC2SPacket.Action SAVE = new TestInstanceBlockActionC2SPacket.Action(4);
    public static final /* enum */ TestInstanceBlockActionC2SPacket.Action EXPORT = new TestInstanceBlockActionC2SPacket.Action(5);
    public static final /* enum */ TestInstanceBlockActionC2SPacket.Action RUN = new TestInstanceBlockActionC2SPacket.Action(6);
    private static final IntFunction<TestInstanceBlockActionC2SPacket.Action> INDEX_MAPPER;
    public static final PacketCodec<ByteBuf, TestInstanceBlockActionC2SPacket.Action> CODEC;
    private final int index;
    private static final /* synthetic */ TestInstanceBlockActionC2SPacket.Action[] field_55930;

    public static TestInstanceBlockActionC2SPacket.Action[] values() {
        return (TestInstanceBlockActionC2SPacket.Action[])field_55930.clone();
    }

    public static TestInstanceBlockActionC2SPacket.Action valueOf(String string) {
        return Enum.valueOf(TestInstanceBlockActionC2SPacket.Action.class, string);
    }

    private TestInstanceBlockActionC2SPacket.Action(int index) {
        this.index = index;
    }

    private static /* synthetic */ TestInstanceBlockActionC2SPacket.Action[] method_66585() {
        return new TestInstanceBlockActionC2SPacket.Action[]{INIT, QUERY, SET, RESET, SAVE, EXPORT, RUN};
    }

    static {
        field_55930 = TestInstanceBlockActionC2SPacket.Action.method_66585();
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(action -> action.index, TestInstanceBlockActionC2SPacket.Action.values(), ValueLists.OutOfBoundsHandling.ZERO);
        CODEC = PacketCodecs.indexed(INDEX_MAPPER, action -> action.index);
    }
}
