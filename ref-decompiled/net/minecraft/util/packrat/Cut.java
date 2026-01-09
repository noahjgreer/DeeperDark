package net.minecraft.util.packrat;

public interface Cut {
   Cut NOOP = new Cut() {
      public void cut() {
      }

      public boolean isCut() {
         return false;
      }
   };

   void cut();

   boolean isCut();
}
