package net.minecraft.command;

import java.util.List;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.function.Procedure;
import net.minecraft.server.function.Tracer;

public class CommandFunctionAction implements SourcedCommandAction {
   private final Procedure function;
   private final ReturnValueConsumer returnValueConsumer;
   private final boolean propagateReturn;

   public CommandFunctionAction(Procedure function, ReturnValueConsumer returnValueConsumer, boolean propagateReturn) {
      this.function = function;
      this.returnValueConsumer = returnValueConsumer;
      this.propagateReturn = propagateReturn;
   }

   public void execute(AbstractServerCommandSource abstractServerCommandSource, CommandExecutionContext commandExecutionContext, Frame frame) {
      commandExecutionContext.decrementCommandQuota();
      List list = this.function.entries();
      Tracer tracer = commandExecutionContext.getTracer();
      if (tracer != null) {
         tracer.traceFunctionCall(frame.depth(), this.function.id(), this.function.entries().size());
      }

      int i = frame.depth() + 1;
      Frame.Control control = this.propagateReturn ? frame.frameControl() : commandExecutionContext.getEscapeControl(i);
      Frame frame2 = new Frame(i, this.returnValueConsumer, control);
      SteppedCommandAction.enqueueCommands(commandExecutionContext, frame2, list, (framex, action) -> {
         return new CommandQueueEntry(framex, action.bind(abstractServerCommandSource));
      });
   }

   // $FF: synthetic method
   public void execute(final Object object, final CommandExecutionContext commandExecutionContext, final Frame frame) {
      this.execute((AbstractServerCommandSource)object, commandExecutionContext, frame);
   }
}
