/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 */
package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import net.minecraft.util.math.BlockPos;

static class BlockPos.4
extends AbstractIterator<BlockPos> {
    private final BlockPos.Mutable pos = new BlockPos.Mutable();
    private int index;
    final /* synthetic */ int field_48428;
    final /* synthetic */ int field_48429;
    final /* synthetic */ int field_48430;
    final /* synthetic */ int field_48431;
    final /* synthetic */ int field_48432;
    final /* synthetic */ int field_48433;

    BlockPos.4(int i, int j, int k, int l, int m, int n) {
        this.field_48428 = i;
        this.field_48429 = j;
        this.field_48430 = k;
        this.field_48431 = l;
        this.field_48432 = m;
        this.field_48433 = n;
    }

    protected BlockPos computeNext() {
        if (this.index == this.field_48428) {
            return (BlockPos)this.endOfData();
        }
        int i = this.index % this.field_48429;
        int j = this.index / this.field_48429;
        int k = j % this.field_48430;
        int l = j / this.field_48430;
        ++this.index;
        return this.pos.set(this.field_48431 + i, this.field_48432 + k, this.field_48433 + l);
    }

    protected /* synthetic */ Object computeNext() {
        return this.computeNext();
    }
}
