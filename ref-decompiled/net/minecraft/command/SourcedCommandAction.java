package net.minecraft.command;

@FunctionalInterface
public interface SourcedCommandAction {
   void execute(Object source, CommandExecutionContext context, Frame frame);

   default CommandAction bind(Object source) {
      return (context, frame) -> {
         this.execute(source, context, frame);
      };
   }
}
