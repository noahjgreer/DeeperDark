package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class SpriteContents implements TextureStitcher.Stitchable, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Identifier id;
   final int width;
   final int height;
   private final NativeImage image;
   NativeImage[] mipmapLevelsImages;
   @Nullable
   private final Animation animation;
   private final ResourceMetadata metadata;

   public SpriteContents(Identifier id, SpriteDimensions dimensions, NativeImage image, ResourceMetadata metadata) {
      this.id = id;
      this.width = dimensions.width();
      this.height = dimensions.height();
      this.metadata = metadata;
      this.animation = (Animation)metadata.decode(AnimationResourceMetadata.SERIALIZER).map((animationMetadata) -> {
         return this.createAnimation(dimensions, image.getWidth(), image.getHeight(), animationMetadata);
      }).orElse((Object)null);
      this.image = image;
      this.mipmapLevelsImages = new NativeImage[]{this.image};
   }

   public void generateMipmaps(int mipmapLevels) {
      try {
         this.mipmapLevelsImages = MipmapHelper.getMipmapLevelsImages(this.mipmapLevelsImages, mipmapLevels);
      } catch (Throwable var6) {
         CrashReport crashReport = CrashReport.create(var6, "Generating mipmaps for frame");
         CrashReportSection crashReportSection = crashReport.addElement("Sprite being mipmapped");
         crashReportSection.add("First frame", () -> {
            StringBuilder stringBuilder = new StringBuilder();
            if (stringBuilder.length() > 0) {
               stringBuilder.append(", ");
            }

            stringBuilder.append(this.image.getWidth()).append("x").append(this.image.getHeight());
            return stringBuilder.toString();
         });
         CrashReportSection crashReportSection2 = crashReport.addElement("Frame being iterated");
         crashReportSection2.add("Sprite name", (Object)this.id);
         crashReportSection2.add("Sprite size", () -> {
            return this.width + " x " + this.height;
         });
         crashReportSection2.add("Sprite frames", () -> {
            return this.getFrameCount() + " frames";
         });
         crashReportSection2.add("Mipmap levels", (Object)mipmapLevels);
         throw new CrashException(crashReport);
      }
   }

   private int getFrameCount() {
      return this.animation != null ? this.animation.frames.size() : 1;
   }

   @Nullable
   private Animation createAnimation(SpriteDimensions dimensions, int imageWidth, int imageHeight, AnimationResourceMetadata metadata) {
      int i = imageWidth / dimensions.width();
      int j = imageHeight / dimensions.height();
      int k = i * j;
      int l = metadata.defaultFrameTime();
      ArrayList list;
      if (metadata.frames().isEmpty()) {
         list = new ArrayList(k);

         for(int m = 0; m < k; ++m) {
            list.add(new AnimationFrame(m, l));
         }
      } else {
         List list2 = (List)metadata.frames().get();
         list = new ArrayList(list2.size());
         Iterator var11 = list2.iterator();

         while(var11.hasNext()) {
            AnimationFrameResourceMetadata animationFrameResourceMetadata = (AnimationFrameResourceMetadata)var11.next();
            list.add(new AnimationFrame(animationFrameResourceMetadata.index(), animationFrameResourceMetadata.getTime(l)));
         }

         int n = 0;
         IntSet intSet = new IntOpenHashSet();

         for(Iterator iterator = list.iterator(); iterator.hasNext(); ++n) {
            AnimationFrame animationFrame = (AnimationFrame)iterator.next();
            boolean bl = true;
            if (animationFrame.time <= 0) {
               LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", new Object[]{this.id, n, animationFrame.time});
               bl = false;
            }

            if (animationFrame.index < 0 || animationFrame.index >= k) {
               LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", new Object[]{this.id, n, animationFrame.index});
               bl = false;
            }

            if (bl) {
               intSet.add(animationFrame.index);
            } else {
               iterator.remove();
            }
         }

         int[] is = IntStream.range(0, k).filter((ix) -> {
            return !intSet.contains(ix);
         }).toArray();
         if (is.length > 0) {
            LOGGER.warn("Unused frames in sprite {}: {}", this.id, Arrays.toString(is));
         }
      }

      return list.size() <= 1 ? null : new Animation(List.copyOf(list), i, metadata.interpolate());
   }

   void upload(int x, int y, int unpackSkipPixels, int unpackSkipRows, NativeImage[] images, GpuTexture texture) {
      for(int i = 0; i < this.mipmapLevelsImages.length; ++i) {
         RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, images[i], i, 0, x >> i, y >> i, this.width >> i, this.height >> i, unpackSkipPixels >> i, unpackSkipRows >> i);
      }

   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public Identifier getId() {
      return this.id;
   }

   public IntStream getDistinctFrameCount() {
      return this.animation != null ? this.animation.getDistinctFrameCount() : IntStream.of(1);
   }

   @Nullable
   public Animator createAnimator() {
      return this.animation != null ? this.animation.createAnimator() : null;
   }

   public ResourceMetadata getMetadata() {
      return this.metadata;
   }

   public void close() {
      NativeImage[] var1 = this.mipmapLevelsImages;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         NativeImage nativeImage = var1[var3];
         nativeImage.close();
      }

   }

   public String toString() {
      String var10000 = String.valueOf(this.id);
      return "SpriteContents{name=" + var10000 + ", frameCount=" + this.getFrameCount() + ", height=" + this.height + ", width=" + this.width + "}";
   }

   public boolean isPixelTransparent(int frame, int x, int y) {
      int i = x;
      int j = y;
      if (this.animation != null) {
         i = x + this.animation.getFrameX(frame) * this.width;
         j = y + this.animation.getFrameY(frame) * this.height;
      }

      return ColorHelper.getAlpha(this.image.getColorArgb(i, j)) == 0;
   }

   public void upload(int x, int y, GpuTexture texture) {
      if (this.animation != null) {
         this.animation.upload(x, y, texture);
      } else {
         this.upload(x, y, 0, 0, this.mipmapLevelsImages, texture);
      }

   }

   @Environment(EnvType.CLIENT)
   class Animation {
      final List frames;
      private final int frameCount;
      private final boolean interpolation;

      Animation(final List frames, final int frameCount, final boolean interpolation) {
         this.frames = frames;
         this.frameCount = frameCount;
         this.interpolation = interpolation;
      }

      int getFrameX(int frame) {
         return frame % this.frameCount;
      }

      int getFrameY(int frame) {
         return frame / this.frameCount;
      }

      void upload(int x, int y, int frame, GpuTexture texture) {
         int i = this.getFrameX(frame) * SpriteContents.this.width;
         int j = this.getFrameY(frame) * SpriteContents.this.height;
         SpriteContents.this.upload(x, y, i, j, SpriteContents.this.mipmapLevelsImages, texture);
      }

      public Animator createAnimator() {
         return SpriteContents.this.new AnimatorImpl(SpriteContents.this, this, this.interpolation ? SpriteContents.this.new Interpolation() : null);
      }

      public void upload(int x, int y, GpuTexture texture) {
         this.upload(x, y, ((AnimationFrame)this.frames.get(0)).index, texture);
      }

      public IntStream getDistinctFrameCount() {
         return this.frames.stream().mapToInt((frame) -> {
            return frame.index;
         }).distinct();
      }
   }

   @Environment(EnvType.CLIENT)
   private static record AnimationFrame(int index, int time) {
      final int index;
      final int time;

      AnimationFrame(int index, int time) {
         this.index = index;
         this.time = time;
      }

      public int index() {
         return this.index;
      }

      public int time() {
         return this.time;
      }
   }

   @Environment(EnvType.CLIENT)
   private class AnimatorImpl implements Animator {
      int frame;
      int currentTime;
      final Animation animation;
      @Nullable
      private final Interpolation interpolation;

      AnimatorImpl(final SpriteContents spriteContents, @Nullable final Animation animation, final Interpolation interpolation) {
         this.animation = animation;
         this.interpolation = interpolation;
      }

      public void tick(int x, int y, GpuTexture texture) {
         ++this.currentTime;
         AnimationFrame animationFrame = (AnimationFrame)this.animation.frames.get(this.frame);
         if (this.currentTime >= animationFrame.time) {
            int i = animationFrame.index;
            this.frame = (this.frame + 1) % this.animation.frames.size();
            this.currentTime = 0;
            int j = ((AnimationFrame)this.animation.frames.get(this.frame)).index;
            if (i != j) {
               this.animation.upload(x, y, j, texture);
            }
         } else if (this.interpolation != null) {
            this.interpolation.method_24128(x, y, this, texture);
         }

      }

      public void close() {
         if (this.interpolation != null) {
            this.interpolation.close();
         }

      }
   }

   @Environment(EnvType.CLIENT)
   private final class Interpolation implements AutoCloseable {
      private final NativeImage[] images;

      Interpolation() {
         this.images = new NativeImage[SpriteContents.this.mipmapLevelsImages.length];

         for(int i = 0; i < this.images.length; ++i) {
            int j = SpriteContents.this.width >> i;
            int k = SpriteContents.this.height >> i;
            this.images[i] = new NativeImage(j, k, false);
         }

      }

      void method_24128(int i, int j, AnimatorImpl animatorImpl, GpuTexture gpuTexture) {
         Animation animation = animatorImpl.animation;
         List list = animation.frames;
         AnimationFrame animationFrame = (AnimationFrame)list.get(animatorImpl.frame);
         float f = (float)animatorImpl.currentTime / (float)animationFrame.time;
         int k = animationFrame.index;
         int l = ((AnimationFrame)list.get((animatorImpl.frame + 1) % list.size())).index;
         if (k != l) {
            for(int m = 0; m < this.images.length; ++m) {
               int n = SpriteContents.this.width >> m;
               int o = SpriteContents.this.height >> m;

               for(int p = 0; p < o; ++p) {
                  for(int q = 0; q < n; ++q) {
                     int r = this.getPixelColor(animation, k, m, q, p);
                     int s = this.getPixelColor(animation, l, m, q, p);
                     this.images[m].setColorArgb(q, p, ColorHelper.lerp(f, r, s));
                  }
               }
            }

            SpriteContents.this.upload(i, j, 0, 0, this.images, gpuTexture);
         }

      }

      private int getPixelColor(Animation animation, int frameIndex, int layer, int x, int y) {
         return SpriteContents.this.mipmapLevelsImages[layer].getColorArgb(x + (animation.getFrameX(frameIndex) * SpriteContents.this.width >> layer), y + (animation.getFrameY(frameIndex) * SpriteContents.this.height >> layer));
      }

      public void close() {
         NativeImage[] var1 = this.images;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            NativeImage nativeImage = var1[var3];
            nativeImage.close();
         }

      }
   }
}
