/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.resource.server.DownloadQueuer
 *  net.minecraft.util.Downloader$DownloadEntry
 *  net.minecraft.util.Downloader$DownloadResult
 */
package net.minecraft.client.resource.server;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Downloader;

@Environment(value=EnvType.CLIENT)
public interface DownloadQueuer {
    public void enqueue(Map<UUID, Downloader.DownloadEntry> var1, Consumer<Downloader.DownloadResult> var2);
}

