/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.animation.Keyframe
 *  net.minecraft.client.render.entity.animation.Transformation
 *  net.minecraft.client.render.entity.animation.Transformation$Target
 */
package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

@Environment(value=EnvType.CLIENT)
public record Transformation(Target target, Keyframe[] keyframes) {
    private final Target target;
    private final Keyframe[] keyframes;

    public Transformation(Target target, Keyframe ... keyframes) {
        this.target = target;
        this.keyframes = keyframes;
    }

    public Target target() {
        return this.target;
    }

    public Keyframe[] keyframes() {
        return this.keyframes;
    }
}

