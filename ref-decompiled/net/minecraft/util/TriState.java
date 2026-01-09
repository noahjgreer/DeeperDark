package net.minecraft.util;

public enum TriState {
   TRUE,
   FALSE,
   DEFAULT;

   public boolean asBoolean(boolean fallback) {
      boolean var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = true;
            break;
         case 1:
            var10000 = false;
            break;
         default:
            var10000 = fallback;
      }

      return var10000;
   }

   // $FF: synthetic method
   private static TriState[] method_61347() {
      return new TriState[]{TRUE, FALSE, DEFAULT};
   }
}
