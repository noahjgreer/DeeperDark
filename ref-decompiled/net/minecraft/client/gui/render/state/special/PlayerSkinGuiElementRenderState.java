/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.special.PlayerSkinGuiElementRenderState
 *  net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render.state.special;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record PlayerSkinGuiElementRenderState(PlayerEntityModel playerModel, Identifier texture, float xRotation, float yRotation, float yPivot, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SpecialGuiElementRenderState
{
    private final PlayerEntityModel playerModel;
    private final Identifier texture;
    private final float xRotation;
    private final float yRotation;
    private final float yPivot;
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final float scale;
    private final @Nullable ScreenRect scissorArea;
    private final @Nullable ScreenRect bounds;

    public PlayerSkinGuiElementRenderState(PlayerEntityModel model, Identifier texture, float xRotation, float yRotation, float yPivot, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea) {
        this(model, texture, xRotation, yRotation, yPivot, x1, y1, x2, y2, scale, scissorArea, SpecialGuiElementRenderState.createBounds((int)x1, (int)y1, (int)x2, (int)y2, (ScreenRect)scissorArea));
    }

    public PlayerSkinGuiElementRenderState(PlayerEntityModel playerModel, Identifier texture, float xRotation, float yRotation, float yPivot, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) {
        this.playerModel = playerModel;
        this.texture = texture;
        this.xRotation = xRotation;
        this.yRotation = yRotation;
        this.yPivot = yPivot;
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
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PlayerSkinGuiElementRenderState.class, "playerModel;texture;rotationX;rotationY;pivotY;x0;y0;x1;y1;scale;scissorArea;bounds", "playerModel", "texture", "xRotation", "yRotation", "yPivot", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PlayerSkinGuiElementRenderState.class, "playerModel;texture;rotationX;rotationY;pivotY;x0;y0;x1;y1;scale;scissorArea;bounds", "playerModel", "texture", "xRotation", "yRotation", "yPivot", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PlayerSkinGuiElementRenderState.class, "playerModel;texture;rotationX;rotationY;pivotY;x0;y0;x1;y1;scale;scissorArea;bounds", "playerModel", "texture", "xRotation", "yRotation", "yPivot", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this, object);
    }

    public PlayerEntityModel playerModel() {
        return this.playerModel;
    }

    public Identifier texture() {
        return this.texture;
    }

    public float xRotation() {
        return this.xRotation;
    }

    public float yRotation() {
        return this.yRotation;
    }

    public float yPivot() {
        return this.yPivot;
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

