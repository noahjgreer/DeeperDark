/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.special.ProfilerChartGuiElementRenderState
 *  net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState
 *  net.minecraft.util.profiler.ProfilerTiming
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render.state.special;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.util.profiler.ProfilerTiming;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ProfilerChartGuiElementRenderState(List<ProfilerTiming> chartData, int x1, int y1, int x2, int y2, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SpecialGuiElementRenderState
{
    private final List<ProfilerTiming> chartData;
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final @Nullable ScreenRect scissorArea;
    private final @Nullable ScreenRect bounds;

    public ProfilerChartGuiElementRenderState(List<ProfilerTiming> chartData, int x1, int y1, int x2, int y2, @Nullable ScreenRect scissorArea) {
        this(chartData, x1, y1, x2, y2, scissorArea, SpecialGuiElementRenderState.createBounds((int)x1, (int)y1, (int)x2, (int)y2, (ScreenRect)scissorArea));
    }

    public ProfilerChartGuiElementRenderState(List<ProfilerTiming> chartData, int x1, int y1, int x2, int y2, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) {
        this.chartData = chartData;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.scissorArea = scissorArea;
        this.bounds = bounds;
    }

    public float scale() {
        return 1.0f;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ProfilerChartGuiElementRenderState.class, "chartData;x0;y0;x1;y1;scissorArea;bounds", "chartData", "x1", "y1", "x2", "y2", "scissorArea", "bounds"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ProfilerChartGuiElementRenderState.class, "chartData;x0;y0;x1;y1;scissorArea;bounds", "chartData", "x1", "y1", "x2", "y2", "scissorArea", "bounds"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ProfilerChartGuiElementRenderState.class, "chartData;x0;y0;x1;y1;scissorArea;bounds", "chartData", "x1", "y1", "x2", "y2", "scissorArea", "bounds"}, this, object);
    }

    public List<ProfilerTiming> chartData() {
        return this.chartData;
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

