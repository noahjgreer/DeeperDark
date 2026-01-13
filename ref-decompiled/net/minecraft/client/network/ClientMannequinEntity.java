/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.network.ClientMannequinEntity
 *  net.minecraft.client.network.ClientPlayerLikeEntity
 *  net.minecraft.client.network.ClientPlayerLikeState
 *  net.minecraft.client.texture.PlayerSkinCache
 *  net.minecraft.client.texture.PlayerSkinCache$Entry
 *  net.minecraft.client.util.DefaultSkinHelper
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.data.TrackedData
 *  net.minecraft.entity.decoration.MannequinEntity
 *  net.minecraft.entity.player.SkinTextures
 *  net.minecraft.text.Text
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.network.ClientPlayerLikeState;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.MannequinEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientMannequinEntity
extends MannequinEntity
implements ClientPlayerLikeEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final SkinTextures DEFAULT_TEXTURES = DefaultSkinHelper.getSkinTextures((GameProfile)MannequinEntity.DEFAULT_INFO.getGameProfile());
    private final ClientPlayerLikeState state = new ClientPlayerLikeState();
    private @Nullable CompletableFuture<Optional<SkinTextures>> skinLookup;
    private SkinTextures skin = DEFAULT_TEXTURES;
    private final PlayerSkinCache skinCache;

    public static void setFactory(PlayerSkinCache cache) {
        MannequinEntity.factory = (type, world) -> world instanceof ClientWorld ? new ClientMannequinEntity(world, cache) : new MannequinEntity(type, world);
    }

    public ClientMannequinEntity(World world, PlayerSkinCache skinCache) {
        super(world);
        this.skinCache = skinCache;
    }

    public void tick() {
        super.tick();
        this.state.tick(this.getEntityPos(), this.getVelocity());
        if (this.skinLookup != null && this.skinLookup.isDone()) {
            try {
                ((Optional)this.skinLookup.get()).ifPresent(arg_0 -> this.setSkin(arg_0));
                this.skinLookup = null;
            }
            catch (Exception exception) {
                LOGGER.error("Error when trying to look up skin", (Throwable)exception);
            }
        }
    }

    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (data.equals((Object)PROFILE)) {
            this.refreshSkin();
        }
    }

    private void refreshSkin() {
        if (this.skinLookup != null) {
            CompletableFuture completableFuture = this.skinLookup;
            this.skinLookup = null;
            completableFuture.cancel(false);
        }
        this.skinLookup = this.skinCache.getFuture(this.getMannequinProfile()).thenApply(skin -> skin.map(PlayerSkinCache.Entry::getTextures));
    }

    public ClientPlayerLikeState getState() {
        return this.state;
    }

    public SkinTextures getSkin() {
        return this.skin;
    }

    private void setSkin(SkinTextures skin) {
        this.skin = skin;
    }

    public @Nullable Text getMannequinName() {
        return this.getDescription();
    }

    public // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ParrotEntity.Variant getShoulderParrotVariant(boolean leftShoulder) {
        return null;
    }

    public boolean hasExtraEars() {
        return false;
    }
}

