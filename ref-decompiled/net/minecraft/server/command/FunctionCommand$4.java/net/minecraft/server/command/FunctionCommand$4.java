/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.CommandContext
 */
package net.minecraft.server.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;

static class FunctionCommand.4
extends FunctionCommand.Command {
    FunctionCommand.4() {
    }

    @Override
    protected NbtCompound getArguments(CommandContext<ServerCommandSource> context) {
        return NbtCompoundArgumentType.getNbtCompound(context, "arguments");
    }
}
