/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.item.ItemStack;

@FunctionalInterface
static interface LootCommand.FeedbackMessage {
    public void accept(List<ItemStack> var1) throws CommandSyntaxException;
}
