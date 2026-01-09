package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;

@Environment(EnvType.CLIENT)
public class ConduitModelRenderer implements SimpleSpecialModelRenderer {
   private final ModelPart shell;

   public ConduitModelRenderer(ModelPart shell) {
      this.shell = shell;
   }

   public void render(ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
      VertexConsumer vertexConsumer = ConduitBlockEntityRenderer.BASE_TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
      matrices.push();
      matrices.translate(0.5F, 0.5F, 0.5F);
      this.shell.render(matrices, vertexConsumer, light, overlay);
      matrices.pop();
   }

   public void collectVertices(Set vertices) {
      MatrixStack matrixStack = new MatrixStack();
      matrixStack.translate(0.5F, 0.5F, 0.5F);
      this.shell.collectVertices(matrixStack, vertices);
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked() implements SpecialModelRenderer.Unbaked {
      public static final MapCodec CODEC = MapCodec.unit(new Unbaked());

      public MapCodec getCodec() {
         return CODEC;
      }

      public SpecialModelRenderer bake(LoadedEntityModels entityModels) {
         return new ConduitModelRenderer(entityModels.getModelPart(EntityModelLayers.CONDUIT_SHELL));
      }
   }
}
