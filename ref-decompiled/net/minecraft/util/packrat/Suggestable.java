package net.minecraft.util.packrat;

import java.util.stream.Stream;

public interface Suggestable {
   Stream possibleValues(ParsingState state);

   static Suggestable empty() {
      return (state) -> {
         return Stream.empty();
      };
   }
}
