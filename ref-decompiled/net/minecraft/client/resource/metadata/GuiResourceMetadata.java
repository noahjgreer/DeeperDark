package net.minecraft.client.resource.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Scaling;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;

@Environment(EnvType.CLIENT)
public record GuiResourceMetadata(Scaling scaling) {
   public static final GuiResourceMetadata DEFAULT;
   public static final Codec CODEC;
   public static final ResourceMetadataSerializer SERIALIZER;

   public GuiResourceMetadata(Scaling scaling) {
      this.scaling = scaling;
   }

   public Scaling scaling() {
      return this.scaling;
   }

   static {
      DEFAULT = new GuiResourceMetadata(Scaling.STRETCH);
      CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Scaling.CODEC.optionalFieldOf("scaling", Scaling.STRETCH).forGetter(GuiResourceMetadata::scaling)).apply(instance, GuiResourceMetadata::new);
      });
      SERIALIZER = new ResourceMetadataSerializer("gui", CODEC);
   }
}
