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
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.dedicated.management.RpcPlayer;

public record ServerRpcDispatcher.RpcStatus(boolean started, List<RpcPlayer> players, ServerMetadata.Version version) {
    public static final Codec<ServerRpcDispatcher.RpcStatus> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.BOOL.fieldOf("started").forGetter(ServerRpcDispatcher.RpcStatus::started), (App)RpcPlayer.CODEC.codec().listOf().lenientOptionalFieldOf("players", List.of()).forGetter(ServerRpcDispatcher.RpcStatus::players), (App)ServerMetadata.Version.CODEC.fieldOf("version").forGetter(ServerRpcDispatcher.RpcStatus::version)).apply((Applicative)instance, ServerRpcDispatcher.RpcStatus::new));
    public static final ServerRpcDispatcher.RpcStatus EMPTY = new ServerRpcDispatcher.RpcStatus(false, List.of(), ServerMetadata.Version.create());
}
