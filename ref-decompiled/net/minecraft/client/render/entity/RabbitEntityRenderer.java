package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.RabbitEntityRenderState;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RabbitEntityRenderer extends AgeableMobEntityRenderer {
   private static final Identifier BROWN_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/brown.png");
   private static final Identifier WHITE_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/white.png");
   private static final Identifier BLACK_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/black.png");
   private static final Identifier GOLD_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/gold.png");
   private static final Identifier SALT_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/salt.png");
   private static final Identifier WHITE_SPLOTCHED_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/white_splotched.png");
   private static final Identifier TOAST_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/toast.png");
   private static final Identifier CAERBANNOG_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/caerbannog.png");

   public RabbitEntityRenderer(EntityRendererFactory.Context context) {
      super(context, new RabbitEntityModel(context.getPart(EntityModelLayers.RABBIT)), new RabbitEntityModel(context.getPart(EntityModelLayers.RABBIT_BABY)), 0.3F);
   }

   public Identifier getTexture(RabbitEntityRenderState rabbitEntityRenderState) {
      if (rabbitEntityRenderState.isToast) {
         return TOAST_TEXTURE;
      } else {
         Identifier var10000;
         switch (rabbitEntityRenderState.type) {
            case BROWN:
               var10000 = BROWN_TEXTURE;
               break;
            case WHITE:
               var10000 = WHITE_TEXTURE;
               break;
            case BLACK:
               var10000 = BLACK_TEXTURE;
               break;
            case GOLD:
               var10000 = GOLD_TEXTURE;
               break;
            case SALT:
               var10000 = SALT_TEXTURE;
               break;
            case WHITE_SPLOTCHED:
               var10000 = WHITE_SPLOTCHED_TEXTURE;
               break;
            case EVIL:
               var10000 = CAERBANNOG_TEXTURE;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }

   public RabbitEntityRenderState createRenderState() {
      return new RabbitEntityRenderState();
   }

   public void updateRenderState(RabbitEntity rabbitEntity, RabbitEntityRenderState rabbitEntityRenderState, float f) {
      super.updateRenderState(rabbitEntity, rabbitEntityRenderState, f);
      rabbitEntityRenderState.jumpProgress = rabbitEntity.getJumpProgress(f);
      rabbitEntityRenderState.isToast = "Toast".equals(Formatting.strip(rabbitEntity.getName().getString()));
      rabbitEntityRenderState.type = rabbitEntity.getVariant();
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((RabbitEntityRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
