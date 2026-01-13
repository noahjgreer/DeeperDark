/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.context.ContextChain
 */
package net.minecraft.server.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import java.util.List;
import java.util.function.IntPredicate;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.ExecutionFlags;
import net.minecraft.command.Forkable;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.command.ExecuteCommand;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;

static class ExecuteCommand.IfUnlessRedirector
implements Forkable.RedirectModifier<ServerCommandSource> {
    private final IntPredicate predicate;

    ExecuteCommand.IfUnlessRedirector(boolean success) {
        this.predicate = success ? result -> result != 0 : result -> result == 0;
    }

    @Override
    public void execute(ServerCommandSource serverCommandSource, List<ServerCommandSource> list, ContextChain<ServerCommandSource> contextChain, ExecutionFlags executionFlags, ExecutionControl<ServerCommandSource> executionControl) {
        ExecuteCommand.enqueueExecutions(serverCommandSource, list, FunctionCommand::createFunctionCommandSource, this.predicate, contextChain, null, executionControl, context -> CommandFunctionArgumentType.getFunctions((CommandContext<ServerCommandSource>)context, "name"), executionFlags);
    }
}
