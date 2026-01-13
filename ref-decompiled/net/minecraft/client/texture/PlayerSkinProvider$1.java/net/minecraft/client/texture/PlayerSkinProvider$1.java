/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheLoader
 *  com.mojang.authlib.SignatureState
 *  com.mojang.authlib.minecraft.MinecraftProfileTextures
 *  com.mojang.authlib.properties.Property
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.google.common.cache.CacheLoader;
import com.mojang.authlib.SignatureState;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.properties.Property;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
class PlayerSkinProvider.1
extends CacheLoader<PlayerSkinProvider.Key, CompletableFuture<Optional<SkinTextures>>> {
    final /* synthetic */ ApiServices field_62264;
    final /* synthetic */ Executor field_45638;

    PlayerSkinProvider.1(ApiServices apiServices, Executor executor) {
        this.field_62264 = apiServices;
        this.field_45638 = executor;
    }

    public CompletableFuture<Optional<SkinTextures>> load(PlayerSkinProvider.Key key) {
        return ((CompletableFuture)CompletableFuture.supplyAsync(() -> {
            Property property = key.packedTextures();
            if (property == null) {
                return MinecraftProfileTextures.EMPTY;
            }
            MinecraftProfileTextures minecraftProfileTextures = this.field_62264.sessionService().unpackTextures(property);
            if (minecraftProfileTextures.signatureState() == SignatureState.INVALID) {
                LOGGER.warn("Profile contained invalid signature for textures property (profile id: {})", (Object)key.profileId());
            }
            return minecraftProfileTextures;
        }, Util.getMainWorkerExecutor().named("unpackSkinTextures")).thenComposeAsync(textures -> PlayerSkinProvider.this.fetchSkinTextures(key.profileId(), (MinecraftProfileTextures)textures), this.field_45638)).handle((skinTextures, throwable) -> {
            if (throwable != null) {
                LOGGER.warn("Failed to load texture for profile {}", (Object)key.profileId, throwable);
            }
            return Optional.ofNullable(skinTextures);
        });
    }

    public /* synthetic */ Object load(Object value) throws Exception {
        return this.load((PlayerSkinProvider.Key)value);
    }
}
