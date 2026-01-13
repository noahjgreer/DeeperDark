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

public static abstract sealed class ArmadilloEntity.State
extends Enum<ArmadilloEntity.State>
implements StringIdentifiable {
    public static final /* enum */ ArmadilloEntity.State IDLE = new ArmadilloEntity.State("idle", false, 0, 0){

        @Override
        public boolean isRolledUp(long currentStateTicks) {
            return false;
        }
    };
    public static final /* enum */ ArmadilloEntity.State ROLLING = new ArmadilloEntity.State("rolling", true, 10, 1){

        @Override
        public boolean isRolledUp(long currentStateTicks) {
            return currentStateTicks > 5L;
        }
    };
    public static final /* enum */ ArmadilloEntity.State SCARED = new ArmadilloEntity.State("scared", true, 50, 2){

        @Override
        public boolean isRolledUp(long currentStateTicks) {
            return true;
        }
    };
    public static final /* enum */ ArmadilloEntity.State UNROLLING = new ArmadilloEntity.State("unrolling", true, 30, 3){

        @Override
        public boolean isRolledUp(long currentStateTicks) {
            return currentStateTicks < 26L;
        }
    };
    static final Codec<ArmadilloEntity.State> CODEC;
    private static final IntFunction<ArmadilloEntity.State> INDEX_TO_VALUE;
    public static final PacketCodec<ByteBuf, ArmadilloEntity.State> PACKET_CODEC;
    private final String name;
    private final boolean runRollUpTask;
    private final int lengthInTicks;
    private final int index;
    private static final /* synthetic */ ArmadilloEntity.State[] field_47795;

    public static ArmadilloEntity.State[] values() {
        return (ArmadilloEntity.State[])field_47795.clone();
    }

    public static ArmadilloEntity.State valueOf(String string) {
        return Enum.valueOf(ArmadilloEntity.State.class, string);
    }

    ArmadilloEntity.State(String name, boolean runRollUpTask, int lengthInTicks, int index) {
        this.name = name;
        this.runRollUpTask = runRollUpTask;
        this.lengthInTicks = lengthInTicks;
        this.index = index;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private int getIndex() {
        return this.index;
    }

    public abstract boolean isRolledUp(long var1);

    public boolean shouldRunRollUpTask() {
        return this.runRollUpTask;
    }

    public int getLengthInTicks() {
        return this.lengthInTicks;
    }

    private static /* synthetic */ ArmadilloEntity.State[] method_55726() {
        return new ArmadilloEntity.State[]{IDLE, ROLLING, SCARED, UNROLLING};
    }

    static {
        field_47795 = ArmadilloEntity.State.method_55726();
        CODEC = StringIdentifiable.createCodec(ArmadilloEntity.State::values);
        INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(ArmadilloEntity.State::getIndex, ArmadilloEntity.State.values(), ValueLists.OutOfBoundsHandling.ZERO);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, ArmadilloEntity.State::getIndex);
    }
}
