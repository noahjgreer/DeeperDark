/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.util.Identifier;

public static interface CommandFunctionArgumentType.FunctionArgument {
    public Collection<CommandFunction<ServerCommandSource>> getFunctions(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;

    public Pair<Identifier, Either<CommandFunction<ServerCommandSource>, Collection<CommandFunction<ServerCommandSource>>>> getFunctionOrTag(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;

    public Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> getIdentifiedFunctions(CommandContext<ServerCommandSource> var1) throws CommandSyntaxException;
}
