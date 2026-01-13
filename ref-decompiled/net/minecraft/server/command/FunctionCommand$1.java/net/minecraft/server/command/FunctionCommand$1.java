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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;

static class FunctionCommand.1
extends FunctionCommand.Command {
    final /* synthetic */ DataCommand.ObjectType field_46647;

    FunctionCommand.1(DataCommand.ObjectType objectType) {
        this.field_46647 = objectType;
    }

    @Override
    protected NbtCompound getArguments(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return this.field_46647.getObject(context).getNbt();
    }
}
