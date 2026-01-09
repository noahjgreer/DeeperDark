package net.minecraft.util.packrat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.chars.CharList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Literals {
   static Term string(String string) {
      return new StringLiteral(string);
   }

   static Term character(final char c) {
      return new CharacterLiteral(CharList.of(c)) {
         protected boolean accepts(char cx) {
            return c == cx;
         }
      };
   }

   static Term character(final char c1, final char c2) {
      return new CharacterLiteral(CharList.of(c1, c2)) {
         protected boolean accepts(char c) {
            return c == c1 || c == c2;
         }
      };
   }

   static StringReader createReader(String string, int cursor) {
      StringReader stringReader = new StringReader(string);
      stringReader.setCursor(cursor);
      return stringReader;
   }

   public static final class StringLiteral implements Term {
      private final String value;
      private final CursorExceptionType exception;
      private final Suggestable suggestions;

      public StringLiteral(String value) {
         this.value = value;
         this.exception = CursorExceptionType.create(CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect(), value);
         this.suggestions = (state) -> {
            return Stream.of(value);
         };
      }

      public boolean matches(ParsingState state, ParseResults results, Cut cut) {
         ((StringReader)state.getReader()).skipWhitespace();
         int i = state.getCursor();
         String string = ((StringReader)state.getReader()).readUnquotedString();
         if (!string.equals(this.value)) {
            state.getErrors().add(i, this.suggestions, this.exception);
            return false;
         } else {
            return true;
         }
      }

      public String toString() {
         return "terminal[" + this.value + "]";
      }
   }

   public abstract static class CharacterLiteral implements Term {
      private final CursorExceptionType exception;
      private final Suggestable suggestions;

      public CharacterLiteral(CharList values) {
         String string = (String)values.intStream().mapToObj(Character::toString).collect(Collectors.joining("|"));
         this.exception = CursorExceptionType.create(CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect(), String.valueOf(string));
         this.suggestions = (state) -> {
            return values.intStream().mapToObj(Character::toString);
         };
      }

      public boolean matches(ParsingState state, ParseResults results, Cut cut) {
         ((StringReader)state.getReader()).skipWhitespace();
         int i = state.getCursor();
         if (((StringReader)state.getReader()).canRead() && this.accepts(((StringReader)state.getReader()).read())) {
            return true;
         } else {
            state.getErrors().add(i, this.suggestions, this.exception);
            return false;
         }
      }

      protected abstract boolean accepts(char c);
   }
}
