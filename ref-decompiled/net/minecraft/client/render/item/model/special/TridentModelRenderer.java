package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;

@Environment(EnvType.CLIENT)
public class TridentModelRenderer implements SimpleSpecialModelRenderer {
   private final TridentEntityModel model;

   public TridentModelRenderer(TridentEntityModel model) {
      this.model = model;
   }

   public void render(ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, boolean glint) {
      matrices.push();
      matrices.scale(1.0F, -1.0F, -1.0F);
      VertexConsumer vertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumers, this.model.getLayer(TridentEntityModel.TEXTURE), false, glint);
      this.model.render(matrices, vertexConsumer, light, overlay);
      matrices.pop();
   }

   public void collectVertices(Set vertices) {
      MatrixStack matrixStack = new MatrixStack();
      matrixStack.scale(1.0F, -1.0F, -1.0F);
      this.model.getRootPart().collectVertices(matrixStack, vertices);
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked() implements SpecialModelRenderer.Unbaked {
      public static final MapCodec CODEC = MapCodec.unit(new Unbaked());

      public MapCodec getCodec() {
         return CODEC;
      }

      public SpecialModelRenderer bake(LoadedEntityModels entityModels) {
         return new TridentModelRenderer(new TridentEntityModel(entityModels.getModelPart(EntityModelLayers.TRIDENT)));
      }
   }
}
