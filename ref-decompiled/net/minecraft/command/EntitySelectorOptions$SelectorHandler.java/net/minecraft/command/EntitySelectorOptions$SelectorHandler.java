/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.EntitySelectorReader;

@FunctionalInterface
public static interface EntitySelectorOptions.SelectorHandler {
    public void handle(EntitySelectorReader var1) throws CommandSyntaxException;
}
