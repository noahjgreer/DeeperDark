/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public record Transformation(Target target, Keyframe[] keyframes) {

    @Environment(value=EnvType.CLIENT)
    public static interface Target {
        public void apply(ModelPart var1, Vector3f var2);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Interpolations {
        public static final Interpolation LINEAR = (dest, delta, keyframes, start, end, scale) -> {
            Vector3fc vector3fc = keyframes[start].postTarget();
            Vector3fc vector3fc2 = keyframes[end].preTarget();
            return vector3fc.lerp(vector3fc2, delta, dest).mul(scale);
        };
        public static final Interpolation CUBIC = (dest, delta, keyframes, start, end, scale) -> {
            Vector3fc vector3fc = keyframes[Math.max(0, start - 1)].postTarget();
            Vector3fc vector3fc2 = keyframes[start].postTarget();
            Vector3fc vector3fc3 = keyframes[end].postTarget();
            Vector3fc vector3fc4 = keyframes[Math.min(keyframes.length - 1, end + 1)].postTarget();
            dest.set(MathHelper.catmullRom(delta, vector3fc.x(), vector3fc2.x(), vector3fc3.x(), vector3fc4.x()) * scale, MathHelper.catmullRom(delta, vector3fc.y(), vector3fc2.y(), vector3fc3.y(), vector3fc4.y()) * scale, MathHelper.catmullRom(delta, vector3fc.z(), vector3fc2.z(), vector3fc3.z(), vector3fc4.z()) * scale);
            return dest;
        };
    }

    @Environment(value=EnvType.CLIENT)
    public static class Targets {
        public static final Target MOVE_ORIGIN = ModelPart::moveOrigin;
        public static final Target ROTATE = ModelPart::rotate;
        public static final Target SCALE = ModelPart::scale;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Interpolation {
        public Vector3f apply(Vector3f var1, float var2, Keyframe[] var3, int var4, int var5, float var6);
    }
}
