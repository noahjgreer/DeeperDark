package net.minecraft.command;

public record CommandQueueEntry(Frame frame, CommandAction action) {
   public CommandQueueEntry(Frame frame, CommandAction commandAction) {
      this.frame = frame;
      this.action = commandAction;
   }

   public void execute(CommandExecutionContext context) {
      this.action.execute(context, this.frame);
   }

   public Frame frame() {
      return this.frame;
   }

   public CommandAction action() {
      return this.action;
   }
}
