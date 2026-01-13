/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public static class Camera.Projection {
    final Vec3d center;
    private final Vec3d x;
    private final Vec3d y;

    Camera.Projection(Vec3d center, Vec3d x, Vec3d y) {
        this.center = center;
        this.x = x;
        this.y = y;
    }

    public Vec3d getBottomRight() {
        return this.center.add(this.y).add(this.x);
    }

    public Vec3d getTopRight() {
        return this.center.add(this.y).subtract(this.x);
    }

    public Vec3d getBottomLeft() {
        return this.center.subtract(this.y).add(this.x);
    }

    public Vec3d getTopLeft() {
        return this.center.subtract(this.y).subtract(this.x);
    }

    public Vec3d getPosition(float factorX, float factorY) {
        return this.center.add(this.y.multiply(factorY)).subtract(this.x.multiply(factorX));
    }
}
