package net.minecraft.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.function.Tracer;
import org.jetbrains.annotations.Nullable;

public interface ControlFlowAware {
   void execute(Object source, ContextChain contextChain, ExecutionFlags flags, ExecutionControl control);

   public abstract static class Helper implements ControlFlowAware {
      public final void execute(AbstractServerCommandSource abstractServerCommandSource, ContextChain contextChain, ExecutionFlags executionFlags, ExecutionControl executionControl) {
         try {
            this.executeInner(abstractServerCommandSource, contextChain, executionFlags, executionControl);
         } catch (CommandSyntaxException var6) {
            this.sendError(var6, abstractServerCommandSource, executionFlags, executionControl.getTracer());
            abstractServerCommandSource.getReturnValueConsumer().onFailure();
         }

      }

      protected void sendError(CommandSyntaxException exception, AbstractServerCommandSource source, ExecutionFlags flags, @Nullable Tracer tracer) {
         source.handleException(exception, flags.isSilent(), tracer);
      }

      protected abstract void executeInner(AbstractServerCommandSource source, ContextChain contextChain, ExecutionFlags flags, ExecutionControl control) throws CommandSyntaxException;
   }

   public interface Command extends com.mojang.brigadier.Command, ControlFlowAware {
      default int run(CommandContext context) throws CommandSyntaxException {
         throw new UnsupportedOperationException("This function should not run");
      }
   }
}
