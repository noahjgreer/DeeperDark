package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SpectralArrowEntityRenderer extends ProjectileEntityRenderer {
   public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/projectiles/spectral_arrow.png");

   public SpectralArrowEntityRenderer(EntityRendererFactory.Context context) {
      super(context);
   }

   protected Identifier getTexture(ProjectileEntityRenderState state) {
      return TEXTURE;
   }

   public ProjectileEntityRenderState createRenderState() {
      return new ProjectileEntityRenderState();
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
