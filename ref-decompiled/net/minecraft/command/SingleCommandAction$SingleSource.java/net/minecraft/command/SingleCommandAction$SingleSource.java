/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.ContextChain
 */
package net.minecraft.command;

import com.mojang.brigadier.context.ContextChain;
import java.util.List;
import net.minecraft.command.CommandAction;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.command.ExecutionFlags;
import net.minecraft.command.Frame;
import net.minecraft.command.SingleCommandAction;
import net.minecraft.server.command.AbstractServerCommandSource;

public static class SingleCommandAction.SingleSource<T extends AbstractServerCommandSource<T>>
extends SingleCommandAction<T>
implements CommandAction<T> {
    private final T source;

    public SingleCommandAction.SingleSource(String command, ContextChain<T> contextChain, T source) {
        super(command, contextChain);
        this.source = source;
    }

    @Override
    public void execute(CommandExecutionContext<T> commandExecutionContext, Frame frame) {
        this.traceCommandStart(commandExecutionContext, frame);
        this.execute(this.source, List.of(this.source), commandExecutionContext, frame, ExecutionFlags.NONE);
    }
}
