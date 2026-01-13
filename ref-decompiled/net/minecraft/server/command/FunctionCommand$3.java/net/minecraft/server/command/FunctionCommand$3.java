/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.CommandContext
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.jspecify.annotations.Nullable;

static class FunctionCommand.3
extends FunctionCommand.Command {
    FunctionCommand.3() {
    }

    @Override
    protected @Nullable NbtCompound getArguments(CommandContext<ServerCommandSource> context) {
        return null;
    }
}
