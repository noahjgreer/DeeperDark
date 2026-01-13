/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 */
package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import net.minecraft.util.math.BlockPos;

static class BlockPos.3
extends AbstractIterator<BlockPos> {
    private final BlockPos.Mutable pos = new BlockPos.Mutable();
    private int manhattanDistance;
    private int limitX;
    private int limitY;
    private int dx;
    private int dy;
    private boolean swapZ;
    final /* synthetic */ int field_48415;
    final /* synthetic */ int field_48416;
    final /* synthetic */ int field_48417;
    final /* synthetic */ int field_48418;
    final /* synthetic */ int field_48419;
    final /* synthetic */ int field_48420;
    final /* synthetic */ int field_48421;

    BlockPos.3(int i, int j, int k, int l, int m, int n, int o) {
        this.field_48415 = i;
        this.field_48416 = j;
        this.field_48417 = k;
        this.field_48418 = l;
        this.field_48419 = m;
        this.field_48420 = n;
        this.field_48421 = o;
    }

    protected BlockPos computeNext() {
        if (this.swapZ) {
            this.swapZ = false;
            this.pos.setZ(this.field_48415 - (this.pos.getZ() - this.field_48415));
            return this.pos;
        }
        BlockPos.Mutable blockPos = null;
        while (blockPos == null) {
            if (this.dy > this.limitY) {
                ++this.dx;
                if (this.dx > this.limitX) {
                    ++this.manhattanDistance;
                    if (this.manhattanDistance > this.field_48416) {
                        return (BlockPos)this.endOfData();
                    }
                    this.limitX = Math.min(this.field_48417, this.manhattanDistance);
                    this.dx = -this.limitX;
                }
                this.limitY = Math.min(this.field_48418, this.manhattanDistance - Math.abs(this.dx));
                this.dy = -this.limitY;
            }
            int i = this.dx;
            int j = this.dy;
            int k = this.manhattanDistance - Math.abs(i) - Math.abs(j);
            if (k <= this.field_48419) {
                this.swapZ = k != 0;
                blockPos = this.pos.set(this.field_48420 + i, this.field_48421 + j, this.field_48415 + k);
            }
            ++this.dy;
        }
        return blockPos;
    }

    protected /* synthetic */ Object computeNext() {
        return this.computeNext();
    }
}
