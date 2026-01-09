package net.minecraft.recipe.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureSet;

public interface RecipeDisplay {
   Codec CODEC = Registries.RECIPE_DISPLAY.getCodec().dispatch(RecipeDisplay::serializer, Serializer::codec);
   PacketCodec PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.RECIPE_DISPLAY).dispatch(RecipeDisplay::serializer, Serializer::streamCodec);

   SlotDisplay result();

   SlotDisplay craftingStation();

   Serializer serializer();

   default boolean isEnabled(FeatureSet features) {
      return this.result().isEnabled(features) && this.craftingStation().isEnabled(features);
   }

   public static record Serializer(MapCodec codec, PacketCodec streamCodec) {
      public Serializer(MapCodec mapCodec, PacketCodec packetCodec) {
         this.codec = mapCodec;
         this.streamCodec = packetCodec;
      }

      public MapCodec codec() {
         return this.codec;
      }

      public PacketCodec streamCodec() {
         return this.streamCodec;
      }
   }
}
