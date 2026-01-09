package net.minecraft.resource.metadata;

import com.mojang.serialization.Codec;

public record ResourceMetadataSerializer(String name, Codec codec) {
   public ResourceMetadataSerializer(String string, Codec codec) {
      this.name = string;
      this.codec = codec;
   }

   public String name() {
      return this.name;
   }

   public Codec codec() {
      return this.codec;
   }
}
