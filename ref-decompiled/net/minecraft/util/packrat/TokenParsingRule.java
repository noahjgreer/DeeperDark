package net.minecraft.util.packrat;

import com.mojang.brigadier.StringReader;
import org.jetbrains.annotations.Nullable;

public abstract class TokenParsingRule implements ParsingRule {
   private final int minLength;
   private final int maxLength;
   private final CursorExceptionType tooShortException;

   public TokenParsingRule(int minLength, CursorExceptionType tooShortException) {
      this(minLength, Integer.MAX_VALUE, tooShortException);
   }

   public TokenParsingRule(int minLength, int maxLength, CursorExceptionType tooShortException) {
      this.minLength = minLength;
      this.maxLength = maxLength;
      this.tooShortException = tooShortException;
   }

   @Nullable
   public String parse(ParsingState parsingState) {
      StringReader stringReader = (StringReader)parsingState.getReader();
      String string = stringReader.getString();
      int i = stringReader.getCursor();

      int j;
      for(j = i; j < string.length() && this.isValidChar(string.charAt(j)) && j - i < this.maxLength; ++j) {
      }

      int k = j - i;
      if (k < this.minLength) {
         parsingState.getErrors().add(parsingState.getCursor(), this.tooShortException);
         return null;
      } else {
         stringReader.setCursor(j);
         return string.substring(i, j);
      }
   }

   protected abstract boolean isValidChar(char c);

   // $FF: synthetic method
   @Nullable
   public Object parse(final ParsingState state) {
      return this.parse(state);
   }
}
