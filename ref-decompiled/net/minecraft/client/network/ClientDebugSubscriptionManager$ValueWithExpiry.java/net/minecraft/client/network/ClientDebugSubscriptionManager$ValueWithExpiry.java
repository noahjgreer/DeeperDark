/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
record ClientDebugSubscriptionManager.ValueWithExpiry<T>(T value, long expiresAfterTime) {
    private static final long INEXPIRABLE = -1L;

    public boolean hasExpired(long time) {
        if (this.expiresAfterTime == -1L) {
            return false;
        }
        return time >= this.expiresAfterTime;
    }
}
