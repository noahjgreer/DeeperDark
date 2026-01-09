package net.minecraft.client.texture;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.sprite.FabricStitchResult;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.atlas.AtlasLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class SpriteLoader {
   public static final Set METADATA_SERIALIZERS;
   private static final Logger LOGGER;
   private final Identifier id;
   private final int maxTextureSize;
   private final int width;
   private final int height;

   public SpriteLoader(Identifier id, int maxTextureSize, int width, int height) {
      this.id = id;
      this.maxTextureSize = maxTextureSize;
      this.width = width;
      this.height = height;
   }

   public static SpriteLoader fromAtlas(SpriteAtlasTexture atlasTexture) {
      return new SpriteLoader(atlasTexture.getId(), atlasTexture.getMaxTextureSize(), atlasTexture.getWidth(), atlasTexture.getHeight());
   }

   public StitchResult stitch(List sprites, int mipLevel, Executor executor) {
      ScopedProfiler scopedProfiler = Profilers.get().scoped(() -> {
         return "stitch " + String.valueOf(this.id);
      });

      StitchResult var17;
      try {
         int i = this.maxTextureSize;
         TextureStitcher textureStitcher = new TextureStitcher(i, i, mipLevel);
         int j = Integer.MAX_VALUE;
         int k = 1 << mipLevel;

         SpriteContents spriteContents;
         int l;
         for(Iterator var9 = sprites.iterator(); var9.hasNext(); textureStitcher.add(spriteContents)) {
            spriteContents = (SpriteContents)var9.next();
            j = Math.min(j, Math.min(spriteContents.getWidth(), spriteContents.getHeight()));
            l = Math.min(Integer.lowestOneBit(spriteContents.getWidth()), Integer.lowestOneBit(spriteContents.getHeight()));
            if (l < k) {
               LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", new Object[]{spriteContents.getId(), spriteContents.getWidth(), spriteContents.getHeight(), MathHelper.floorLog2(k), MathHelper.floorLog2(l)});
               k = l;
            }
         }

         int m = Math.min(j, k);
         int n = MathHelper.floorLog2(m);
         if (n < mipLevel) {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", new Object[]{this.id, mipLevel, n, m});
            l = n;
         } else {
            l = mipLevel;
         }

         try {
            textureStitcher.stitch();
         } catch (TextureStitcherCannotFitException var19) {
            CrashReport crashReport = CrashReport.create(var19, "Stitching");
            CrashReportSection crashReportSection = crashReport.addElement("Stitcher");
            crashReportSection.add("Sprites", var19.getSprites().stream().map((spritex) -> {
               return String.format(Locale.ROOT, "%s[%dx%d]", spritex.getId(), spritex.getWidth(), spritex.getHeight());
            }).collect(Collectors.joining(",")));
            crashReportSection.add("Max Texture Size", (Object)i);
            throw new CrashException(crashReport);
         }

         int o = Math.max(textureStitcher.getWidth(), this.width);
         int p = Math.max(textureStitcher.getHeight(), this.height);
         Map map = this.collectStitchedSprites(textureStitcher, o, p);
         Sprite sprite = (Sprite)map.get(MissingSprite.getMissingSpriteId());
         CompletableFuture completableFuture;
         if (l > 0) {
            completableFuture = CompletableFuture.runAsync(() -> {
               map.values().forEach((sprite) -> {
                  sprite.getContents().generateMipmaps(l);
               });
            }, executor);
         } else {
            completableFuture = CompletableFuture.completedFuture((Object)null);
         }

         var17 = new StitchResult(o, p, l, sprite, map, completableFuture);
      } catch (Throwable var20) {
         if (scopedProfiler != null) {
            try {
               scopedProfiler.close();
            } catch (Throwable var18) {
               var20.addSuppressed(var18);
            }
         }

         throw var20;
      }

      if (scopedProfiler != null) {
         scopedProfiler.close();
      }

      return var17;
   }

   public static CompletableFuture loadAll(SpriteOpener opener, List sources, Executor executor) {
      List list = sources.stream().map((sprite) -> {
         return CompletableFuture.supplyAsync(() -> {
            return (SpriteContents)sprite.apply(opener);
         }, executor);
      }).toList();
      return Util.combineSafe(list).thenApply((sprites) -> {
         return sprites.stream().filter(Objects::nonNull).toList();
      });
   }

   public CompletableFuture load(ResourceManager resourceManager, Identifier path, int mipLevel, Executor executor) {
      return this.load(resourceManager, path, mipLevel, executor, METADATA_SERIALIZERS);
   }

   public CompletableFuture load(ResourceManager resourceManager, Identifier path, int mipLevel, Executor executor, Collection metadatas) {
      SpriteOpener spriteOpener = SpriteOpener.create(metadatas);
      return CompletableFuture.supplyAsync(() -> {
         return AtlasLoader.of(resourceManager, path).loadSources(resourceManager);
      }, executor).thenCompose((sources) -> {
         return loadAll(spriteOpener, sources, executor);
      }).thenApply((sprites) -> {
         return this.stitch(sprites, mipLevel, executor);
      });
   }

   private Map collectStitchedSprites(TextureStitcher stitcher, int atlasWidth, int atlasHeight) {
      Map map = new HashMap();
      stitcher.getStitchedSprites((info, x, y) -> {
         map.put(info.getId(), new Sprite(this.id, info, atlasWidth, atlasHeight, x, y));
      });
      return map;
   }

   static {
      METADATA_SERIALIZERS = Set.of(AnimationResourceMetadata.SERIALIZER);
      LOGGER = LogUtils.getLogger();
   }

   @Environment(EnvType.CLIENT)
   public static record StitchResult(int width, int height, int mipLevel, Sprite missing, Map regions, CompletableFuture readyForUpload) implements FabricStitchResult {
      public StitchResult(int i, int j, int k, Sprite sprite, Map map, CompletableFuture completableFuture) {
         this.width = i;
         this.height = j;
         this.mipLevel = k;
         this.missing = sprite;
         this.regions = map;
         this.readyForUpload = completableFuture;
      }

      public CompletableFuture whenComplete() {
         return this.readyForUpload.thenApply((void_) -> {
            return this;
         });
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }

      public int mipLevel() {
         return this.mipLevel;
      }

      public Sprite missing() {
         return this.missing;
      }

      public Map regions() {
         return this.regions;
      }

      public CompletableFuture readyForUpload() {
         return this.readyForUpload;
      }
   }
}
