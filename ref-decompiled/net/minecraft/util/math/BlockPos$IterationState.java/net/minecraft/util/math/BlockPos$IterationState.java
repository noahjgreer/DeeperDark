/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math;

public static final class BlockPos.IterationState
extends Enum<BlockPos.IterationState> {
    public static final /* enum */ BlockPos.IterationState ACCEPT = new BlockPos.IterationState();
    public static final /* enum */ BlockPos.IterationState SKIP = new BlockPos.IterationState();
    public static final /* enum */ BlockPos.IterationState STOP = new BlockPos.IterationState();
    private static final /* synthetic */ BlockPos.IterationState[] field_55168;

    public static BlockPos.IterationState[] values() {
        return (BlockPos.IterationState[])field_55168.clone();
    }

    public static BlockPos.IterationState valueOf(String string) {
        return Enum.valueOf(BlockPos.IterationState.class, string);
    }

    private static /* synthetic */ BlockPos.IterationState[] method_65259() {
        return new BlockPos.IterationState[]{ACCEPT, SKIP, STOP};
    }

    static {
        field_55168 = BlockPos.IterationState.method_65259();
    }
}
