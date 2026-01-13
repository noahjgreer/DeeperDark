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
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.util.Identifier;

class CommandFunctionArgumentType.1
implements CommandFunctionArgumentType.FunctionArgument {
    final /* synthetic */ Identifier field_10785;

    CommandFunctionArgumentType.1() {
        this.field_10785 = identifier;
    }

    @Override
    public Collection<CommandFunction<ServerCommandSource>> getFunctions(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return CommandFunctionArgumentType.getFunctionTag(context, this.field_10785);
    }

    @Override
    public Pair<Identifier, Either<CommandFunction<ServerCommandSource>, Collection<CommandFunction<ServerCommandSource>>>> getFunctionOrTag(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return Pair.of((Object)this.field_10785, (Object)Either.right(CommandFunctionArgumentType.getFunctionTag(context, this.field_10785)));
    }

    @Override
    public Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> getIdentifiedFunctions(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return Pair.of((Object)this.field_10785, CommandFunctionArgumentType.getFunctionTag(context, this.field_10785));
    }
}
