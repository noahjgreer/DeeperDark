/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.QuickPlayLogger;

@Environment(value=EnvType.CLIENT)
class QuickPlayLogger.1
extends QuickPlayLogger {
    QuickPlayLogger.1(String string) {
        super(string);
    }

    @Override
    public void save(MinecraftClient client) {
    }

    @Override
    public void setWorld(QuickPlayLogger.WorldType worldType, String id, String name) {
    }
}
