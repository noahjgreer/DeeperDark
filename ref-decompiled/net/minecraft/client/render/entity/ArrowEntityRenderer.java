package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.ArrowEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ArrowEntityRenderer extends ProjectileEntityRenderer {
   public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/projectiles/arrow.png");
   public static final Identifier TIPPED_TEXTURE = Identifier.ofVanilla("textures/entity/projectiles/tipped_arrow.png");

   public ArrowEntityRenderer(EntityRendererFactory.Context context) {
      super(context);
   }

   protected Identifier getTexture(ArrowEntityRenderState arrowEntityRenderState) {
      return arrowEntityRenderState.tipped ? TIPPED_TEXTURE : TEXTURE;
   }

   public ArrowEntityRenderState createRenderState() {
      return new ArrowEntityRenderState();
   }

   public void updateRenderState(ArrowEntity arrowEntity, ArrowEntityRenderState arrowEntityRenderState, float f) {
      super.updateRenderState((PersistentProjectileEntity)arrowEntity, (ProjectileEntityRenderState)arrowEntityRenderState, f);
      arrowEntityRenderState.tipped = arrowEntity.getColor() > 0;
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
