/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.net.InetAddresses
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.dedicated.management.dispatch;

import com.google.common.net.InetAddresses;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Instant;
import java.util.Optional;
import net.minecraft.server.dedicated.management.RpcPlayer;
import net.minecraft.server.dedicated.management.dispatch.IpBansRpcDispatcher;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.dynamic.Codecs;
import org.jspecify.annotations.Nullable;

public record IpBansRpcDispatcher.IncomingRpcIpBanData(Optional<RpcPlayer> player, Optional<String> ipAddress, Optional<String> reason, Optional<String> source, Optional<Instant> expires) {
    public static final MapCodec<IpBansRpcDispatcher.IncomingRpcIpBanData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)RpcPlayer.CODEC.codec().optionalFieldOf("player").forGetter(IpBansRpcDispatcher.IncomingRpcIpBanData::player), (App)Codec.STRING.optionalFieldOf("ip").forGetter(IpBansRpcDispatcher.IncomingRpcIpBanData::ipAddress), (App)Codec.STRING.optionalFieldOf("reason").forGetter(IpBansRpcDispatcher.IncomingRpcIpBanData::reason), (App)Codec.STRING.optionalFieldOf("source").forGetter(IpBansRpcDispatcher.IncomingRpcIpBanData::source), (App)Codecs.INSTANT.optionalFieldOf("expires").forGetter(IpBansRpcDispatcher.IncomingRpcIpBanData::expires)).apply((Applicative)instance, IpBansRpcDispatcher.IncomingRpcIpBanData::new));

    IpBansRpcDispatcher.IpBanInfo toIpBanInfoFromPlayer(ServerPlayerEntity player) {
        return new IpBansRpcDispatcher.IpBanInfo(player.getIp(), this.reason().orElse(null), this.source().orElse(IpBansRpcDispatcher.DEFAULT_SOURCE), this.expires());
    }

    @Nullable IpBansRpcDispatcher.IpBanInfo toIpBanInfoOrNull() {
        if (this.ipAddress().isEmpty() || !InetAddresses.isInetAddress((String)this.ipAddress().get())) {
            return null;
        }
        return new IpBansRpcDispatcher.IpBanInfo(this.ipAddress().get(), this.reason().orElse(null), this.source().orElse(IpBansRpcDispatcher.DEFAULT_SOURCE), this.expires());
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{IpBansRpcDispatcher.IncomingRpcIpBanData.class, "player;ip;reason;source;expires", "player", "ipAddress", "reason", "source", "expires"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{IpBansRpcDispatcher.IncomingRpcIpBanData.class, "player;ip;reason;source;expires", "player", "ipAddress", "reason", "source", "expires"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{IpBansRpcDispatcher.IncomingRpcIpBanData.class, "player;ip;reason;source;expires", "player", "ipAddress", "reason", "source", "expires"}, this, object);
    }
}
