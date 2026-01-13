/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.ControlFlowAware;

public static interface ControlFlowAware.Command<T>
extends Command<T>,
ControlFlowAware<T> {
    default public int run(CommandContext<T> context) throws CommandSyntaxException {
        throw new UnsupportedOperationException("This function should not run");
    }
}
