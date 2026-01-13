/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.special.EntityGuiElementRenderState
 *  net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render.state.special;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record EntityGuiElementRenderState(EntityRenderState renderState, Vector3f translation, Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SpecialGuiElementRenderState
{
    private final EntityRenderState renderState;
    private final Vector3f translation;
    private final Quaternionf rotation;
    private final @Nullable Quaternionf overrideCameraAngle;
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final float scale;
    private final @Nullable ScreenRect scissorArea;
    private final @Nullable ScreenRect bounds;

    public EntityGuiElementRenderState(EntityRenderState renderState, Vector3f translation, Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea) {
        this(renderState, translation, rotation, overrideCameraAngle, x1, y1, x2, y2, scale, scissorArea, SpecialGuiElementRenderState.createBounds((int)x1, (int)y1, (int)x2, (int)y2, (ScreenRect)scissorArea));
    }

    public EntityGuiElementRenderState(EntityRenderState renderState, Vector3f translation, Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) {
        this.renderState = renderState;
        this.translation = translation;
        this.rotation = rotation;
        this.overrideCameraAngle = overrideCameraAngle;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.scale = scale;
        this.scissorArea = scissorArea;
        this.bounds = bounds;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{EntityGuiElementRenderState.class, "renderState;translation;rotation;overrideCameraAngle;x0;y0;x1;y1;scale;scissorArea;bounds", "renderState", "translation", "rotation", "overrideCameraAngle", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{EntityGuiElementRenderState.class, "renderState;translation;rotation;overrideCameraAngle;x0;y0;x1;y1;scale;scissorArea;bounds", "renderState", "translation", "rotation", "overrideCameraAngle", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{EntityGuiElementRenderState.class, "renderState;translation;rotation;overrideCameraAngle;x0;y0;x1;y1;scale;scissorArea;bounds", "renderState", "translation", "rotation", "overrideCameraAngle", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this, object);
    }

    public EntityRenderState renderState() {
        return this.renderState;
    }

    public Vector3f translation() {
        return this.translation;
    }

    public Quaternionf rotation() {
        return this.rotation;
    }

    public @Nullable Quaternionf overrideCameraAngle() {
        return this.overrideCameraAngle;
    }

    public int x1() {
        return this.x1;
    }

    public int y1() {
        return this.y1;
    }

    public int x2() {
        return this.x2;
    }

    public int y2() {
        return this.y2;
    }

    public float scale() {
        return this.scale;
    }

    public @Nullable ScreenRect scissorArea() {
        return this.scissorArea;
    }

    public @Nullable ScreenRect bounds() {
        return this.bounds;
    }
}

