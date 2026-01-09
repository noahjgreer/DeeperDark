package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class CloudRenderer extends SinglePreparationResourceReloader implements AutoCloseable {
   private static final int field_60075 = 16;
   private static final int field_60076 = 32;
   private static final int field_60319 = 128;
   private static final float field_53043 = 12.0F;
   private static final int UBO_SIZE = (new Std140SizeCalculator()).putVec4().putVec3().putVec3().get();
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Identifier CLOUD_TEXTURE = Identifier.ofVanilla("textures/environment/clouds.png");
   private static final float field_53045 = 0.6F;
   private static final long field_53046 = 0L;
   private static final int field_53047 = 4;
   private static final int field_53048 = 3;
   private static final int field_53049 = 2;
   private static final int field_53050 = 1;
   private static final int field_53051 = 0;
   private boolean rebuild = true;
   private int centerX = Integer.MIN_VALUE;
   private int centerZ = Integer.MIN_VALUE;
   private ViewMode viewMode;
   @Nullable
   private CloudRenderMode renderMode;
   @Nullable
   private CloudCells cells;
   private int instanceCount;
   private final RenderSystem.ShapeIndexBuffer indexBuffer;
   private final MappableRingBuffer cloudInfoBuffer;
   @Nullable
   private MappableRingBuffer cloudFacesBuffer;

   public CloudRenderer() {
      this.viewMode = CloudRenderer.ViewMode.INSIDE_CLOUDS;
      this.instanceCount = 0;
      this.indexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
      this.cloudInfoBuffer = new MappableRingBuffer(() -> {
         return "Cloud UBO";
      }, 130, UBO_SIZE);
   }

   protected Optional prepare(ResourceManager resourceManager, Profiler profiler) {
      try {
         InputStream inputStream = resourceManager.open(CLOUD_TEXTURE);

         Optional var20;
         try {
            NativeImage nativeImage = NativeImage.read(inputStream);

            try {
               int i = nativeImage.getWidth();
               int j = nativeImage.getHeight();
               long[] ls = new long[i * j];
               int k = 0;

               while(true) {
                  if (k >= j) {
                     var20 = Optional.of(new CloudCells(ls, i, j));
                     break;
                  }

                  for(int l = 0; l < i; ++l) {
                     int m = nativeImage.getColorArgb(l, k);
                     if (isEmpty(m)) {
                        ls[l + k * i] = 0L;
                     } else {
                        boolean bl = isEmpty(nativeImage.getColorArgb(l, Math.floorMod(k - 1, j)));
                        boolean bl2 = isEmpty(nativeImage.getColorArgb(Math.floorMod(l + 1, j), k));
                        boolean bl3 = isEmpty(nativeImage.getColorArgb(l, Math.floorMod(k + 1, j)));
                        boolean bl4 = isEmpty(nativeImage.getColorArgb(Math.floorMod(l - 1, j), k));
                        ls[l + k * i] = packCloudCell(m, bl, bl2, bl3, bl4);
                     }
                  }

                  ++k;
               }
            } catch (Throwable var17) {
               if (nativeImage != null) {
                  try {
                     nativeImage.close();
                  } catch (Throwable var16) {
                     var17.addSuppressed(var16);
                  }
               }

               throw var17;
            }

            if (nativeImage != null) {
               nativeImage.close();
            }
         } catch (Throwable var18) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var15) {
                  var18.addSuppressed(var15);
               }
            }

            throw var18;
         }

         if (inputStream != null) {
            inputStream.close();
         }

         return var20;
      } catch (IOException var19) {
         LOGGER.error("Failed to load cloud texture", var19);
         return Optional.empty();
      }
   }

   private static int calcCloudBufferSize(int cloudRange) {
      int i = true;
      int j = (cloudRange + 1) * 2 * (cloudRange + 1) * 2 / 2;
      int k = j * 4 + 54;
      return k * 3;
   }

   protected void apply(Optional optional, ResourceManager resourceManager, Profiler profiler) {
      this.cells = (CloudCells)optional.orElse((Object)null);
      this.rebuild = true;
   }

   private static boolean isEmpty(int color) {
      return ColorHelper.getAlpha(color) < 10;
   }

   private static long packCloudCell(int color, boolean borderNorth, boolean borderEast, boolean borderSouth, boolean borderWest) {
      return (long)color << 4 | (long)((borderNorth ? 1 : 0) << 3) | (long)((borderEast ? 1 : 0) << 2) | (long)((borderSouth ? 1 : 0) << 1) | (long)((borderWest ? 1 : 0) << 0);
   }

   private static boolean hasBorderNorth(long packed) {
      return (packed >> 3 & 1L) != 0L;
   }

   private static boolean hasBorderEast(long packed) {
      return (packed >> 2 & 1L) != 0L;
   }

   private static boolean hasBorderSouth(long packed) {
      return (packed >> 1 & 1L) != 0L;
   }

   private static boolean hasBorderWest(long packed) {
      return (packed >> 0 & 1L) != 0L;
   }

   public void renderClouds(int color, CloudRenderMode mode, float cloudHeight, Vec3d cameraPos, float cloudPhase) {
      if (this.cells != null) {
         int i = Math.min((Integer)MinecraftClient.getInstance().options.getCloudRenderDistance().getValue(), 128) * 16;
         int j = MathHelper.ceil((float)i / 12.0F);
         int k = calcCloudBufferSize(j);
         if (this.cloudFacesBuffer == null || this.cloudFacesBuffer.getBlocking().size() != k) {
            if (this.cloudFacesBuffer != null) {
               this.cloudFacesBuffer.close();
            }

            this.cloudFacesBuffer = new MappableRingBuffer(() -> {
               return "Cloud UTB";
            }, 258, k);
         }

         float f = (float)((double)cloudHeight - cameraPos.y);
         float g = f + 4.0F;
         ViewMode viewMode;
         if (g < 0.0F) {
            viewMode = CloudRenderer.ViewMode.ABOVE_CLOUDS;
         } else if (f > 0.0F) {
            viewMode = CloudRenderer.ViewMode.BELOW_CLOUDS;
         } else {
            viewMode = CloudRenderer.ViewMode.INSIDE_CLOUDS;
         }

         double d = cameraPos.x + (double)(cloudPhase * 0.030000001F);
         double e = cameraPos.z + 3.9600000381469727;
         double h = (double)this.cells.width * 12.0;
         double l = (double)this.cells.height * 12.0;
         d -= (double)MathHelper.floor(d / h) * h;
         e -= (double)MathHelper.floor(e / l) * l;
         int m = MathHelper.floor(d / 12.0);
         int n = MathHelper.floor(e / 12.0);
         float o = (float)(d - (double)((float)m * 12.0F));
         float p = (float)(e - (double)((float)n * 12.0F));
         boolean bl = mode == CloudRenderMode.FANCY;
         RenderPipeline renderPipeline = bl ? RenderPipelines.CLOUDS : RenderPipelines.FLAT_CLOUDS;
         GpuBuffer.MappedView mappedView;
         if (this.rebuild || m != this.centerX || n != this.centerZ || viewMode != this.viewMode || mode != this.renderMode) {
            this.rebuild = false;
            this.centerX = m;
            this.centerZ = n;
            this.viewMode = viewMode;
            this.renderMode = mode;
            this.cloudFacesBuffer.rotate();
            mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.cloudFacesBuffer.getBlocking(), false, true);

            try {
               this.buildCloudCells(viewMode, mappedView.data(), m, n, bl, j);
               this.instanceCount = mappedView.data().position() / 3;
            } catch (Throwable var40) {
               if (mappedView != null) {
                  try {
                     mappedView.close();
                  } catch (Throwable var37) {
                     var40.addSuppressed(var37);
                  }
               }

               throw var40;
            }

            if (mappedView != null) {
               mappedView.close();
            }
         }

         if (this.instanceCount != 0) {
            mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.cloudInfoBuffer.getBlocking(), false, true);

            try {
               Std140Builder.intoBuffer(mappedView.data()).putVec4(ColorHelper.getRedFloat(color), ColorHelper.getGreenFloat(color), ColorHelper.getBlueFloat(color), 1.0F).putVec3(-o, f, -p).putVec3(12.0F, 4.0F, 12.0F);
            } catch (Throwable var39) {
               if (mappedView != null) {
                  try {
                     mappedView.close();
                  } catch (Throwable var38) {
                     var39.addSuppressed(var38);
                  }
               }

               throw var39;
            }

            if (mappedView != null) {
               mappedView.close();
            }

            GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write(RenderSystem.getModelViewMatrix(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f(), 0.0F);
            Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
            Framebuffer framebuffer2 = MinecraftClient.getInstance().worldRenderer.getCloudsFramebuffer();
            RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
            GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(6 * this.instanceCount);
            GpuTextureView gpuTextureView;
            GpuTextureView gpuTextureView2;
            if (framebuffer2 != null) {
               gpuTextureView = framebuffer2.getColorAttachmentView();
               gpuTextureView2 = framebuffer2.getDepthAttachmentView();
            } else {
               gpuTextureView = framebuffer.getColorAttachmentView();
               gpuTextureView2 = framebuffer.getDepthAttachmentView();
            }

            RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
               return "Clouds";
            }, gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());

            try {
               renderPass.setPipeline(renderPipeline);
               RenderSystem.bindDefaultUniforms(renderPass);
               renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
               renderPass.setIndexBuffer(gpuBuffer, shapeIndexBuffer.getIndexType());
               renderPass.setVertexBuffer(0, RenderSystem.getQuadVertexBuffer());
               renderPass.setUniform("CloudInfo", this.cloudInfoBuffer.getBlocking());
               renderPass.setUniform("CloudFaces", this.cloudFacesBuffer.getBlocking());
               renderPass.setPipeline(renderPipeline);
               renderPass.drawIndexed(0, 0, 6 * this.instanceCount, 1);
            } catch (Throwable var41) {
               if (renderPass != null) {
                  try {
                     renderPass.close();
                  } catch (Throwable var36) {
                     var41.addSuppressed(var36);
                  }
               }

               throw var41;
            }

            if (renderPass != null) {
               renderPass.close();
            }

         }
      }
   }

   private void buildCloudCells(ViewMode viewMode, ByteBuffer byteBuffer, int x, int z, boolean bl, int i) {
      if (this.cells != null) {
         long[] ls = this.cells.cells;
         int j = this.cells.width;
         int k = this.cells.height;

         for(int l = 0; l <= 2 * i; ++l) {
            for(int m = -l; m <= l; ++m) {
               int n = l - Math.abs(m);
               if (n >= 0 && n <= i && m * m + n * n <= i * i) {
                  if (n != 0) {
                     this.method_72155(viewMode, byteBuffer, x, z, bl, m, j, -n, k, ls);
                  }

                  this.method_72155(viewMode, byteBuffer, x, z, bl, m, j, n, k, ls);
               }
            }
         }

      }
   }

   private void method_72155(ViewMode viewMode, ByteBuffer byteBuffer, int i, int j, boolean bl, int k, int l, int m, int n, long[] ls) {
      int o = Math.floorMod(i + k, l);
      int p = Math.floorMod(j + m, n);
      long q = ls[o + p * l];
      if (q != 0L) {
         if (bl) {
            this.buildCloudCellFancy(viewMode, byteBuffer, k, m, q);
         } else {
            this.buildCloudCellFast(byteBuffer, k, m);
         }

      }
   }

   private void buildCloudCellFast(ByteBuffer byteBuffer, int color, int x) {
      this.method_71098(byteBuffer, color, x, Direction.DOWN, 32);
   }

   private void method_71098(ByteBuffer byteBuffer, int i, int j, Direction direction, int k) {
      int l = direction.getIndex() | k;
      l |= (i & 1) << 7;
      l |= (j & 1) << 6;
      byteBuffer.put((byte)(i >> 1)).put((byte)(j >> 1)).put((byte)l);
   }

   private void buildCloudCellFancy(ViewMode viewMode, ByteBuffer byteBuffer, int i, int j, long l) {
      if (viewMode != CloudRenderer.ViewMode.BELOW_CLOUDS) {
         this.method_71098(byteBuffer, i, j, Direction.UP, 0);
      }

      if (viewMode != CloudRenderer.ViewMode.ABOVE_CLOUDS) {
         this.method_71098(byteBuffer, i, j, Direction.DOWN, 0);
      }

      if (hasBorderNorth(l) && j > 0) {
         this.method_71098(byteBuffer, i, j, Direction.NORTH, 0);
      }

      if (hasBorderSouth(l) && j < 0) {
         this.method_71098(byteBuffer, i, j, Direction.SOUTH, 0);
      }

      if (hasBorderWest(l) && i > 0) {
         this.method_71098(byteBuffer, i, j, Direction.WEST, 0);
      }

      if (hasBorderEast(l) && i < 0) {
         this.method_71098(byteBuffer, i, j, Direction.EAST, 0);
      }

      boolean bl = Math.abs(i) <= 1 && Math.abs(j) <= 1;
      if (bl) {
         Direction[] var8 = Direction.values();
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            Direction direction = var8[var10];
            this.method_71098(byteBuffer, i, j, direction, 16);
         }
      }

   }

   public void scheduleTerrainUpdate() {
      this.rebuild = true;
   }

   public void rotate() {
      this.cloudInfoBuffer.rotate();
   }

   public void close() {
      this.cloudInfoBuffer.close();
      if (this.cloudFacesBuffer != null) {
         this.cloudFacesBuffer.close();
      }

   }

   // $FF: synthetic method
   protected Object prepare(final ResourceManager manager, final Profiler profiler) {
      return this.prepare(manager, profiler);
   }

   @Environment(EnvType.CLIENT)
   private static enum ViewMode {
      ABOVE_CLOUDS,
      INSIDE_CLOUDS,
      BELOW_CLOUDS;

      // $FF: synthetic method
      private static ViewMode[] method_62182() {
         return new ViewMode[]{ABOVE_CLOUDS, INSIDE_CLOUDS, BELOW_CLOUDS};
      }
   }

   @Environment(EnvType.CLIENT)
   public static record CloudCells(long[] cells, int width, int height) {
      final long[] cells;
      final int width;
      final int height;

      public CloudCells(long[] ls, int i, int j) {
         this.cells = ls;
         this.width = i;
         this.height = j;
      }

      public long[] cells() {
         return this.cells;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }
   }
}
