package net.minecraft.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.state.special.BookModelGuiElementRenderState;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class BookModelGuiElementRenderer extends SpecialGuiElementRenderer {
   public BookModelGuiElementRenderer(VertexConsumerProvider.Immediate immediate) {
      super(immediate);
   }

   public Class getElementClass() {
      return BookModelGuiElementRenderState.class;
   }

   protected void render(BookModelGuiElementRenderState bookModelGuiElementRenderState, MatrixStack matrixStack) {
      MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ENTITY_IN_UI);
      matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
      matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(25.0F));
      float f = bookModelGuiElementRenderState.open();
      matrixStack.translate((1.0F - f) * 0.2F, (1.0F - f) * 0.1F, (1.0F - f) * 0.25F);
      matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-(1.0F - f) * 90.0F - 90.0F));
      matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
      float g = bookModelGuiElementRenderState.flip();
      float h = MathHelper.clamp(MathHelper.fractionalPart(g + 0.25F) * 1.6F - 0.3F, 0.0F, 1.0F);
      float i = MathHelper.clamp(MathHelper.fractionalPart(g + 0.75F) * 1.6F - 0.3F, 0.0F, 1.0F);
      BookModel bookModel = bookModelGuiElementRenderState.bookModel();
      bookModel.setPageAngles(0.0F, h, i, f);
      Identifier identifier = bookModelGuiElementRenderState.texture();
      VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(bookModel.getLayer(identifier));
      bookModel.render(matrixStack, vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV);
   }

   protected float getYOffset(int height, int windowScaleFactor) {
      return (float)(17 * windowScaleFactor);
   }

   protected String getName() {
      return "book model";
   }
}
