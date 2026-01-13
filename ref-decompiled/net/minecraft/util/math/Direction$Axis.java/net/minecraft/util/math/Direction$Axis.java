/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util.math;

import java.util.function.Predicate;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

public static abstract sealed class Direction.Axis
extends Enum<Direction.Axis>
implements StringIdentifiable,
Predicate<Direction> {
    public static final /* enum */ Direction.Axis X = new Direction.Axis("x"){

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
    };
    public static final /* enum */ Direction.Axis Y = new Direction.Axis("y"){

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
    };
    public static final /* enum */ Direction.Axis Z = new Direction.Axis("z"){

        @Override
        public int choose(int x, int y, int z) {
            return z;
        }

        @Override
        public double choose(double x, double y, double z) {
            return z;
        }

        @Override
        public boolean choose(boolean x, boolean y, boolean z) {
            return z;
        }

        @Override
        public Direction getPositiveDirection() {
            return SOUTH;
        }

        @Override
        public Direction getNegativeDirection() {
            return NORTH;
        }

        @Override
        public /* synthetic */ boolean test(@Nullable Object object) {
            return super.test((Direction)object);
        }
    };
    public static final Direction.Axis[] VALUES;
    public static final StringIdentifiable.EnumCodec<Direction.Axis> CODEC;
    private final String id;
    private static final /* synthetic */ Direction.Axis[] field_11049;

    public static Direction.Axis[] values() {
        return (Direction.Axis[])field_11049.clone();
    }

    public static Direction.Axis valueOf(String string) {
        return Enum.valueOf(Direction.Axis.class, string);
    }

    Direction.Axis(String id) {
        this.id = id;
    }

    public static @Nullable Direction.Axis fromId(String id) {
        return CODEC.byId(id);
    }

    public String getId() {
        return this.id;
    }

    public boolean isVertical() {
        return this == Y;
    }

    public boolean isHorizontal() {
        return this == X || this == Z;
    }

    public abstract Direction getPositiveDirection();

    public abstract Direction getNegativeDirection();

    public Direction[] getDirections() {
        return new Direction[]{this.getPositiveDirection(), this.getNegativeDirection()};
    }

    public String toString() {
        return this.id;
    }

    public static Direction.Axis pickRandomAxis(Random random) {
        return Util.getRandom(VALUES, random);
    }

    @Override
    public boolean test(@Nullable Direction direction) {
        return direction != null && direction.getAxis() == this;
    }

    public Direction.Type getType() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0, 2 -> Direction.Type.HORIZONTAL;
            case 1 -> Direction.Type.VERTICAL;
        };
    }

    @Override
    public String asString() {
        return this.id;
    }

    public abstract int choose(int var1, int var2, int var3);

    public abstract double choose(double var1, double var3, double var5);

    public abstract boolean choose(boolean var1, boolean var2, boolean var3);

    @Override
    public /* synthetic */ boolean test(@Nullable Object object) {
        return this.test((Direction)object);
    }

    private static /* synthetic */ Direction.Axis[] method_36932() {
        return new Direction.Axis[]{X, Y, Z};
    }

    static {
        field_11049 = Direction.Axis.method_36932();
        VALUES = Direction.Axis.values();
        CODEC = StringIdentifiable.createCodec(Direction.Axis::values);
    }
}
