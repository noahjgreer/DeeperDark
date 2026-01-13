/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server.dedicated.management.dispatch;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.dedicated.management.RpcKickReason;
import net.minecraft.server.dedicated.management.RpcPlayer;

public record ServerRpcDispatcher.RpcSystemMessage(RpcKickReason message, boolean overlay, Optional<List<RpcPlayer>> receivingPlayers) {
    public static final Codec<ServerRpcDispatcher.RpcSystemMessage> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)RpcKickReason.CODEC.fieldOf("message").forGetter(ServerRpcDispatcher.RpcSystemMessage::message), (App)Codec.BOOL.fieldOf("overlay").forGetter(ServerRpcDispatcher.RpcSystemMessage::overlay), (App)RpcPlayer.CODEC.codec().listOf().lenientOptionalFieldOf("receivingPlayers").forGetter(ServerRpcDispatcher.RpcSystemMessage::receivingPlayers)).apply((Applicative)instance, ServerRpcDispatcher.RpcSystemMessage::new));
}
