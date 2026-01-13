/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.animation.Keyframe
 *  net.minecraft.client.render.entity.animation.Transformation$Interpolation
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.animation.Transformation;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public record Keyframe(float timestamp, Vector3fc preTarget, Vector3fc postTarget, Transformation.Interpolation interpolation) {
    private final float timestamp;
    private final Vector3fc preTarget;
    private final Vector3fc postTarget;
    private final Transformation.Interpolation interpolation;

    public Keyframe(float f, Vector3fc vector3fc, Transformation.Interpolation interpolation) {
        this(f, vector3fc, vector3fc, interpolation);
    }

    public Keyframe(float timestamp, Vector3fc preTarget, Vector3fc postTarget, Transformation.Interpolation interpolation) {
        this.timestamp = timestamp;
        this.preTarget = preTarget;
        this.postTarget = postTarget;
        this.interpolation = interpolation;
    }

    public float timestamp() {
        return this.timestamp;
    }

    public Vector3fc preTarget() {
        return this.preTarget;
    }

    public Vector3fc postTarget() {
        return this.postTarget;
    }

    public Transformation.Interpolation interpolation() {
        return this.interpolation;
    }
}

