package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.command.CommandFunctionAction;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.ExecutionFlags;
import net.minecraft.command.Frame;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.MacroException;
import net.minecraft.server.function.Procedure;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.ProfileResult;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class DebugCommand {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final SimpleCommandExceptionType NOT_RUNNING_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.debug.notRunning"));
   private static final SimpleCommandExceptionType ALREADY_RUNNING_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.debug.alreadyRunning"));
   static final SimpleCommandExceptionType NO_RECURSION_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.debug.function.noRecursion"));
   static final SimpleCommandExceptionType NO_RETURN_RUN_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.debug.function.noReturnRun"));

   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("debug").requires(CommandManager.requirePermissionLevel(3))).then(CommandManager.literal("start").executes((context) -> {
         return executeStart((ServerCommandSource)context.getSource());
      }))).then(CommandManager.literal("stop").executes((context) -> {
         return executeStop((ServerCommandSource)context.getSource());
      }))).then(((LiteralArgumentBuilder)CommandManager.literal("function").requires(CommandManager.requirePermissionLevel(3))).then(CommandManager.argument("name", CommandFunctionArgumentType.commandFunction()).suggests(FunctionCommand.SUGGESTION_PROVIDER).executes(new Command()))));
   }

   private static int executeStart(ServerCommandSource source) throws CommandSyntaxException {
      MinecraftServer minecraftServer = source.getServer();
      if (minecraftServer.isDebugRunning()) {
         throw ALREADY_RUNNING_EXCEPTION.create();
      } else {
         minecraftServer.startDebug();
         source.sendFeedback(() -> {
            return Text.translatable("commands.debug.started");
         }, true);
         return 0;
      }
   }

   private static int executeStop(ServerCommandSource source) throws CommandSyntaxException {
      MinecraftServer minecraftServer = source.getServer();
      if (!minecraftServer.isDebugRunning()) {
         throw NOT_RUNNING_EXCEPTION.create();
      } else {
         ProfileResult profileResult = minecraftServer.stopDebug();
         double d = (double)profileResult.getTimeSpan() / (double)TimeHelper.SECOND_IN_NANOS;
         double e = (double)profileResult.getTickSpan() / d;
         source.sendFeedback(() -> {
            return Text.translatable("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", d), profileResult.getTickSpan(), String.format(Locale.ROOT, "%.2f", e));
         }, true);
         return (int)e;
      }
   }

   static class Command extends ControlFlowAware.Helper implements ControlFlowAware.Command {
      public void executeInner(ServerCommandSource serverCommandSource, ContextChain contextChain, ExecutionFlags executionFlags, ExecutionControl executionControl) throws CommandSyntaxException {
         if (executionFlags.isInsideReturnRun()) {
            throw DebugCommand.NO_RETURN_RUN_EXCEPTION.create();
         } else if (executionControl.getTracer() != null) {
            throw DebugCommand.NO_RECURSION_EXCEPTION.create();
         } else {
            CommandContext commandContext = contextChain.getTopContext();
            Collection collection = CommandFunctionArgumentType.getFunctions(commandContext, "name");
            MinecraftServer minecraftServer = serverCommandSource.getServer();
            String string = "debug-trace-" + Util.getFormattedCurrentTime() + ".txt";
            CommandDispatcher commandDispatcher = serverCommandSource.getServer().getCommandFunctionManager().getDispatcher();
            int i = 0;

            try {
               Path path = minecraftServer.getPath("debug");
               Files.createDirectories(path);
               final PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(path.resolve(string), StandardCharsets.UTF_8));
               Tracer tracer = new Tracer(printWriter);
               executionControl.setTracer(tracer);
               Iterator var14 = collection.iterator();

               while(var14.hasNext()) {
                  final CommandFunction commandFunction = (CommandFunction)var14.next();

                  try {
                     ServerCommandSource serverCommandSource2 = serverCommandSource.withOutput(tracer).withMaxLevel(2);
                     Procedure procedure = commandFunction.withMacroReplaced((NbtCompound)null, commandDispatcher);
                     executionControl.enqueueAction((new CommandFunctionAction(this, procedure, ReturnValueConsumer.EMPTY, false) {
                        public void execute(ServerCommandSource serverCommandSource, CommandExecutionContext commandExecutionContext, Frame frame) {
                           printWriter.println(commandFunction.id());
                           super.execute((AbstractServerCommandSource)serverCommandSource, commandExecutionContext, frame);
                        }

                        // $FF: synthetic method
                        public void execute(final Object object, final CommandExecutionContext commandExecutionContext, final Frame frame) {
                           this.execute((ServerCommandSource)object, commandExecutionContext, frame);
                        }
                     }).bind(serverCommandSource2));
                     i += procedure.entries().size();
                  } catch (MacroException var18) {
                     serverCommandSource.sendError(var18.getMessage());
                  }
               }
            } catch (IOException | UncheckedIOException var19) {
               DebugCommand.LOGGER.warn("Tracing failed", var19);
               serverCommandSource.sendError(Text.translatable("commands.debug.function.traceFailed"));
            }

            executionControl.enqueueAction((context, frame) -> {
               if (collection.size() == 1) {
                  serverCommandSource.sendFeedback(() -> {
                     return Text.translatable("commands.debug.function.success.single", i, Text.of(((CommandFunction)collection.iterator().next()).id()), string);
                  }, true);
               } else {
                  serverCommandSource.sendFeedback(() -> {
                     return Text.translatable("commands.debug.function.success.multiple", i, collection.size(), string);
                  }, true);
               }

            });
         }
      }

      // $FF: synthetic method
      public void executeInner(final AbstractServerCommandSource source, final ContextChain contextChain, final ExecutionFlags flags, final ExecutionControl control) throws CommandSyntaxException {
         this.executeInner((ServerCommandSource)source, contextChain, flags, control);
      }
   }

   private static class Tracer implements CommandOutput, net.minecraft.server.function.Tracer {
      public static final int MARGIN = 1;
      private final PrintWriter writer;
      private int lastIndentWidth;
      private boolean expectsCommandResult;

      Tracer(PrintWriter writer) {
         this.writer = writer;
      }

      private void writeIndent(int width) {
         this.writeIndentWithoutRememberingWidth(width);
         this.lastIndentWidth = width;
      }

      private void writeIndentWithoutRememberingWidth(int width) {
         for(int i = 0; i < width + 1; ++i) {
            this.writer.write("    ");
         }

      }

      private void writeNewLine() {
         if (this.expectsCommandResult) {
            this.writer.println();
            this.expectsCommandResult = false;
         }

      }

      public void traceCommandStart(int depth, String command) {
         this.writeNewLine();
         this.writeIndent(depth);
         this.writer.print("[C] ");
         this.writer.print(command);
         this.expectsCommandResult = true;
      }

      public void traceCommandEnd(int depth, String command, int result) {
         if (this.expectsCommandResult) {
            this.writer.print(" -> ");
            this.writer.println(result);
            this.expectsCommandResult = false;
         } else {
            this.writeIndent(depth);
            this.writer.print("[R = ");
            this.writer.print(result);
            this.writer.print("] ");
            this.writer.println(command);
         }

      }

      public void traceFunctionCall(int depth, Identifier function, int size) {
         this.writeNewLine();
         this.writeIndent(depth);
         this.writer.print("[F] ");
         this.writer.print(function);
         this.writer.print(" size=");
         this.writer.println(size);
      }

      public void traceError(String message) {
         this.writeNewLine();
         this.writeIndent(this.lastIndentWidth + 1);
         this.writer.print("[E] ");
         this.writer.print(message);
      }

      public void sendMessage(Text message) {
         this.writeNewLine();
         this.writeIndentWithoutRememberingWidth(this.lastIndentWidth + 1);
         this.writer.print("[M] ");
         this.writer.println(message.getString());
      }

      public boolean shouldReceiveFeedback() {
         return true;
      }

      public boolean shouldTrackOutput() {
         return true;
      }

      public boolean shouldBroadcastConsoleToOps() {
         return false;
      }

      public boolean cannotBeSilenced() {
         return true;
      }

      public void close() {
         IOUtils.closeQuietly(this.writer);
      }
   }
}
