package net.minecraft.util.packrat;

import com.mojang.brigadier.StringReader;
import org.jetbrains.annotations.Nullable;

public abstract class NumeralParsingRule implements ParsingRule {
   private final CursorExceptionType invalidCharException;
   private final CursorExceptionType unexpectedUnderscoreException;

   public NumeralParsingRule(CursorExceptionType invalidCharException, CursorExceptionType unexpectedUnderscoreException) {
      this.invalidCharException = invalidCharException;
      this.unexpectedUnderscoreException = unexpectedUnderscoreException;
   }

   @Nullable
   public String parse(ParsingState parsingState) {
      StringReader stringReader = (StringReader)parsingState.getReader();
      stringReader.skipWhitespace();
      String string = stringReader.getString();
      int i = stringReader.getCursor();

      int j;
      for(j = i; j < string.length() && this.accepts(string.charAt(j)); ++j) {
      }

      int k = j - i;
      if (k == 0) {
         parsingState.getErrors().add(parsingState.getCursor(), this.invalidCharException);
         return null;
      } else if (string.charAt(i) != '_' && string.charAt(j - 1) != '_') {
         stringReader.setCursor(j);
         return string.substring(i, j);
      } else {
         parsingState.getErrors().add(parsingState.getCursor(), this.unexpectedUnderscoreException);
         return null;
      }
   }

   protected abstract boolean accepts(char c);

   // $FF: synthetic method
   @Nullable
   public Object parse(final ParsingState state) {
      return this.parse(state);
   }
}
