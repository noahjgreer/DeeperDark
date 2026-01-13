/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.enums.RailShape
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
public final class RailShape
extends Enum<RailShape>
implements StringIdentifiable {
    public static final /* enum */ RailShape NORTH_SOUTH = new RailShape("NORTH_SOUTH", 0, "north_south");
    public static final /* enum */ RailShape EAST_WEST = new RailShape("EAST_WEST", 1, "east_west");
    public static final /* enum */ RailShape ASCENDING_EAST = new RailShape("ASCENDING_EAST", 2, "ascending_east");
    public static final /* enum */ RailShape ASCENDING_WEST = new RailShape("ASCENDING_WEST", 3, "ascending_west");
    public static final /* enum */ RailShape ASCENDING_NORTH = new RailShape("ASCENDING_NORTH", 4, "ascending_north");
    public static final /* enum */ RailShape ASCENDING_SOUTH = new RailShape("ASCENDING_SOUTH", 5, "ascending_south");
    public static final /* enum */ RailShape SOUTH_EAST = new RailShape("SOUTH_EAST", 6, "south_east");
    public static final /* enum */ RailShape SOUTH_WEST = new RailShape("SOUTH_WEST", 7, "south_west");
    public static final /* enum */ RailShape NORTH_WEST = new RailShape("NORTH_WEST", 8, "north_west");
    public static final /* enum */ RailShape NORTH_EAST = new RailShape("NORTH_EAST", 9, "north_east");
    private final String name;
    private static final /* synthetic */ RailShape[] field_12673;

    public static RailShape[] values() {
        return (RailShape[])field_12673.clone();
    }

    public static RailShape valueOf(String string) {
        return Enum.valueOf(RailShape.class, string);
    }

    private RailShape(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    public boolean isAscending() {
        return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
    }

    public String asString() {
        return this.name;
    }

    private static /* synthetic */ RailShape[] method_36732() {
        return new RailShape[]{NORTH_SOUTH, EAST_WEST, ASCENDING_EAST, ASCENDING_WEST, ASCENDING_NORTH, ASCENDING_SOUTH, SOUTH_EAST, SOUTH_WEST, NORTH_WEST, NORTH_EAST};
    }

    static {
        field_12673 = RailShape.method_36732();
    }
}

