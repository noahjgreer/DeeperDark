package net.minecraft.command;

public class FallthroughCommandAction implements CommandAction {
   private static final FallthroughCommandAction INSTANCE = new FallthroughCommandAction();

   public static CommandAction getInstance() {
      return INSTANCE;
   }

   public void execute(CommandExecutionContext commandExecutionContext, Frame frame) {
      frame.fail();
      frame.doReturn();
   }
}
