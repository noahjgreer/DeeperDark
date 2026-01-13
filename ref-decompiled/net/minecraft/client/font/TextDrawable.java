/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextDrawable
 *  net.minecraft.client.font.TextRenderer$TextLayerType
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.VertexConsumer
 *  org.joml.Matrix4f
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
}

