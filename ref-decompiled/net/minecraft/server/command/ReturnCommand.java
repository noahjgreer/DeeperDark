package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.ContextChain;
import java.util.List;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.ExecutionFlags;
import net.minecraft.command.FallthroughCommandAction;
import net.minecraft.command.Forkable;
import net.minecraft.command.Frame;
import net.minecraft.command.SingleCommandAction;

public class ReturnCommand {
   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)LiteralArgumentBuilder.literal("return").requires(CommandManager.requirePermissionLevel(2))).then(RequiredArgumentBuilder.argument("value", IntegerArgumentType.integer()).executes(new ValueCommand()))).then(LiteralArgumentBuilder.literal("fail").executes(new FailCommand()))).then(LiteralArgumentBuilder.literal("run").forward(dispatcher.getRoot(), new ReturnRunRedirector(), false)));
   }

   private static class ValueCommand implements ControlFlowAware.Command {
      ValueCommand() {
      }

      public void execute(AbstractServerCommandSource abstractServerCommandSource, ContextChain contextChain, ExecutionFlags executionFlags, ExecutionControl executionControl) {
         int i = IntegerArgumentType.getInteger(contextChain.getTopContext(), "value");
         abstractServerCommandSource.getReturnValueConsumer().onSuccess(i);
         Frame frame = executionControl.getFrame();
         frame.succeed(i);
         frame.doReturn();
      }
   }

   private static class FailCommand implements ControlFlowAware.Command {
      FailCommand() {
      }

      public void execute(AbstractServerCommandSource abstractServerCommandSource, ContextChain contextChain, ExecutionFlags executionFlags, ExecutionControl executionControl) {
         abstractServerCommandSource.getReturnValueConsumer().onFailure();
         Frame frame = executionControl.getFrame();
         frame.fail();
         frame.doReturn();
      }
   }

   private static class ReturnRunRedirector implements Forkable.RedirectModifier {
      ReturnRunRedirector() {
      }

      public void execute(AbstractServerCommandSource abstractServerCommandSource, List list, ContextChain contextChain, ExecutionFlags executionFlags, ExecutionControl executionControl) {
         if (list.isEmpty()) {
            if (executionFlags.isInsideReturnRun()) {
               executionControl.enqueueAction(FallthroughCommandAction.getInstance());
            }

         } else {
            executionControl.getFrame().doReturn();
            ContextChain contextChain2 = contextChain.nextStage();
            String string = contextChain2.getTopContext().getInput();
            executionControl.enqueueAction(new SingleCommandAction.MultiSource(string, contextChain2, executionFlags.setInsideReturnRun(), abstractServerCommandSource, list));
         }
      }
   }
}
