/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3fc
 */
package net.minecraft.util.math;

import net.minecraft.util.math.Box;
import org.joml.Vector3fc;

public static class Box.Builder {
    private float minX = Float.POSITIVE_INFINITY;
    private float minY = Float.POSITIVE_INFINITY;
    private float minZ = Float.POSITIVE_INFINITY;
    private float maxX = Float.NEGATIVE_INFINITY;
    private float maxY = Float.NEGATIVE_INFINITY;
    private float maxZ = Float.NEGATIVE_INFINITY;

    public void encompass(Vector3fc vec) {
        this.minX = Math.min(this.minX, vec.x());
        this.minY = Math.min(this.minY, vec.y());
        this.minZ = Math.min(this.minZ, vec.z());
        this.maxX = Math.max(this.maxX, vec.x());
        this.maxY = Math.max(this.maxY, vec.y());
        this.maxZ = Math.max(this.maxZ, vec.z());
    }

    public Box build() {
        return new Box(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }
}
