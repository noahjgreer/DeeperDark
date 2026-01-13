/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.EntityDataObject;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;

static class EntityDataObject.1
implements DataCommand.ObjectType {
    final /* synthetic */ String argumentName;

    EntityDataObject.1(String string) {
        this.argumentName = string;
    }

    @Override
    public DataCommandObject getObject(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return new EntityDataObject(EntityArgumentType.getEntity(context, this.argumentName));
    }

    @Override
    public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ServerCommandSource, ?> argument, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> argumentAdder) {
        return argument.then(CommandManager.literal("entity").then(argumentAdder.apply((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument(this.argumentName, EntityArgumentType.entity()))));
    }
}
