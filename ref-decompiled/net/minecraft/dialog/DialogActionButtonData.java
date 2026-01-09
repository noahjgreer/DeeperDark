package net.minecraft.dialog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.dialog.action.DialogAction;

public record DialogActionButtonData(DialogButtonData data, Optional action) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(DialogButtonData.CODEC.forGetter(DialogActionButtonData::data), DialogAction.CODEC.optionalFieldOf("action").forGetter(DialogActionButtonData::action)).apply(instance, DialogActionButtonData::new);
   });

   public DialogActionButtonData(DialogButtonData dialogButtonData, Optional optional) {
      this.data = dialogButtonData;
      this.action = optional;
   }

   public DialogButtonData data() {
      return this.data;
   }

   public Optional action() {
      return this.action;
   }
}
