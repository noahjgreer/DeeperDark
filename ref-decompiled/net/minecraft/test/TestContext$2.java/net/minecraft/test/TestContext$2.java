/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.test;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestContext;
import net.minecraft.world.GameMode;

class TestContext.2
extends ServerPlayerEntity {
    TestContext.2(TestContext testContext, MinecraftServer minecraftServer, ServerWorld serverWorld, GameProfile gameProfile, SyncedClientOptions syncedClientOptions) {
        super(minecraftServer, serverWorld, gameProfile, syncedClientOptions);
    }

    @Override
    public GameMode getGameMode() {
        return GameMode.CREATIVE;
    }
}
