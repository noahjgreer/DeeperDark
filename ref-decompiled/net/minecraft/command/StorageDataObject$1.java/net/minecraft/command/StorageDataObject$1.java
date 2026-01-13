/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 */
package net.minecraft.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.function.Function;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.StorageDataObject;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;

static class StorageDataObject.1
implements DataCommand.ObjectType {
    final /* synthetic */ String argumentName;

    StorageDataObject.1(String string) {
        this.argumentName = string;
    }

    @Override
    public DataCommandObject getObject(CommandContext<ServerCommandSource> context) {
        return new StorageDataObject(StorageDataObject.of(context), IdentifierArgumentType.getIdentifier(context, this.argumentName));
    }

    @Override
    public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ServerCommandSource, ?> argument, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> argumentAdder) {
        return argument.then(CommandManager.literal("storage").then(argumentAdder.apply((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument(this.argumentName, IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER))));
    }
}
