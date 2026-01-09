package net.minecraft.resource.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.dynamic.Range;

public record PackResourceMetadata(Text description, int packFormat, Optional supportedFormats) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(TextCodecs.CODEC.fieldOf("description").forGetter(PackResourceMetadata::description), Codec.INT.fieldOf("pack_format").forGetter(PackResourceMetadata::packFormat), Range.createCodec(Codec.INT).lenientOptionalFieldOf("supported_formats").forGetter(PackResourceMetadata::supportedFormats)).apply(instance, PackResourceMetadata::new);
   });
   public static final ResourceMetadataSerializer SERIALIZER;

   public PackResourceMetadata(Text description, int format, Optional optional) {
      this.description = description;
      this.packFormat = format;
      this.supportedFormats = optional;
   }

   public Text description() {
      return this.description;
   }

   public int packFormat() {
      return this.packFormat;
   }

   public Optional supportedFormats() {
      return this.supportedFormats;
   }

   static {
      SERIALIZER = new ResourceMetadataSerializer("pack", CODEC);
   }
}
