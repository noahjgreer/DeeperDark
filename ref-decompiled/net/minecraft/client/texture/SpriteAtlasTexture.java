package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.sprite.FabricSpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class SpriteAtlasTexture extends AbstractTexture implements DynamicTexture, TextureTickListener, FabricSpriteAtlasTexture {
   private static final Logger LOGGER = LogUtils.getLogger();
   /** @deprecated */
   @Deprecated
   public static final Identifier BLOCK_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/blocks.png");
   /** @deprecated */
   @Deprecated
   public static final Identifier PARTICLE_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/particles.png");
   private List spritesToLoad = List.of();
   private List animatedSprites = List.of();
   private Map sprites = Map.of();
   @Nullable
   private Sprite missingSprite;
   private final Identifier id;
   private final int maxTextureSize;
   private int width;
   private int height;
   private int mipLevel;

   public SpriteAtlasTexture(Identifier id) {
      this.id = id;
      this.maxTextureSize = RenderSystem.getDevice().getMaxTextureSize();
   }

   private void method_72240(int i, int j, int k) {
      LOGGER.info("Created: {}x{}x{} {}-atlas", new Object[]{i, j, k, this.id});
      GpuDevice gpuDevice = RenderSystem.getDevice();
      this.close();
      Identifier var10002 = this.id;
      Objects.requireNonNull(var10002);
      this.glTexture = gpuDevice.createTexture((Supplier)(var10002::toString), 7, TextureFormat.RGBA8, i, j, 1, k + 1);
      this.glTextureView = gpuDevice.createTextureView(this.glTexture);
      this.width = i;
      this.height = j;
      this.mipLevel = k;
   }

   public void upload(SpriteLoader.StitchResult stitchResult) {
      this.method_72240(stitchResult.width(), stitchResult.height(), stitchResult.mipLevel());
      this.clear();
      this.setFilter(false, this.mipLevel > 1);
      this.sprites = Map.copyOf(stitchResult.regions());
      this.missingSprite = (Sprite)this.sprites.get(MissingSprite.getMissingSpriteId());
      if (this.missingSprite == null) {
         String var10002 = String.valueOf(this.id);
         throw new IllegalStateException("Atlas '" + var10002 + "' (" + this.sprites.size() + " sprites) has no missing texture sprite");
      } else {
         List list = new ArrayList();
         List list2 = new ArrayList();
         Iterator var4 = stitchResult.regions().values().iterator();

         while(var4.hasNext()) {
            Sprite sprite = (Sprite)var4.next();
            list.add(sprite.getContents());

            try {
               sprite.upload(this.glTexture);
            } catch (Throwable var9) {
               CrashReport crashReport = CrashReport.create(var9, "Stitching texture atlas");
               CrashReportSection crashReportSection = crashReport.addElement("Texture being stitched together");
               crashReportSection.add("Atlas path", (Object)this.id);
               crashReportSection.add("Sprite", (Object)sprite);
               throw new CrashException(crashReport);
            }

            Sprite.TickableAnimation tickableAnimation = sprite.createAnimation();
            if (tickableAnimation != null) {
               list2.add(tickableAnimation);
            }
         }

         this.spritesToLoad = List.copyOf(list);
         this.animatedSprites = List.copyOf(list2);
      }
   }

   public void save(Identifier id, Path path) throws IOException {
      String string = id.toUnderscoreSeparatedString();
      TextureUtil.writeAsPNG(path, string, this.getGlTexture(), this.mipLevel, (color) -> {
         return color;
      });
      dumpAtlasInfos(path, string, this.sprites);
   }

   private static void dumpAtlasInfos(Path path, String id, Map sprites) {
      Path path2 = path.resolve(id + ".txt");

      try {
         Writer writer = Files.newBufferedWriter(path2);

         try {
            Iterator var5 = sprites.entrySet().stream().sorted(Entry.comparingByKey()).toList().iterator();

            while(var5.hasNext()) {
               Map.Entry entry = (Map.Entry)var5.next();
               Sprite sprite = (Sprite)entry.getValue();
               writer.write(String.format(Locale.ROOT, "%s\tx=%d\ty=%d\tw=%d\th=%d%n", entry.getKey(), sprite.getX(), sprite.getY(), sprite.getContents().getWidth(), sprite.getContents().getHeight()));
            }
         } catch (Throwable var9) {
            if (writer != null) {
               try {
                  writer.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (writer != null) {
            writer.close();
         }
      } catch (IOException var10) {
         LOGGER.warn("Failed to write file {}", path2, var10);
      }

   }

   public void tickAnimatedSprites() {
      if (this.glTexture != null) {
         Iterator var1 = this.animatedSprites.iterator();

         while(var1.hasNext()) {
            Sprite.TickableAnimation tickableAnimation = (Sprite.TickableAnimation)var1.next();
            tickableAnimation.tick(this.glTexture);
         }

      }
   }

   public void tick() {
      this.tickAnimatedSprites();
   }

   public Sprite getSprite(Identifier id) {
      Sprite sprite = (Sprite)this.sprites.getOrDefault(id, this.missingSprite);
      if (sprite == null) {
         throw new IllegalStateException("Tried to lookup sprite, but atlas is not initialized");
      } else {
         return sprite;
      }
   }

   public void clear() {
      this.spritesToLoad.forEach(SpriteContents::close);
      this.animatedSprites.forEach(Sprite.TickableAnimation::close);
      this.spritesToLoad = List.of();
      this.animatedSprites = List.of();
      this.sprites = Map.of();
      this.missingSprite = null;
   }

   public Identifier getId() {
      return this.id;
   }

   public int getMaxTextureSize() {
      return this.maxTextureSize;
   }

   int getWidth() {
      return this.width;
   }

   int getHeight() {
      return this.height;
   }
}
