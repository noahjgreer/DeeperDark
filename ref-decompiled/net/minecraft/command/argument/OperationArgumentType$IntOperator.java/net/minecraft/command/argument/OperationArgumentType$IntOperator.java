/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.OperationArgumentType;
import net.minecraft.scoreboard.ScoreAccess;

@FunctionalInterface
static interface OperationArgumentType.IntOperator
extends OperationArgumentType.Operation {
    public int apply(int var1, int var2) throws CommandSyntaxException;

    @Override
    default public void apply(ScoreAccess scoreAccess, ScoreAccess scoreAccess2) throws CommandSyntaxException {
        scoreAccess.setScore(this.apply(scoreAccess.getScore(), scoreAccess2.getScore()));
    }
}
