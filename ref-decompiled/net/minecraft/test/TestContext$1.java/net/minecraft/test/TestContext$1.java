/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.test;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

class TestContext.1
extends PlayerEntity {
    final /* synthetic */ GameMode field_48983;

    TestContext.1(TestContext testContext, World world, GameProfile gameProfile, GameMode gameMode) {
        this.field_48983 = gameMode;
        super(world, gameProfile);
    }

    @Override
    public GameMode getGameMode() {
        return this.field_48983;
    }

    @Override
    public boolean isControlledByPlayer() {
        return false;
    }
}
