/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DefaultSkinHelper {
    private static final SkinTextures[] SKINS = new SkinTextures[]{DefaultSkinHelper.createSkinTextures("entity/player/slim/alex", PlayerSkinType.SLIM), DefaultSkinHelper.createSkinTextures("entity/player/slim/ari", PlayerSkinType.SLIM), DefaultSkinHelper.createSkinTextures("entity/player/slim/efe", PlayerSkinType.SLIM), DefaultSkinHelper.createSkinTextures("entity/player/slim/kai", PlayerSkinType.SLIM), DefaultSkinHelper.createSkinTextures("entity/player/slim/makena", PlayerSkinType.SLIM), DefaultSkinHelper.createSkinTextures("entity/player/slim/noor", PlayerSkinType.SLIM), DefaultSkinHelper.createSkinTextures("entity/player/slim/steve", PlayerSkinType.SLIM), DefaultSkinHelper.createSkinTextures("entity/player/slim/sunny", PlayerSkinType.SLIM), DefaultSkinHelper.createSkinTextures("entity/player/slim/zuri", PlayerSkinType.SLIM), DefaultSkinHelper.createSkinTextures("entity/player/wide/alex", PlayerSkinType.WIDE), DefaultSkinHelper.createSkinTextures("entity/player/wide/ari", PlayerSkinType.WIDE), DefaultSkinHelper.createSkinTextures("entity/player/wide/efe", PlayerSkinType.WIDE), DefaultSkinHelper.createSkinTextures("entity/player/wide/kai", PlayerSkinType.WIDE), DefaultSkinHelper.createSkinTextures("entity/player/wide/makena", PlayerSkinType.WIDE), DefaultSkinHelper.createSkinTextures("entity/player/wide/noor", PlayerSkinType.WIDE), DefaultSkinHelper.createSkinTextures("entity/player/wide/steve", PlayerSkinType.WIDE), DefaultSkinHelper.createSkinTextures("entity/player/wide/sunny", PlayerSkinType.WIDE), DefaultSkinHelper.createSkinTextures("entity/player/wide/zuri", PlayerSkinType.WIDE)};

    public static Identifier getTexture() {
        return DefaultSkinHelper.getSteve().body().texturePath();
    }

    public static SkinTextures getSteve() {
        return SKINS[6];
    }

    public static SkinTextures getSkinTextures(UUID uuid) {
        return SKINS[Math.floorMod(uuid.hashCode(), SKINS.length)];
    }

    public static SkinTextures getSkinTextures(GameProfile profile) {
        return DefaultSkinHelper.getSkinTextures(profile.id());
    }

    private static SkinTextures createSkinTextures(String texture, PlayerSkinType type) {
        return new SkinTextures(new AssetInfo.TextureAssetInfo(Identifier.ofVanilla(texture)), null, null, type, true);
    }
}
