package net.minecraft.resource.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.util.dynamic.Range;

public record PackOverlaysMetadata(List overlays) {
   private static final Pattern DIRECTORY_NAME_PATTERN = Pattern.compile("[-_a-zA-Z0-9.]+");
   private static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(PackOverlaysMetadata.Entry.CODEC.listOf().fieldOf("entries").forGetter(PackOverlaysMetadata::overlays)).apply(instance, PackOverlaysMetadata::new);
   });
   public static final ResourceMetadataSerializer SERIALIZER;

   public PackOverlaysMetadata(List list) {
      this.overlays = list;
   }

   private static DataResult validate(String directoryName) {
      return !DIRECTORY_NAME_PATTERN.matcher(directoryName).matches() ? DataResult.error(() -> {
         return directoryName + " is not accepted directory name";
      }) : DataResult.success(directoryName);
   }

   public List getAppliedOverlays(int packFormat) {
      return this.overlays.stream().filter((overlay) -> {
         return overlay.isValid(packFormat);
      }).map(Entry::overlay).toList();
   }

   public List overlays() {
      return this.overlays;
   }

   static {
      SERIALIZER = new ResourceMetadataSerializer("overlays", CODEC);
   }

   public static record Entry(Range format, String overlay) {
      static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Range.createCodec(Codec.INT).fieldOf("formats").forGetter(Entry::format), Codec.STRING.validate(PackOverlaysMetadata::validate).fieldOf("directory").forGetter(Entry::overlay)).apply(instance, Entry::new);
      });

      public Entry(Range range, String string) {
         this.format = range;
         this.overlay = string;
      }

      public boolean isValid(int packFormat) {
         return this.format.contains((Comparable)packFormat);
      }

      public Range format() {
         return this.format;
      }

      public String overlay() {
         return this.overlay;
      }
   }
}
