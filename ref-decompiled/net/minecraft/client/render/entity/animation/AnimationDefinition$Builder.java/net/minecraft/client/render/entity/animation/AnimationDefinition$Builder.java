/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.animation;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.animation.AnimationDefinition;
import net.minecraft.client.render.entity.animation.Transformation;

@Environment(value=EnvType.CLIENT)
public static class AnimationDefinition.Builder {
    private final float lengthInSeconds;
    private final Map<String, List<Transformation>> transformations = Maps.newHashMap();
    private boolean looping;

    public static AnimationDefinition.Builder create(float lengthInSeconds) {
        return new AnimationDefinition.Builder(lengthInSeconds);
    }

    private AnimationDefinition.Builder(float lengthInSeconds) {
        this.lengthInSeconds = lengthInSeconds;
    }

    public AnimationDefinition.Builder looping() {
        this.looping = true;
        return this;
    }

    public AnimationDefinition.Builder addBoneAnimation(String name2, Transformation transformation) {
        this.transformations.computeIfAbsent(name2, name -> new ArrayList()).add(transformation);
        return this;
    }

    public AnimationDefinition build() {
        return new AnimationDefinition(this.lengthInSeconds, this.looping, this.transformations);
    }
}
