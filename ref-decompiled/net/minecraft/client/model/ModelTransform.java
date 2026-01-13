/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelTransform
 */
package net.minecraft.client.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public record ModelTransform(float x, float y, float z, float pitch, float yaw, float roll, float xScale, float yScale, float zScale) {
    private final float x;
    private final float y;
    private final float z;
    private final float pitch;
    private final float yaw;
    private final float roll;
    private final float xScale;
    private final float yScale;
    private final float zScale;
    public static final ModelTransform NONE = ModelTransform.of((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);

    public ModelTransform(float pivotX, float pivotY, float pivotZ, float pitch, float yaw, float roll, float xScale, float yScale, float zScale) {
        this.x = pivotX;
        this.y = pivotY;
        this.z = pivotZ;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
        this.xScale = xScale;
        this.yScale = yScale;
        this.zScale = zScale;
    }

    public static ModelTransform origin(float x, float y, float z) {
        return ModelTransform.of((float)x, (float)y, (float)z, (float)0.0f, (float)0.0f, (float)0.0f);
    }

    public static ModelTransform rotation(float pitch, float yaw, float roll) {
        return ModelTransform.of((float)0.0f, (float)0.0f, (float)0.0f, (float)pitch, (float)yaw, (float)roll);
    }

    public static ModelTransform of(float originX, float originY, float originZ, float pitch, float yaw, float roll) {
        return new ModelTransform(originX, originY, originZ, pitch, yaw, roll, 1.0f, 1.0f, 1.0f);
    }

    public ModelTransform moveOrigin(float x, float y, float z) {
        return new ModelTransform(this.x + x, this.y + y, this.z + z, this.pitch, this.yaw, this.roll, this.xScale, this.yScale, this.zScale);
    }

    public ModelTransform withScale(float scale) {
        return new ModelTransform(this.x, this.y, this.z, this.pitch, this.yaw, this.roll, scale, scale, scale);
    }

    public ModelTransform scaled(float scale) {
        if (scale == 1.0f) {
            return this;
        }
        return this.scaled(scale, scale, scale);
    }

    public ModelTransform scaled(float xScale, float yScale, float zScale) {
        return new ModelTransform(this.x * xScale, this.y * yScale, this.z * zScale, this.pitch, this.yaw, this.roll, this.xScale * xScale, this.yScale * yScale, this.zScale * zScale);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ModelTransform.class, "x;y;z;xRot;yRot;zRot;xScale;yScale;zScale", "x", "y", "z", "pitch", "yaw", "roll", "xScale", "yScale", "zScale"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ModelTransform.class, "x;y;z;xRot;yRot;zRot;xScale;yScale;zScale", "x", "y", "z", "pitch", "yaw", "roll", "xScale", "yScale", "zScale"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ModelTransform.class, "x;y;z;xRot;yRot;zRot;xScale;yScale;zScale", "x", "y", "z", "pitch", "yaw", "roll", "xScale", "yScale", "zScale"}, this, object);
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float z() {
        return this.z;
    }

    public float pitch() {
        return this.pitch;
    }

    public float yaw() {
        return this.yaw;
    }

    public float roll() {
        return this.roll;
    }

    public float xScale() {
        return this.xScale;
    }

    public float yScale() {
        return this.yScale;
    }

    public float zScale() {
        return this.zScale;
    }
}

