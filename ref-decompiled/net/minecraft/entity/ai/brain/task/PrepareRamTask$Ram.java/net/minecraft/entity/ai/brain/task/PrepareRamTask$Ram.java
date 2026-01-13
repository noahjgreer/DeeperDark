/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public static class PrepareRamTask.Ram {
    private final BlockPos start;
    private final BlockPos end;
    final LivingEntity entity;

    public PrepareRamTask.Ram(BlockPos start, BlockPos end, LivingEntity entity) {
        this.start = start;
        this.end = end;
        this.entity = entity;
    }

    public BlockPos getStart() {
        return this.start;
    }

    public BlockPos getEnd() {
        return this.end;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }
}
