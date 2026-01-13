/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.test;

import com.mojang.authlib.GameProfile;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.GameProfileResolver;

static class TestServer.DummyGameProfileResolver
implements GameProfileResolver {
    TestServer.DummyGameProfileResolver() {
    }

    @Override
    public Optional<GameProfile> getProfileByName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<GameProfile> getProfileById(UUID id) {
        return Optional.empty();
    }
}
