package net.minecraft.client.gui.render;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.OversizedItemGuiElementRenderState;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.KeyedItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class OversizedItemGuiElementRenderer extends SpecialGuiElementRenderer {
   private boolean oversized;
   @Nullable
   private Object modelKey;

   public OversizedItemGuiElementRenderer(VertexConsumerProvider.Immediate immediate) {
      super(immediate);
   }

   public boolean isOversized() {
      return this.oversized;
   }

   public void clearOversized() {
      this.oversized = false;
   }

   public void clearModel() {
      this.modelKey = null;
   }

   public Class getElementClass() {
      return OversizedItemGuiElementRenderState.class;
   }

   protected void render(OversizedItemGuiElementRenderState oversizedItemGuiElementRenderState, MatrixStack matrixStack) {
      matrixStack.scale(1.0F, -1.0F, -1.0F);
      ItemGuiElementRenderState itemGuiElementRenderState = oversizedItemGuiElementRenderState.guiItemRenderState();
      ScreenRect screenRect = itemGuiElementRenderState.oversizedBounds();
      Objects.requireNonNull(screenRect);
      float f = (float)(screenRect.getLeft() + screenRect.getRight()) / 2.0F;
      float g = (float)(screenRect.getTop() + screenRect.getBottom()) / 2.0F;
      float h = (float)itemGuiElementRenderState.x() + 8.0F;
      float i = (float)itemGuiElementRenderState.y() + 8.0F;
      matrixStack.translate((h - f) / 16.0F, (g - i) / 16.0F, 0.0F);
      KeyedItemRenderState keyedItemRenderState = itemGuiElementRenderState.state();
      boolean bl = !keyedItemRenderState.isSideLit();
      if (bl) {
         MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_FLAT);
      } else {
         MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_3D);
      }

      keyedItemRenderState.render(matrixStack, this.vertexConsumers, 15728880, OverlayTexture.DEFAULT_UV);
      this.modelKey = keyedItemRenderState.getModelKey();
   }

   public void renderElement(OversizedItemGuiElementRenderState oversizedItemGuiElementRenderState, GuiRenderState guiRenderState) {
      super.renderElement(oversizedItemGuiElementRenderState, guiRenderState);
      this.oversized = true;
   }

   public boolean shouldBypassScaling(OversizedItemGuiElementRenderState oversizedItemGuiElementRenderState) {
      KeyedItemRenderState keyedItemRenderState = oversizedItemGuiElementRenderState.guiItemRenderState().state();
      return !keyedItemRenderState.isAnimated() && keyedItemRenderState.getModelKey().equals(this.modelKey);
   }

   protected float getYOffset(int height, int windowScaleFactor) {
      return (float)height / 2.0F;
   }

   protected String getName() {
      return "oversized_item";
   }
}
