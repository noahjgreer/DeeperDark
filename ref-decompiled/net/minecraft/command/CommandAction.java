package net.minecraft.command;

@FunctionalInterface
public interface CommandAction {
   void execute(CommandExecutionContext context, Frame frame);
}
