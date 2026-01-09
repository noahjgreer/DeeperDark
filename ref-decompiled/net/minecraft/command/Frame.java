package net.minecraft.command;

public record Frame(int depth, ReturnValueConsumer returnValueConsumer, Control frameControl) {
   public Frame(int i, ReturnValueConsumer returnValueConsumer, Control control) {
      this.depth = i;
      this.returnValueConsumer = returnValueConsumer;
      this.frameControl = control;
   }

   public void succeed(int returnValue) {
      this.returnValueConsumer.onSuccess(returnValue);
   }

   public void fail() {
      this.returnValueConsumer.onFailure();
   }

   public void doReturn() {
      this.frameControl.discard();
   }

   public int depth() {
      return this.depth;
   }

   public ReturnValueConsumer returnValueConsumer() {
      return this.returnValueConsumer;
   }

   public Control frameControl() {
      return this.frameControl;
   }

   @FunctionalInterface
   public interface Control {
      void discard();
   }
}
