/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.EntityHitbox
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public record EntityHitbox(double x0, double y0, double z0, double x1, double y1, double z1, float offsetX, float offsetY, float offsetZ, float red, float green, float blue) {
    private final double x0;
    private final double y0;
    private final double z0;
    private final double x1;
    private final double y1;
    private final double z1;
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;
    private final float red;
    private final float green;
    private final float blue;

    public EntityHitbox(double x0, double y0, double z0, double x1, double y1, double z1, float red, float green, float blue) {
        this(x0, y0, z0, x1, y1, z1, 0.0f, 0.0f, 0.0f, red, green, blue);
    }

    public EntityHitbox(double x0, double y0, double z0, double x1, double y1, double z1, float offsetX, float offsetY, float offsetZ, float red, float green, float blue) {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public double x0() {
        return this.x0;
    }

    public double y0() {
        return this.y0;
    }

    public double z0() {
        return this.z0;
    }

    public double x1() {
        return this.x1;
    }

    public double y1() {
        return this.y1;
    }

    public double z1() {
        return this.z1;
    }

    public float offsetX() {
        return this.offsetX;
    }

    public float offsetY() {
        return this.offsetY;
    }

    public float offsetZ() {
        return this.offsetZ;
    }

    public float red() {
        return this.red;
    }

    public float green() {
        return this.green;
    }

    public float blue() {
        return this.blue;
    }
}

