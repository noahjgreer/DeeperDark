package net.minecraft.dialog;

import com.mojang.serialization.MapCodec;
import net.minecraft.dialog.input.BooleanInputControl;
import net.minecraft.dialog.input.NumberRangeInputControl;
import net.minecraft.dialog.input.SingleOptionInputControl;
import net.minecraft.dialog.input.TextInputControl;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class InputControlTypes {
   public static MapCodec registerAndGetDefault(Registry registry) {
      Registry.register(registry, (Identifier)Identifier.ofVanilla("boolean"), BooleanInputControl.CODEC);
      Registry.register(registry, (Identifier)Identifier.ofVanilla("number_range"), NumberRangeInputControl.CODEC);
      Registry.register(registry, (Identifier)Identifier.ofVanilla("single_option"), SingleOptionInputControl.CODEC);
      return (MapCodec)Registry.register(registry, (Identifier)Identifier.ofVanilla("text"), TextInputControl.CODEC);
   }
}
