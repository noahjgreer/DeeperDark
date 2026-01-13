/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 */
package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

static class BlockPos.2
extends AbstractIterator<BlockPos> {
    final BlockPos.Mutable pos = new BlockPos.Mutable();
    int remaining = this.field_48407;
    final /* synthetic */ int field_48407;
    final /* synthetic */ int field_48408;
    final /* synthetic */ Random field_48409;
    final /* synthetic */ int field_48410;
    final /* synthetic */ int field_48411;
    final /* synthetic */ int field_48412;
    final /* synthetic */ int field_48413;
    final /* synthetic */ int field_48414;

    BlockPos.2(int i, int j, Random random, int k, int l, int m, int n, int o) {
        this.field_48407 = i;
        this.field_48408 = j;
        this.field_48409 = random;
        this.field_48410 = k;
        this.field_48411 = l;
        this.field_48412 = m;
        this.field_48413 = n;
        this.field_48414 = o;
    }

    protected BlockPos computeNext() {
        if (this.remaining <= 0) {
            return (BlockPos)this.endOfData();
        }
        BlockPos.Mutable blockPos = this.pos.set(this.field_48408 + this.field_48409.nextInt(this.field_48410), this.field_48411 + this.field_48409.nextInt(this.field_48412), this.field_48413 + this.field_48409.nextInt(this.field_48414));
        --this.remaining;
        return blockPos;
    }

    protected /* synthetic */ Object computeNext() {
        return this.computeNext();
    }
}
