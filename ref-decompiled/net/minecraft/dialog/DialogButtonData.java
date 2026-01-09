package net.minecraft.dialog;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record DialogButtonData(Text label, Optional tooltip, int width) {
   public static final int DEFAULT_WIDTH = 150;
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(TextCodecs.CODEC.fieldOf("label").forGetter(DialogButtonData::label), TextCodecs.CODEC.optionalFieldOf("tooltip").forGetter(DialogButtonData::tooltip), Dialog.WIDTH_CODEC.optionalFieldOf("width", 150).forGetter(DialogButtonData::width)).apply(instance, DialogButtonData::new);
   });

   public DialogButtonData(Text label, int width) {
      this(label, Optional.empty(), width);
   }

   public DialogButtonData(Text text, Optional optional, int i) {
      this.label = text;
      this.tooltip = optional;
      this.width = i;
   }

   public Text label() {
      return this.label;
   }

   public Optional tooltip() {
      return this.tooltip;
   }

   public int width() {
      return this.width;
   }
}
