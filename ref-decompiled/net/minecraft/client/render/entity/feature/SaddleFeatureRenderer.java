package net.minecraft.client.render.entity.feature;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;

@Environment(EnvType.CLIENT)
public class SaddleFeatureRenderer extends FeatureRenderer {
   private final EquipmentRenderer equipmentRenderer;
   private final EquipmentModel.LayerType layerType;
   private final Function saddleStackGetter;
   private final EntityModel adultModel;
   private final EntityModel babyModel;

   public SaddleFeatureRenderer(FeatureRendererContext context, EquipmentRenderer equipmentRenderer, EquipmentModel.LayerType layerType, Function saddleStackGetter, EntityModel adultModel, EntityModel babyModel) {
      super(context);
      this.equipmentRenderer = equipmentRenderer;
      this.layerType = layerType;
      this.saddleStackGetter = saddleStackGetter;
      this.adultModel = adultModel;
      this.babyModel = babyModel;
   }

   public SaddleFeatureRenderer(FeatureRendererContext context, EquipmentRenderer equipmentRenderer, EntityModel model, EquipmentModel.LayerType layerType, Function saddleStackGetter) {
      this(context, equipmentRenderer, layerType, saddleStackGetter, model, model);
   }

   public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntityRenderState livingEntityRenderState, float f, float g) {
      ItemStack itemStack = (ItemStack)this.saddleStackGetter.apply(livingEntityRenderState);
      EquippableComponent equippableComponent = (EquippableComponent)itemStack.get(DataComponentTypes.EQUIPPABLE);
      if (equippableComponent != null && !equippableComponent.assetId().isEmpty()) {
         EntityModel entityModel = livingEntityRenderState.baby ? this.babyModel : this.adultModel;
         entityModel.setAngles(livingEntityRenderState);
         this.equipmentRenderer.render(this.layerType, (RegistryKey)equippableComponent.assetId().get(), entityModel, itemStack, matrixStack, vertexConsumerProvider, i);
      }
   }
}
