package net.minecraft.command;

import net.minecraft.server.function.Tracer;
import org.jetbrains.annotations.Nullable;

public interface ExecutionControl {
   void enqueueAction(CommandAction action);

   void setTracer(@Nullable Tracer tracer);

   @Nullable
   Tracer getTracer();

   Frame getFrame();

   static ExecutionControl of(final CommandExecutionContext context, final Frame frame) {
      return new ExecutionControl() {
         public void enqueueAction(CommandAction action) {
            context.enqueueCommand(new CommandQueueEntry(frame, action));
         }

         public void setTracer(@Nullable Tracer tracer) {
            context.setTracer(tracer);
         }

         @Nullable
         public Tracer getTracer() {
            return context.getTracer();
         }

         public Frame getFrame() {
            return frame;
         }
      };
   }
}
