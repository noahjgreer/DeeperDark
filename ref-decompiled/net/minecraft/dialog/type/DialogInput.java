package net.minecraft.dialog.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.dialog.action.ParsedTemplate;
import net.minecraft.dialog.input.InputControl;

public record DialogInput(String key, InputControl control) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(ParsedTemplate.NAME_CODEC.fieldOf("key").forGetter(DialogInput::key), InputControl.CODEC.forGetter(DialogInput::control)).apply(instance, DialogInput::new);
   });

   public DialogInput(String string, InputControl inputControl) {
      this.key = string;
      this.control = inputControl;
   }

   public String key() {
      return this.key;
   }

   public InputControl control() {
      return this.control;
   }
}
