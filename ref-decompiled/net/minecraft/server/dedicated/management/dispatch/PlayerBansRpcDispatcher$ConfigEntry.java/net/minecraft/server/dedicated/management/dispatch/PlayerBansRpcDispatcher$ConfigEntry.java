/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.dedicated.management.dispatch;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.PlayerConfigEntry;
import org.jspecify.annotations.Nullable;

record PlayerBansRpcDispatcher.ConfigEntry(PlayerConfigEntry player, @Nullable String reason, String source, Optional<Instant> expires) {
    static PlayerBansRpcDispatcher.ConfigEntry of(BannedPlayerEntry entry) {
        return new PlayerBansRpcDispatcher.ConfigEntry(Objects.requireNonNull((PlayerConfigEntry)entry.getKey()), entry.getReason(), entry.getSource(), Optional.ofNullable(entry.getExpiryDate()).map(Date::toInstant));
    }

    BannedPlayerEntry toBannedPlayerEntry() {
        return new BannedPlayerEntry(new PlayerConfigEntry(this.player().id(), this.player().name()), null, this.source(), (Date)this.expires().map(Date::from).orElse(null), this.reason());
    }
}
