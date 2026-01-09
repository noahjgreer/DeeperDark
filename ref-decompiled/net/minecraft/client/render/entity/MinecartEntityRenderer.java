package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;

@Environment(EnvType.CLIENT)
public class MinecartEntityRenderer extends AbstractMinecartEntityRenderer {
   public MinecartEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer entityModelLayer) {
      super(context, entityModelLayer);
   }

   public MinecartEntityRenderState createRenderState() {
      return new MinecartEntityRenderState();
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
