/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheLoader
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.google.common.cache.CacheLoader;
import com.mojang.authlib.GameProfile;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.SkinTextures;

@Environment(value=EnvType.CLIENT)
class PlayerSkinCache.1
extends CacheLoader<ProfileComponent, CompletableFuture<Optional<PlayerSkinCache.Entry>>> {
    PlayerSkinCache.1() {
    }

    public CompletableFuture<Optional<PlayerSkinCache.Entry>> load(ProfileComponent profileComponent) {
        return profileComponent.resolve(PlayerSkinCache.this.gameProfileResolver).thenCompose(gameProfile -> PlayerSkinCache.this.playerSkinProvider.fetchSkinTextures((GameProfile)gameProfile).thenApply(optional -> optional.map(skinTextures -> new PlayerSkinCache.Entry(PlayerSkinCache.this, (GameProfile)gameProfile, (SkinTextures)skinTextures, profileComponent.getOverride()))));
    }

    public /* synthetic */ Object load(Object profile) throws Exception {
        return this.load((ProfileComponent)profile);
    }
}
