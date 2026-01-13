/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util.math;

import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

final class Direction.Axis.2
extends Direction.Axis {
    Direction.Axis.2(String string2) {
    }

    @Override
    public int choose(int x, int y, int z) {
        return y;
    }

    @Override
    public double choose(double x, double y, double z) {
        return y;
    }

    @Override
    public boolean choose(boolean x, boolean y, boolean z) {
        return y;
    }

    @Override
    public Direction getPositiveDirection() {
        return UP;
    }

    @Override
    public Direction getNegativeDirection() {
        return DOWN;
    }

    @Override
    public /* synthetic */ boolean test(@Nullable Object object) {
        return super.test((Direction)object);
    }
}
