package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;

@Environment(EnvType.CLIENT)
public class EmptyEntityRenderer extends EntityRenderer {
   public EmptyEntityRenderer(EntityRendererFactory.Context context) {
      super(context);
   }

   public EntityRenderState createRenderState() {
      return new EntityRenderState();
   }
}
