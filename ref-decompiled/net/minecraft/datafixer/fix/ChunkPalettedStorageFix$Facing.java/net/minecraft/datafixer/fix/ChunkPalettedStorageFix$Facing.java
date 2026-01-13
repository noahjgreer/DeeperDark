/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.datafixer.fix;

public static final class ChunkPalettedStorageFix.Facing
extends Enum<ChunkPalettedStorageFix.Facing> {
    public static final /* enum */ ChunkPalettedStorageFix.Facing DOWN = new ChunkPalettedStorageFix.Facing(Direction.NEGATIVE, Axis.Y);
    public static final /* enum */ ChunkPalettedStorageFix.Facing UP = new ChunkPalettedStorageFix.Facing(Direction.POSITIVE, Axis.Y);
    public static final /* enum */ ChunkPalettedStorageFix.Facing NORTH = new ChunkPalettedStorageFix.Facing(Direction.NEGATIVE, Axis.Z);
    public static final /* enum */ ChunkPalettedStorageFix.Facing SOUTH = new ChunkPalettedStorageFix.Facing(Direction.POSITIVE, Axis.Z);
    public static final /* enum */ ChunkPalettedStorageFix.Facing WEST = new ChunkPalettedStorageFix.Facing(Direction.NEGATIVE, Axis.X);
    public static final /* enum */ ChunkPalettedStorageFix.Facing EAST = new ChunkPalettedStorageFix.Facing(Direction.POSITIVE, Axis.X);
    private final Axis axis;
    private final Direction direction;
    private static final /* synthetic */ ChunkPalettedStorageFix.Facing[] field_15865;

    public static ChunkPalettedStorageFix.Facing[] values() {
        return (ChunkPalettedStorageFix.Facing[])field_15865.clone();
    }

    public static ChunkPalettedStorageFix.Facing valueOf(String string) {
        return Enum.valueOf(ChunkPalettedStorageFix.Facing.class, string);
    }

    private ChunkPalettedStorageFix.Facing(Direction direction, Axis axis) {
        this.axis = axis;
        this.direction = direction;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Axis getAxis() {
        return this.axis;
    }

    private static /* synthetic */ ChunkPalettedStorageFix.Facing[] method_36590() {
        return new ChunkPalettedStorageFix.Facing[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
    }

    static {
        field_15865 = ChunkPalettedStorageFix.Facing.method_36590();
    }

    public static final class Axis
    extends Enum<Axis> {
        public static final /* enum */ Axis X = new Axis();
        public static final /* enum */ Axis Y = new Axis();
        public static final /* enum */ Axis Z = new Axis();
        private static final /* synthetic */ Axis[] field_15868;

        public static Axis[] values() {
            return (Axis[])field_15868.clone();
        }

        public static Axis valueOf(String string) {
            return Enum.valueOf(Axis.class, string);
        }

        private static /* synthetic */ Axis[] method_36591() {
            return new Axis[]{X, Y, Z};
        }

        static {
            field_15868 = Axis.method_36591();
        }
    }

    public static final class Direction
    extends Enum<Direction> {
        public static final /* enum */ Direction POSITIVE = new Direction(1);
        public static final /* enum */ Direction NEGATIVE = new Direction(-1);
        private final int offset;
        private static final /* synthetic */ Direction[] field_15871;

        public static Direction[] values() {
            return (Direction[])field_15871.clone();
        }

        public static Direction valueOf(String string) {
            return Enum.valueOf(Direction.class, string);
        }

        private Direction(int offset) {
            this.offset = offset;
        }

        public int getOffset() {
            return this.offset;
        }

        private static /* synthetic */ Direction[] method_36592() {
            return new Direction[]{POSITIVE, NEGATIVE};
        }

        static {
            field_15871 = Direction.method_36592();
        }
    }
}
