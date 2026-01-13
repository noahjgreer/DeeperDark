/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
record Animation.TransformationEntry(ModelPart part, Transformation.Target target, Keyframe[] keyframes) {
    public void apply(float runningSeconds, float scale, Vector3f vec) {
        int i = Math.max(0, MathHelper.binarySearch(0, this.keyframes.length, index -> runningSeconds <= this.keyframes[index].timestamp()) - 1);
        int j = Math.min(this.keyframes.length - 1, i + 1);
        Keyframe keyframe = this.keyframes[i];
        Keyframe keyframe2 = this.keyframes[j];
        float f = runningSeconds - keyframe.timestamp();
        float g = j != i ? MathHelper.clamp(f / (keyframe2.timestamp() - keyframe.timestamp()), 0.0f, 1.0f) : 0.0f;
        keyframe2.interpolation().apply(vec, g, this.keyframes, i, j, scale);
        this.target.apply(this.part, vec);
    }
}
