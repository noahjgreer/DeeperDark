/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.google.common.hash.Hashing;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
class PlayerSkinProvider.FileCache {
    private final Path directory;
    private final MinecraftProfileTexture.Type type;
    private final Map<String, CompletableFuture<AssetInfo.TextureAsset>> hashToTexture = new Object2ObjectOpenHashMap();

    PlayerSkinProvider.FileCache(Path directory, MinecraftProfileTexture.Type type) {
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
