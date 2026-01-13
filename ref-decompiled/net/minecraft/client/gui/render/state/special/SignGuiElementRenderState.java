/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.WoodType
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.special.SignGuiElementRenderState
 *  net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState
 *  net.minecraft.client.model.Model$SinglePartModel
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render.state.special;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WoodType;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.model.Model;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record SignGuiElementRenderState(Model.SinglePartModel signModel, WoodType woodType, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SpecialGuiElementRenderState
{
    private final Model.SinglePartModel signModel;
    private final WoodType woodType;
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final float scale;
    private final @Nullable ScreenRect scissorArea;
    private final @Nullable ScreenRect bounds;

    public SignGuiElementRenderState(Model.SinglePartModel part, WoodType woodType, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea) {
        this(part, woodType, x1, y1, x2, y2, scale, scissorArea, SpecialGuiElementRenderState.createBounds((int)x1, (int)y1, (int)x2, (int)y2, (ScreenRect)scissorArea));
    }

    public SignGuiElementRenderState(Model.SinglePartModel signModel, WoodType woodType, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) {
        this.signModel = signModel;
        this.woodType = woodType;
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
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SignGuiElementRenderState.class, "signModel;woodType;x0;y0;x1;y1;scale;scissorArea;bounds", "signModel", "woodType", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SignGuiElementRenderState.class, "signModel;woodType;x0;y0;x1;y1;scale;scissorArea;bounds", "signModel", "woodType", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SignGuiElementRenderState.class, "signModel;woodType;x0;y0;x1;y1;scale;scissorArea;bounds", "signModel", "woodType", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this, object);
    }

    public Model.SinglePartModel signModel() {
        return this.signModel;
    }

    public WoodType woodType() {
        return this.woodType;
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

