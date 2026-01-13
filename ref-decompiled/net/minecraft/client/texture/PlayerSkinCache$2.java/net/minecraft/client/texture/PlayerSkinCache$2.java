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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.component.type.ProfileComponent;

@Environment(value=EnvType.CLIENT)
class PlayerSkinCache.2
extends CacheLoader<ProfileComponent, PlayerSkinCache.Entry> {
    PlayerSkinCache.2() {
    }

    public PlayerSkinCache.Entry load(ProfileComponent profileComponent) {
        GameProfile gameProfile = profileComponent.getGameProfile();
        return new PlayerSkinCache.Entry(PlayerSkinCache.this, gameProfile, DefaultSkinHelper.getSkinTextures(gameProfile), profileComponent.getOverride());
    }

    public /* synthetic */ Object load(Object profile) throws Exception {
        return this.load((ProfileComponent)profile);
    }
}
