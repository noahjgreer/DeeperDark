package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.LlamaDecorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.LlamaEntityRenderState;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LlamaEntityRenderer extends AgeableMobEntityRenderer {
   private static final Identifier CREAMY_TEXTURE = Identifier.ofVanilla("textures/entity/llama/creamy.png");
   private static final Identifier WHITE_TEXTURE = Identifier.ofVanilla("textures/entity/llama/white.png");
   private static final Identifier BROWN_TEXTURE = Identifier.ofVanilla("textures/entity/llama/brown.png");
   private static final Identifier GRAY_TEXTURE = Identifier.ofVanilla("textures/entity/llama/gray.png");

   public LlamaEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer layer, EntityModelLayer babyLayer) {
      super(context, new LlamaEntityModel(context.getPart(layer)), new LlamaEntityModel(context.getPart(babyLayer)), 0.7F);
      this.addFeature(new LlamaDecorFeatureRenderer(this, context.getEntityModels(), context.getEquipmentRenderer()));
   }

   public Identifier getTexture(LlamaEntityRenderState llamaEntityRenderState) {
      Identifier var10000;
      switch (llamaEntityRenderState.variant) {
         case CREAMY:
            var10000 = CREAMY_TEXTURE;
            break;
         case WHITE:
            var10000 = WHITE_TEXTURE;
            break;
         case BROWN:
            var10000 = BROWN_TEXTURE;
            break;
         case GRAY:
            var10000 = GRAY_TEXTURE;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public LlamaEntityRenderState createRenderState() {
      return new LlamaEntityRenderState();
   }

   public void updateRenderState(LlamaEntity llamaEntity, LlamaEntityRenderState llamaEntityRenderState, float f) {
      super.updateRenderState(llamaEntity, llamaEntityRenderState, f);
      llamaEntityRenderState.variant = llamaEntity.getVariant();
      llamaEntityRenderState.hasChest = !llamaEntity.isBaby() && llamaEntity.hasChest();
      llamaEntityRenderState.bodyArmor = llamaEntity.getBodyArmor();
      llamaEntityRenderState.trader = llamaEntity.isTrader();
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((LlamaEntityRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
