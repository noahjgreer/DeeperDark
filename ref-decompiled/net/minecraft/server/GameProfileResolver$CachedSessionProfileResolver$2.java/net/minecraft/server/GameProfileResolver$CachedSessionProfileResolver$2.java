/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheLoader
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.server;

import com.google.common.cache.CacheLoader;
import com.mojang.authlib.GameProfile;
import java.util.Optional;
import net.minecraft.util.NameToIdCache;

class GameProfileResolver.CachedSessionProfileResolver.2
extends CacheLoader<String, Optional<GameProfile>> {
    final /* synthetic */ NameToIdCache field_62067;

    GameProfileResolver.CachedSessionProfileResolver.2(NameToIdCache nameToIdCache) {
        this.field_62067 = nameToIdCache;
    }

    public Optional<GameProfile> load(String string) {
        return this.field_62067.findByName(string).flatMap(entry -> (Optional)CachedSessionProfileResolver.this.idCache.getUnchecked((Object)entry.id()));
    }

    public /* synthetic */ Object load(Object name) throws Exception {
        return this.load((String)name);
    }
}
