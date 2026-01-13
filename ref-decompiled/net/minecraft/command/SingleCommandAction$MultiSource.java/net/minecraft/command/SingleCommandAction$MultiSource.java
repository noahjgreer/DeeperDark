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

public static class SingleCommandAction.MultiSource<T extends AbstractServerCommandSource<T>>
extends SingleCommandAction<T>
implements CommandAction<T> {
    private final ExecutionFlags flags;
    private final T baseSource;
    private final List<T> sources;

    public SingleCommandAction.MultiSource(String command, ContextChain<T> contextChain, ExecutionFlags flags, T baseSource, List<T> sources) {
        super(command, contextChain);
        this.baseSource = baseSource;
        this.sources = sources;
        this.flags = flags;
    }

    @Override
    public void execute(CommandExecutionContext<T> commandExecutionContext, Frame frame) {
        this.execute(this.baseSource, this.sources, commandExecutionContext, frame, this.flags);
    }
}
