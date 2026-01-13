/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.server.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

@FunctionalInterface
static interface DataCommand.Processor {
    public String process(String var1) throws CommandSyntaxException;
}
