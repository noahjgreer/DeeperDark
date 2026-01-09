package net.minecraft.util.packrat;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public interface CursorExceptionType {
   Exception create(String input, int cursor);

   static CursorExceptionType create(SimpleCommandExceptionType type) {
      return (input, cursor) -> {
         return type.createWithContext(Literals.createReader(input, cursor));
      };
   }

   static CursorExceptionType create(DynamicCommandExceptionType type, String arg) {
      return (input, cursor) -> {
         return type.createWithContext(Literals.createReader(input, cursor), arg);
      };
   }
}
