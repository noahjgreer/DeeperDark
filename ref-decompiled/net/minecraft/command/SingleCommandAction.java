package net.minecraft.command;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.context.ContextChain.Stage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.function.Tracer;
import net.minecraft.text.Text;

public class SingleCommandAction {
   @VisibleForTesting
   public static final DynamicCommandExceptionType FORK_LIMIT_EXCEPTION = new DynamicCommandExceptionType((count) -> {
      return Text.stringifiedTranslatable("command.forkLimit", count);
   });
   private final String command;
   private final ContextChain contextChain;

   public SingleCommandAction(String command, ContextChain contextChain) {
      this.command = command;
      this.contextChain = contextChain;
   }

   protected void execute(AbstractServerCommandSource baseSource, List sources, CommandExecutionContext context, Frame frame, ExecutionFlags flags) {
      ContextChain contextChain = this.contextChain;
      ExecutionFlags executionFlags = flags;
      List list = sources;
      if (contextChain.getStage() != Stage.EXECUTE) {
         context.getProfiler().push(() -> {
            return "prepare " + this.command;
         });

         try {
            for(int i = context.getForkLimit(); contextChain.getStage() != Stage.EXECUTE; contextChain = contextChain.nextStage()) {
               CommandContext commandContext = contextChain.getTopContext();
               if (commandContext.isForked()) {
                  executionFlags = executionFlags.setSilent();
               }

               RedirectModifier redirectModifier = commandContext.getRedirectModifier();
               if (redirectModifier instanceof Forkable) {
                  Forkable forkable = (Forkable)redirectModifier;
                  forkable.execute(baseSource, (List)list, contextChain, executionFlags, ExecutionControl.of(context, frame));
                  return;
               }

               if (redirectModifier != null) {
                  context.decrementCommandQuota();
                  boolean bl = executionFlags.isSilent();
                  List list2 = new ObjectArrayList();
                  Iterator var14 = ((List)list).iterator();

                  while(var14.hasNext()) {
                     AbstractServerCommandSource abstractServerCommandSource = (AbstractServerCommandSource)var14.next();

                     try {
                        Collection collection = ContextChain.runModifier(commandContext, abstractServerCommandSource, (contextx, successful, returnValue) -> {
                        }, bl);
                        if (list2.size() + collection.size() >= i) {
                           baseSource.handleException(FORK_LIMIT_EXCEPTION.create(i), bl, context.getTracer());
                           return;
                        }

                        list2.addAll(collection);
                     } catch (CommandSyntaxException var20) {
                        abstractServerCommandSource.handleException(var20, bl, context.getTracer());
                        if (!bl) {
                           return;
                        }
                     }
                  }

                  list = list2;
               }
            }
         } finally {
            context.getProfiler().pop();
         }
      }

      if (((List)list).isEmpty()) {
         if (executionFlags.isInsideReturnRun()) {
            context.enqueueCommand(new CommandQueueEntry(frame, FallthroughCommandAction.getInstance()));
         }

      } else {
         CommandContext commandContext2 = contextChain.getTopContext();
         Command command = commandContext2.getCommand();
         if (command instanceof ControlFlowAware) {
            ControlFlowAware controlFlowAware = (ControlFlowAware)command;
            ExecutionControl executionControl = ExecutionControl.of(context, frame);
            Iterator var29 = ((List)list).iterator();

            while(var29.hasNext()) {
               AbstractServerCommandSource abstractServerCommandSource2 = (AbstractServerCommandSource)var29.next();
               controlFlowAware.execute(abstractServerCommandSource2, contextChain, executionFlags, executionControl);
            }
         } else {
            if (executionFlags.isInsideReturnRun()) {
               AbstractServerCommandSource abstractServerCommandSource3 = (AbstractServerCommandSource)((List)list).get(0);
               abstractServerCommandSource3 = abstractServerCommandSource3.withReturnValueConsumer(ReturnValueConsumer.chain(abstractServerCommandSource3.getReturnValueConsumer(), frame.returnValueConsumer()));
               list = List.of(abstractServerCommandSource3);
            }

            FixedCommandAction fixedCommandAction = new FixedCommandAction(this.command, executionFlags, commandContext2);
            SteppedCommandAction.enqueueCommands(context, frame, (List)list, (framex, source) -> {
               return new CommandQueueEntry(framex, fixedCommandAction.bind(source));
            });
         }

      }
   }

   protected void traceCommandStart(CommandExecutionContext context, Frame frame) {
      Tracer tracer = context.getTracer();
      if (tracer != null) {
         tracer.traceCommandStart(frame.depth(), this.command);
      }

   }

   public String toString() {
      return this.command;
   }

   public static class SingleSource extends SingleCommandAction implements CommandAction {
      private final AbstractServerCommandSource source;

      public SingleSource(String command, ContextChain contextChain, AbstractServerCommandSource source) {
         super(command, contextChain);
         this.source = source;
      }

      public void execute(CommandExecutionContext commandExecutionContext, Frame frame) {
         this.traceCommandStart(commandExecutionContext, frame);
         this.execute(this.source, List.of(this.source), commandExecutionContext, frame, ExecutionFlags.NONE);
      }
   }

   public static class MultiSource extends SingleCommandAction implements CommandAction {
      private final ExecutionFlags flags;
      private final AbstractServerCommandSource baseSource;
      private final List sources;

      public MultiSource(String command, ContextChain contextChain, ExecutionFlags flags, AbstractServerCommandSource baseSource, List sources) {
         super(command, contextChain);
         this.baseSource = baseSource;
         this.sources = sources;
         this.flags = flags;
      }

      public void execute(CommandExecutionContext commandExecutionContext, Frame frame) {
         this.execute(this.baseSource, this.sources, commandExecutionContext, frame, this.flags);
      }
   }

   public static class Sourced extends SingleCommandAction implements SourcedCommandAction {
      public Sourced(String string, ContextChain contextChain) {
         super(string, contextChain);
      }

      public void execute(AbstractServerCommandSource abstractServerCommandSource, CommandExecutionContext commandExecutionContext, Frame frame) {
         this.traceCommandStart(commandExecutionContext, frame);
         this.execute(abstractServerCommandSource, List.of(abstractServerCommandSource), commandExecutionContext, frame, ExecutionFlags.NONE);
      }

      // $FF: synthetic method
      public void execute(final Object object, final CommandExecutionContext commandExecutionContext, final Frame frame) {
         this.execute((AbstractServerCommandSource)object, commandExecutionContext, frame);
      }
   }
}
