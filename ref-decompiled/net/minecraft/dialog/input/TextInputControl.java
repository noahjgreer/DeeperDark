package net.minecraft.dialog.input;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.dynamic.Codecs;

public record TextInputControl(int width, Text label, boolean labelVisible, String initial, int maxLength, Optional multiline) implements InputControl {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Dialog.WIDTH_CODEC.optionalFieldOf("width", 200).forGetter(TextInputControl::width), TextCodecs.CODEC.fieldOf("label").forGetter(TextInputControl::label), Codec.BOOL.optionalFieldOf("label_visible", true).forGetter(TextInputControl::labelVisible), Codec.STRING.optionalFieldOf("initial", "").forGetter(TextInputControl::initial), Codecs.POSITIVE_INT.optionalFieldOf("max_length", 32).forGetter(TextInputControl::maxLength), TextInputControl.Multiline.CODEC.optionalFieldOf("multiline").forGetter(TextInputControl::multiline)).apply(instance, TextInputControl::new);
   }).validate((inputControl) -> {
      return inputControl.initial.length() > inputControl.maxLength() ? DataResult.error(() -> {
         return "Default text length exceeds allowed size";
      }) : DataResult.success(inputControl);
   });

   public TextInputControl(int i, Text text, boolean bl, String string, int j, Optional optional) {
      this.width = i;
      this.label = text;
      this.labelVisible = bl;
      this.initial = string;
      this.maxLength = j;
      this.multiline = optional;
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public int width() {
      return this.width;
   }

   public Text label() {
      return this.label;
   }

   public boolean labelVisible() {
      return this.labelVisible;
   }

   public String initial() {
      return this.initial;
   }

   public int maxLength() {
      return this.maxLength;
   }

   public Optional multiline() {
      return this.multiline;
   }

   public static record Multiline(Optional maxLines, Optional height) {
      public static final int MAX_HEIGHT = 512;
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codecs.POSITIVE_INT.optionalFieldOf("max_lines").forGetter(Multiline::maxLines), Codecs.rangedInt(1, 512).optionalFieldOf("height").forGetter(Multiline::height)).apply(instance, Multiline::new);
      });

      public Multiline(Optional optional, Optional optional2) {
         this.maxLines = optional;
         this.height = optional2;
      }

      public Optional maxLines() {
         return this.maxLines;
      }

      public Optional height() {
         return this.height;
      }
   }
}
