/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.GlyphRect;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public interface TextDrawable {
    public void render(Matrix4f var1, VertexConsumer var2, int var3, boolean var4);

    public RenderLayer getRenderLayer(TextRenderer.TextLayerType var1);

    public GpuTextureView textureView();

    public RenderPipeline getPipeline();

    public float getEffectiveMinX();

    public float getEffectiveMinY();

    public float getEffectiveMaxX();

    public float getEffectiveMaxY();

    @Environment(value=EnvType.CLIENT)
    public static interface DrawnGlyphRect
    extends GlyphRect,
    TextDrawable {
        @Override
        default public float getLeft() {
            return this.getEffectiveMinX();
        }

        @Override
        default public float getTop() {
            return this.getEffectiveMinY();
        }

        @Override
        default public float getRight() {
            return this.getEffectiveMaxX();
        }

        @Override
        default public float getBottom() {
            return this.getEffectiveMaxY();
        }
    }
}
