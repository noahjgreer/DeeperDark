/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.entity.animation.Animation
 *  net.minecraft.client.render.entity.animation.Animation$TransformationEntry
 *  net.minecraft.client.render.entity.animation.AnimationDefinition
 *  net.minecraft.client.render.entity.animation.Transformation
 *  net.minecraft.entity.AnimationState
 *  org.joml.Vector3f
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationDefinition;
import net.minecraft.client.render.entity.animation.Transformation;
import net.minecraft.entity.AnimationState;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Animation {
    private final AnimationDefinition definition;
    private final List<TransformationEntry> entries;

    private Animation(AnimationDefinition definition, List<TransformationEntry> entries) {
        this.definition = definition;
        this.entries = entries;
    }

    /*
     * Issues handling annotations - annotations may be inaccurate
     */
    static Animation of(ModelPart root, AnimationDefinition definition) {
        ArrayList<TransformationEntry> list = new ArrayList<TransformationEntry>();
        @Nullable Function function = root.createPartGetter();
        for (Map.Entry entry : definition.boneAnimations().entrySet()) {
            String string = (String)entry.getKey();
            List list2 = (List)entry.getValue();
            ModelPart modelPart = (ModelPart)function.apply(string);
            if (modelPart == null) {
                throw new IllegalArgumentException("Cannot animate " + string + ", which does not exist in model");
            }
            for (Transformation transformation : list2) {
                list.add(new TransformationEntry(modelPart, transformation.target(), transformation.keyframes()));
            }
        }
        return new Animation(definition, List.copyOf(list));
    }

    public void applyStatic() {
        this.apply(0L, 1.0f);
    }

    public void applyWalking(float limbSwingAnimationProgress, float limbSwingAmplitude, float f, float g) {
        long l = (long)(limbSwingAnimationProgress * 50.0f * f);
        float h = Math.min(limbSwingAmplitude * g, 1.0f);
        this.apply(l, h);
    }

    public void apply(AnimationState animationState, float age) {
        this.apply(animationState, age, 1.0f);
    }

    public void apply(AnimationState animationState, float age, float speedMultiplier) {
        animationState.run(state -> this.apply((long)((float)state.getTimeInMilliseconds(age) * speedMultiplier), 1.0f));
    }

    public void apply(long timeInMilliseconds, float scale) {
        float f = this.getRunningSeconds(timeInMilliseconds);
        Vector3f vector3f = new Vector3f();
        for (TransformationEntry transformationEntry : this.entries) {
            transformationEntry.apply(f, scale, vector3f);
        }
    }

    private float getRunningSeconds(long timeInMilliseconds) {
        float f = (float)timeInMilliseconds / 1000.0f;
        return this.definition.looping() ? f % this.definition.lengthInSeconds() : f;
    }
}

