package net.minecraft.client.option;

import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

@Environment(EnvType.CLIENT)
public enum GraphicsMode implements TranslatableOption {
   FAST(0, "options.graphics.fast"),
   FANCY(1, "options.graphics.fancy"),
   FABULOUS(2, "options.graphics.fabulous");

   private static final IntFunction BY_ID = ValueLists.createIndexToValueFunction(GraphicsMode::getId, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.WRAP);
   private final int id;
   private final String translationKey;

   private GraphicsMode(final int id, final String translationKey) {
      this.id = id;
      this.translationKey = translationKey;
   }

   public int getId() {
      return this.id;
   }

   public String getTranslationKey() {
      return this.translationKey;
   }

   public String toString() {
      String var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = "fast";
            break;
         case 1:
            var10000 = "fancy";
            break;
         case 2:
            var10000 = "fabulous";
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static GraphicsMode byId(int id) {
      return (GraphicsMode)BY_ID.apply(id);
   }

   // $FF: synthetic method
   private static GraphicsMode[] method_36861() {
      return new GraphicsMode[]{FAST, FANCY, FABULOUS};
   }
}
