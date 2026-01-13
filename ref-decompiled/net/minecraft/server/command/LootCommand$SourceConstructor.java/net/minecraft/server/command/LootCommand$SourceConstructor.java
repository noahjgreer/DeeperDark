/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 */
package net.minecraft.server.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.server.command.LootCommand;
import net.minecraft.server.command.ServerCommandSource;

@FunctionalInterface
static interface LootCommand.SourceConstructor {
    public ArgumentBuilder<ServerCommandSource, ?> construct(ArgumentBuilder<ServerCommandSource, ?> var1, LootCommand.Target var2);
}
