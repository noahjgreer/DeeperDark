/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.EnumMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public final class CubeFace
extends Enum<CubeFace> {
    public static final /* enum */ CubeFace DOWN = new CubeFace(new Corner(CornerCoord.MIN_X, CornerCoord.MIN_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MIN_X, CornerCoord.MIN_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MIN_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MIN_Y, CornerCoord.MAX_Z));
    public static final /* enum */ CubeFace UP = new CubeFace(new Corner(CornerCoord.MIN_X, CornerCoord.MAX_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MIN_X, CornerCoord.MAX_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MAX_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MAX_Y, CornerCoord.MIN_Z));
    public static final /* enum */ CubeFace NORTH = new CubeFace(new Corner(CornerCoord.MAX_X, CornerCoord.MAX_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MIN_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MIN_X, CornerCoord.MIN_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MIN_X, CornerCoord.MAX_Y, CornerCoord.MIN_Z));
    public static final /* enum */ CubeFace SOUTH = new CubeFace(new Corner(CornerCoord.MIN_X, CornerCoord.MAX_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MIN_X, CornerCoord.MIN_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MIN_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MAX_Y, CornerCoord.MAX_Z));
    public static final /* enum */ CubeFace WEST = new CubeFace(new Corner(CornerCoord.MIN_X, CornerCoord.MAX_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MIN_X, CornerCoord.MIN_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MIN_X, CornerCoord.MIN_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MIN_X, CornerCoord.MAX_Y, CornerCoord.MAX_Z));
    public static final /* enum */ CubeFace EAST = new CubeFace(new Corner(CornerCoord.MAX_X, CornerCoord.MAX_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MIN_Y, CornerCoord.MAX_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MIN_Y, CornerCoord.MIN_Z), new Corner(CornerCoord.MAX_X, CornerCoord.MAX_Y, CornerCoord.MIN_Z));
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
        return DIRECTION_LOOKUP.get(direction);
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
        DIRECTION_LOOKUP = Util.make(new EnumMap(Direction.class), map -> {
            map.put(Direction.DOWN, DOWN);
            map.put(Direction.UP, UP);
            map.put(Direction.NORTH, NORTH);
            map.put(Direction.SOUTH, SOUTH);
            map.put(Direction.WEST, WEST);
            map.put(Direction.EAST, EAST);
        });
    }

    @Environment(value=EnvType.CLIENT)
    public record Corner(CornerCoord xSide, CornerCoord ySide, CornerCoord zSide) {
        public Vector3f get(Vector3fc from, Vector3fc to) {
            return new Vector3f(this.xSide.get(from, to), this.ySide.get(from, to), this.zSide.get(from, to));
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Corner.class, "xFace;yFace;zFace", "xSide", "ySide", "zSide"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Corner.class, "xFace;yFace;zFace", "xSide", "ySide", "zSide"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Corner.class, "xFace;yFace;zFace", "xSide", "ySide", "zSide"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class CornerCoord
    extends Enum<CornerCoord> {
        public static final /* enum */ CornerCoord MIN_X = new CornerCoord();
        public static final /* enum */ CornerCoord MIN_Y = new CornerCoord();
        public static final /* enum */ CornerCoord MIN_Z = new CornerCoord();
        public static final /* enum */ CornerCoord MAX_X = new CornerCoord();
        public static final /* enum */ CornerCoord MAX_Y = new CornerCoord();
        public static final /* enum */ CornerCoord MAX_Z = new CornerCoord();
        private static final /* synthetic */ CornerCoord[] field_64565;

        public static CornerCoord[] values() {
            return (CornerCoord[])field_64565.clone();
        }

        public static CornerCoord valueOf(String string) {
            return Enum.valueOf(CornerCoord.class, string);
        }

        public float get(Vector3fc from, Vector3fc to) {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> from.x();
                case 1 -> from.y();
                case 2 -> from.z();
                case 3 -> to.x();
                case 4 -> to.y();
                case 5 -> to.z();
            };
        }

        public float get(float fromX, float fromY, float fromZ, float toX, float toY, float toZ) {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> fromX;
                case 1 -> fromY;
                case 2 -> fromZ;
                case 3 -> toX;
                case 4 -> toY;
                case 5 -> toZ;
            };
        }

        private static /* synthetic */ CornerCoord[] method_76643() {
            return new CornerCoord[]{MIN_X, MIN_Y, MIN_Z, MAX_X, MAX_Y, MAX_Z};
        }

        static {
            field_64565 = CornerCoord.method_76643();
        }
    }
}
