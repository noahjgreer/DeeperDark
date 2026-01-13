/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.context.ContextChain
 */
package net.minecraft.server.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.ExecutionFlags;
import net.minecraft.command.Frame;
import net.minecraft.server.command.AbstractServerCommandSource;

static class ReturnCommand.ValueCommand<T extends AbstractServerCommandSource<T>>
implements ControlFlowAware.Command<T> {
    ReturnCommand.ValueCommand() {
    }

    @Override
    public void execute(T abstractServerCommandSource, ContextChain<T> contextChain, ExecutionFlags executionFlags, ExecutionControl<T> executionControl) {
        int i = IntegerArgumentType.getInteger((CommandContext)contextChain.getTopContext(), (String)"value");
        abstractServerCommandSource.getReturnValueConsumer().onSuccess(i);
        Frame frame = executionControl.getFrame();
        frame.succeed(i);
        frame.doReturn();
    }
}
