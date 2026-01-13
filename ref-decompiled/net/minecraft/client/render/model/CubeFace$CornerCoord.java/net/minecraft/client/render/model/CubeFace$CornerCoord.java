/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public static final class CubeFace.CornerCoord
extends Enum<CubeFace.CornerCoord> {
    public static final /* enum */ CubeFace.CornerCoord MIN_X = new CubeFace.CornerCoord();
    public static final /* enum */ CubeFace.CornerCoord MIN_Y = new CubeFace.CornerCoord();
    public static final /* enum */ CubeFace.CornerCoord MIN_Z = new CubeFace.CornerCoord();
    public static final /* enum */ CubeFace.CornerCoord MAX_X = new CubeFace.CornerCoord();
    public static final /* enum */ CubeFace.CornerCoord MAX_Y = new CubeFace.CornerCoord();
    public static final /* enum */ CubeFace.CornerCoord MAX_Z = new CubeFace.CornerCoord();
    private static final /* synthetic */ CubeFace.CornerCoord[] field_64565;

    public static CubeFace.CornerCoord[] values() {
        return (CubeFace.CornerCoord[])field_64565.clone();
    }

    public static CubeFace.CornerCoord valueOf(String string) {
        return Enum.valueOf(CubeFace.CornerCoord.class, string);
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

    private static /* synthetic */ CubeFace.CornerCoord[] method_76643() {
        return new CubeFace.CornerCoord[]{MIN_X, MIN_Y, MIN_Z, MAX_X, MAX_Y, MAX_Z};
    }

    static {
        field_64565 = CubeFace.CornerCoord.method_76643();
    }
}
