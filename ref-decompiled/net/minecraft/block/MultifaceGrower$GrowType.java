/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.MultifaceGrower;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public static abstract sealed class MultifaceGrower.GrowType
extends Enum<MultifaceGrower.GrowType> {
    public static final /* enum */ MultifaceGrower.GrowType SAME_POSITION = new MultifaceGrower.GrowType(){

        @Override
        public MultifaceGrower.GrowPos getGrowPos(BlockPos pos, Direction newDirection, Direction oldDirection) {
            return new MultifaceGrower.GrowPos(pos, newDirection);
        }
    };
    public static final /* enum */ MultifaceGrower.GrowType SAME_PLANE = new MultifaceGrower.GrowType(){

        @Override
        public MultifaceGrower.GrowPos getGrowPos(BlockPos pos, Direction newDirection, Direction oldDirection) {
            return new MultifaceGrower.GrowPos(pos.offset(newDirection), oldDirection);
        }
    };
    public static final /* enum */ MultifaceGrower.GrowType WRAP_AROUND = new MultifaceGrower.GrowType(){

        @Override
        public MultifaceGrower.GrowPos getGrowPos(BlockPos pos, Direction newDirection, Direction oldDirection) {
            return new MultifaceGrower.GrowPos(pos.offset(newDirection).offset(oldDirection), newDirection.getOpposite());
        }
    };
    private static final /* synthetic */ MultifaceGrower.GrowType[] field_37601;

    public static MultifaceGrower.GrowType[] values() {
        return (MultifaceGrower.GrowType[])field_37601.clone();
    }

    public static MultifaceGrower.GrowType valueOf(String string) {
        return Enum.valueOf(MultifaceGrower.GrowType.class, string);
    }

    public abstract MultifaceGrower.GrowPos getGrowPos(BlockPos var1, Direction var2, Direction var3);

    private static /* synthetic */ MultifaceGrower.GrowType[] method_41465() {
        return new MultifaceGrower.GrowType[]{SAME_POSITION, SAME_PLANE, WRAP_AROUND};
    }

    static {
        field_37601 = MultifaceGrower.GrowType.method_41465();
    }
}
