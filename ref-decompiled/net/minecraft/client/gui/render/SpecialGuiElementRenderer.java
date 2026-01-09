package net.minecraft.client.gui.render;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.render.ProjectionMatrix2;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class SpecialGuiElementRenderer implements AutoCloseable {
   protected final VertexConsumerProvider.Immediate vertexConsumers;
   @Nullable
   private GpuTexture texture;
   @Nullable
   private GpuTextureView textureView;
   @Nullable
   private GpuTexture depthTexture;
   @Nullable
   private GpuTextureView depthTextureView;
   private final ProjectionMatrix2 projectionMatrix = new ProjectionMatrix2("PIP - " + this.getClass().getSimpleName(), -1000.0F, 1000.0F, true);

   protected SpecialGuiElementRenderer(VertexConsumerProvider.Immediate vertexConsumers) {
      this.vertexConsumers = vertexConsumers;
   }

   public void render(SpecialGuiElementRenderState elementState, GuiRenderState state, int windowScaleFactor) {
      int i = (elementState.x2() - elementState.x1()) * windowScaleFactor;
      int j = (elementState.y2() - elementState.y1()) * windowScaleFactor;
      boolean bl = this.texture == null || this.texture.getWidth(0) != i || this.texture.getHeight(0) != j;
      if (!bl && this.shouldBypassScaling(elementState)) {
         this.renderElement(elementState, state);
      } else {
         this.prepareTextures(bl, i, j);
         RenderSystem.outputColorTextureOverride = this.textureView;
         RenderSystem.outputDepthTextureOverride = this.depthTextureView;
         MatrixStack matrixStack = new MatrixStack();
         matrixStack.translate((float)i / 2.0F, this.getYOffset(j, windowScaleFactor), 0.0F);
         float f = (float)windowScaleFactor * elementState.scale();
         matrixStack.scale(f, f, -f);
         this.render(elementState, matrixStack);
         this.vertexConsumers.draw();
         RenderSystem.outputColorTextureOverride = null;
         RenderSystem.outputDepthTextureOverride = null;
         this.renderElement(elementState, state);
      }
   }

   protected void renderElement(SpecialGuiElementRenderState element, GuiRenderState state) {
      state.addSimpleElementToCurrentLayer(new TexturedQuadGuiElementRenderState(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, TextureSetup.withoutGlTexture(this.textureView), element.pose(), element.x1(), element.y1(), element.x2(), element.y2(), 0.0F, 1.0F, 1.0F, 0.0F, -1, element.scissorArea(), (ScreenRect)null));
   }

   private void prepareTextures(boolean bl, int i, int j) {
      if (this.texture != null && bl) {
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
         this.texture = gpuDevice.createTexture((Supplier)(() -> {
            return "UI " + this.getName() + " texture";
         }), 12, TextureFormat.RGBA8, i, j, 1, 1);
         this.texture.setTextureFilter(FilterMode.NEAREST, false);
         this.textureView = gpuDevice.createTextureView(this.texture);
         this.depthTexture = gpuDevice.createTexture((Supplier)(() -> {
            return "UI " + this.getName() + " depth texture";
         }), 8, TextureFormat.DEPTH32, i, j, 1, 1);
         this.depthTextureView = gpuDevice.createTextureView(this.depthTexture);
      }

      gpuDevice.createCommandEncoder().clearColorAndDepthTextures(this.texture, 0, this.depthTexture, 1.0);
      RenderSystem.setProjectionMatrix(this.projectionMatrix.set((float)i, (float)j), ProjectionType.ORTHOGRAPHIC);
   }

   protected boolean shouldBypassScaling(SpecialGuiElementRenderState specialGuiElementRenderState) {
      return false;
   }

   protected float getYOffset(int height, int windowScaleFactor) {
      return (float)height;
   }

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

   public abstract Class getElementClass();

   protected abstract void render(SpecialGuiElementRenderState state, MatrixStack matrices);

   protected abstract String getName();
}
