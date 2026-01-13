/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 */
package net.minecraft.server.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;

@FunctionalInterface
static interface DataCommand.ModifyArgumentCreator {
    public ArgumentBuilder<ServerCommandSource, ?> create(DataCommand.ModifyOperation var1);
}
