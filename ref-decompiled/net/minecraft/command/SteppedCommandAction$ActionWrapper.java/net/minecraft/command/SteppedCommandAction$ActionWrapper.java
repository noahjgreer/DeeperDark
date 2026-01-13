/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import net.minecraft.command.CommandQueueEntry;
import net.minecraft.command.Frame;

@FunctionalInterface
public static interface SteppedCommandAction.ActionWrapper<T, P> {
    public CommandQueueEntry<T> create(Frame var1, P var2);
}
