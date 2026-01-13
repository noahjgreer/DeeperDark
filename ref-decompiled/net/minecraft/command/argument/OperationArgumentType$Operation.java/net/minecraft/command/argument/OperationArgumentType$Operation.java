/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.scoreboard.ScoreAccess;

@FunctionalInterface
public static interface OperationArgumentType.Operation {
    public void apply(ScoreAccess var1, ScoreAccess var2) throws CommandSyntaxException;
}
