/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.SimpleGuiElementRenderState
 *  net.minecraft.client.gui.render.state.TiledTexturedQuadGuiElementRenderState
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.texture.TextureSetup
 *  net.minecraft.util.math.MathHelper
 *  org.joml.Matrix3x2f
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
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public record TiledTexturedQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int tileWidth, int tileHeight, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SimpleGuiElementRenderState
{
    private final RenderPipeline pipeline;
    private final TextureSetup textureSetup;
    private final Matrix3x2f pose;
    private final int tileWidth;
    private final int tileHeight;
    private final int x0;
    private final int y0;
    private final int x1;
    private final int y1;
    private final float u0;
    private final float u1;
    private final float v0;
    private final float v1;
    private final int color;
    private final @Nullable ScreenRect scissorArea;
    private final @Nullable ScreenRect bounds;

    public TiledTexturedQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int tileWidth, int tileHeight, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color, @Nullable ScreenRect scissorArea) {
        this(pipeline, textureSetup, pose, tileWidth, tileHeight, x0, y0, x1, y1, u0, u1, v0, v1, color, scissorArea, TiledTexturedQuadGuiElementRenderState.createBounds((int)x0, (int)y0, (int)x1, (int)y1, (Matrix3x2f)pose, (ScreenRect)scissorArea));
    }

    public TiledTexturedQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int tileWidth, int tileHeight, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) {
        this.pipeline = pipeline;
        this.textureSetup = textureSetup;
        this.pose = pose;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.u0 = u0;
        this.u1 = u1;
        this.v0 = v0;
        this.v1 = v1;
        this.color = color;
        this.scissorArea = scissorArea;
        this.bounds = bounds;
    }

    public void setupVertices(VertexConsumer vertices) {
        int i = this.x1() - this.x0();
        int j = this.y1() - this.y0();
        for (int k = 0; k < i; k += this.tileWidth()) {
            float f;
            int m;
            int l = i - k;
            if (this.tileWidth() <= l) {
                m = this.tileWidth();
                f = this.u1();
            } else {
                m = l;
                f = MathHelper.lerp((float)((float)l / (float)this.tileWidth()), (float)this.u0(), (float)this.u1());
            }
            for (int n = 0; n < j; n += this.tileHeight()) {
                float g;
                int p;
                int o = j - n;
                if (this.tileHeight() <= o) {
                    p = this.tileHeight();
                    g = this.v1();
                } else {
                    p = o;
                    g = MathHelper.lerp((float)((float)o / (float)this.tileHeight()), (float)this.v0(), (float)this.v1());
                }
                int q = this.x0() + k;
                int r = this.x0() + k + m;
                int s = this.y0() + n;
                int t = this.y0() + n + p;
                vertices.vertex((Matrix3x2fc)this.pose(), (float)q, (float)s).texture(this.u0(), this.v0()).color(this.color());
                vertices.vertex((Matrix3x2fc)this.pose(), (float)q, (float)t).texture(this.u0(), g).color(this.color());
                vertices.vertex((Matrix3x2fc)this.pose(), (float)r, (float)t).texture(f, g).color(this.color());
                vertices.vertex((Matrix3x2fc)this.pose(), (float)r, (float)s).texture(f, this.v0()).color(this.color());
            }
        }
    }

    private static @Nullable ScreenRect createBounds(int x1, int y1, int x2, int y2, Matrix3x2f pose, @Nullable ScreenRect rect) {
        ScreenRect screenRect = new ScreenRect(x1, y1, x2 - x1, y2 - y1).transformEachVertex((Matrix3x2fc)pose);
        return rect != null ? rect.intersection(screenRect) : screenRect;
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

    public int tileWidth() {
        return this.tileWidth;
    }

    public int tileHeight() {
        return this.tileHeight;
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

    public float u0() {
        return this.u0;
    }

    public float u1() {
        return this.u1;
    }

    public float v0() {
        return this.v0;
    }

    public float v1() {
        return this.v1;
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

