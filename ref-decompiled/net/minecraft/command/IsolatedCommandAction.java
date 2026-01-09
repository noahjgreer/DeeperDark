package net.minecraft.command;

import java.util.function.Consumer;

public class IsolatedCommandAction implements CommandAction {
   private final Consumer controlConsumer;
   private final ReturnValueConsumer returnValueConsumer;

   public IsolatedCommandAction(Consumer controlConsumer, ReturnValueConsumer returnValueConsumer) {
      this.controlConsumer = controlConsumer;
      this.returnValueConsumer = returnValueConsumer;
   }

   public void execute(CommandExecutionContext commandExecutionContext, Frame frame) {
      int i = frame.depth() + 1;
      Frame frame2 = new Frame(i, this.returnValueConsumer, commandExecutionContext.getEscapeControl(i));
      this.controlConsumer.accept(ExecutionControl.of(commandExecutionContext, frame2));
   }
}
