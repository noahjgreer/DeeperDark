/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.context.ContextChain
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.command.CommandFunctionAction;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.ExecutionFlags;
import net.minecraft.command.Frame;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.command.permission.LeveledPermissionPredicate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.command.DebugCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.MacroException;
import net.minecraft.server.function.Procedure;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

static class DebugCommand.Command
extends ControlFlowAware.Helper<ServerCommandSource>
implements ControlFlowAware.Command<ServerCommandSource> {
    DebugCommand.Command() {
    }

    @Override
    public void executeInner(ServerCommandSource serverCommandSource, ContextChain<ServerCommandSource> contextChain, ExecutionFlags executionFlags, ExecutionControl<ServerCommandSource> executionControl) throws CommandSyntaxException {
        if (executionFlags.isInsideReturnRun()) {
            throw NO_RETURN_RUN_EXCEPTION.create();
        }
        if (executionControl.getTracer() != null) {
            throw NO_RECURSION_EXCEPTION.create();
        }
        CommandContext commandContext = contextChain.getTopContext();
        Collection<CommandFunction<ServerCommandSource>> collection = CommandFunctionArgumentType.getFunctions((CommandContext<ServerCommandSource>)commandContext, "name");
        MinecraftServer minecraftServer = serverCommandSource.getServer();
        String string = "debug-trace-" + Util.getFormattedCurrentTime() + ".txt";
        CommandDispatcher<ServerCommandSource> commandDispatcher = serverCommandSource.getServer().getCommandFunctionManager().getDispatcher();
        int i = 0;
        try {
            Path path = minecraftServer.getPath("debug");
            Files.createDirectories(path, new FileAttribute[0]);
            final PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(path.resolve(string), StandardCharsets.UTF_8, new OpenOption[0]));
            DebugCommand.Tracer tracer = new DebugCommand.Tracer(printWriter);
            executionControl.setTracer(tracer);
            for (final CommandFunction<ServerCommandSource> commandFunction : collection) {
                try {
                    ServerCommandSource serverCommandSource2 = serverCommandSource.withOutput(tracer).withAdditionalPermissions(LeveledPermissionPredicate.GAMEMASTERS);
                    Procedure<ServerCommandSource> procedure = commandFunction.withMacroReplaced(null, commandDispatcher);
                    executionControl.enqueueAction(new CommandFunctionAction<ServerCommandSource>(this, procedure, ReturnValueConsumer.EMPTY, false){

                        @Override
                        public void execute(ServerCommandSource serverCommandSource, CommandExecutionContext<ServerCommandSource> commandExecutionContext, Frame frame) {
                            printWriter.println(commandFunction.id());
                            super.execute(serverCommandSource, commandExecutionContext, frame);
                        }

                        @Override
                        public /* synthetic */ void execute(Object object, CommandExecutionContext commandExecutionContext, Frame frame) {
                            this.execute((ServerCommandSource)object, (CommandExecutionContext<ServerCommandSource>)commandExecutionContext, frame);
                        }
                    }.bind(serverCommandSource2));
                    i += procedure.entries().size();
                }
                catch (MacroException macroException) {
                    serverCommandSource.sendError(macroException.getMessage());
                }
            }
        }
        catch (IOException | UncheckedIOException exception) {
            LOGGER.warn("Tracing failed", (Throwable)exception);
            serverCommandSource.sendError(Text.translatable("commands.debug.function.traceFailed"));
        }
        int j = i;
        executionControl.enqueueAction((context, frame) -> {
            if (collection.size() == 1) {
                serverCommandSource.sendFeedback(() -> Text.translatable("commands.debug.function.success.single", j, Text.of(((CommandFunction)collection.iterator().next()).id()), string), true);
            } else {
                serverCommandSource.sendFeedback(() -> Text.translatable("commands.debug.function.success.multiple", j, collection.size(), string), true);
            }
        });
    }

    @Override
    public /* synthetic */ void executeInner(AbstractServerCommandSource source, ContextChain contextChain, ExecutionFlags flags, ExecutionControl control) throws CommandSyntaxException {
        this.executeInner((ServerCommandSource)source, (ContextChain<ServerCommandSource>)contextChain, flags, (ExecutionControl<ServerCommandSource>)control);
    }
}
