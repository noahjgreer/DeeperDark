/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.function.Supplier;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.server.command.ServerCommandSource;

@FunctionalInterface
public static interface ScoreHolderArgumentType.ScoreHolders {
    public Collection<ScoreHolder> getNames(ServerCommandSource var1, Supplier<Collection<ScoreHolder>> var2) throws CommandSyntaxException;
}
