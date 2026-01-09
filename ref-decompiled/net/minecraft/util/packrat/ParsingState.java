package net.minecraft.util.packrat;

import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public interface ParsingState {
   ParseResults getResults();

   ParseErrorList getErrors();

   default Optional startParsing(ParsingRuleEntry rule) {
      Object object = this.parse(rule);
      if (object != null) {
         this.getErrors().setCursor(this.getCursor());
      }

      if (!this.getResults().areFramesPlacedCorrectly()) {
         throw new IllegalStateException("Malformed scope: " + String.valueOf(this.getResults()));
      } else {
         return Optional.ofNullable(object);
      }
   }

   @Nullable
   Object parse(ParsingRuleEntry rule);

   Object getReader();

   int getCursor();

   void setCursor(int cursor);

   Cut pushCutter();

   void popCutter();

   ParsingState getErrorSuppressingState();
}
