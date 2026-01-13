/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.resource.server.PackStateChangeCallback
 *  net.minecraft.client.resource.server.PackStateChangeCallback$FinishState
 *  net.minecraft.client.resource.server.PackStateChangeCallback$State
 */
package net.minecraft.client.resource.server;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.server.PackStateChangeCallback;

@Environment(value=EnvType.CLIENT)
public interface PackStateChangeCallback {
    public void onStateChanged(UUID var1, State var2);

    public void onFinish(UUID var1, FinishState var2);
}

