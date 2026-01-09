package net.minecraft.client.render.entity.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.StringIdentifiable;

@Environment(EnvType.CLIENT)
public record VillagerResourceMetadata(HatType hatType) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(VillagerResourceMetadata.HatType.CODEC.optionalFieldOf("hat", VillagerResourceMetadata.HatType.NONE).forGetter(VillagerResourceMetadata::hatType)).apply(instance, VillagerResourceMetadata::new);
   });
   public static final ResourceMetadataSerializer SERIALIZER;

   public VillagerResourceMetadata(HatType hatType) {
      this.hatType = hatType;
   }

   public HatType hatType() {
      return this.hatType;
   }

   static {
      SERIALIZER = new ResourceMetadataSerializer("villager", CODEC);
   }

   @Environment(EnvType.CLIENT)
   public static enum HatType implements StringIdentifiable {
      NONE("none"),
      PARTIAL("partial"),
      FULL("full");

      public static final Codec CODEC = StringIdentifiable.createCodec(HatType::values);
      private final String name;

      private HatType(final String name) {
         this.name = name;
      }

      public String asString() {
         return this.name;
      }

      // $FF: synthetic method
      private static HatType[] method_36924() {
         return new HatType[]{NONE, PARTIAL, FULL};
      }
   }
}
