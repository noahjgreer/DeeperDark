package net.minecraft.client.render.entity.equipment;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class EquipmentRenderer {
   private static final int field_54178 = 0;
   private final EquipmentModelLoader equipmentModelLoader;
   private final Function layerTextures;
   private final Function trimSprites;

   public EquipmentRenderer(EquipmentModelLoader equipmentModelLoader, SpriteAtlasTexture armorTrimsAtlas) {
      this.equipmentModelLoader = equipmentModelLoader;
      this.layerTextures = Util.memoize((key) -> {
         return key.layer.getFullTextureId(key.layerType);
      });
      this.trimSprites = Util.memoize((key) -> {
         return armorTrimsAtlas.getSprite(key.getTexture());
      });
   }

   public void render(EquipmentModel.LayerType layerType, RegistryKey assetKey, Model model, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      this.render(layerType, assetKey, model, stack, matrices, vertexConsumers, light, (Identifier)null);
   }

   public void render(EquipmentModel.LayerType layerType, RegistryKey assetKey, Model model, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, @Nullable Identifier texture) {
      List list = this.equipmentModelLoader.get(assetKey).getLayers(layerType);
      if (!list.isEmpty()) {
         int i = DyedColorComponent.getColor(stack, 0);
         boolean bl = stack.hasGlint();
         Iterator var12 = list.iterator();

         while(true) {
            EquipmentModel.Layer layer;
            int j;
            do {
               if (!var12.hasNext()) {
                  ArmorTrim armorTrim = (ArmorTrim)stack.get(DataComponentTypes.TRIM);
                  if (armorTrim != null) {
                     Sprite sprite = (Sprite)this.trimSprites.apply(new TrimSpriteKey(armorTrim, layerType, assetKey));
                     VertexConsumer vertexConsumer2 = sprite.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(TexturedRenderLayers.getArmorTrims(((ArmorTrimPattern)armorTrim.pattern().value()).decal())));
                     model.render(matrices, vertexConsumer2, light, OverlayTexture.DEFAULT_UV);
                  }

                  return;
               }

               layer = (EquipmentModel.Layer)var12.next();
               j = getDyeColor(layer, i);
            } while(j == 0);

            Identifier identifier = layer.usePlayerTexture() && texture != null ? texture : (Identifier)this.layerTextures.apply(new LayerTextureKey(layerType, layer));
            VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(identifier), bl);
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, j);
            bl = false;
         }
      }
   }

   private static int getDyeColor(EquipmentModel.Layer layer, int dyeColor) {
      Optional optional = layer.dyeable();
      if (optional.isPresent()) {
         int i = (Integer)((EquipmentModel.Dyeable)optional.get()).colorWhenUndyed().map(ColorHelper::fullAlpha).orElse(0);
         return dyeColor != 0 ? dyeColor : i;
      } else {
         return -1;
      }
   }

   @Environment(EnvType.CLIENT)
   static record LayerTextureKey(EquipmentModel.LayerType layerType, EquipmentModel.Layer layer) {
      final EquipmentModel.LayerType layerType;
      final EquipmentModel.Layer layer;

      LayerTextureKey(EquipmentModel.LayerType layerType, EquipmentModel.Layer layer) {
         this.layerType = layerType;
         this.layer = layer;
      }

      public EquipmentModel.LayerType layerType() {
         return this.layerType;
      }

      public EquipmentModel.Layer layer() {
         return this.layer;
      }
   }

   @Environment(EnvType.CLIENT)
   static record TrimSpriteKey(ArmorTrim trim, EquipmentModel.LayerType layerType, RegistryKey equipmentAssetId) {
      TrimSpriteKey(ArmorTrim armorTrim, EquipmentModel.LayerType layerType, RegistryKey registryKey) {
         this.trim = armorTrim;
         this.layerType = layerType;
         this.equipmentAssetId = registryKey;
      }

      public Identifier getTexture() {
         return this.trim.getTextureId(this.layerType.getTrimsDirectory(), this.equipmentAssetId);
      }

      public ArmorTrim trim() {
         return this.trim;
      }

      public EquipmentModel.LayerType layerType() {
         return this.layerType;
      }

      public RegistryKey equipmentAssetId() {
         return this.equipmentAssetId;
      }
   }
}
