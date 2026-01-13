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
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public static class Transformation.Interpolations {
    public static final Transformation.Interpolation LINEAR = (dest, delta, keyframes, start, end, scale) -> {
        Vector3fc vector3fc = keyframes[start].postTarget();
        Vector3fc vector3fc2 = keyframes[end].preTarget();
        return vector3fc.lerp(vector3fc2, delta, dest).mul(scale);
    };
    public static final Transformation.Interpolation CUBIC = (dest, delta, keyframes, start, end, scale) -> {
        Vector3fc vector3fc = keyframes[Math.max(0, start - 1)].postTarget();
        Vector3fc vector3fc2 = keyframes[start].postTarget();
        Vector3fc vector3fc3 = keyframes[end].postTarget();
        Vector3fc vector3fc4 = keyframes[Math.min(keyframes.length - 1, end + 1)].postTarget();
        dest.set(MathHelper.catmullRom(delta, vector3fc.x(), vector3fc2.x(), vector3fc3.x(), vector3fc4.x()) * scale, MathHelper.catmullRom(delta, vector3fc.y(), vector3fc2.y(), vector3fc3.y(), vector3fc4.y()) * scale, MathHelper.catmullRom(delta, vector3fc.z(), vector3fc2.z(), vector3fc3.z(), vector3fc4.z()) * scale);
        return dest;
    };
}
