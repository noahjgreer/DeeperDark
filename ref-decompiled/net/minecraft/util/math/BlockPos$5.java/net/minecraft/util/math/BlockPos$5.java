/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 */
package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

static class BlockPos.5
extends AbstractIterator<BlockPos.Mutable> {
    private final Direction[] directions;
    private final BlockPos.Mutable pos;
    private final int maxDirectionChanges;
    private int directionChangeCount;
    private int maxSteps;
    private int steps;
    private int currentX;
    private int currentY;
    private int currentZ;
    final /* synthetic */ Direction field_48436;
    final /* synthetic */ Direction field_48437;
    final /* synthetic */ BlockPos field_48438;
    final /* synthetic */ int field_48439;

    BlockPos.5(Direction direction, Direction direction2, BlockPos blockPos, int i) {
        this.field_48436 = direction;
        this.field_48437 = direction2;
        this.field_48438 = blockPos;
        this.field_48439 = i;
        this.directions = new Direction[]{this.field_48436, this.field_48437, this.field_48436.getOpposite(), this.field_48437.getOpposite()};
        this.pos = this.field_48438.mutableCopy().move(this.field_48437);
        this.maxDirectionChanges = 4 * this.field_48439;
        this.directionChangeCount = -1;
        this.currentX = this.pos.getX();
        this.currentY = this.pos.getY();
        this.currentZ = this.pos.getZ();
    }

    protected BlockPos.Mutable computeNext() {
        this.pos.set(this.currentX, this.currentY, this.currentZ).move(this.directions[(this.directionChangeCount + 4) % 4]);
        this.currentX = this.pos.getX();
        this.currentY = this.pos.getY();
        this.currentZ = this.pos.getZ();
        if (this.steps >= this.maxSteps) {
            if (this.directionChangeCount >= this.maxDirectionChanges) {
                return (BlockPos.Mutable)this.endOfData();
            }
            ++this.directionChangeCount;
            this.steps = 0;
            this.maxSteps = this.directionChangeCount / 2 + 1;
        }
        ++this.steps;
        return this.pos;
    }

    protected /* synthetic */ Object computeNext() {
        return this.computeNext();
    }
}
