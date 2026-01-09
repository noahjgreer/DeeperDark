package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ElytraFeatureRenderer extends FeatureRenderer {
   private final ElytraEntityModel model;
   private final ElytraEntityModel babyModel;
   private final EquipmentRenderer equipmentRenderer;

   public ElytraFeatureRenderer(FeatureRendererContext context, LoadedEntityModels loader, EquipmentRenderer equipmentRenderer) {
      super(context);
      this.model = new ElytraEntityModel(loader.getModelPart(EntityModelLayers.ELYTRA));
      this.babyModel = new ElytraEntityModel(loader.getModelPart(EntityModelLayers.ELYTRA_BABY));
      this.equipmentRenderer = equipmentRenderer;
   }

   public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, BipedEntityRenderState bipedEntityRenderState, float f, float g) {
      ItemStack itemStack = bipedEntityRenderState.equippedChestStack;
      EquippableComponent equippableComponent = (EquippableComponent)itemStack.get(DataComponentTypes.EQUIPPABLE);
      if (equippableComponent != null && !equippableComponent.assetId().isEmpty()) {
         Identifier identifier = getTexture(bipedEntityRenderState);
         ElytraEntityModel elytraEntityModel = bipedEntityRenderState.baby ? this.babyModel : this.model;
         matrixStack.push();
         matrixStack.translate(0.0F, 0.0F, 0.125F);
         elytraEntityModel.setAngles(bipedEntityRenderState);
         this.equipmentRenderer.render(EquipmentModel.LayerType.WINGS, (RegistryKey)equippableComponent.assetId().get(), elytraEntityModel, itemStack, matrixStack, vertexConsumerProvider, i, identifier);
         matrixStack.pop();
      }
   }

   @Nullable
   private static Identifier getTexture(BipedEntityRenderState state) {
      if (state instanceof PlayerEntityRenderState playerEntityRenderState) {
         SkinTextures skinTextures = playerEntityRenderState.skinTextures;
         if (skinTextures.elytraTexture() != null) {
            return skinTextures.elytraTexture();
         }

         if (skinTextures.capeTexture() != null && playerEntityRenderState.capeVisible) {
            return skinTextures.capeTexture();
         }
      }

      return null;
   }
}
