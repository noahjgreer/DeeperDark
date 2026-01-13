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
import net.minecraft.util.function.ValueLists;

public static final class SnifferEntity.State
extends Enum<SnifferEntity.State> {
    public static final /* enum */ SnifferEntity.State IDLING = new SnifferEntity.State(0);
    public static final /* enum */ SnifferEntity.State FEELING_HAPPY = new SnifferEntity.State(1);
    public static final /* enum */ SnifferEntity.State SCENTING = new SnifferEntity.State(2);
    public static final /* enum */ SnifferEntity.State SNIFFING = new SnifferEntity.State(3);
    public static final /* enum */ SnifferEntity.State SEARCHING = new SnifferEntity.State(4);
    public static final /* enum */ SnifferEntity.State DIGGING = new SnifferEntity.State(5);
    public static final /* enum */ SnifferEntity.State RISING = new SnifferEntity.State(6);
    public static final IntFunction<SnifferEntity.State> INDEX_TO_VALUE;
    public static final PacketCodec<ByteBuf, SnifferEntity.State> PACKET_CODEC;
    private final int index;
    private static final /* synthetic */ SnifferEntity.State[] field_42672;

    public static SnifferEntity.State[] values() {
        return (SnifferEntity.State[])field_42672.clone();
    }

    public static SnifferEntity.State valueOf(String string) {
        return Enum.valueOf(SnifferEntity.State.class, string);
    }

    private SnifferEntity.State(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    private static /* synthetic */ SnifferEntity.State[] method_49151() {
        return new SnifferEntity.State[]{IDLING, FEELING_HAPPY, SCENTING, SNIFFING, SEARCHING, DIGGING, RISING};
    }

    static {
        field_42672 = SnifferEntity.State.method_49151();
        INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(SnifferEntity.State::getIndex, SnifferEntity.State.values(), ValueLists.OutOfBoundsHandling.ZERO);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, SnifferEntity.State::getIndex);
    }
}
