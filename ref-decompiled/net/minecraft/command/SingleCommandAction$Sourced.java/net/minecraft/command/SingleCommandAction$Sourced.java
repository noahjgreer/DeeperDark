/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.ContextChain
 */
package net.minecraft.command;

import com.mojang.brigadier.context.ContextChain;
import java.util.List;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.command.ExecutionFlags;
import net.minecraft.command.Frame;
import net.minecraft.command.SingleCommandAction;
import net.minecraft.command.SourcedCommandAction;
import net.minecraft.server.command.AbstractServerCommandSource;

public static class SingleCommandAction.Sourced<T extends AbstractServerCommandSource<T>>
extends SingleCommandAction<T>
implements SourcedCommandAction<T> {
    public SingleCommandAction.Sourced(String string, ContextChain<T> contextChain) {
        super(string, contextChain);
    }

    @Override
    public void execute(T abstractServerCommandSource, CommandExecutionContext<T> commandExecutionContext, Frame frame) {
        this.traceCommandStart(commandExecutionContext, frame);
        this.execute(abstractServerCommandSource, List.of(abstractServerCommandSource), commandExecutionContext, frame, ExecutionFlags.NONE);
    }

    @Override
    public /* synthetic */ void execute(Object object, CommandExecutionContext commandExecutionContext, Frame frame) {
        this.execute((AbstractServerCommandSource)object, commandExecutionContext, frame);
    }
}
