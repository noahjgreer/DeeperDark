package net.minecraft.util.packrat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Util;

public interface ParseErrorList {
   void add(int cursor, Suggestable suggestions, Object reason);

   default void add(int cursor, Object reason) {
      this.add(cursor, Suggestable.empty(), reason);
   }

   void setCursor(int cursor);

   public static class Impl implements ParseErrorList {
      private Entry[] errors = new Entry[16];
      private int topIndex;
      private int cursor = -1;

      private void moveCursor(int cursor) {
         if (cursor > this.cursor) {
            this.cursor = cursor;
            this.topIndex = 0;
         }

      }

      public void setCursor(int cursor) {
         this.moveCursor(cursor);
      }

      public void add(int cursor, Suggestable suggestions, Object reason) {
         this.moveCursor(cursor);
         if (cursor == this.cursor) {
            this.add(suggestions, reason);
         }

      }

      private void add(Suggestable suggestions, Object reason) {
         int i = this.errors.length;
         int j;
         if (this.topIndex >= i) {
            j = Util.nextCapacity(i, this.topIndex + 1);
            Entry[] entrys = new Entry[j];
            System.arraycopy(this.errors, 0, entrys, 0, i);
            this.errors = entrys;
         }

         j = this.topIndex++;
         Entry entry = this.errors[j];
         if (entry == null) {
            entry = new Entry();
            this.errors[j] = entry;
         }

         entry.suggestions = suggestions;
         entry.reason = reason;
      }

      public List getErrors() {
         int i = this.topIndex;
         if (i == 0) {
            return List.of();
         } else {
            List list = new ArrayList(i);

            for(int j = 0; j < i; ++j) {
               Entry entry = this.errors[j];
               list.add(new ParseError(this.cursor, entry.suggestions, entry.reason));
            }

            return list;
         }
      }

      public int getCursor() {
         return this.cursor;
      }

      static class Entry {
         Suggestable suggestions = Suggestable.empty();
         Object reason = "empty";
      }
   }

   public static class Noop implements ParseErrorList {
      public void add(int cursor, Suggestable suggestions, Object reason) {
      }

      public void setCursor(int cursor) {
      }
   }
}
