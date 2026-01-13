/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.main;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;

@Environment(value=EnvType.CLIENT)
static class Main.2
extends Thread {
    Main.2(String string) {
        super(string);
    }

    @Override
    public void run() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) {
            return;
        }
        IntegratedServer integratedServer = minecraftClient.getServer();
        if (integratedServer != null) {
            integratedServer.stop(true);
        }
    }
}
