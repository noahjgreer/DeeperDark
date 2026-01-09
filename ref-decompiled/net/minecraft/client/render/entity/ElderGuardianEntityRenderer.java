package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.GuardianEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ElderGuardianEntityRenderer extends GuardianEntityRenderer {
   public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/guardian_elder.png");

   public ElderGuardianEntityRenderer(EntityRendererFactory.Context context) {
      super(context, 1.2F, EntityModelLayers.ELDER_GUARDIAN);
   }

   public Identifier getTexture(GuardianEntityRenderState guardianEntityRenderState) {
      return TEXTURE;
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((GuardianEntityRenderState)state);
   }
}
