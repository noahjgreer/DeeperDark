package net.minecraft.command;

import com.google.common.collect.Queues;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.function.Procedure;
import net.minecraft.server.function.Tracer;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class CommandExecutionContext implements AutoCloseable {
   private static final int MAX_COMMAND_QUEUE_LENGTH = 10000000;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final int maxCommandChainLength;
   private final int forkLimit;
   private final Profiler profiler;
   @Nullable
   private Tracer tracer;
   private int commandsRemaining;
   private boolean queueOverflowed;
   private final Deque commandQueue = Queues.newArrayDeque();
   private final List pendingCommands = new ObjectArrayList();
   private int currentDepth;

   public CommandExecutionContext(int maxCommandChainLength, int maxCommandForkCount, Profiler profiler) {
      this.maxCommandChainLength = maxCommandChainLength;
      this.forkLimit = maxCommandForkCount;
      this.profiler = profiler;
      this.commandsRemaining = maxCommandChainLength;
   }

   private static Frame frame(CommandExecutionContext context, ReturnValueConsumer returnValueConsumer) {
      if (context.currentDepth == 0) {
         Deque var10004 = context.commandQueue;
         Objects.requireNonNull(var10004);
         return new Frame(0, returnValueConsumer, var10004::clear);
      } else {
         int i = context.currentDepth + 1;
         return new Frame(i, returnValueConsumer, context.getEscapeControl(i));
      }
   }

   public static void enqueueProcedureCall(CommandExecutionContext context, Procedure procedure, AbstractServerCommandSource source, ReturnValueConsumer returnValueConsumer) {
      context.enqueueCommand(new CommandQueueEntry(frame(context, returnValueConsumer), (new CommandFunctionAction(procedure, source.getReturnValueConsumer(), false)).bind(source)));
   }

   public static void enqueueCommand(CommandExecutionContext context, String command, ContextChain contextChain, AbstractServerCommandSource source, ReturnValueConsumer returnValueConsumer) {
      context.enqueueCommand(new CommandQueueEntry(frame(context, returnValueConsumer), new SingleCommandAction.SingleSource(command, contextChain, source)));
   }

   private void markQueueOverflowed() {
      this.queueOverflowed = true;
      this.pendingCommands.clear();
      this.commandQueue.clear();
   }

   public void enqueueCommand(CommandQueueEntry entry) {
      if (this.pendingCommands.size() + this.commandQueue.size() > 10000000) {
         this.markQueueOverflowed();
      }

      if (!this.queueOverflowed) {
         this.pendingCommands.add(entry);
      }

   }

   public void escape(int depth) {
      while(!this.commandQueue.isEmpty() && ((CommandQueueEntry)this.commandQueue.peek()).frame().depth() >= depth) {
         this.commandQueue.removeFirst();
      }

   }

   public Frame.Control getEscapeControl(int depth) {
      return () -> {
         this.escape(depth);
      };
   }

   public void run() {
      this.queuePendingCommands();

      while(true) {
         if (this.commandsRemaining <= 0) {
            LOGGER.info("Command execution stopped due to limit (executed {} commands)", this.maxCommandChainLength);
            break;
         }

         CommandQueueEntry commandQueueEntry = (CommandQueueEntry)this.commandQueue.pollFirst();
         if (commandQueueEntry == null) {
            return;
         }

         this.currentDepth = commandQueueEntry.frame().depth();
         commandQueueEntry.execute(this);
         if (this.queueOverflowed) {
            LOGGER.error("Command execution stopped due to command queue overflow (max {})", 10000000);
            break;
         }

         this.queuePendingCommands();
      }

      this.currentDepth = 0;
   }

   private void queuePendingCommands() {
      for(int i = this.pendingCommands.size() - 1; i >= 0; --i) {
         this.commandQueue.addFirst((CommandQueueEntry)this.pendingCommands.get(i));
      }

      this.pendingCommands.clear();
   }

   public void setTracer(@Nullable Tracer tracer) {
      this.tracer = tracer;
   }

   @Nullable
   public Tracer getTracer() {
      return this.tracer;
   }

   public Profiler getProfiler() {
      return this.profiler;
   }

   public int getForkLimit() {
      return this.forkLimit;
   }

   public void decrementCommandQuota() {
      --this.commandsRemaining;
   }

   public void close() {
      if (this.tracer != null) {
         this.tracer.close();
      }

   }
}
