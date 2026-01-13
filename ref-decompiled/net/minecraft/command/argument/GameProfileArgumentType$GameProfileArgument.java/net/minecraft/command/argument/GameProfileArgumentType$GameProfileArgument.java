/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.command.ServerCommandSource;

@FunctionalInterface
public static interface GameProfileArgumentType.GameProfileArgument {
    public Collection<PlayerConfigEntry> getNames(ServerCommandSource var1) throws CommandSyntaxException;
}
