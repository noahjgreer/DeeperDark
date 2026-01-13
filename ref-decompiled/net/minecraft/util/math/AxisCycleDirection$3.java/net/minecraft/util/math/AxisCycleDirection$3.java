/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math;

import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.Direction;

final class AxisCycleDirection.3
extends AxisCycleDirection {
    @Override
    public int choose(int x, int y, int z, Direction.Axis axis) {
        return axis.choose(y, z, x);
    }

    @Override
    public double choose(double x, double y, double z, Direction.Axis axis) {
        return axis.choose(y, z, x);
    }

    @Override
    public Direction.Axis cycle(Direction.Axis axis) {
        return AXES[Math.floorMod(axis.ordinal() - 1, 3)];
    }

    @Override
    public AxisCycleDirection opposite() {
        return FORWARD;
    }
}
