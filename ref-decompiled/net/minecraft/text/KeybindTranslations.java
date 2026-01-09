package net.minecraft.text;

import java.util.function.Function;

public class KeybindTranslations {
   static Function factory = (key) -> {
      return () -> {
         return Text.literal(key);
      };
   };

   public static void setFactory(Function factory) {
      KeybindTranslations.factory = factory;
   }
}
