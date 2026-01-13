/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.server.command.ServerCommandSource;

public static class ScoreHolderArgumentType.SelectorScoreHolders
implements ScoreHolderArgumentType.ScoreHolders {
    private final EntitySelector selector;

    public ScoreHolderArgumentType.SelectorScoreHolders(EntitySelector selector) {
        this.selector = selector;
    }

    @Override
    public Collection<ScoreHolder> getNames(ServerCommandSource serverCommandSource, Supplier<Collection<ScoreHolder>> supplier) throws CommandSyntaxException {
        List<? extends Entity> list = this.selector.getEntities(serverCommandSource);
        if (list.isEmpty()) {
            throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
        }
        return List.copyOf(list);
    }
}
