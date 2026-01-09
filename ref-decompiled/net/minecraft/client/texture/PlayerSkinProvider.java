package net.minecraft.client.texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.SignatureState;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nullables;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class PlayerSkinProvider {
   static final Logger LOGGER = LogUtils.getLogger();
   private final MinecraftSessionService sessionService;
   private final LoadingCache cache;
   private final FileCache skinCache;
   private final FileCache capeCache;
   private final FileCache elytraCache;

   public PlayerSkinProvider(Path directory, final MinecraftSessionService sessionService, final Executor executor) {
      this.sessionService = sessionService;
      this.skinCache = new FileCache(directory, Type.SKIN);
      this.capeCache = new FileCache(directory, Type.CAPE);
      this.elytraCache = new FileCache(directory, Type.ELYTRA);
      this.cache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofSeconds(15L)).build(new CacheLoader() {
         public CompletableFuture load(Key key) {
            return CompletableFuture.supplyAsync(() -> {
               Property property = key.packedTextures();
               if (property == null) {
                  return MinecraftProfileTextures.EMPTY;
               } else {
                  MinecraftProfileTextures minecraftProfileTextures = sessionService.unpackTextures(property);
                  if (minecraftProfileTextures.signatureState() == SignatureState.INVALID) {
                     PlayerSkinProvider.LOGGER.warn("Profile contained invalid signature for textures property (profile id: {})", key.profileId());
                  }

                  return minecraftProfileTextures;
               }
            }, Util.getMainWorkerExecutor().named("unpackSkinTextures")).thenComposeAsync((textures) -> {
               return PlayerSkinProvider.this.fetchSkinTextures(key.profileId(), textures);
            }, executor).handle((skinTextures, throwable) -> {
               if (throwable != null) {
                  PlayerSkinProvider.LOGGER.warn("Failed to load texture for profile {}", key.profileId, throwable);
               }

               return Optional.ofNullable(skinTextures);
            });
         }

         // $FF: synthetic method
         public Object load(final Object value) throws Exception {
            return this.load((Key)value);
         }
      });
   }

   public Supplier getSkinTexturesSupplier(GameProfile profile) {
      CompletableFuture completableFuture = this.fetchSkinTextures(profile);
      SkinTextures skinTextures = DefaultSkinHelper.getSkinTextures(profile);
      return () -> {
         return (SkinTextures)((Optional)completableFuture.getNow(Optional.empty())).orElse(skinTextures);
      };
   }

   public SkinTextures getSkinTextures(GameProfile profile) {
      SkinTextures skinTextures = this.getSkinTextures(profile, (SkinTextures)null);
      return skinTextures != null ? skinTextures : DefaultSkinHelper.getSkinTextures(profile);
   }

   @Nullable
   public SkinTextures getSkinTextures(GameProfile profile, @Nullable SkinTextures fallback) {
      return (SkinTextures)((Optional)this.fetchSkinTextures(profile).getNow(Optional.empty())).orElse(fallback);
   }

   public CompletableFuture fetchSkinTextures(GameProfile profile) {
      Property property = this.sessionService.getPackedTextures(profile);
      return (CompletableFuture)this.cache.getUnchecked(new Key(profile.getId(), property));
   }

   CompletableFuture fetchSkinTextures(UUID uuid, MinecraftProfileTextures textures) {
      MinecraftProfileTexture minecraftProfileTexture = textures.skin();
      CompletableFuture completableFuture;
      SkinTextures.Model model;
      if (minecraftProfileTexture != null) {
         completableFuture = this.skinCache.get(minecraftProfileTexture);
         model = SkinTextures.Model.fromName(minecraftProfileTexture.getMetadata("model"));
      } else {
         SkinTextures skinTextures = DefaultSkinHelper.getSkinTextures(uuid);
         completableFuture = CompletableFuture.completedFuture(skinTextures.texture());
         model = skinTextures.model();
      }

      String string = (String)Nullables.map(minecraftProfileTexture, MinecraftProfileTexture::getUrl);
      MinecraftProfileTexture minecraftProfileTexture2 = textures.cape();
      CompletableFuture completableFuture2 = minecraftProfileTexture2 != null ? this.capeCache.get(minecraftProfileTexture2) : CompletableFuture.completedFuture((Object)null);
      MinecraftProfileTexture minecraftProfileTexture3 = textures.elytra();
      CompletableFuture completableFuture3 = minecraftProfileTexture3 != null ? this.elytraCache.get(minecraftProfileTexture3) : CompletableFuture.completedFuture((Object)null);
      return CompletableFuture.allOf(completableFuture, completableFuture2, completableFuture3).thenApply((v) -> {
         return new SkinTextures((Identifier)completableFuture.join(), string, (Identifier)completableFuture2.join(), (Identifier)completableFuture3.join(), model, textures.signatureState() == SignatureState.SIGNED);
      });
   }

   @Environment(EnvType.CLIENT)
   static class FileCache {
      private final Path directory;
      private final MinecraftProfileTexture.Type type;
      private final Map hashToTexture = new Object2ObjectOpenHashMap();

      FileCache(Path directory, MinecraftProfileTexture.Type type) {
         this.directory = directory;
         this.type = type;
      }

      public CompletableFuture get(MinecraftProfileTexture texture) {
         String string = texture.getHash();
         CompletableFuture completableFuture = (CompletableFuture)this.hashToTexture.get(string);
         if (completableFuture == null) {
            completableFuture = this.store(texture);
            this.hashToTexture.put(string, completableFuture);
         }

         return completableFuture;
      }

      private CompletableFuture store(MinecraftProfileTexture texture) {
         String string = Hashing.sha1().hashUnencodedChars(texture.getHash()).toString();
         Identifier identifier = this.getTexturePath(string);
         Path path = this.directory.resolve(string.length() > 2 ? string.substring(0, 2) : "xx").resolve(string);
         return PlayerSkinTextureDownloader.downloadAndRegisterTexture(identifier, path, texture.getUrl(), this.type == Type.SKIN);
      }

      private Identifier getTexturePath(String hash) {
         String var10000;
         switch (this.type) {
            case SKIN:
               var10000 = "skins";
               break;
            case CAPE:
               var10000 = "capes";
               break;
            case ELYTRA:
               var10000 = "elytra";
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         String string = var10000;
         return Identifier.ofVanilla(string + "/" + hash);
      }
   }

   @Environment(EnvType.CLIENT)
   private static record Key(UUID profileId, @Nullable Property packedTextures) {
      final UUID profileId;

      Key(UUID uUID, @Nullable Property property) {
         this.profileId = uUID;
         this.packedTextures = property;
      }

      public UUID profileId() {
         return this.profileId;
      }

      @Nullable
      public Property packedTextures() {
         return this.packedTextures;
      }
   }
}
