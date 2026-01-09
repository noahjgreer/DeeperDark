package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.RaftEntityModel;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RaftEntityRenderer extends AbstractBoatEntityRenderer {
   private final EntityModel model;
   private final Identifier texture;

   public RaftEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer layer) {
      super(context);
      this.texture = layer.id().withPath((path) -> {
         return "textures/entity/" + path + ".png";
      });
      this.model = new RaftEntityModel(context.getPart(layer));
   }

   protected EntityModel getModel() {
      return this.model;
   }

   protected RenderLayer getRenderLayer() {
      return this.model.getLayer(this.texture);
   }
}
