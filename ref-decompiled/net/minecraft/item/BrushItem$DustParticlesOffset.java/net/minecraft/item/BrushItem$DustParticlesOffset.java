/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

record BrushItem.DustParticlesOffset(double xd, double yd, double zd) {
    private static final double field_42685 = 1.0;
    private static final double field_42686 = 0.1;

    public static BrushItem.DustParticlesOffset fromSide(Vec3d userRotation, Direction side) {
        double d = 0.0;
        return switch (side) {
            default -> throw new MatchException(null, null);
            case Direction.DOWN, Direction.UP -> new BrushItem.DustParticlesOffset(userRotation.getZ(), 0.0, -userRotation.getX());
            case Direction.NORTH -> new BrushItem.DustParticlesOffset(1.0, 0.0, -0.1);
            case Direction.SOUTH -> new BrushItem.DustParticlesOffset(-1.0, 0.0, 0.1);
            case Direction.WEST -> new BrushItem.DustParticlesOffset(-0.1, 0.0, -1.0);
            case Direction.EAST -> new BrushItem.DustParticlesOffset(0.1, 0.0, 1.0);
        };
    }
}
