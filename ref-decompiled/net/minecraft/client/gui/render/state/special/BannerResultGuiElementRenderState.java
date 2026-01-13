/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.special.BannerResultGuiElementRenderState
 *  net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState
 *  net.minecraft.client.render.block.entity.model.BannerFlagBlockModel
 *  net.minecraft.component.type.BannerPatternsComponent
 *  net.minecraft.util.DyeColor
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render.state.special;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.render.block.entity.model.BannerFlagBlockModel;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.util.DyeColor;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record BannerResultGuiElementRenderState(BannerFlagBlockModel flag, DyeColor baseColor, BannerPatternsComponent resultBannerPatterns, int x1, int y1, int x2, int y2, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SpecialGuiElementRenderState
{
    private final BannerFlagBlockModel flag;
    private final DyeColor baseColor;
    private final BannerPatternsComponent resultBannerPatterns;
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final @Nullable ScreenRect scissorArea;
    private final @Nullable ScreenRect bounds;

    public BannerResultGuiElementRenderState(BannerFlagBlockModel model, DyeColor color, BannerPatternsComponent bannerPatterns, int x1, int y1, int x2, int y2, @Nullable ScreenRect scissorArea) {
        this(model, color, bannerPatterns, x1, y1, x2, y2, scissorArea, SpecialGuiElementRenderState.createBounds((int)x1, (int)y1, (int)x2, (int)y2, (ScreenRect)scissorArea));
    }

    public BannerResultGuiElementRenderState(BannerFlagBlockModel flag, DyeColor baseColor, BannerPatternsComponent resultBannerPatterns, int x1, int y1, int x2, int y2, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) {
        this.flag = flag;
        this.baseColor = baseColor;
        this.resultBannerPatterns = resultBannerPatterns;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.scissorArea = scissorArea;
        this.bounds = bounds;
    }

    public float scale() {
        return 16.0f;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BannerResultGuiElementRenderState.class, "flag;baseColor;resultBannerPatterns;x0;y0;x1;y1;scissorArea;bounds", "flag", "baseColor", "resultBannerPatterns", "x1", "y1", "x2", "y2", "scissorArea", "bounds"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BannerResultGuiElementRenderState.class, "flag;baseColor;resultBannerPatterns;x0;y0;x1;y1;scissorArea;bounds", "flag", "baseColor", "resultBannerPatterns", "x1", "y1", "x2", "y2", "scissorArea", "bounds"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BannerResultGuiElementRenderState.class, "flag;baseColor;resultBannerPatterns;x0;y0;x1;y1;scissorArea;bounds", "flag", "baseColor", "resultBannerPatterns", "x1", "y1", "x2", "y2", "scissorArea", "bounds"}, this, object);
    }

    public BannerFlagBlockModel flag() {
        return this.flag;
    }

    public DyeColor baseColor() {
        return this.baseColor;
    }

    public BannerPatternsComponent resultBannerPatterns() {
        return this.resultBannerPatterns;
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

    public @Nullable ScreenRect scissorArea() {
        return this.scissorArea;
    }

    public @Nullable ScreenRect bounds() {
        return this.bounds;
    }
}

