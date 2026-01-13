/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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

@Environment(value=EnvType.CLIENT)
public record TiledTexturedQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int tileWidth, int tileHeight, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SimpleGuiElementRenderState
{
    public TiledTexturedQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int tileWidth, int tileHeight, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color, @Nullable ScreenRect scissorArea) {
        this(pipeline, textureSetup, pose, tileWidth, tileHeight, x0, y0, x1, y1, u0, u1, v0, v1, color, scissorArea, TiledTexturedQuadGuiElementRenderState.createBounds(x0, y0, x1, y1, pose, scissorArea));
    }

    @Override
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
                f = MathHelper.lerp((float)l / (float)this.tileWidth(), this.u0(), this.u1());
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
                    g = MathHelper.lerp((float)o / (float)this.tileHeight(), this.v0(), this.v1());
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
}
