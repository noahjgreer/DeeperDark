/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.util.Date;
import net.minecraft.server.PlayerConfigEntry;

static class UserCache.Entry {
    private final PlayerConfigEntry player;
    final Date expirationDate;
    private volatile long lastAccessed;

    UserCache.Entry(PlayerConfigEntry player, Date expirationDate) {
        this.player = player;
        this.expirationDate = expirationDate;
    }

    public PlayerConfigEntry getPlayer() {
        return this.player;
    }

    public Date getExpirationDate() {
        return this.expirationDate;
    }

    public void setLastAccessed(long lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public long getLastAccessed() {
        return this.lastAccessed;
    }
}
