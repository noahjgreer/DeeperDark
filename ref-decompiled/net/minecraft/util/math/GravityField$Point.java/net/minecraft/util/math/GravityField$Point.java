/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math;

import net.minecraft.util.math.BlockPos;

static class GravityField.Point {
    private final BlockPos pos;
    private final double mass;

    public GravityField.Point(BlockPos pos, double mass) {
        this.pos = pos;
        this.mass = mass;
    }

    public double getGravityFactor(BlockPos pos) {
        double d = this.pos.getSquaredDistance(pos);
        if (d == 0.0) {
            return Double.POSITIVE_INFINITY;
        }
        return this.mass / Math.sqrt(d);
    }
}
