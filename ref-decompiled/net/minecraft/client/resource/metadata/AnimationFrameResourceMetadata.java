package net.minecraft.client.resource.metadata;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.dynamic.Codecs;

@Environment(EnvType.CLIENT)
public record AnimationFrameResourceMetadata(int index, Optional time) {
   public static final Codec BASE_CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codecs.NON_NEGATIVE_INT.fieldOf("index").forGetter(AnimationFrameResourceMetadata::index), Codecs.POSITIVE_INT.optionalFieldOf("time").forGetter(AnimationFrameResourceMetadata::time)).apply(instance, AnimationFrameResourceMetadata::new);
   });
   public static final Codec CODEC;

   public AnimationFrameResourceMetadata(int index) {
      this(index, Optional.empty());
   }

   public AnimationFrameResourceMetadata(int index, Optional optional) {
      this.index = index;
      this.time = optional;
   }

   public int getTime(int defaultTime) {
      return (Integer)this.time.orElse(defaultTime);
   }

   public int index() {
      return this.index;
   }

   public Optional time() {
      return this.time;
   }

   static {
      CODEC = Codec.either(Codecs.NON_NEGATIVE_INT, BASE_CODEC).xmap((either) -> {
         return (AnimationFrameResourceMetadata)either.map(AnimationFrameResourceMetadata::new, (metadata) -> {
            return metadata;
         });
      }, (metadatax) -> {
         return metadatax.time.isPresent() ? Either.right(metadatax) : Either.left(metadatax.index);
      });
   }
}
