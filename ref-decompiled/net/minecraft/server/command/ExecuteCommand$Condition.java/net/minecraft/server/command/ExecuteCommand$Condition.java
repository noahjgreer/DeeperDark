/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.server.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

@FunctionalInterface
static interface ExecuteCommand.Condition {
    public boolean test(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;
}
