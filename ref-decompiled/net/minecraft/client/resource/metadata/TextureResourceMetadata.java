package net.minecraft.client.resource.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;

@Environment(EnvType.CLIENT)
public record TextureResourceMetadata(boolean blur, boolean clamp) {
   public static final boolean field_32980 = false;
   public static final boolean field_32981 = false;
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codec.BOOL.optionalFieldOf("blur", false).forGetter(TextureResourceMetadata::blur), Codec.BOOL.optionalFieldOf("clamp", false).forGetter(TextureResourceMetadata::clamp)).apply(instance, TextureResourceMetadata::new);
   });
   public static final ResourceMetadataSerializer SERIALIZER;

   public TextureResourceMetadata(boolean blur, boolean clamp) {
      this.blur = blur;
      this.clamp = clamp;
   }

   public boolean blur() {
      return this.blur;
   }

   public boolean clamp() {
      return this.clamp;
   }

   static {
      SERIALIZER = new ResourceMetadataSerializer("texture", CODEC);
   }
}
