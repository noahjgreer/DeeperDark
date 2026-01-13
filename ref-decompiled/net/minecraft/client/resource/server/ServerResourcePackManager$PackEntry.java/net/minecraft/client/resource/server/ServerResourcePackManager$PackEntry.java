/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashCode
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.resource.server;

import com.google.common.hash.HashCode;
import java.net.URL;
import java.nio.file.Path;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.server.ServerResourcePackManager;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class ServerResourcePackManager.PackEntry {
    final UUID id;
    final URL url;
    final @Nullable HashCode hashCode;
    @Nullable Path path;
     @Nullable ServerResourcePackManager.DiscardReason discardReason;
    ServerResourcePackManager.LoadStatus loadStatus = ServerResourcePackManager.LoadStatus.REQUESTED;
    ServerResourcePackManager.Status status = ServerResourcePackManager.Status.INACTIVE;
    boolean accepted;

    ServerResourcePackManager.PackEntry(UUID id, URL url, @Nullable HashCode hashCode) {
        this.id = id;
        this.url = url;
        this.hashCode = hashCode;
    }

    public void discard(ServerResourcePackManager.DiscardReason reason) {
        if (this.discardReason == null) {
            this.discardReason = reason;
        }
    }

    public boolean isDiscarded() {
        return this.discardReason != null;
    }
}
