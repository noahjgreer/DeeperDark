package net.minecraft.util.packrat;

import com.mojang.brigadier.StringReader;
import org.jetbrains.annotations.Nullable;

public class UnquotedStringParsingRule implements ParsingRule {
   private final int minLength;
   private final CursorExceptionType tooShortException;

   public UnquotedStringParsingRule(int minLength, CursorExceptionType tooShortException) {
      this.minLength = minLength;
      this.tooShortException = tooShortException;
   }

   @Nullable
   public String parse(ParsingState parsingState) {
      ((StringReader)parsingState.getReader()).skipWhitespace();
      int i = parsingState.getCursor();
      String string = ((StringReader)parsingState.getReader()).readUnquotedString();
      if (string.length() < this.minLength) {
         parsingState.getErrors().add(i, this.tooShortException);
         return null;
      } else {
         return string;
      }
   }

   // $FF: synthetic method
   @Nullable
   public Object parse(final ParsingState state) {
      return this.parse(state);
   }
}
