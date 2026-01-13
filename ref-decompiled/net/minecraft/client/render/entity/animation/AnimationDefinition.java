/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.entity.animation.Animation
 *  net.minecraft.client.render.entity.animation.AnimationDefinition
 *  net.minecraft.client.render.entity.animation.Transformation
 */
package net.minecraft.client.render.entity.animation;

import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.Transformation;

@Environment(value=EnvType.CLIENT)
public record AnimationDefinition(float lengthInSeconds, boolean looping, Map<String, List<Transformation>> boneAnimations) {
    private final float lengthInSeconds;
    private final boolean looping;
    private final Map<String, List<Transformation>> boneAnimations;

    public AnimationDefinition(float lengthInSeconds, boolean looping, Map<String, List<Transformation>> boneAnimations) {
        this.lengthInSeconds = lengthInSeconds;
        this.looping = looping;
        this.boneAnimations = boneAnimations;
    }

    public Animation createAnimation(ModelPart root) {
        return Animation.of((ModelPart)root, (AnimationDefinition)this);
    }

    public float lengthInSeconds() {
        return this.lengthInSeconds;
    }

    public boolean looping() {
        return this.looping;
    }

    public Map<String, List<Transformation>> boneAnimations() {
        return this.boneAnimations;
    }
}

