package net.minecraft.dialog.body;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record PlainMessageDialogBody(Text contents, int width) implements DialogBody {
   public static final int DEFAULT_WIDTH = 200;
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(TextCodecs.CODEC.fieldOf("contents").forGetter(PlainMessageDialogBody::contents), Dialog.WIDTH_CODEC.optionalFieldOf("width", 200).forGetter(PlainMessageDialogBody::width)).apply(instance, PlainMessageDialogBody::new);
   });
   public static final Codec ALTERNATIVE_CODEC;

   public PlainMessageDialogBody(Text text, int i) {
      this.contents = text;
      this.width = i;
   }

   public MapCodec getTypeCodec() {
      return CODEC;
   }

   public Text contents() {
      return this.contents;
   }

   public int width() {
      return this.width;
   }

   static {
      ALTERNATIVE_CODEC = Codec.withAlternative(CODEC.codec(), TextCodecs.CODEC, (contents) -> {
         return new PlainMessageDialogBody(contents, 200);
      });
   }
}
