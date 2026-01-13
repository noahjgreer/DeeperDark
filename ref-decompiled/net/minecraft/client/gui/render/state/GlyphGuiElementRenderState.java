/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextDrawable
 *  net.minecraft.client.gl.GpuSampler
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.GlyphGuiElementRenderState
 *  net.minecraft.client.gui.render.state.SimpleGuiElementRenderState
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.texture.TextureSetup
 *  org.joml.Matrix3x2fc
 *  org.joml.Matrix4f
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record GlyphGuiElementRenderState(Matrix3x2fc pose, TextDrawable renderable, @Nullable ScreenRect scissorArea) implements SimpleGuiElementRenderState
{
    private final Matrix3x2fc pose;
    private final TextDrawable renderable;
    private final @Nullable ScreenRect scissorArea;

    public GlyphGuiElementRenderState(Matrix3x2fc pose, TextDrawable renderable, @Nullable ScreenRect scissorArea) {
        this.pose = pose;
        this.renderable = renderable;
        this.scissorArea = scissorArea;
    }

    public void setupVertices(VertexConsumer vertices) {
        this.renderable.render(new Matrix4f().mul(this.pose), vertices, 0xF000F0, true);
    }

    public RenderPipeline pipeline() {
        return this.renderable.getPipeline();
    }

    public TextureSetup textureSetup() {
        return TextureSetup.withLightmap((GpuTextureView)this.renderable.textureView(), (GpuSampler)RenderSystem.getSamplerCache().get(FilterMode.NEAREST));
    }

    public @Nullable ScreenRect bounds() {
        return null;
    }

    public Matrix3x2fc pose() {
        return this.pose;
    }

    public TextDrawable renderable() {
        return this.renderable;
    }

    public @Nullable ScreenRect scissorArea() {
        return this.scissorArea;
    }
}

