package net.minecraft.util.packrat;

import com.mojang.brigadier.StringReader;

public class ReaderBackedParsingState extends ParsingStateImpl {
   private final StringReader reader;

   public ReaderBackedParsingState(ParseErrorList errors, StringReader reader) {
      super(errors);
      this.reader = reader;
   }

   public StringReader getReader() {
      return this.reader;
   }

   public int getCursor() {
      return this.reader.getCursor();
   }

   public void setCursor(int cursor) {
      this.reader.setCursor(cursor);
   }

   // $FF: synthetic method
   public Object getReader() {
      return this.getReader();
   }
}
