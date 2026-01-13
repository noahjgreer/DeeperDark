/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashFunction
 *  com.google.common.hash.Hashing
 *  com.mojang.util.UndashedUuid
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.server;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.mojang.util.UndashedUuid;
import java.net.Proxy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.GameVersion;
import net.minecraft.SharedConstants;
import net.minecraft.client.resource.server.DownloadQueuer;
import net.minecraft.client.session.Session;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Downloader;

@Environment(value=EnvType.CLIENT)
class ServerResourcePackLoader.4
implements DownloadQueuer {
    private static final int MAX_BYTES = 0xFA00000;
    private static final HashFunction SHA1 = Hashing.sha1();
    final /* synthetic */ Session field_47609;
    final /* synthetic */ Downloader field_47610;
    final /* synthetic */ Proxy field_47611;
    final /* synthetic */ Executor field_47612;

    ServerResourcePackLoader.4() {
        this.field_47609 = session;
        this.field_47610 = downloader;
        this.field_47611 = proxy;
        this.field_47612 = executor;
    }

    private Map<String, String> getHeaders() {
        GameVersion gameVersion = SharedConstants.getGameVersion();
        return Map.of("X-Minecraft-Username", this.field_47609.getUsername(), "X-Minecraft-UUID", UndashedUuid.toString((UUID)this.field_47609.getUuidOrNull()), "X-Minecraft-Version", gameVersion.name(), "X-Minecraft-Version-ID", gameVersion.id(), "X-Minecraft-Pack-Format", String.valueOf(gameVersion.packVersion(ResourceType.CLIENT_RESOURCES)), "User-Agent", "Minecraft Java/" + gameVersion.name());
    }

    @Override
    public void enqueue(Map<UUID, Downloader.DownloadEntry> entries, Consumer<Downloader.DownloadResult> callback) {
        this.field_47610.downloadAsync(new Downloader.Config(SHA1, 0xFA00000, this.getHeaders(), this.field_47611, ServerResourcePackLoader.this.createListener(entries.size())), entries).thenAcceptAsync((Consumer)callback, this.field_47612);
    }
}
