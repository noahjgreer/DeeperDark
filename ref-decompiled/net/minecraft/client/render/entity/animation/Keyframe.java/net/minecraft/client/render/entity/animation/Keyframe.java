/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.animation.Transformation;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public record Keyframe(float timestamp, Vector3fc preTarget, Vector3fc postTarget, Transformation.Interpolation interpolation) {
    public Keyframe(float f, Vector3fc vector3fc, Transformation.Interpolation interpolation) {
        this(f, vector3fc, vector3fc, interpolation);
    }
}
