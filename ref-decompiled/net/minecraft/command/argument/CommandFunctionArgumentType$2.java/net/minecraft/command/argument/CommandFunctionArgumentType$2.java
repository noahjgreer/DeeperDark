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
import java.util.Collections;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.util.Identifier;

class CommandFunctionArgumentType.2
implements CommandFunctionArgumentType.FunctionArgument {
    final /* synthetic */ Identifier field_10787;

    CommandFunctionArgumentType.2() {
        this.field_10787 = identifier;
    }

    @Override
    public Collection<CommandFunction<ServerCommandSource>> getFunctions(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return Collections.singleton(CommandFunctionArgumentType.getFunction(context, this.field_10787));
    }

    @Override
    public Pair<Identifier, Either<CommandFunction<ServerCommandSource>, Collection<CommandFunction<ServerCommandSource>>>> getFunctionOrTag(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return Pair.of((Object)this.field_10787, (Object)Either.left(CommandFunctionArgumentType.getFunction(context, this.field_10787)));
    }

    @Override
    public Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> getIdentifiedFunctions(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return Pair.of((Object)this.field_10787, Collections.singleton(CommandFunctionArgumentType.getFunction(context, this.field_10787)));
    }
}
