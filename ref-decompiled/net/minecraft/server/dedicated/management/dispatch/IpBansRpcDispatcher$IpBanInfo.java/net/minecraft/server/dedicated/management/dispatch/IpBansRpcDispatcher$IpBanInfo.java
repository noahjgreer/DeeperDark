/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.dedicated.management.dispatch;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.server.BannedIpEntry;
import org.jspecify.annotations.Nullable;

record IpBansRpcDispatcher.IpBanInfo(String ipAddress, @Nullable String reason, String source, Optional<Instant> expires) {
    static IpBansRpcDispatcher.IpBanInfo fromBannedIpEntry(BannedIpEntry bannedIpEntry) {
        return new IpBansRpcDispatcher.IpBanInfo(Objects.requireNonNull((String)bannedIpEntry.getKey()), bannedIpEntry.getReason(), bannedIpEntry.getSource(), Optional.ofNullable(bannedIpEntry.getExpiryDate()).map(Date::toInstant));
    }

    BannedIpEntry toBannedIpEntry() {
        return new BannedIpEntry(this.ipAddress(), null, this.source(), (Date)this.expires().map(Date::from).orElse(null), this.reason());
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{IpBansRpcDispatcher.IpBanInfo.class, "ip;reason;source;expires", "ipAddress", "reason", "source", "expires"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{IpBansRpcDispatcher.IpBanInfo.class, "ip;reason;source;expires", "ipAddress", "reason", "source", "expires"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{IpBansRpcDispatcher.IpBanInfo.class, "ip;reason;source;expires", "ipAddress", "reason", "source", "expires"}, this, object);
    }
}
