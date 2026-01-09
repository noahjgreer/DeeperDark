package net.minecraft.client.resource.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.dynamic.Codecs;

@Environment(EnvType.CLIENT)
public record AnimationResourceMetadata(Optional frames, Optional width, Optional height, int defaultFrameTime, boolean interpolate) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(AnimationFrameResourceMetadata.CODEC.listOf().optionalFieldOf("frames").forGetter(AnimationResourceMetadata::frames), Codecs.POSITIVE_INT.optionalFieldOf("width").forGetter(AnimationResourceMetadata::width), Codecs.POSITIVE_INT.optionalFieldOf("height").forGetter(AnimationResourceMetadata::height), Codecs.POSITIVE_INT.optionalFieldOf("frametime", 1).forGetter(AnimationResourceMetadata::defaultFrameTime), Codec.BOOL.optionalFieldOf("interpolate", false).forGetter(AnimationResourceMetadata::interpolate)).apply(instance, AnimationResourceMetadata::new);
   });
   public static final ResourceMetadataSerializer SERIALIZER;

   public AnimationResourceMetadata(Optional optional, Optional optional2, Optional optional3, int defaultFrameTime, boolean interpolate) {
      this.frames = optional;
      this.width = optional2;
      this.height = optional3;
      this.defaultFrameTime = defaultFrameTime;
      this.interpolate = interpolate;
   }

   public SpriteDimensions getSize(int defaultWidth, int defaultHeight) {
      if (this.width.isPresent()) {
         return this.height.isPresent() ? new SpriteDimensions((Integer)this.width.get(), (Integer)this.height.get()) : new SpriteDimensions((Integer)this.width.get(), defaultHeight);
      } else if (this.height.isPresent()) {
         return new SpriteDimensions(defaultWidth, (Integer)this.height.get());
      } else {
         int i = Math.min(defaultWidth, defaultHeight);
         return new SpriteDimensions(i, i);
      }
   }

   public Optional frames() {
      return this.frames;
   }

   public Optional width() {
      return this.width;
   }

   public Optional height() {
      return this.height;
   }

   public int defaultFrameTime() {
      return this.defaultFrameTime;
   }

   public boolean interpolate() {
      return this.interpolate;
   }

   static {
      SERIALIZER = new ResourceMetadataSerializer("animation", CODEC);
   }
}
