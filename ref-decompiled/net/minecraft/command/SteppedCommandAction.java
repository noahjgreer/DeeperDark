package net.minecraft.command;

import java.util.List;

public class SteppedCommandAction implements CommandAction {
   private final ActionWrapper wrapper;
   private final List actions;
   private final CommandQueueEntry selfCommandQueueEntry;
   private int nextActionIndex;

   private SteppedCommandAction(ActionWrapper wrapper, List actions, Frame frame) {
      this.wrapper = wrapper;
      this.actions = actions;
      this.selfCommandQueueEntry = new CommandQueueEntry(frame, this);
   }

   public void execute(CommandExecutionContext commandExecutionContext, Frame frame) {
      Object object = this.actions.get(this.nextActionIndex);
      commandExecutionContext.enqueueCommand(this.wrapper.create(frame, object));
      if (++this.nextActionIndex < this.actions.size()) {
         commandExecutionContext.enqueueCommand(this.selfCommandQueueEntry);
      }

   }

   public static void enqueueCommands(CommandExecutionContext context, Frame frame, List actions, ActionWrapper wrapper) {
      int i = actions.size();
      switch (i) {
         case 0:
            break;
         case 1:
            context.enqueueCommand(wrapper.create(frame, actions.get(0)));
            break;
         case 2:
            context.enqueueCommand(wrapper.create(frame, actions.get(0)));
            context.enqueueCommand(wrapper.create(frame, actions.get(1)));
            break;
         default:
            context.enqueueCommand((new SteppedCommandAction(wrapper, actions, frame)).selfCommandQueueEntry);
      }

   }

   @FunctionalInterface
   public interface ActionWrapper {
      CommandQueueEntry create(Frame frame, Object action);
   }
}
