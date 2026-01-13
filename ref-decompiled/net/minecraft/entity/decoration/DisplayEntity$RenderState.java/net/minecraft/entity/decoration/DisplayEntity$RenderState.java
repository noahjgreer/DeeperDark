/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.decoration;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.math.AffineTransformation;

public static final class DisplayEntity.RenderState
extends Record {
    final DisplayEntity.AbstractInterpolator<AffineTransformation> transformation;
    private final DisplayEntity.BillboardMode billboardConstraints;
    private final int brightnessOverride;
    final DisplayEntity.FloatLerper shadowRadius;
    final DisplayEntity.FloatLerper shadowStrength;
    private final int glowColorOverride;

    public DisplayEntity.RenderState(DisplayEntity.AbstractInterpolator<AffineTransformation> transformation, DisplayEntity.BillboardMode billboardConstraints, int brightnessOverride, DisplayEntity.FloatLerper shadowRadius, DisplayEntity.FloatLerper shadowStrength, int glowColorOverride) {
        this.transformation = transformation;
        this.billboardConstraints = billboardConstraints;
        this.brightnessOverride = brightnessOverride;
        this.shadowRadius = shadowRadius;
        this.shadowStrength = shadowStrength;
        this.glowColorOverride = glowColorOverride;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{DisplayEntity.RenderState.class, "transformation;billboardConstraints;brightnessOverride;shadowRadius;shadowStrength;glowColorOverride", "transformation", "billboardConstraints", "brightnessOverride", "shadowRadius", "shadowStrength", "glowColorOverride"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DisplayEntity.RenderState.class, "transformation;billboardConstraints;brightnessOverride;shadowRadius;shadowStrength;glowColorOverride", "transformation", "billboardConstraints", "brightnessOverride", "shadowRadius", "shadowStrength", "glowColorOverride"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DisplayEntity.RenderState.class, "transformation;billboardConstraints;brightnessOverride;shadowRadius;shadowStrength;glowColorOverride", "transformation", "billboardConstraints", "brightnessOverride", "shadowRadius", "shadowStrength", "glowColorOverride"}, this, object);
    }

    public DisplayEntity.AbstractInterpolator<AffineTransformation> transformation() {
        return this.transformation;
    }

    public DisplayEntity.BillboardMode billboardConstraints() {
        return this.billboardConstraints;
    }

    public int brightnessOverride() {
        return this.brightnessOverride;
    }

    public DisplayEntity.FloatLerper shadowRadius() {
        return this.shadowRadius;
    }

    public DisplayEntity.FloatLerper shadowStrength() {
        return this.shadowStrength;
    }

    public int glowColorOverride() {
        return this.glowColorOverride;
    }
}
