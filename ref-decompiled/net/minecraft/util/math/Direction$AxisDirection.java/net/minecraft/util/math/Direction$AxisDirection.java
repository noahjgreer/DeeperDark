/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math;

public static final class Direction.AxisDirection
extends Enum<Direction.AxisDirection> {
    public static final /* enum */ Direction.AxisDirection POSITIVE = new Direction.AxisDirection(1, "Towards positive");
    public static final /* enum */ Direction.AxisDirection NEGATIVE = new Direction.AxisDirection(-1, "Towards negative");
    private final int offset;
    private final String description;
    private static final /* synthetic */ Direction.AxisDirection[] field_11058;

    public static Direction.AxisDirection[] values() {
        return (Direction.AxisDirection[])field_11058.clone();
    }

    public static Direction.AxisDirection valueOf(String string) {
        return Enum.valueOf(Direction.AxisDirection.class, string);
    }

    private Direction.AxisDirection(int offset, String description) {
        this.offset = offset;
        this.description = description;
    }

    public int offset() {
        return this.offset;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return this.description;
    }

    public Direction.AxisDirection getOpposite() {
        return this == POSITIVE ? NEGATIVE : POSITIVE;
    }

    private static /* synthetic */ Direction.AxisDirection[] method_36933() {
        return new Direction.AxisDirection[]{POSITIVE, NEGATIVE};
    }

    static {
        field_11058 = Direction.AxisDirection.method_36933();
    }
}
