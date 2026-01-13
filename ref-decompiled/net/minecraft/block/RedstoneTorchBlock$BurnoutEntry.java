/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.util.math.BlockPos;

public static class RedstoneTorchBlock.BurnoutEntry {
    final BlockPos pos;
    final long time;

    public RedstoneTorchBlock.BurnoutEntry(BlockPos pos, long time) {
        this.pos = pos;
        this.time = time;
    }
}
