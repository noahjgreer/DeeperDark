/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math;

import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.Direction;

final class AxisCycleDirection.1
extends AxisCycleDirection {
    @Override
    public int choose(int x, int y, int z, Direction.Axis axis) {
        return axis.choose(x, y, z);
    }

    @Override
    public double choose(double x, double y, double z, Direction.Axis axis) {
        return axis.choose(x, y, z);
    }

    @Override
    public Direction.Axis cycle(Direction.Axis axis) {
        return axis;
    }

    @Override
    public AxisCycleDirection opposite() {
        return this;
    }
}
