package net.minecraft.resource.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public record PackFeatureSetMetadata(FeatureSet flags) {
   private static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(FeatureFlags.CODEC.fieldOf("enabled").forGetter(PackFeatureSetMetadata::flags)).apply(instance, PackFeatureSetMetadata::new);
   });
   public static final ResourceMetadataSerializer SERIALIZER;

   public PackFeatureSetMetadata(FeatureSet featureSet) {
      this.flags = featureSet;
   }

   public FeatureSet flags() {
      return this.flags;
   }

   static {
      SERIALIZER = new ResourceMetadataSerializer("features", CODEC);
   }
}
