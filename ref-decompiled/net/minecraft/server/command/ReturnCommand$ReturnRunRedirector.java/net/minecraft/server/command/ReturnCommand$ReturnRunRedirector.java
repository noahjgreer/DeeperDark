/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.ContextChain
 */
package net.minecraft.server.command;

import com.mojang.brigadier.context.ContextChain;
import java.util.List;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.ExecutionFlags;
import net.minecraft.command.FallthroughCommandAction;
import net.minecraft.command.Forkable;
import net.minecraft.command.SingleCommandAction;
import net.minecraft.server.command.AbstractServerCommandSource;

static class ReturnCommand.ReturnRunRedirector<T extends AbstractServerCommandSource<T>>
implements Forkable.RedirectModifier<T> {
    ReturnCommand.ReturnRunRedirector() {
    }

    @Override
    public void execute(T abstractServerCommandSource, List<T> list, ContextChain<T> contextChain, ExecutionFlags executionFlags, ExecutionControl<T> executionControl) {
        if (list.isEmpty()) {
            if (executionFlags.isInsideReturnRun()) {
                executionControl.enqueueAction(FallthroughCommandAction.getInstance());
            }
            return;
        }
        executionControl.getFrame().doReturn();
        ContextChain contextChain2 = contextChain.nextStage();
        String string = contextChain2.getTopContext().getInput();
        executionControl.enqueueAction(new SingleCommandAction.MultiSource<T>(string, contextChain2, executionFlags.setInsideReturnRun(), abstractServerCommandSource, list));
    }
}
