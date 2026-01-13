/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.command;

import net.minecraft.command.CommandAction;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.command.CommandQueueEntry;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.Frame;
import net.minecraft.server.function.Tracer;
import org.jspecify.annotations.Nullable;

static class ExecutionControl.1
implements ExecutionControl<T> {
    final /* synthetic */ CommandExecutionContext field_46742;
    final /* synthetic */ Frame field_47163;

    ExecutionControl.1(CommandExecutionContext commandExecutionContext, Frame frame) {
        this.field_46742 = commandExecutionContext;
        this.field_47163 = frame;
    }

    @Override
    public void enqueueAction(CommandAction<T> action) {
        this.field_46742.enqueueCommand(new CommandQueueEntry(this.field_47163, action));
    }

    @Override
    public void setTracer(@Nullable Tracer tracer) {
        this.field_46742.setTracer(tracer);
    }

    @Override
    public @Nullable Tracer getTracer() {
        return this.field_46742.getTracer();
    }

    @Override
    public Frame getFrame() {
        return this.field_47163;
    }
}
