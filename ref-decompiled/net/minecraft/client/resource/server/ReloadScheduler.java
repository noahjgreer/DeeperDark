/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.resource.server.ReloadScheduler
 *  net.minecraft.client.resource.server.ReloadScheduler$ReloadContext
 */
package net.minecraft.client.resource.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.server.ReloadScheduler;

@Environment(value=EnvType.CLIENT)
public interface ReloadScheduler {
    public void scheduleReload(ReloadContext var1);
}

