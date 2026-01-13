/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.GuiElementRenderState
 *  net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState
 *  org.joml.Matrix3x2f
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render.state.special;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface SpecialGuiElementRenderState
extends GuiElementRenderState {
    public static final Matrix3x2f pose = new Matrix3x2f();

    public int x1();

    public int x2();

    public int y1();

    public int y2();

    public float scale();

    default public Matrix3x2f pose() {
        return pose;
    }

    public @Nullable ScreenRect scissorArea();

    public static @Nullable ScreenRect createBounds(int x1, int y1, int x2, int y2, @Nullable ScreenRect scissorArea) {
        ScreenRect screenRect = new ScreenRect(x1, y1, x2 - x1, y2 - y1);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}

