/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.ColoredQuadGuiElementRenderState
 *  net.minecraft.client.gui.render.state.SimpleGuiElementRenderState
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.texture.TextureSetup
 *  org.joml.Matrix3x2fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public record ColoredQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2fc pose, int x0, int y0, int x1, int y1, int col1, int col2, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SimpleGuiElementRenderState
{
    private final RenderPipeline pipeline;
    private final TextureSetup textureSetup;
    private final Matrix3x2fc pose;
    private final int x0;
    private final int y0;
    private final int x1;
    private final int y1;
    private final int col1;
    private final int col2;
    private final @Nullable ScreenRect scissorArea;
    private final @Nullable ScreenRect bounds;

    public ColoredQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2fc pose, int x0, int y0, int x1, int y1, int col1, int col2, @Nullable ScreenRect scissorArea) {
        this(pipeline, textureSetup, pose, x0, y0, x1, y1, col1, col2, scissorArea, ColoredQuadGuiElementRenderState.createBounds((int)x0, (int)y0, (int)x1, (int)y1, (Matrix3x2fc)pose, (ScreenRect)scissorArea));
    }

    public ColoredQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2fc pose, int x0, int y0, int x1, int y1, int col1, int col2, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) {
        this.pipeline = pipeline;
        this.textureSetup = textureSetup;
        this.pose = pose;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.col1 = col1;
        this.col2 = col2;
        this.scissorArea = scissorArea;
        this.bounds = bounds;
    }

    public void setupVertices(VertexConsumer vertices) {
        vertices.vertex(this.pose(), (float)this.x0(), (float)this.y0()).color(this.col1());
        vertices.vertex(this.pose(), (float)this.x0(), (float)this.y1()).color(this.col2());
        vertices.vertex(this.pose(), (float)this.x1(), (float)this.y1()).color(this.col2());
        vertices.vertex(this.pose(), (float)this.x1(), (float)this.y0()).color(this.col1());
    }

    private static @Nullable ScreenRect createBounds(int x0, int y0, int x1, int y1, Matrix3x2fc pose, @Nullable ScreenRect scissorArea) {
        ScreenRect screenRect = new ScreenRect(x0, y0, x1 - x0, y1 - y0).transformEachVertex(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }

    public RenderPipeline pipeline() {
        return this.pipeline;
    }

    public TextureSetup textureSetup() {
        return this.textureSetup;
    }

    public Matrix3x2fc pose() {
        return this.pose;
    }

    public int x0() {
        return this.x0;
    }

    public int y0() {
        return this.y0;
    }

    public int x1() {
        return this.x1;
    }

    public int y1() {
        return this.y1;
    }

    public int col1() {
        return this.col1;
    }

    public int col2() {
        return this.col2;
    }

    public @Nullable ScreenRect scissorArea() {
        return this.scissorArea;
    }

    public @Nullable ScreenRect bounds() {
        return this.bounds;
    }
}

