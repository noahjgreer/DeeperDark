/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server.dedicated.management.dispatch;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.util.Optional;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.dedicated.management.RpcPlayer;
import net.minecraft.server.dedicated.management.dispatch.PlayerBansRpcDispatcher;
import net.minecraft.util.dynamic.Codecs;

public record PlayerBansRpcDispatcher.RpcEntry(RpcPlayer player, Optional<String> reason, Optional<String> source, Optional<Instant> expires) {
    public static final MapCodec<PlayerBansRpcDispatcher.RpcEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)RpcPlayer.CODEC.codec().fieldOf("player").forGetter(PlayerBansRpcDispatcher.RpcEntry::player), (App)Codec.STRING.optionalFieldOf("reason").forGetter(PlayerBansRpcDispatcher.RpcEntry::reason), (App)Codec.STRING.optionalFieldOf("source").forGetter(PlayerBansRpcDispatcher.RpcEntry::source), (App)Codecs.INSTANT.optionalFieldOf("expires").forGetter(PlayerBansRpcDispatcher.RpcEntry::expires)).apply((Applicative)instance, PlayerBansRpcDispatcher.RpcEntry::new));

    private static PlayerBansRpcDispatcher.RpcEntry of(PlayerBansRpcDispatcher.ConfigEntry entry) {
        return new PlayerBansRpcDispatcher.RpcEntry(RpcPlayer.of(entry.player()), Optional.ofNullable(entry.reason()), Optional.of(entry.source()), entry.expires());
    }

    public static PlayerBansRpcDispatcher.RpcEntry of(BannedPlayerEntry entry) {
        return PlayerBansRpcDispatcher.RpcEntry.of(PlayerBansRpcDispatcher.ConfigEntry.of(entry));
    }

    private PlayerBansRpcDispatcher.ConfigEntry toConfigEntry(PlayerConfigEntry player) {
        return new PlayerBansRpcDispatcher.ConfigEntry(player, this.reason().orElse(null), this.source().orElse(PlayerBansRpcDispatcher.DEFAULT_SOURCE), this.expires());
    }
}
