package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ParrotEntityRenderState;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ParrotEntityRenderer extends MobEntityRenderer {
   private static final Identifier RED_BLUE_TEXTURE = Identifier.ofVanilla("textures/entity/parrot/parrot_red_blue.png");
   private static final Identifier BLUE_TEXTURE = Identifier.ofVanilla("textures/entity/parrot/parrot_blue.png");
   private static final Identifier GREEN_TEXTURE = Identifier.ofVanilla("textures/entity/parrot/parrot_green.png");
   private static final Identifier YELLOW_TEXTURE = Identifier.ofVanilla("textures/entity/parrot/parrot_yellow_blue.png");
   private static final Identifier GREY_TEXTURE = Identifier.ofVanilla("textures/entity/parrot/parrot_grey.png");

   public ParrotEntityRenderer(EntityRendererFactory.Context context) {
      super(context, new ParrotEntityModel(context.getPart(EntityModelLayers.PARROT)), 0.3F);
   }

   public Identifier getTexture(ParrotEntityRenderState parrotEntityRenderState) {
      return getTexture(parrotEntityRenderState.variant);
   }

   public ParrotEntityRenderState createRenderState() {
      return new ParrotEntityRenderState();
   }

   public void updateRenderState(ParrotEntity parrotEntity, ParrotEntityRenderState parrotEntityRenderState, float f) {
      super.updateRenderState(parrotEntity, parrotEntityRenderState, f);
      parrotEntityRenderState.variant = parrotEntity.getVariant();
      float g = MathHelper.lerp(f, parrotEntity.lastFlapProgress, parrotEntity.flapProgress);
      float h = MathHelper.lerp(f, parrotEntity.lastMaxWingDeviation, parrotEntity.maxWingDeviation);
      parrotEntityRenderState.flapAngle = (MathHelper.sin(g) + 1.0F) * h;
      parrotEntityRenderState.parrotPose = ParrotEntityModel.getPose(parrotEntity);
   }

   public static Identifier getTexture(ParrotEntity.Variant variant) {
      Identifier var10000;
      switch (variant) {
         case RED_BLUE:
            var10000 = RED_BLUE_TEXTURE;
            break;
         case BLUE:
            var10000 = BLUE_TEXTURE;
            break;
         case GREEN:
            var10000 = GREEN_TEXTURE;
            break;
         case YELLOW_BLUE:
            var10000 = YELLOW_TEXTURE;
            break;
         case GRAY:
            var10000 = GREY_TEXTURE;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((ParrotEntityRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
