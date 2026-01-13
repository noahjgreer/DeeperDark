/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.server;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.server.ReloadScheduler;

@Environment(value=EnvType.CLIENT)
public static interface ReloadScheduler.ReloadContext {
    public void onSuccess();

    public void onFailure(boolean var1);

    public List<ReloadScheduler.PackInfo> getPacks();
}
