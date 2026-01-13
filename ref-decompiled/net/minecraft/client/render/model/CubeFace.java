/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.CubeFace
 *  net.minecraft.client.render.model.CubeFace$Corner
 *  net.minecraft.client.render.model.CubeFace$CornerCoord
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.Direction
 */
package net.minecraft.client.render.model;

import java.util.EnumMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.CubeFace;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class CubeFace
extends Enum<CubeFace> {
    public static final /* enum */ CubeFace DOWN = new CubeFace("DOWN", 0, new Corner[]{new Corner(CornerCoord.MIN_X, CornerCoord.MIN_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MIN_X, CornerCoord.MIN_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MIN_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MIN_Y, CornerCoord.MAX_Z)});
    public static final /* enum */ CubeFace UP = new CubeFace("UP", 1, new Corner[]{new Corner(CornerCoord.MIN_X, CornerCoord.MAX_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MIN_X, CornerCoord.MAX_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MAX_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MAX_Y, CornerCoord.MIN_Z)});
    public static final /* enum */ CubeFace NORTH = new CubeFace("NORTH", 2, new Corner[]{new Corner(CornerCoord.MAX_X, CornerCoord.MAX_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MIN_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MIN_X, CornerCoord.MIN_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MIN_X, CornerCoord.MAX_Y, CornerCoord.MIN_Z)});
    public static final /* enum */ CubeFace SOUTH = new CubeFace("SOUTH", 3, new Corner[]{new Corner(CornerCoord.MIN_X, CornerCoord.MAX_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MIN_X, CornerCoord.MIN_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MIN_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MAX_Y, CornerCoord.MAX_Z)});
    public static final /* enum */ CubeFace WEST = new CubeFace("WEST", 4, new Corner[]{new Corner(CornerCoord.MIN_X, CornerCoord.MAX_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MIN_X, CornerCoord.MIN_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MIN_X, CornerCoord.MIN_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MIN_X, CornerCoord.MAX_Y, CornerCoord.MAX_Z)});
    public static final /* enum */ CubeFace EAST = new CubeFace("EAST", 5, new Corner[]{new Corner(CornerCoord.MAX_X, CornerCoord.MAX_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MIN_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MIN_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MAX_Y, CornerCoord.MIN_Z)});
    private static final Map<Direction, CubeFace> DIRECTION_LOOKUP;
    private final Corner[] corners;
    private static final /* synthetic */ CubeFace[] field_3964;

    public static CubeFace[] values() {
        return (CubeFace[])field_3964.clone();
    }

    public static CubeFace valueOf(String string) {
        return Enum.valueOf(CubeFace.class, string);
    }

    public static CubeFace getFace(Direction direction) {
        return (CubeFace)DIRECTION_LOOKUP.get(direction);
    }

    private CubeFace(Corner ... corners) {
        this.corners = corners;
    }

    public Corner getCorner(int corner) {
        return this.corners[corner];
    }

    private static /* synthetic */ CubeFace[] method_36913() {
        return new CubeFace[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
    }

    static {
        field_3964 = CubeFace.method_36913();
        DIRECTION_LOOKUP = (Map)Util.make(new EnumMap(Direction.class), map -> {
            map.put(Direction.DOWN, DOWN);
            map.put(Direction.UP, UP);
            map.put(Direction.NORTH, NORTH);
            map.put(Direction.SOUTH, SOUTH);
            map.put(Direction.WEST, WEST);
            map.put(Direction.EAST, EAST);
        });
    }
}

