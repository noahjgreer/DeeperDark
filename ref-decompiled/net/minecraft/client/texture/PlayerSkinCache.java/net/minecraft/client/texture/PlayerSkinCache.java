/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderLayerSet;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.server.GameProfileResolver;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinCache {
    public static final RenderLayer DEFAULT_RENDER_LAYER = PlayerSkinCache.getRenderLayer(DefaultSkinHelper.getSteve());
    public static final Duration TIME_TO_LIVE = Duration.ofMinutes(5L);
    private final LoadingCache<ProfileComponent, CompletableFuture<Optional<Entry>>> fetchingCache = CacheBuilder.newBuilder().expireAfterAccess(TIME_TO_LIVE).build((CacheLoader)new CacheLoader<ProfileComponent, CompletableFuture<Optional<Entry>>>(){

        public CompletableFuture<Optional<Entry>> load(ProfileComponent profileComponent) {
            return profileComponent.resolve(PlayerSkinCache.this.gameProfileResolver).thenCompose(gameProfile -> PlayerSkinCache.this.playerSkinProvider.fetchSkinTextures((GameProfile)gameProfile).thenApply(optional -> optional.map(skinTextures -> new Entry((GameProfile)gameProfile, (SkinTextures)skinTextures, profileComponent.getOverride()))));
        }

        public /* synthetic */ Object load(Object profile) throws Exception {
            return this.load((ProfileComponent)profile);
        }
    });
    private final LoadingCache<ProfileComponent, Entry> immediateCache = CacheBuilder.newBuilder().expireAfterAccess(TIME_TO_LIVE).build((CacheLoader)new CacheLoader<ProfileComponent, Entry>(){

        public Entry load(ProfileComponent profileComponent) {
            GameProfile gameProfile = profileComponent.getGameProfile();
            return new Entry(gameProfile, DefaultSkinHelper.getSkinTextures(gameProfile), profileComponent.getOverride());
        }

        public /* synthetic */ Object load(Object profile) throws Exception {
            return this.load((ProfileComponent)profile);
        }
    });
    final TextureManager textureManager;
    final PlayerSkinProvider playerSkinProvider;
    final GameProfileResolver gameProfileResolver;

    public PlayerSkinCache(TextureManager textureManager, PlayerSkinProvider playerSkinProvider, GameProfileResolver gameProfileResolver) {
        this.textureManager = textureManager;
        this.playerSkinProvider = playerSkinProvider;
        this.gameProfileResolver = gameProfileResolver;
    }

    public Entry get(ProfileComponent profile) {
        Entry entry = this.getFuture(profile).getNow(Optional.empty()).orElse(null);
        if (entry != null) {
            return entry;
        }
        return (Entry)this.immediateCache.getUnchecked((Object)profile);
    }

    public Supplier<Entry> getSupplier(ProfileComponent profile) {
        Entry entry = (Entry)this.immediateCache.getUnchecked((Object)profile);
        CompletableFuture completableFuture = (CompletableFuture)this.fetchingCache.getUnchecked((Object)profile);
        Optional optional = completableFuture.getNow(null);
        if (optional != null) {
            Entry entry2 = optional.orElse(entry);
            return () -> entry2;
        }
        return () -> completableFuture.getNow(Optional.empty()).orElse(entry);
    }

    public CompletableFuture<Optional<Entry>> getFuture(ProfileComponent profile) {
        return (CompletableFuture)this.fetchingCache.getUnchecked((Object)profile);
    }

    static RenderLayer getRenderLayer(SkinTextures skinTextures) {
        return SkullBlockEntityRenderer.getTranslucentRenderLayer(skinTextures.body().texturePath());
    }

    @Environment(value=EnvType.CLIENT)
    public final class Entry {
        private final GameProfile profile;
        private final SkinTextures textures;
        private @Nullable RenderLayer renderLayer;
        private @Nullable GpuTextureView textureView;
        private @Nullable TextRenderLayerSet textRenderLayers;

        public Entry(GameProfile profile, SkinTextures textures, SkinTextures.SkinOverride skinOverride) {
            this.profile = profile;
            this.textures = textures.withOverride(skinOverride);
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public SkinTextures getTextures() {
            return this.textures;
        }

        public RenderLayer getRenderLayer() {
            if (this.renderLayer == null) {
                this.renderLayer = PlayerSkinCache.getRenderLayer(this.textures);
            }
            return this.renderLayer;
        }

        public GpuTextureView getTextureView() {
            if (this.textureView == null) {
                this.textureView = PlayerSkinCache.this.textureManager.getTexture(this.textures.body().texturePath()).getGlTextureView();
            }
            return this.textureView;
        }

        public TextRenderLayerSet getTextRenderLayers() {
            if (this.textRenderLayers == null) {
                this.textRenderLayers = TextRenderLayerSet.of(this.textures.body().texturePath());
            }
            return this.textRenderLayers;
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Entry)) return false;
            Entry entry = (Entry)o;
            if (!this.profile.equals((Object)entry.profile)) return false;
            if (!this.textures.equals(entry.textures)) return false;
            return true;
        }

        public int hashCode() {
            int i = 1;
            i = 31 * i + this.profile.hashCode();
            i = 31 * i + this.textures.hashCode();
            return i;
        }
    }
}
