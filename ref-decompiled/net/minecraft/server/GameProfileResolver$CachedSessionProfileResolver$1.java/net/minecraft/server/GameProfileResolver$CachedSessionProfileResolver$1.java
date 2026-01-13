/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheLoader
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.yggdrasil.ProfileResult
 */
package net.minecraft.server;

import com.google.common.cache.CacheLoader;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.GameProfileResolver;

class GameProfileResolver.CachedSessionProfileResolver.1
extends CacheLoader<UUID, Optional<GameProfile>> {
    final /* synthetic */ MinecraftSessionService field_62066;

    GameProfileResolver.CachedSessionProfileResolver.1(GameProfileResolver.CachedSessionProfileResolver cachedSessionProfileResolver, MinecraftSessionService minecraftSessionService) {
        this.field_62066 = minecraftSessionService;
    }

    public Optional<GameProfile> load(UUID uUID) {
        ProfileResult profileResult = this.field_62066.fetchProfile(uUID, true);
        return Optional.ofNullable(profileResult).map(ProfileResult::profile);
    }

    public /* synthetic */ Object load(Object id) throws Exception {
        return this.load((UUID)id);
    }
}
