/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.test;

import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.dedicated.management.listener.ManagementListener;
import net.minecraft.test.TestServer;
import net.minecraft.world.PlayerSaveHandler;

class TestServer.1
extends PlayerManager {
    TestServer.1(TestServer testServer, MinecraftServer minecraftServer, CombinedDynamicRegistries combinedDynamicRegistries, PlayerSaveHandler playerSaveHandler, ManagementListener managementListener) {
        super(minecraftServer, combinedDynamicRegistries, playerSaveHandler, managementListener);
    }
}
