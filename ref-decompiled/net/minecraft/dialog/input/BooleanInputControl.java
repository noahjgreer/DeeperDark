package net.minecraft.dialog.input;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record BooleanInputControl(Text label, boolean initial, String onTrue, String onFalse) implements InputControl {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(TextCodecs.CODEC.fieldOf("label").forGetter(BooleanInputControl::label), Codec.BOOL.optionalFieldOf("initial", false).forGetter(BooleanInputControl::initial), Codec.STRING.optionalFieldOf("on_true", "true").forGetter(BooleanInputControl::onTrue), Codec.STRING.optionalFieldOf("on_false", "false").forGetter(BooleanInputControl::onFalse)).apply(instance, BooleanInputControl::new);
   });

   public BooleanInputControl(Text text, boolean bl, String string, String string2) {
      this.label = text;
      this.initial = bl;
      this.onTrue = string;
      this.onFalse = string2;
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public Text label() {
      return this.label;
   }

   public boolean initial() {
      return this.initial;
   }

   public String onTrue() {
      return this.onTrue;
   }

   public String onFalse() {
      return this.onFalse;
   }
}
