package net.minecraft.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.function.Tracer;

public class FixedCommandAction implements SourcedCommandAction {
   private final String command;
   private final ExecutionFlags flags;
   private final CommandContext context;

   public FixedCommandAction(String command, ExecutionFlags flags, CommandContext context) {
      this.command = command;
      this.flags = flags;
      this.context = context;
   }

   public void execute(AbstractServerCommandSource abstractServerCommandSource, CommandExecutionContext commandExecutionContext, Frame frame) {
      commandExecutionContext.getProfiler().push(() -> {
         return "execute " + this.command;
      });

      try {
         commandExecutionContext.decrementCommandQuota();
         int i = ContextChain.runExecutable(this.context, abstractServerCommandSource, AbstractServerCommandSource.asResultConsumer(), this.flags.isSilent());
         Tracer tracer = commandExecutionContext.getTracer();
         if (tracer != null) {
            tracer.traceCommandEnd(frame.depth(), this.command, i);
         }
      } catch (CommandSyntaxException var9) {
         abstractServerCommandSource.handleException(var9, this.flags.isSilent(), commandExecutionContext.getTracer());
      } finally {
         commandExecutionContext.getProfiler().pop();
      }

   }

   // $FF: synthetic method
   public void execute(final Object object, final CommandExecutionContext commandExecutionContext, final Frame frame) {
      this.execute((AbstractServerCommandSource)object, commandExecutionContext, frame);
   }
}
