package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.IntConsumer;
import java.util.function.LongSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.DynamicUniforms;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.gl.ScissorState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.tracy.TracyFrameCapturer;
import net.minecraft.util.TimeSupplier;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.DeobfuscateClass;
import net.minecraft.util.collection.ArrayListDeque;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public class RenderSystem {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
   public static final int PROJECTION_MATRIX_UBO_SIZE = (new Std140SizeCalculator()).putMat4f().get();
   @Nullable
   private static Thread renderThread;
   @Nullable
   private static GpuDevice DEVICE;
   private static double lastDrawTime = Double.MIN_VALUE;
   private static final ShapeIndexBuffer sharedSequential = new ShapeIndexBuffer(1, 1, IntConsumer::accept);
   private static final ShapeIndexBuffer sharedSequentialQuad = new ShapeIndexBuffer(4, 6, (indexConsumer, firstVertexIndex) -> {
      indexConsumer.accept(firstVertexIndex);
      indexConsumer.accept(firstVertexIndex + 1);
      indexConsumer.accept(firstVertexIndex + 2);
      indexConsumer.accept(firstVertexIndex + 2);
      indexConsumer.accept(firstVertexIndex + 3);
      indexConsumer.accept(firstVertexIndex);
   });
   private static final ShapeIndexBuffer sharedSequentialLines = new ShapeIndexBuffer(4, 6, (indexConsumer, firstVertexIndex) -> {
      indexConsumer.accept(firstVertexIndex);
      indexConsumer.accept(firstVertexIndex + 1);
      indexConsumer.accept(firstVertexIndex + 2);
      indexConsumer.accept(firstVertexIndex + 3);
      indexConsumer.accept(firstVertexIndex + 2);
      indexConsumer.accept(firstVertexIndex + 1);
   });
   private static ProjectionType projectionType;
   private static ProjectionType savedProjectionType;
   private static final Matrix4fStack modelViewStack;
   private static Matrix4f textureMatrix;
   public static final int TEXTURE_COUNT = 12;
   private static final GpuTextureView[] shaderTextures;
   @Nullable
   private static GpuBufferSlice shaderFog;
   @Nullable
   private static GpuBufferSlice shaderLightDirections;
   @Nullable
   private static GpuBufferSlice projectionMatrixBuffer;
   @Nullable
   private static GpuBufferSlice savedProjectionMatrixBuffer;
   private static final Vector3f modelOffset;
   private static float shaderLineWidth;
   private static String apiDescription;
   private static final AtomicLong pollEventsWaitStart;
   private static final AtomicBoolean pollingEvents;
   @Nullable
   private static GpuBuffer QUAD_VERTEX_BUFFER;
   private static final ArrayListDeque PENDING_FENCES;
   @Nullable
   public static GpuTextureView outputColorTextureOverride;
   @Nullable
   public static GpuTextureView outputDepthTextureOverride;
   @Nullable
   private static GpuBuffer globalSettingsUniform;
   @Nullable
   private static DynamicUniforms dynamicUniforms;
   private static ScissorState scissorStateForRenderTypeDraws;

   public static void initRenderThread() {
      if (renderThread != null) {
         throw new IllegalStateException("Could not initialize render thread");
      } else {
         renderThread = Thread.currentThread();
      }
   }

   public static boolean isOnRenderThread() {
      return Thread.currentThread() == renderThread;
   }

   public static void assertOnRenderThread() {
      if (!isOnRenderThread()) {
         throw constructThreadException();
      }
   }

   private static IllegalStateException constructThreadException() {
      return new IllegalStateException("Rendersystem called from wrong thread");
   }

   private static void pollEvents() {
      pollEventsWaitStart.set(Util.getMeasuringTimeMs());
      pollingEvents.set(true);
      GLFW.glfwPollEvents();
      pollingEvents.set(false);
   }

   public static boolean isFrozenAtPollEvents() {
      return pollingEvents.get() && Util.getMeasuringTimeMs() - pollEventsWaitStart.get() > 200L;
   }

   public static void flipFrame(long window, @Nullable TracyFrameCapturer capturer) {
      pollEvents();
      Tessellator.getInstance().clear();
      GLFW.glfwSwapBuffers(window);
      if (capturer != null) {
         capturer.markFrame();
      }

      dynamicUniforms.clear();
      MinecraftClient.getInstance().worldRenderer.rotate();
      pollEvents();
   }

   public static void limitDisplayFPS(int fps) {
      double d = lastDrawTime + 1.0 / (double)fps;

      double e;
      for(e = GLFW.glfwGetTime(); e < d; e = GLFW.glfwGetTime()) {
         GLFW.glfwWaitEventsTimeout(d - e);
      }

      lastDrawTime = e;
   }

   public static void setShaderFog(GpuBufferSlice shaderFog) {
      RenderSystem.shaderFog = shaderFog;
   }

   @Nullable
   public static GpuBufferSlice getShaderFog() {
      return shaderFog;
   }

   public static void setShaderLights(GpuBufferSlice shaderLightDirections) {
      RenderSystem.shaderLightDirections = shaderLightDirections;
   }

   @Nullable
   public static GpuBufferSlice getShaderLights() {
      return shaderLightDirections;
   }

   public static void lineWidth(float width) {
      assertOnRenderThread();
      shaderLineWidth = width;
   }

   public static float getShaderLineWidth() {
      assertOnRenderThread();
      return shaderLineWidth;
   }

   public static void enableScissorForRenderTypeDraws(int i, int j, int k, int l) {
      scissorStateForRenderTypeDraws.enable(i, j, k, l);
   }

   public static void disableScissorForRenderTypeDraws() {
      scissorStateForRenderTypeDraws.disable();
   }

   public static ScissorState getScissorStateForRenderTypeDraws() {
      return scissorStateForRenderTypeDraws;
   }

   public static String getBackendDescription() {
      return String.format(Locale.ROOT, "LWJGL version %s", GLX._getLWJGLVersion());
   }

   public static String getApiDescription() {
      return apiDescription;
   }

   public static TimeSupplier.Nanoseconds initBackendSystem() {
      LongSupplier var10000 = GLX._initGlfw();
      Objects.requireNonNull(var10000);
      return var10000::getAsLong;
   }

   public static void initRenderer(long windowHandle, int debugVerbosity, boolean sync, BiFunction shaderSourceGetter, boolean renderDebugLabels) {
      DEVICE = new GlBackend(windowHandle, debugVerbosity, sync, shaderSourceGetter, renderDebugLabels);
      apiDescription = getDevice().getImplementationInformation();
      dynamicUniforms = new DynamicUniforms();
      BufferAllocator bufferAllocator = BufferAllocator.method_72201(VertexFormats.POSITION.getVertexSize() * 4);

      try {
         BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
         bufferBuilder.vertex(0.0F, 0.0F, 0.0F);
         bufferBuilder.vertex(1.0F, 0.0F, 0.0F);
         bufferBuilder.vertex(1.0F, 1.0F, 0.0F);
         bufferBuilder.vertex(0.0F, 1.0F, 0.0F);
         BuiltBuffer builtBuffer = bufferBuilder.end();

         try {
            QUAD_VERTEX_BUFFER = getDevice().createBuffer(() -> {
               return "Quad";
            }, 32, builtBuffer.getBuffer());
         } catch (Throwable var13) {
            if (builtBuffer != null) {
               try {
                  builtBuffer.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }
            }

            throw var13;
         }

         if (builtBuffer != null) {
            builtBuffer.close();
         }
      } catch (Throwable var14) {
         if (bufferAllocator != null) {
            try {
               bufferAllocator.close();
            } catch (Throwable var11) {
               var14.addSuppressed(var11);
            }
         }

         throw var14;
      }

      if (bufferAllocator != null) {
         bufferAllocator.close();
      }

   }

   public static void setErrorCallback(GLFWErrorCallbackI callback) {
      GLX._setGlfwErrorCallback(callback);
   }

   public static void setupDefaultState() {
      modelViewStack.clear();
      textureMatrix.identity();
   }

   public static void setupOverlayColor(@Nullable GpuTextureView texture) {
      assertOnRenderThread();
      setShaderTexture(1, texture);
   }

   public static void teardownOverlayColor() {
      assertOnRenderThread();
      setShaderTexture(1, (GpuTextureView)null);
   }

   public static void setShaderTexture(int index, @Nullable GpuTextureView texture) {
      assertOnRenderThread();
      if (index >= 0 && index < shaderTextures.length) {
         shaderTextures[index] = texture;
      }

   }

   @Nullable
   public static GpuTextureView getShaderTexture(int index) {
      assertOnRenderThread();
      return index >= 0 && index < shaderTextures.length ? shaderTextures[index] : null;
   }

   public static void setProjectionMatrix(GpuBufferSlice projectionMatrixBuffer, ProjectionType projectionType) {
      assertOnRenderThread();
      RenderSystem.projectionMatrixBuffer = projectionMatrixBuffer;
      RenderSystem.projectionType = projectionType;
   }

   public static void setTextureMatrix(Matrix4f textureMatrix) {
      assertOnRenderThread();
      RenderSystem.textureMatrix = new Matrix4f(textureMatrix);
   }

   public static void resetTextureMatrix() {
      assertOnRenderThread();
      textureMatrix.identity();
   }

   public static void backupProjectionMatrix() {
      assertOnRenderThread();
      savedProjectionMatrixBuffer = projectionMatrixBuffer;
      savedProjectionType = projectionType;
   }

   public static void restoreProjectionMatrix() {
      assertOnRenderThread();
      projectionMatrixBuffer = savedProjectionMatrixBuffer;
      projectionType = savedProjectionType;
   }

   @Nullable
   public static GpuBufferSlice getProjectionMatrixBuffer() {
      assertOnRenderThread();
      return projectionMatrixBuffer;
   }

   public static Matrix4f getModelViewMatrix() {
      assertOnRenderThread();
      return modelViewStack;
   }

   public static Matrix4fStack getModelViewStack() {
      assertOnRenderThread();
      return modelViewStack;
   }

   public static Matrix4f getTextureMatrix() {
      assertOnRenderThread();
      return textureMatrix;
   }

   public static ShapeIndexBuffer getSequentialBuffer(VertexFormat.DrawMode drawMode) {
      assertOnRenderThread();
      ShapeIndexBuffer var10000;
      switch (drawMode) {
         case QUADS:
            var10000 = sharedSequentialQuad;
            break;
         case LINES:
            var10000 = sharedSequentialLines;
            break;
         default:
            var10000 = sharedSequential;
      }

      return var10000;
   }

   public static void setGlobalSettingsUniform(GpuBuffer globalSettingsUniform) {
      RenderSystem.globalSettingsUniform = globalSettingsUniform;
   }

   @Nullable
   public static GpuBuffer getGlobalSettingsUniform() {
      return globalSettingsUniform;
   }

   public static ProjectionType getProjectionType() {
      assertOnRenderThread();
      return projectionType;
   }

   public static GpuBuffer getQuadVertexBuffer() {
      if (QUAD_VERTEX_BUFFER == null) {
         throw new IllegalStateException("Can't getQuadVertexBuffer() before renderer was initialized");
      } else {
         return QUAD_VERTEX_BUFFER;
      }
   }

   public static void setModelOffset(float offsetX, float offsetY, float offsetZ) {
      assertOnRenderThread();
      modelOffset.set(offsetX, offsetY, offsetZ);
   }

   public static void resetModelOffset() {
      assertOnRenderThread();
      modelOffset.set(0.0F, 0.0F, 0.0F);
   }

   public static Vector3f getModelOffset() {
      assertOnRenderThread();
      return modelOffset;
   }

   public static void queueFencedTask(Runnable task) {
      PENDING_FENCES.addLast(new Task(task, getDevice().createCommandEncoder().createFence()));
   }

   public static void executePendingTasks() {
      for(Task task = (Task)PENDING_FENCES.peekFirst(); task != null; task = (Task)PENDING_FENCES.peekFirst()) {
         if (!task.fence.awaitCompletion(0L)) {
            return;
         }

         try {
            task.callback.run();
         } finally {
            task.fence.close();
         }

         PENDING_FENCES.removeFirst();
      }

   }

   public static GpuDevice getDevice() {
      if (DEVICE == null) {
         throw new IllegalStateException("Can't getDevice() before it was initialized");
      } else {
         return DEVICE;
      }
   }

   @Nullable
   public static GpuDevice tryGetDevice() {
      return DEVICE;
   }

   public static DynamicUniforms getDynamicUniforms() {
      if (dynamicUniforms == null) {
         throw new IllegalStateException("Can't getDynamicUniforms() before device was initialized");
      } else {
         return dynamicUniforms;
      }
   }

   public static void bindDefaultUniforms(RenderPass pass) {
      GpuBufferSlice gpuBufferSlice = getProjectionMatrixBuffer();
      if (gpuBufferSlice != null) {
         pass.setUniform("Projection", gpuBufferSlice);
      }

      GpuBufferSlice gpuBufferSlice2 = getShaderFog();
      if (gpuBufferSlice2 != null) {
         pass.setUniform("Fog", gpuBufferSlice2);
      }

      GpuBuffer gpuBuffer = getGlobalSettingsUniform();
      if (gpuBuffer != null) {
         pass.setUniform("Globals", gpuBuffer);
      }

      GpuBufferSlice gpuBufferSlice3 = getShaderLights();
      if (gpuBufferSlice3 != null) {
         pass.setUniform("Lighting", gpuBufferSlice3);
      }

   }

   static {
      projectionType = ProjectionType.PERSPECTIVE;
      savedProjectionType = ProjectionType.PERSPECTIVE;
      modelViewStack = new Matrix4fStack(16);
      textureMatrix = new Matrix4f();
      shaderTextures = new GpuTextureView[12];
      shaderFog = null;
      modelOffset = new Vector3f();
      shaderLineWidth = 1.0F;
      apiDescription = "Unknown";
      pollEventsWaitStart = new AtomicLong();
      pollingEvents = new AtomicBoolean(false);
      PENDING_FENCES = new ArrayListDeque();
      scissorStateForRenderTypeDraws = new ScissorState();
   }

   @Environment(EnvType.CLIENT)
   public static final class ShapeIndexBuffer {
      private final int vertexCountInShape;
      private final int vertexCountInTriangulated;
      private final Triangulator triangulator;
      @Nullable
      private GpuBuffer indexBuffer;
      private VertexFormat.IndexType indexType;
      private int size;

      ShapeIndexBuffer(int vertexCountInShape, int vertexCountInTriangulated, Triangulator triangulator) {
         this.indexType = VertexFormat.IndexType.SHORT;
         this.vertexCountInShape = vertexCountInShape;
         this.vertexCountInTriangulated = vertexCountInTriangulated;
         this.triangulator = triangulator;
      }

      public boolean isLargeEnough(int requiredSize) {
         return requiredSize <= this.size;
      }

      public GpuBuffer getIndexBuffer(int requiredSize) {
         this.grow(requiredSize);
         return this.indexBuffer;
      }

      private void grow(int requiredSize) {
         if (!this.isLargeEnough(requiredSize)) {
            requiredSize = MathHelper.roundUpToMultiple(requiredSize * 2, this.vertexCountInTriangulated);
            RenderSystem.LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", this.size, requiredSize);
            int i = requiredSize / this.vertexCountInTriangulated;
            int j = i * this.vertexCountInShape;
            VertexFormat.IndexType indexType = VertexFormat.IndexType.smallestFor(j);
            int k = MathHelper.roundUpToMultiple(requiredSize * indexType.size, 4);
            ByteBuffer byteBuffer = MemoryUtil.memAlloc(k);

            try {
               this.indexType = indexType;
               it.unimi.dsi.fastutil.ints.IntConsumer intConsumer = this.getIndexConsumer(byteBuffer);

               for(int l = 0; l < requiredSize; l += this.vertexCountInTriangulated) {
                  this.triangulator.accept(intConsumer, l * this.vertexCountInShape / this.vertexCountInTriangulated);
               }

               byteBuffer.flip();
               if (this.indexBuffer != null) {
                  this.indexBuffer.close();
               }

               this.indexBuffer = RenderSystem.getDevice().createBuffer(() -> {
                  return "Auto Storage index buffer";
               }, 64, byteBuffer);
            } finally {
               MemoryUtil.memFree(byteBuffer);
            }

            this.size = requiredSize;
         }
      }

      private it.unimi.dsi.fastutil.ints.IntConsumer getIndexConsumer(ByteBuffer indexBuffer) {
         switch (this.indexType) {
            case SHORT:
               return (index) -> {
                  indexBuffer.putShort((short)index);
               };
            case INT:
            default:
               Objects.requireNonNull(indexBuffer);
               return indexBuffer::putInt;
         }
      }

      public VertexFormat.IndexType getIndexType() {
         return this.indexType;
      }

      @Environment(EnvType.CLIENT)
      interface Triangulator {
         void accept(it.unimi.dsi.fastutil.ints.IntConsumer indexConsumer, int firstVertexIndex);
      }
   }

   @Environment(EnvType.CLIENT)
   static record Task(Runnable callback, GpuFence fence) {
      final Runnable callback;
      final GpuFence fence;

      Task(Runnable runnable, GpuFence gpuFence) {
         this.callback = runnable;
         this.fence = gpuFence;
      }

      public Runnable callback() {
         return this.callback;
      }

      public GpuFence fence() {
         return this.fence;
      }
   }
}
