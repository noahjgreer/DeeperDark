/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.command;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.function.Predicate;
import net.minecraft.command.permission.PermissionPredicate;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

class CommandManager.1
implements CommandTreeS2CPacket.CommandNodeInspector<ServerCommandSource> {
    private final ServerCommandSource source = CommandManager.createSource(PermissionPredicate.NONE);

    CommandManager.1() {
    }

    @Override
    public @Nullable Identifier getSuggestionProviderId(ArgumentCommandNode<ServerCommandSource, ?> node) {
        SuggestionProvider suggestionProvider = node.getCustomSuggestions();
        return suggestionProvider != null ? SuggestionProviders.computeId(suggestionProvider) : null;
    }

    @Override
    public boolean isExecutable(CommandNode<ServerCommandSource> node) {
        return node.getCommand() != null;
    }

    @Override
    public boolean hasRequiredLevel(CommandNode<ServerCommandSource> node) {
        Predicate predicate = node.getRequirement();
        return !predicate.test(this.source);
    }
}
