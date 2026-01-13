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
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Instant;
import java.util.Optional;
import net.minecraft.server.BannedIpEntry;
import net.minecraft.server.dedicated.management.dispatch.IpBansRpcDispatcher;
import net.minecraft.util.dynamic.Codecs;

public record IpBansRpcDispatcher.IpBanData(String ipAddress, Optional<String> reason, Optional<String> source, Optional<Instant> expires) {
    public static final MapCodec<IpBansRpcDispatcher.IpBanData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.STRING.fieldOf("ip").forGetter(IpBansRpcDispatcher.IpBanData::ipAddress), (App)Codec.STRING.optionalFieldOf("reason").forGetter(IpBansRpcDispatcher.IpBanData::reason), (App)Codec.STRING.optionalFieldOf("source").forGetter(IpBansRpcDispatcher.IpBanData::source), (App)Codecs.INSTANT.optionalFieldOf("expires").forGetter(IpBansRpcDispatcher.IpBanData::expires)).apply((Applicative)instance, IpBansRpcDispatcher.IpBanData::new));

    private static IpBansRpcDispatcher.IpBanData fromIpBanInfo(IpBansRpcDispatcher.IpBanInfo ipBanInfo) {
        return new IpBansRpcDispatcher.IpBanData(ipBanInfo.ipAddress(), Optional.ofNullable(ipBanInfo.reason()), Optional.of(ipBanInfo.source()), ipBanInfo.expires());
    }

    public static IpBansRpcDispatcher.IpBanData fromBannedIpEntry(BannedIpEntry bannedIpEntry) {
        return IpBansRpcDispatcher.IpBanData.fromIpBanInfo(IpBansRpcDispatcher.IpBanInfo.fromBannedIpEntry(bannedIpEntry));
    }

    private IpBansRpcDispatcher.IpBanInfo toIpBanInfo() {
        return new IpBansRpcDispatcher.IpBanInfo(this.ipAddress(), this.reason().orElse(null), this.source().orElse(IpBansRpcDispatcher.DEFAULT_SOURCE), this.expires());
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{IpBansRpcDispatcher.IpBanData.class, "ip;reason;source;expires", "ipAddress", "reason", "source", "expires"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{IpBansRpcDispatcher.IpBanData.class, "ip;reason;source;expires", "ipAddress", "reason", "source", "expires"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{IpBansRpcDispatcher.IpBanData.class, "ip;reason;source;expires", "ipAddress", "reason", "source", "expires"}, this, object);
    }
}
