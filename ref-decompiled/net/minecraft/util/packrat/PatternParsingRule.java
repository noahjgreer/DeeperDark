package net.minecraft.util.packrat;

import com.mojang.brigadier.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PatternParsingRule implements ParsingRule {
   private final Pattern pattern;
   private final CursorExceptionType exception;

   public PatternParsingRule(Pattern pattern, CursorExceptionType exception) {
      this.pattern = pattern;
      this.exception = exception;
   }

   public String parse(ParsingState parsingState) {
      StringReader stringReader = (StringReader)parsingState.getReader();
      String string = stringReader.getString();
      Matcher matcher = this.pattern.matcher(string).region(stringReader.getCursor(), string.length());
      if (!matcher.lookingAt()) {
         parsingState.getErrors().add(parsingState.getCursor(), this.exception);
         return null;
      } else {
         stringReader.setCursor(matcher.end());
         return matcher.group(0);
      }
   }

   // $FF: synthetic method
   public Object parse(final ParsingState state) {
      return this.parse(state);
   }
}
