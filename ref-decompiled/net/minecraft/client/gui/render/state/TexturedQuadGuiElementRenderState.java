/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.SimpleGuiElementRenderState
 *  net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.texture.TextureSetup
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public record TexturedQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x1, int y1, int x2, int y2, float u1, float u2, float v1, float v2, int color, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SimpleGuiElementRenderState
{
    private final RenderPipeline pipeline;
    private final TextureSetup textureSetup;
    private final Matrix3x2f pose;
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final float u1;
    private final float u2;
    private final float v1;
    private final float v2;
    private final int color;
    private final @Nullable ScreenRect scissorArea;
    private final @Nullable ScreenRect bounds;

    public TexturedQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x1, int y1, int x2, int y2, float u1, float u2, float v1, float v2, int color, @Nullable ScreenRect scissorArea) {
        this(pipeline, textureSetup, pose, x1, y1, x2, y2, u1, u2, v1, v2, color, scissorArea, TexturedQuadGuiElementRenderState.createBounds((int)x1, (int)y1, (int)x2, (int)y2, (Matrix3x2f)pose, (ScreenRect)scissorArea));
    }

    public TexturedQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x1, int y1, int x2, int y2, float u1, float u2, float v1, float v2, int color, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) {
        this.pipeline = pipeline;
        this.textureSetup = textureSetup;
        this.pose = pose;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.u1 = u1;
        this.u2 = u2;
        this.v1 = v1;
        this.v2 = v2;
        this.color = color;
        this.scissorArea = scissorArea;
        this.bounds = bounds;
    }

    public void setupVertices(VertexConsumer vertices) {
        vertices.vertex((Matrix3x2fc)this.pose(), (float)this.x1(), (float)this.y1()).texture(this.u1(), this.v1()).color(this.color());
        vertices.vertex((Matrix3x2fc)this.pose(), (float)this.x1(), (float)this.y2()).texture(this.u1(), this.v2()).color(this.color());
        vertices.vertex((Matrix3x2fc)this.pose(), (float)this.x2(), (float)this.y2()).texture(this.u2(), this.v2()).color(this.color());
        vertices.vertex((Matrix3x2fc)this.pose(), (float)this.x2(), (float)this.y1()).texture(this.u2(), this.v1()).color(this.color());
    }

    private static @Nullable ScreenRect createBounds(int x1, int y1, int x2, int y2, Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
        ScreenRect screenRect = new ScreenRect(x1, y1, x2 - x1, y2 - y1).transformEachVertex((Matrix3x2fc)pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{TexturedQuadGuiElementRenderState.class, "pipeline;textureSetup;pose;x0;y0;x1;y1;u0;u1;v0;v1;color;scissorArea;bounds", "pipeline", "textureSetup", "pose", "x1", "y1", "x2", "y2", "u1", "u2", "v1", "v2", "color", "scissorArea", "bounds"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TexturedQuadGuiElementRenderState.class, "pipeline;textureSetup;pose;x0;y0;x1;y1;u0;u1;v0;v1;color;scissorArea;bounds", "pipeline", "textureSetup", "pose", "x1", "y1", "x2", "y2", "u1", "u2", "v1", "v2", "color", "scissorArea", "bounds"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TexturedQuadGuiElementRenderState.class, "pipeline;textureSetup;pose;x0;y0;x1;y1;u0;u1;v0;v1;color;scissorArea;bounds", "pipeline", "textureSetup", "pose", "x1", "y1", "x2", "y2", "u1", "u2", "v1", "v2", "color", "scissorArea", "bounds"}, this, object);
    }

    public RenderPipeline pipeline() {
        return this.pipeline;
    }

    public TextureSetup textureSetup() {
        return this.textureSetup;
    }

    public Matrix3x2f pose() {
        return this.pose;
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

    public float u1() {
        return this.u1;
    }

    public float u2() {
        return this.u2;
    }

    public float v1() {
        return this.v1;
    }

    public float v2() {
        return this.v2;
    }

    public int color() {
        return this.color;
    }

    public @Nullable ScreenRect scissorArea() {
        return this.scissorArea;
    }

    public @Nullable ScreenRect bounds() {
        return this.bounds;
    }
}

