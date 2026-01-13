/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.server.command.ServerCommandSource;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public static interface ScoreboardCommand.NumberFormatCommandExecutor {
    public int run(CommandContext<ServerCommandSource> var1, @Nullable NumberFormat var2) throws CommandSyntaxException;
}
