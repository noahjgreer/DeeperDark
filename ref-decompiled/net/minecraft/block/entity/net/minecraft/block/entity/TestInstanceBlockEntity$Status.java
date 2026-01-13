/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.block.entity;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public static final class TestInstanceBlockEntity.Status
extends Enum<TestInstanceBlockEntity.Status>
implements StringIdentifiable {
    public static final /* enum */ TestInstanceBlockEntity.Status CLEARED = new TestInstanceBlockEntity.Status("cleared", 0);
    public static final /* enum */ TestInstanceBlockEntity.Status RUNNING = new TestInstanceBlockEntity.Status("running", 1);
    public static final /* enum */ TestInstanceBlockEntity.Status FINISHED = new TestInstanceBlockEntity.Status("finished", 2);
    private static final IntFunction<TestInstanceBlockEntity.Status> INDEX_MAPPER;
    public static final Codec<TestInstanceBlockEntity.Status> CODEC;
    public static final PacketCodec<ByteBuf, TestInstanceBlockEntity.Status> PACKET_CODEC;
    private final String id;
    private final int index;
    private static final /* synthetic */ TestInstanceBlockEntity.Status[] field_56022;

    public static TestInstanceBlockEntity.Status[] values() {
        return (TestInstanceBlockEntity.Status[])field_56022.clone();
    }

    public static TestInstanceBlockEntity.Status valueOf(String string) {
        return Enum.valueOf(TestInstanceBlockEntity.Status.class, string);
    }

    private TestInstanceBlockEntity.Status(String id, int index) {
        this.id = id;
        this.index = index;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public static TestInstanceBlockEntity.Status fromIndex(int index) {
        return INDEX_MAPPER.apply(index);
    }

    private static /* synthetic */ TestInstanceBlockEntity.Status[] method_66777() {
        return new TestInstanceBlockEntity.Status[]{CLEARED, RUNNING, FINISHED};
    }

    static {
        field_56022 = TestInstanceBlockEntity.Status.method_66777();
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(status -> status.index, TestInstanceBlockEntity.Status.values(), ValueLists.OutOfBoundsHandling.ZERO);
        CODEC = StringIdentifiable.createCodec(TestInstanceBlockEntity.Status::values);
        PACKET_CODEC = PacketCodecs.indexed(TestInstanceBlockEntity.Status::fromIndex, status -> status.index);
    }
}
