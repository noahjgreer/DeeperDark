package net.minecraft.util.packrat;

import java.util.stream.Stream;
import net.minecraft.util.Identifier;

public interface IdentifierSuggestable extends Suggestable {
   Stream possibleIds();

   default Stream possibleValues(ParsingState parsingState) {
      return this.possibleIds().map(Identifier::toString);
   }
}
