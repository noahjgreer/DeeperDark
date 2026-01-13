/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class ClientPlayNetworkHandler.1
implements CommandTreeS2CPacket.NodeFactory<ClientCommandSource> {
    ClientPlayNetworkHandler.1() {
    }

    @Override
    public ArgumentBuilder<ClientCommandSource, ?> literal(String name) {
        return LiteralArgumentBuilder.literal((String)name);
    }

    @Override
    public ArgumentBuilder<ClientCommandSource, ?> argument(String name, ArgumentType<?> type, @Nullable Identifier suggestionProviderId) {
        RequiredArgumentBuilder requiredArgumentBuilder = RequiredArgumentBuilder.argument((String)name, type);
        if (suggestionProviderId != null) {
            requiredArgumentBuilder.suggests(SuggestionProviders.byId(suggestionProviderId));
        }
        return requiredArgumentBuilder;
    }

    @Override
    public ArgumentBuilder<ClientCommandSource, ?> modifyNode(ArgumentBuilder<ClientCommandSource, ?> arg, boolean disableExecution, boolean requireTrusted) {
        if (disableExecution) {
            arg.executes(context -> 0);
        }
        if (requireTrusted) {
            arg.requires(CommandManager.requirePermissionLevel(RESTRICTED_PERMISSION_CHECK));
        }
        return arg;
    }
}
