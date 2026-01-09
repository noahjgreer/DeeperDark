package net.minecraft.util;

import org.jetbrains.annotations.Nullable;

public class ThrowableDeliverer {
   @Nullable
   private Throwable throwable;

   public void add(Throwable throwable) {
      if (this.throwable == null) {
         this.throwable = throwable;
      } else {
         this.throwable.addSuppressed(throwable);
      }

   }

   public void deliver() throws Throwable {
      if (this.throwable != null) {
         throw this.throwable;
      }
   }
}
