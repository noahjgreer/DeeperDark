/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.ProjectionType
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.textures.TextureFormat
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.GpuSampler
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.render.SpecialGuiElementRenderer
 *  net.minecraft.client.gui.render.state.GuiRenderState
 *  net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState
 *  net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState
 *  net.minecraft.client.render.ProjectionMatrix2
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.texture.TextureSetup
 *  net.minecraft.client.util.math.MatrixStack
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.render.ProjectionMatrix2;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.client.util.math.MatrixStack;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class SpecialGuiElementRenderer<T extends SpecialGuiElementRenderState>
implements AutoCloseable {
    protected final VertexConsumerProvider.Immediate vertexConsumers;
    private @Nullable GpuTexture texture;
    private @Nullable GpuTextureView textureView;
    private @Nullable GpuTexture depthTexture;
    private @Nullable GpuTextureView depthTextureView;
    private final ProjectionMatrix2 projectionMatrix = new ProjectionMatrix2("PIP - " + this.getClass().getSimpleName(), -1000.0f, 1000.0f, true);

    protected SpecialGuiElementRenderer(VertexConsumerProvider.Immediate vertexConsumers) {
        this.vertexConsumers = vertexConsumers;
    }

    public void render(T elementState, GuiRenderState state, int windowScaleFactor) {
        boolean bl;
        int i = (elementState.x2() - elementState.x1()) * windowScaleFactor;
        int j = (elementState.y2() - elementState.y1()) * windowScaleFactor;
        boolean bl2 = bl = this.texture == null || this.texture.getWidth(0) != i || this.texture.getHeight(0) != j;
        if (!bl && this.shouldBypassScaling(elementState)) {
            this.renderElement(elementState, state);
            return;
        }
        this.prepareTextures(bl, i, j);
        RenderSystem.outputColorTextureOverride = this.textureView;
        RenderSystem.outputDepthTextureOverride = this.depthTextureView;
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate((float)i / 2.0f, this.getYOffset(j, windowScaleFactor), 0.0f);
        float f = (float)windowScaleFactor * elementState.scale();
        matrixStack.scale(f, f, -f);
        this.render(elementState, matrixStack);
        this.vertexConsumers.draw();
        RenderSystem.outputColorTextureOverride = null;
        RenderSystem.outputDepthTextureOverride = null;
        this.renderElement(elementState, state);
    }

    protected void renderElement(T element, GuiRenderState state) {
        state.addSimpleElementToCurrentLayer(new TexturedQuadGuiElementRenderState(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, TextureSetup.of((GpuTextureView)this.textureView, (GpuSampler)RenderSystem.getSamplerCache().getRepeated(FilterMode.NEAREST)), element.pose(), element.x1(), element.y1(), element.x2(), element.y2(), 0.0f, 1.0f, 1.0f, 0.0f, -1, element.scissorArea(), null));
    }

    private void prepareTextures(boolean closePrevious, int width, int height) {
        if (this.texture != null && closePrevious) {
            this.texture.close();
            this.texture = null;
            this.textureView.close();
            this.textureView = null;
            this.depthTexture.close();
            this.depthTexture = null;
            this.depthTextureView.close();
            this.depthTextureView = null;
        }
        GpuDevice gpuDevice = RenderSystem.getDevice();
        if (this.texture == null) {
            this.texture = gpuDevice.createTexture(() -> "UI " + this.getName() + " texture", 12, TextureFormat.RGBA8, width, height, 1, 1);
            this.textureView = gpuDevice.createTextureView(this.texture);
            this.depthTexture = gpuDevice.createTexture(() -> "UI " + this.getName() + " depth texture", 8, TextureFormat.DEPTH32, width, height, 1, 1);
            this.depthTextureView = gpuDevice.createTextureView(this.depthTexture);
        }
        gpuDevice.createCommandEncoder().clearColorAndDepthTextures(this.texture, 0, this.depthTexture, 1.0);
        RenderSystem.setProjectionMatrix((GpuBufferSlice)this.projectionMatrix.set((float)width, (float)height), (ProjectionType)ProjectionType.ORTHOGRAPHIC);
    }

    protected boolean shouldBypassScaling(T elementRenderer) {
        return false;
    }

    protected float getYOffset(int height, int windowScaleFactor) {
        return height;
    }

    @Override
    public void close() {
        if (this.texture != null) {
            this.texture.close();
        }
        if (this.textureView != null) {
            this.textureView.close();
        }
        if (this.depthTexture != null) {
            this.depthTexture.close();
        }
        if (this.depthTextureView != null) {
            this.depthTextureView.close();
        }
        this.projectionMatrix.close();
    }

    public abstract Class<T> getElementClass();

    protected abstract void render(T var1, MatrixStack var2);

    protected abstract String getName();
}

