package net.minecraft.util.packrat;

public record ParseError(int cursor, Suggestable suggestions, Object reason) {
   public ParseError(int i, Suggestable suggestable, Object object) {
      this.cursor = i;
      this.suggestions = suggestable;
      this.reason = object;
   }

   public int cursor() {
      return this.cursor;
   }

   public Suggestable suggestions() {
      return this.suggestions;
   }

   public Object reason() {
      return this.reason;
   }
}
