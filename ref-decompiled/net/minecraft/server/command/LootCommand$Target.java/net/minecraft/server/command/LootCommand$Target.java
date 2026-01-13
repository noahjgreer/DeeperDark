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
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.LootCommand;
import net.minecraft.server.command.ServerCommandSource;

@FunctionalInterface
static interface LootCommand.Target {
    public int accept(CommandContext<ServerCommandSource> var1, List<ItemStack> var2, LootCommand.FeedbackMessage var3) throws CommandSyntaxException;
}
