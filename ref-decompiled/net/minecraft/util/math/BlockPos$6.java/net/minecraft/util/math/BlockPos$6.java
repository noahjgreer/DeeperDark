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

static class BlockPos.6
extends AbstractIterator<BlockPos> {
    private final BlockPos.Mutable pos = new BlockPos.Mutable();
    private int deltaAxis1;
    private int deltaAxis2;
    private int deltaAxis3;
    private boolean done;
    private final int axis1x = this.field_61941.getOffsetX();
    private final int axis1y = this.field_61941.getOffsetY();
    private final int axis1z = this.field_61941.getOffsetZ();
    private final int axis2x = this.field_61942.getOffsetX();
    private final int axis2y = this.field_61942.getOffsetY();
    private final int axis2z = this.field_61942.getOffsetZ();
    private final int axis3x = this.field_61943.getOffsetX();
    private final int axis3y = this.field_61943.getOffsetY();
    private final int axis3z = this.field_61943.getOffsetZ();
    final /* synthetic */ Direction field_61941;
    final /* synthetic */ Direction field_61942;
    final /* synthetic */ Direction field_61943;
    final /* synthetic */ int field_61944;
    final /* synthetic */ int field_61945;
    final /* synthetic */ int field_61946;
    final /* synthetic */ int field_61947;
    final /* synthetic */ int field_61948;
    final /* synthetic */ int field_61949;

    BlockPos.6(Direction direction, Direction direction2, Direction direction3, int i, int j, int k, int l, int m, int n) {
        this.field_61941 = direction;
        this.field_61942 = direction2;
        this.field_61943 = direction3;
        this.field_61944 = i;
        this.field_61945 = j;
        this.field_61946 = k;
        this.field_61947 = l;
        this.field_61948 = m;
        this.field_61949 = n;
    }

    protected BlockPos computeNext() {
        if (this.done) {
            return (BlockPos)this.endOfData();
        }
        this.pos.set(this.field_61944 + this.axis1x * this.deltaAxis1 + this.axis2x * this.deltaAxis2 + this.axis3x * this.deltaAxis3, this.field_61945 + this.axis1y * this.deltaAxis1 + this.axis2y * this.deltaAxis2 + this.axis3y * this.deltaAxis3, this.field_61946 + this.axis1z * this.deltaAxis1 + this.axis2z * this.deltaAxis2 + this.axis3z * this.deltaAxis3);
        if (this.deltaAxis3 < this.field_61947) {
            ++this.deltaAxis3;
        } else if (this.deltaAxis2 < this.field_61948) {
            ++this.deltaAxis2;
            this.deltaAxis3 = 0;
        } else if (this.deltaAxis1 < this.field_61949) {
            ++this.deltaAxis1;
            this.deltaAxis3 = 0;
            this.deltaAxis2 = 0;
        } else {
            this.done = true;
        }
        return this.pos;
    }

    protected /* synthetic */ Object computeNext() {
        return this.computeNext();
    }
}
