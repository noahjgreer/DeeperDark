package net.minecraft.client.render.entity.feature;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.VillagerDataRenderState;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;

@Environment(EnvType.CLIENT)
public class VillagerClothingFeatureRenderer extends FeatureRenderer {
   private static final Int2ObjectMap LEVEL_TO_ID = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(), (levelToId) -> {
      levelToId.put(1, Identifier.ofVanilla("stone"));
      levelToId.put(2, Identifier.ofVanilla("iron"));
      levelToId.put(3, Identifier.ofVanilla("gold"));
      levelToId.put(4, Identifier.ofVanilla("emerald"));
      levelToId.put(5, Identifier.ofVanilla("diamond"));
   });
   private final Object2ObjectMap villagerTypeToHat = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap professionToHat = new Object2ObjectOpenHashMap();
   private final ResourceManager resourceManager;
   private final String entityType;

   public VillagerClothingFeatureRenderer(FeatureRendererContext context, ResourceManager resourceManager, String entityType) {
      super(context);
      this.resourceManager = resourceManager;
      this.entityType = entityType;
   }

   public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntityRenderState livingEntityRenderState, float f, float g) {
      if (!livingEntityRenderState.invisible) {
         VillagerData villagerData = ((VillagerDataRenderState)livingEntityRenderState).getVillagerData();
         if (villagerData != null) {
            RegistryEntry registryEntry = villagerData.type();
            RegistryEntry registryEntry2 = villagerData.profession();
            VillagerResourceMetadata.HatType hatType = this.getHatType(this.villagerTypeToHat, "type", registryEntry);
            VillagerResourceMetadata.HatType hatType2 = this.getHatType(this.professionToHat, "profession", registryEntry2);
            EntityModel entityModel = this.getContextModel();
            ((ModelWithHat)entityModel).setHatVisible(hatType2 == VillagerResourceMetadata.HatType.NONE || hatType2 == VillagerResourceMetadata.HatType.PARTIAL && hatType != VillagerResourceMetadata.HatType.FULL);
            Identifier identifier = this.getTexture("type", registryEntry);
            renderModel(entityModel, identifier, matrixStack, vertexConsumerProvider, i, livingEntityRenderState, -1);
            ((ModelWithHat)entityModel).setHatVisible(true);
            if (!registryEntry2.matchesKey(VillagerProfession.NONE) && !livingEntityRenderState.baby) {
               Identifier identifier2 = this.getTexture("profession", registryEntry2);
               renderModel(entityModel, identifier2, matrixStack, vertexConsumerProvider, i, livingEntityRenderState, -1);
               if (!registryEntry2.matchesKey(VillagerProfession.NITWIT)) {
                  Identifier identifier3 = this.getTexture("profession_level", (Identifier)LEVEL_TO_ID.get(MathHelper.clamp(villagerData.level(), 1, LEVEL_TO_ID.size())));
                  renderModel(entityModel, identifier3, matrixStack, vertexConsumerProvider, i, livingEntityRenderState, -1);
               }
            }

         }
      }
   }

   private Identifier getTexture(String keyType, Identifier keyId) {
      return keyId.withPath((path) -> {
         return "textures/entity/" + this.entityType + "/" + keyType + "/" + path + ".png";
      });
   }

   private Identifier getTexture(String keyType, RegistryEntry entry) {
      return (Identifier)entry.getKey().map((key) -> {
         return this.getTexture(keyType, key.getValue());
      }).orElse(MissingSprite.getMissingSpriteId());
   }

   public VillagerResourceMetadata.HatType getHatType(Object2ObjectMap metadataMap, String keyType, RegistryEntry entry) {
      RegistryKey registryKey = (RegistryKey)entry.getKey().orElse((Object)null);
      return registryKey == null ? VillagerResourceMetadata.HatType.NONE : (VillagerResourceMetadata.HatType)metadataMap.computeIfAbsent(registryKey, (object) -> {
         return (VillagerResourceMetadata.HatType)this.resourceManager.getResource(this.getTexture(keyType, registryKey.getValue())).flatMap((resource) -> {
            try {
               return resource.getMetadata().decode(VillagerResourceMetadata.SERIALIZER).map(VillagerResourceMetadata::hatType);
            } catch (IOException var2) {
               return Optional.empty();
            }
         }).orElse(VillagerResourceMetadata.HatType.NONE);
      });
   }
}
