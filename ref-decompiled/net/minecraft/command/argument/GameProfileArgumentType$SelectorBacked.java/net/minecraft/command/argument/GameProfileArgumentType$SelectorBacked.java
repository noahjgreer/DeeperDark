/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public static class GameProfileArgumentType.SelectorBacked
implements GameProfileArgumentType.GameProfileArgument {
    private final EntitySelector selector;

    public GameProfileArgumentType.SelectorBacked(EntitySelector selector) {
        this.selector = selector;
    }

    @Override
    public Collection<PlayerConfigEntry> getNames(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        List<ServerPlayerEntity> list = this.selector.getPlayers(serverCommandSource);
        if (list.isEmpty()) {
            throw EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION.create();
        }
        ArrayList<PlayerConfigEntry> list2 = new ArrayList<PlayerConfigEntry>();
        for (ServerPlayerEntity serverPlayerEntity : list) {
            list2.add(serverPlayerEntity.getPlayerConfigEntry());
        }
        return list2;
    }
}
