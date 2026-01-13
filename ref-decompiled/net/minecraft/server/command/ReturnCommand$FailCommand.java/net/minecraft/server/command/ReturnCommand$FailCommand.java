/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.ContextChain
 */
package net.minecraft.server.command;

import com.mojang.brigadier.context.ContextChain;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.ExecutionFlags;
import net.minecraft.command.Frame;
import net.minecraft.server.command.AbstractServerCommandSource;

static class ReturnCommand.FailCommand<T extends AbstractServerCommandSource<T>>
implements ControlFlowAware.Command<T> {
    ReturnCommand.FailCommand() {
    }

    @Override
    public void execute(T abstractServerCommandSource, ContextChain<T> contextChain, ExecutionFlags executionFlags, ExecutionControl<T> executionControl) {
        abstractServerCommandSource.getReturnValueConsumer().onFailure();
        Frame frame = executionControl.getFrame();
        frame.fail();
        frame.doReturn();
    }
}
