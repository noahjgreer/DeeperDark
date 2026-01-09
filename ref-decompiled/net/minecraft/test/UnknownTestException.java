package net.minecraft.test;

import net.minecraft.text.Text;

public class UnknownTestException extends TestException {
   private final Throwable throwable;

   public UnknownTestException(Throwable throwable) {
      super(throwable.getMessage());
      this.throwable = throwable;
   }

   public Text getText() {
      return Text.translatable("test.error.unknown", this.throwable.getMessage());
   }
}
