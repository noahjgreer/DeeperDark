/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util.math;

import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

final class Direction.Axis.1
extends Direction.Axis {
    Direction.Axis.1(String string2) {
    }

    @Override
    public int choose(int x, int y, int z) {
        return x;
    }

    @Override
    public boolean choose(boolean x, boolean y, boolean z) {
        return x;
    }

    @Override
    public double choose(double x, double y, double z) {
        return x;
    }

    @Override
    public Direction getPositiveDirection() {
        return EAST;
    }

    @Override
    public Direction getNegativeDirection() {
        return WEST;
    }

    @Override
    public /* synthetic */ boolean test(@Nullable Object object) {
        return super.test((Direction)object);
    }
}
