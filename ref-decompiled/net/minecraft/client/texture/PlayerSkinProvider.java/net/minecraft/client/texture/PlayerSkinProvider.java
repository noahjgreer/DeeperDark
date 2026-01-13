/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.hash.Hashing
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.SignatureState
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.authlib.minecraft.MinecraftProfileTextures
 *  com.mojang.authlib.properties.Property
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.SignatureState;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.properties.Property;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.texture.PlayerSkinTextureDownloader;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.ApiServices;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinProvider {
    static final Logger LOGGER = LogUtils.getLogger();
    private final ApiServices apiServices;
    final PlayerSkinTextureDownloader downloader;
    private final LoadingCache<Key, CompletableFuture<Optional<SkinTextures>>> cache;
    private final FileCache skinCache;
    private final FileCache capeCache;
    private final FileCache elytraCache;

    public PlayerSkinProvider(Path cacheDirectory, final ApiServices apiServices, PlayerSkinTextureDownloader downloader, final Executor executor) {
        this.apiServices = apiServices;
        this.downloader = downloader;
        this.skinCache = new FileCache(cacheDirectory, MinecraftProfileTexture.Type.SKIN);
        this.capeCache = new FileCache(cacheDirectory, MinecraftProfileTexture.Type.CAPE);
        this.elytraCache = new FileCache(cacheDirectory, MinecraftProfileTexture.Type.ELYTRA);
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofSeconds(15L)).build((CacheLoader)new CacheLoader<Key, CompletableFuture<Optional<SkinTextures>>>(){

            public CompletableFuture<Optional<SkinTextures>> load(Key key) {
                return ((CompletableFuture)CompletableFuture.supplyAsync(() -> {
                    Property property = key.packedTextures();
                    if (property == null) {
                        return MinecraftProfileTextures.EMPTY;
                    }
                    MinecraftProfileTextures minecraftProfileTextures = apiServices.sessionService().unpackTextures(property);
                    if (minecraftProfileTextures.signatureState() == SignatureState.INVALID) {
                        LOGGER.warn("Profile contained invalid signature for textures property (profile id: {})", (Object)key.profileId());
                    }
                    return minecraftProfileTextures;
                }, Util.getMainWorkerExecutor().named("unpackSkinTextures")).thenComposeAsync(textures -> PlayerSkinProvider.this.fetchSkinTextures(key.profileId(), (MinecraftProfileTextures)textures), executor)).handle((skinTextures, throwable) -> {
                    if (throwable != null) {
                        LOGGER.warn("Failed to load texture for profile {}", (Object)key.profileId, throwable);
                    }
                    return Optional.ofNullable(skinTextures);
                });
            }

            public /* synthetic */ Object load(Object value) throws Exception {
                return this.load((Key)value);
            }
        });
    }

    public Supplier<SkinTextures> supplySkinTextures(GameProfile profile, boolean requireSecure) {
        CompletableFuture<Optional<SkinTextures>> completableFuture = this.fetchSkinTextures(profile);
        SkinTextures skinTextures2 = DefaultSkinHelper.getSkinTextures(profile);
        if (SharedConstants.DEFAULT_SKIN_OVERRIDE) {
            return () -> skinTextures2;
        }
        Optional optional = completableFuture.getNow(null);
        if (optional != null) {
            SkinTextures skinTextures22 = optional.filter(skinTextures -> !requireSecure || skinTextures.secure()).orElse(skinTextures2);
            return () -> skinTextures22;
        }
        return () -> completableFuture.getNow(Optional.empty()).filter(skinTextures -> !requireSecure || skinTextures.secure()).orElse(skinTextures2);
    }

    public CompletableFuture<Optional<SkinTextures>> fetchSkinTextures(GameProfile profile) {
        if (SharedConstants.DEFAULT_SKIN_OVERRIDE) {
            SkinTextures skinTextures = DefaultSkinHelper.getSkinTextures(profile);
            return CompletableFuture.completedFuture(Optional.of(skinTextures));
        }
        Property property = this.apiServices.sessionService().getPackedTextures(profile);
        return (CompletableFuture)this.cache.getUnchecked((Object)new Key(profile.id(), property));
    }

    CompletableFuture<SkinTextures> fetchSkinTextures(UUID uuid, MinecraftProfileTextures textures) {
        PlayerSkinType playerSkinType;
        CompletableFuture<AssetInfo.TextureAsset> completableFuture;
        MinecraftProfileTexture minecraftProfileTexture = textures.skin();
        if (minecraftProfileTexture != null) {
            completableFuture = this.skinCache.get(minecraftProfileTexture);
            playerSkinType = PlayerSkinType.byModelMetadata(minecraftProfileTexture.getMetadata("model"));
        } else {
            SkinTextures skinTextures = DefaultSkinHelper.getSkinTextures(uuid);
            completableFuture = CompletableFuture.completedFuture(skinTextures.body());
            playerSkinType = skinTextures.model();
        }
        MinecraftProfileTexture minecraftProfileTexture2 = textures.cape();
        CompletableFuture<Object> completableFuture2 = minecraftProfileTexture2 != null ? this.capeCache.get(minecraftProfileTexture2) : CompletableFuture.completedFuture(null);
        MinecraftProfileTexture minecraftProfileTexture3 = textures.elytra();
        CompletableFuture<Object> completableFuture3 = minecraftProfileTexture3 != null ? this.elytraCache.get(minecraftProfileTexture3) : CompletableFuture.completedFuture(null);
        return CompletableFuture.allOf(completableFuture, completableFuture2, completableFuture3).thenApply(void_ -> new SkinTextures((AssetInfo.TextureAsset)completableFuture.join(), (AssetInfo.TextureAsset)completableFuture2.join(), (AssetInfo.TextureAsset)completableFuture3.join(), playerSkinType, textures.signatureState() == SignatureState.SIGNED));
    }

    @Environment(value=EnvType.CLIENT)
    class FileCache {
        private final Path directory;
        private final MinecraftProfileTexture.Type type;
        private final Map<String, CompletableFuture<AssetInfo.TextureAsset>> hashToTexture = new Object2ObjectOpenHashMap();

        FileCache(Path directory, MinecraftProfileTexture.Type type) {
            this.directory = directory;
            this.type = type;
        }

        public CompletableFuture<AssetInfo.TextureAsset> get(MinecraftProfileTexture texture) {
            String string = texture.getHash();
            CompletableFuture<AssetInfo.TextureAsset> completableFuture = this.hashToTexture.get(string);
            if (completableFuture == null) {
                completableFuture = this.store(texture);
                this.hashToTexture.put(string, completableFuture);
            }
            return completableFuture;
        }

        private CompletableFuture<AssetInfo.TextureAsset> store(MinecraftProfileTexture texture) {
            String string = Hashing.sha1().hashUnencodedChars((CharSequence)texture.getHash()).toString();
            Identifier identifier = this.getTexturePath(string);
            Path path = this.directory.resolve(string.length() > 2 ? string.substring(0, 2) : "xx").resolve(string);
            return PlayerSkinProvider.this.downloader.downloadAndRegisterTexture(identifier, path, texture.getUrl(), this.type == MinecraftProfileTexture.Type.SKIN);
        }

        private Identifier getTexturePath(String hash) {
            String string = switch (this.type) {
                default -> throw new MatchException(null, null);
                case MinecraftProfileTexture.Type.SKIN -> "skins";
                case MinecraftProfileTexture.Type.CAPE -> "capes";
                case MinecraftProfileTexture.Type.ELYTRA -> "elytra";
            };
            return Identifier.ofVanilla(string + "/" + hash);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Key
    extends Record {
        final UUID profileId;
        private final @Nullable Property packedTextures;

        Key(UUID profileId, @Nullable Property packedTextures) {
            this.profileId = profileId;
            this.packedTextures = packedTextures;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Key.class, "profileId;packedTextures", "profileId", "packedTextures"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Key.class, "profileId;packedTextures", "profileId", "packedTextures"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Key.class, "profileId;packedTextures", "profileId", "packedTextures"}, this, object);
        }

        public UUID profileId() {
            return this.profileId;
        }

        public @Nullable Property packedTextures() {
            return this.packedTextures;
        }
    }
}
