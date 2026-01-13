/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.enums.Orientation
 *  net.minecraft.util.StringIdentifiable
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.Direction
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

/*
 * Exception performing whole class analysis ignored.
 */
public final class Orientation
extends Enum<Orientation>
implements StringIdentifiable {
    public static final /* enum */ Orientation DOWN_EAST = new Orientation("DOWN_EAST", 0, "down_east", Direction.DOWN, Direction.EAST);
    public static final /* enum */ Orientation DOWN_NORTH = new Orientation("DOWN_NORTH", 1, "down_north", Direction.DOWN, Direction.NORTH);
    public static final /* enum */ Orientation DOWN_SOUTH = new Orientation("DOWN_SOUTH", 2, "down_south", Direction.DOWN, Direction.SOUTH);
    public static final /* enum */ Orientation DOWN_WEST = new Orientation("DOWN_WEST", 3, "down_west", Direction.DOWN, Direction.WEST);
    public static final /* enum */ Orientation UP_EAST = new Orientation("UP_EAST", 4, "up_east", Direction.UP, Direction.EAST);
    public static final /* enum */ Orientation UP_NORTH = new Orientation("UP_NORTH", 5, "up_north", Direction.UP, Direction.NORTH);
    public static final /* enum */ Orientation UP_SOUTH = new Orientation("UP_SOUTH", 6, "up_south", Direction.UP, Direction.SOUTH);
    public static final /* enum */ Orientation UP_WEST = new Orientation("UP_WEST", 7, "up_west", Direction.UP, Direction.WEST);
    public static final /* enum */ Orientation WEST_UP = new Orientation("WEST_UP", 8, "west_up", Direction.WEST, Direction.UP);
    public static final /* enum */ Orientation EAST_UP = new Orientation("EAST_UP", 9, "east_up", Direction.EAST, Direction.UP);
    public static final /* enum */ Orientation NORTH_UP = new Orientation("NORTH_UP", 10, "north_up", Direction.NORTH, Direction.UP);
    public static final /* enum */ Orientation SOUTH_UP = new Orientation("SOUTH_UP", 11, "south_up", Direction.SOUTH, Direction.UP);
    private static final int DIRECTIONS;
    private static final Orientation[] VALUES;
    private final String name;
    private final Direction rotation;
    private final Direction facing;
    private static final /* synthetic */ Orientation[] field_23397;

    public static Orientation[] values() {
        return (Orientation[])field_23397.clone();
    }

    public static Orientation valueOf(String string) {
        return Enum.valueOf(Orientation.class, string);
    }

    private static int getIndex(Direction facing, Direction rotation) {
        return facing.ordinal() * DIRECTIONS + rotation.ordinal();
    }

    private Orientation(String name, Direction facing, Direction rotation) {
        this.name = name;
        this.facing = facing;
        this.rotation = rotation;
    }

    public String asString() {
        return this.name;
    }

    public static Orientation byDirections(Direction facing, Direction rotation) {
        return VALUES[Orientation.getIndex((Direction)facing, (Direction)rotation)];
    }

    public Direction getFacing() {
        return this.facing;
    }

    public Direction getRotation() {
        return this.rotation;
    }

    private static /* synthetic */ Orientation[] method_36936() {
        return new Orientation[]{DOWN_EAST, DOWN_NORTH, DOWN_SOUTH, DOWN_WEST, UP_EAST, UP_NORTH, UP_SOUTH, UP_WEST, WEST_UP, EAST_UP, NORTH_UP, SOUTH_UP};
    }

    static {
        field_23397 = Orientation.method_36936();
        DIRECTIONS = Direction.values().length;
        VALUES = (Orientation[])Util.make((Object)new Orientation[DIRECTIONS * DIRECTIONS], values -> {
            Orientation[] orientationArray = Orientation.values();
            int n = orientationArray.length;
            for (int i = 0; i < n; ++i) {
                Orientation orientation;
                values[Orientation.getIndex((Direction)orientation.facing, (Direction)orientation.rotation)] = orientation = orientationArray[i];
            }
        });
    }
}

