package net.minecraft.client.render.entity.equipment;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;

@Environment(EnvType.CLIENT)
public record EquipmentModel(Map layers) {
   private static final Codec LAYER_LIST_CODEC;
   public static final Codec CODEC;

   public EquipmentModel(Map map) {
      this.layers = map;
   }

   public static Builder builder() {
      return new Builder();
   }

   public List getLayers(LayerType layerType) {
      return (List)this.layers.getOrDefault(layerType, List.of());
   }

   public Map layers() {
      return this.layers;
   }

   static {
      LAYER_LIST_CODEC = Codecs.nonEmptyList(EquipmentModel.Layer.CODEC.listOf());
      CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codecs.nonEmptyMap(Codec.unboundedMap(EquipmentModel.LayerType.CODEC, LAYER_LIST_CODEC)).fieldOf("layers").forGetter(EquipmentModel::layers)).apply(instance, EquipmentModel::new);
      });
   }

   @Environment(EnvType.CLIENT)
   public static class Builder {
      private final Map layers = new EnumMap(LayerType.class);

      Builder() {
      }

      public Builder addHumanoidLayers(Identifier textureId) {
         return this.addHumanoidLayers(textureId, false);
      }

      public Builder addHumanoidLayers(Identifier textureId, boolean dyeable) {
         this.addLayers(EquipmentModel.LayerType.HUMANOID_LEGGINGS, EquipmentModel.Layer.createWithLeatherColor(textureId, dyeable));
         this.addMainHumanoidLayer(textureId, dyeable);
         return this;
      }

      public Builder addMainHumanoidLayer(Identifier textureId, boolean dyeable) {
         return this.addLayers(EquipmentModel.LayerType.HUMANOID, EquipmentModel.Layer.createWithLeatherColor(textureId, dyeable));
      }

      public Builder addLayers(LayerType layerType, Layer... layers) {
         Collections.addAll((Collection)this.layers.computeIfAbsent(layerType, (layerTypex) -> {
            return new ArrayList();
         }), layers);
         return this;
      }

      public EquipmentModel build() {
         return new EquipmentModel((Map)this.layers.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entry) -> {
            return List.copyOf((Collection)entry.getValue());
         })));
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum LayerType implements StringIdentifiable {
      HUMANOID("humanoid"),
      HUMANOID_LEGGINGS("humanoid_leggings"),
      WINGS("wings"),
      WOLF_BODY("wolf_body"),
      HORSE_BODY("horse_body"),
      LLAMA_BODY("llama_body"),
      PIG_SADDLE("pig_saddle"),
      STRIDER_SADDLE("strider_saddle"),
      CAMEL_SADDLE("camel_saddle"),
      HORSE_SADDLE("horse_saddle"),
      DONKEY_SADDLE("donkey_saddle"),
      MULE_SADDLE("mule_saddle"),
      ZOMBIE_HORSE_SADDLE("zombie_horse_saddle"),
      SKELETON_HORSE_SADDLE("skeleton_horse_saddle"),
      HAPPY_GHAST_BODY("happy_ghast_body");

      public static final Codec CODEC = StringIdentifiable.createCodec(LayerType::values);
      private final String name;

      private LayerType(final String name) {
         this.name = name;
      }

      public String asString() {
         return this.name;
      }

      public String getTrimsDirectory() {
         return "trims/entity/" + this.name;
      }

      // $FF: synthetic method
      private static LayerType[] method_64010() {
         return new LayerType[]{HUMANOID, HUMANOID_LEGGINGS, WINGS, WOLF_BODY, HORSE_BODY, LLAMA_BODY, PIG_SADDLE, STRIDER_SADDLE, CAMEL_SADDLE, HORSE_SADDLE, DONKEY_SADDLE, MULE_SADDLE, ZOMBIE_HORSE_SADDLE, SKELETON_HORSE_SADDLE, HAPPY_GHAST_BODY};
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Layer(Identifier textureId, Optional dyeable, boolean usePlayerTexture) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Identifier.CODEC.fieldOf("texture").forGetter(Layer::textureId), EquipmentModel.Dyeable.CODEC.optionalFieldOf("dyeable").forGetter(Layer::dyeable), Codec.BOOL.optionalFieldOf("use_player_texture", false).forGetter(Layer::usePlayerTexture)).apply(instance, Layer::new);
      });

      public Layer(Identifier textureId) {
         this(textureId, Optional.empty(), false);
      }

      public Layer(Identifier identifier, Optional optional, boolean bl) {
         this.textureId = identifier;
         this.dyeable = optional;
         this.usePlayerTexture = bl;
      }

      public static Layer createWithLeatherColor(Identifier textureId, boolean dyeable) {
         return new Layer(textureId, dyeable ? Optional.of(new Dyeable(Optional.of(-6265536))) : Optional.empty(), false);
      }

      public static Layer create(Identifier textureId, boolean dyeable) {
         return new Layer(textureId, dyeable ? Optional.of(new Dyeable(Optional.empty())) : Optional.empty(), false);
      }

      public Identifier getFullTextureId(LayerType layerType) {
         return this.textureId.withPath((textureName) -> {
            String var10000 = layerType.asString();
            return "textures/entity/equipment/" + var10000 + "/" + textureName + ".png";
         });
      }

      public Identifier textureId() {
         return this.textureId;
      }

      public Optional dyeable() {
         return this.dyeable;
      }

      public boolean usePlayerTexture() {
         return this.usePlayerTexture;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Dyeable(Optional colorWhenUndyed) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codecs.RGB.optionalFieldOf("color_when_undyed").forGetter(Dyeable::colorWhenUndyed)).apply(instance, Dyeable::new);
      });

      public Dyeable(Optional optional) {
         this.colorWhenUndyed = optional;
      }

      public Optional colorWhenUndyed() {
         return this.colorWhenUndyed;
      }
   }
}
