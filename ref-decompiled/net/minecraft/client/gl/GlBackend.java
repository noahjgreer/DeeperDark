package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.GlTextureView;
import net.minecraft.client.util.TextureAllocationException;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class GlBackend implements GpuDevice {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected static boolean allowGlArbVABinding = true;
   protected static boolean allowGlKhrDebug = true;
   protected static boolean allowExtDebugLabel = true;
   protected static boolean allowGlArbDebugOutput = true;
   protected static boolean allowGlArbDirectAccess = true;
   protected static boolean allowGlBufferStorage = true;
   private final CommandEncoder commandEncoder;
   @Nullable
   private final GlDebug glDebug;
   private final DebugLabelManager debugLabelManager;
   private final int maxTextureSize;
   private final BufferManager bufferManager;
   private final BiFunction defaultShaderSourceGetter;
   private final Map pipelineCompileCache = new IdentityHashMap();
   private final Map shaderCompileCache = new HashMap();
   private final VertexBufferManager vertexBufferManager;
   private final GpuBufferManager gpuBufferManager;
   private final Set usedGlCapabilities = new HashSet();
   private final int uniformOffsetAlignment;

   public GlBackend(long contextId, int debugVerbosity, boolean sync, BiFunction shaderSourceGetter, boolean renderDebugLabels) {
      GLFW.glfwMakeContextCurrent(contextId);
      GLCapabilities gLCapabilities = GL.createCapabilities();
      int i = determineMaxTextureSize();
      GLFW.glfwSetWindowSizeLimits(contextId, -1, -1, i, i);
      this.glDebug = GlDebug.enableDebug(debugVerbosity, sync, this.usedGlCapabilities);
      this.debugLabelManager = DebugLabelManager.create(gLCapabilities, renderDebugLabels, this.usedGlCapabilities);
      this.vertexBufferManager = VertexBufferManager.create(gLCapabilities, this.debugLabelManager, this.usedGlCapabilities);
      this.gpuBufferManager = GpuBufferManager.create(gLCapabilities, this.usedGlCapabilities);
      this.bufferManager = BufferManager.create(gLCapabilities, this.usedGlCapabilities);
      this.maxTextureSize = i;
      this.defaultShaderSourceGetter = shaderSourceGetter;
      this.commandEncoder = new GlCommandEncoder(this);
      this.uniformOffsetAlignment = GL11.glGetInteger(35380);
      GL11.glEnable(34895);
   }

   public DebugLabelManager getDebugLabelManager() {
      return this.debugLabelManager;
   }

   public CommandEncoder createCommandEncoder() {
      return this.commandEncoder;
   }

   public GpuTexture createTexture(@Nullable Supplier supplier, int i, TextureFormat textureFormat, int j, int k, int l, int m) {
      return this.createTexture(this.debugLabelManager.isUsable() && supplier != null ? (String)supplier.get() : null, i, textureFormat, j, k, l, m);
   }

   public GpuTexture createTexture(@Nullable String string, int i, TextureFormat textureFormat, int j, int k, int l, int m) {
      if (m < 1) {
         throw new IllegalArgumentException("mipLevels must be at least 1");
      } else if (l < 1) {
         throw new IllegalArgumentException("depthOrLayers must be at least 1");
      } else {
         boolean bl = (i & 16) != 0;
         if (bl) {
            if (j != k) {
               throw new IllegalArgumentException("Cubemap compatible textures must be square, but size is " + j + "x" + k);
            }

            if (l % 6 != 0) {
               throw new IllegalArgumentException("Cubemap compatible textures must have a layer count with a multiple of 6, was " + l);
            }

            if (l > 6) {
               throw new UnsupportedOperationException("Array textures are not yet supported");
            }
         } else if (l > 1) {
            throw new UnsupportedOperationException("Array or 3D textures are not yet supported");
         }

         GlStateManager.clearGlErrors();
         int n = GlStateManager._genTexture();
         if (string == null) {
            string = String.valueOf(n);
         }

         char o;
         if (bl) {
            GL11.glBindTexture(34067, n);
            o = '蔓';
         } else {
            GlStateManager._bindTexture(n);
            o = 3553;
         }

         GlStateManager._texParameter(o, 33085, m - 1);
         GlStateManager._texParameter(o, 33082, 0);
         GlStateManager._texParameter(o, 33083, m - 1);
         if (textureFormat.hasDepthAspect()) {
            GlStateManager._texParameter(o, 34892, 0);
         }

         int r;
         if (bl) {
            int[] var11 = GlConst.CUBEMAP_TARGETS;
            int var12 = var11.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               int p = var11[var13];

               for(int q = 0; q < m; ++q) {
                  GlStateManager._texImage2D(p, q, GlConst.toGlInternalId(textureFormat), j >> q, k >> q, 0, GlConst.toGlExternalId(textureFormat), GlConst.toGlType(textureFormat), (IntBuffer)null);
               }
            }
         } else {
            for(r = 0; r < m; ++r) {
               GlStateManager._texImage2D(o, r, GlConst.toGlInternalId(textureFormat), j >> r, k >> r, 0, GlConst.toGlExternalId(textureFormat), GlConst.toGlType(textureFormat), (IntBuffer)null);
            }
         }

         r = GlStateManager._getError();
         if (r == 1285) {
            throw new TextureAllocationException("Could not allocate texture of " + j + "x" + k + " for " + string);
         } else if (r != 0) {
            throw new IllegalStateException("OpenGL error " + r);
         } else {
            GlTexture glTexture = new GlTexture(i, string, textureFormat, j, k, l, m, n);
            this.debugLabelManager.labelGlTexture(glTexture);
            return glTexture;
         }
      }
   }

   public GpuTextureView createTextureView(GpuTexture gpuTexture) {
      return this.createTextureView(gpuTexture, 0, gpuTexture.getMipLevels());
   }

   public GpuTextureView createTextureView(GpuTexture gpuTexture, int i, int j) {
      if (gpuTexture.isClosed()) {
         throw new IllegalArgumentException("Can't create texture view with closed texture");
      } else if (i >= 0 && i + j <= gpuTexture.getMipLevels()) {
         return new GlTextureView((GlTexture)gpuTexture, i, j);
      } else {
         throw new IllegalArgumentException("" + j + " mip levels starting from " + i + " would be out of range for texture with only " + gpuTexture.getMipLevels() + " mip levels");
      }
   }

   public GpuBuffer createBuffer(@Nullable Supplier supplier, int i, int j) {
      if (j <= 0) {
         throw new IllegalArgumentException("Buffer size must be greater than zero");
      } else {
         GlStateManager.clearGlErrors();
         GlGpuBuffer glGpuBuffer = this.gpuBufferManager.createBuffer(this.bufferManager, supplier, i, j);
         int k = GlStateManager._getError();
         if (k == 1285) {
            throw new TextureAllocationException("Could not allocate buffer of " + j + " for " + String.valueOf(supplier));
         } else if (k != 0) {
            throw new IllegalStateException("OpenGL error " + k);
         } else {
            this.debugLabelManager.labelGlGpuBuffer(glGpuBuffer);
            return glGpuBuffer;
         }
      }
   }

   public GpuBuffer createBuffer(@Nullable Supplier supplier, int i, ByteBuffer byteBuffer) {
      if (!byteBuffer.hasRemaining()) {
         throw new IllegalArgumentException("Buffer source must not be empty");
      } else {
         GlStateManager.clearGlErrors();
         long l = (long)byteBuffer.remaining();
         GlGpuBuffer glGpuBuffer = this.gpuBufferManager.createBuffer(this.bufferManager, supplier, i, byteBuffer);
         int j = GlStateManager._getError();
         if (j == 1285) {
            throw new TextureAllocationException("Could not allocate buffer of " + l + " for " + String.valueOf(supplier));
         } else if (j != 0) {
            throw new IllegalStateException("OpenGL error " + j);
         } else {
            this.debugLabelManager.labelGlGpuBuffer(glGpuBuffer);
            return glGpuBuffer;
         }
      }
   }

   public String getImplementationInformation() {
      if (GLFW.glfwGetCurrentContext() == 0L) {
         return "NO CONTEXT";
      } else {
         String var10000 = GlStateManager._getString(7937);
         return var10000 + " GL version " + GlStateManager._getString(7938) + ", " + GlStateManager._getString(7936);
      }
   }

   public List getLastDebugMessages() {
      return this.glDebug == null ? Collections.emptyList() : this.glDebug.collectDebugMessages();
   }

   public boolean isDebuggingEnabled() {
      return this.glDebug != null;
   }

   public String getRenderer() {
      return GlStateManager._getString(7937);
   }

   public String getVendor() {
      return GlStateManager._getString(7936);
   }

   public String getBackendName() {
      return "OpenGL";
   }

   public String getVersion() {
      return GlStateManager._getString(7938);
   }

   private static int determineMaxTextureSize() {
      int i = GlStateManager._getInteger(3379);

      int j;
      for(j = Math.max(32768, i); j >= 1024; j >>= 1) {
         GlStateManager._texImage2D(32868, 0, 6408, j, j, 0, 6408, 5121, (IntBuffer)null);
         int k = GlStateManager._getTexLevelParameter(32868, 0, 4096);
         if (k != 0) {
            return j;
         }
      }

      j = Math.max(i, 1024);
      LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", j);
      return j;
   }

   public int getMaxTextureSize() {
      return this.maxTextureSize;
   }

   public int getUniformOffsetAlignment() {
      return this.uniformOffsetAlignment;
   }

   public void clearPipelineCache() {
      Iterator var1 = this.pipelineCompileCache.values().iterator();

      while(var1.hasNext()) {
         CompiledShaderPipeline compiledShaderPipeline = (CompiledShaderPipeline)var1.next();
         if (compiledShaderPipeline.program() != ShaderProgram.INVALID) {
            compiledShaderPipeline.program().close();
         }
      }

      this.pipelineCompileCache.clear();
      var1 = this.shaderCompileCache.values().iterator();

      while(var1.hasNext()) {
         CompiledShader compiledShader = (CompiledShader)var1.next();
         if (compiledShader != CompiledShader.INVALID_SHADER) {
            compiledShader.close();
         }
      }

      this.shaderCompileCache.clear();
      String string = GlStateManager._getString(7937);
      if (string.contains("AMD")) {
         applyAmdCleanupHack();
      }

   }

   private static void applyAmdCleanupHack() {
      int i = GlStateManager.glCreateShader(35633);
      GlStateManager.glShaderSource(i, "#version 150\nvoid main() {\n    gl_Position = vec4(0.0);\n}\n");
      GlStateManager.glCompileShader(i);
      int j = GlStateManager.glCreateShader(35632);
      GlStateManager.glShaderSource(j, "#version 150\nlayout(std140) uniform Dummy {\n    float Value;\n};\nout vec4 fragColor;\nvoid main() {\n    fragColor = vec4(0.0);\n}\n");
      GlStateManager.glCompileShader(j);
      int k = GlStateManager.glCreateProgram();
      GlStateManager.glAttachShader(k, i);
      GlStateManager.glAttachShader(k, j);
      GlStateManager.glLinkProgram(k);
      GL31.glGetUniformBlockIndex(k, "Dummy");
      GlStateManager.glDeleteShader(i);
      GlStateManager.glDeleteShader(j);
      GlStateManager.glDeleteProgram(k);
   }

   public List getEnabledExtensions() {
      return new ArrayList(this.usedGlCapabilities);
   }

   public void close() {
      this.clearPipelineCache();
   }

   public BufferManager getBufferManager() {
      return this.bufferManager;
   }

   protected CompiledShaderPipeline compilePipelineCached(RenderPipeline pipeline) {
      return (CompiledShaderPipeline)this.pipelineCompileCache.computeIfAbsent(pipeline, (p) -> {
         return this.compileRenderPipeline(pipeline, this.defaultShaderSourceGetter);
      });
   }

   protected CompiledShader compileShader(Identifier id, ShaderType type, Defines defines, BiFunction sourceRetriever) {
      ShaderKey shaderKey = new ShaderKey(id, type, defines);
      return (CompiledShader)this.shaderCompileCache.computeIfAbsent(shaderKey, (key) -> {
         return this.compileShader(shaderKey, sourceRetriever);
      });
   }

   public CompiledShaderPipeline precompilePipeline(RenderPipeline renderPipeline, @Nullable BiFunction biFunction) {
      BiFunction biFunction2 = biFunction == null ? this.defaultShaderSourceGetter : biFunction;
      return (CompiledShaderPipeline)this.pipelineCompileCache.computeIfAbsent(renderPipeline, (renderPipeline2) -> {
         return this.compileRenderPipeline(renderPipeline, biFunction2);
      });
   }

   private CompiledShader compileShader(ShaderKey key, BiFunction sourceRetriever) {
      String string = (String)sourceRetriever.apply(key.id, key.type);
      if (string == null) {
         LOGGER.error("Couldn't find source for {} shader ({})", key.type, key.id);
         return CompiledShader.INVALID_SHADER;
      } else {
         String string2 = GlImportProcessor.addDefines(string, key.defines);
         int i = GlStateManager.glCreateShader(GlConst.toGl(key.type));
         GlStateManager.glShaderSource(i, string2);
         GlStateManager.glCompileShader(i);
         if (GlStateManager.glGetShaderi(i, 35713) == 0) {
            String string3 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(i, 32768));
            LOGGER.error("Couldn't compile {} shader ({}): {}", new Object[]{key.type.getName(), key.id, string3});
            return CompiledShader.INVALID_SHADER;
         } else {
            CompiledShader compiledShader = new CompiledShader(i, key.id, key.type);
            this.debugLabelManager.labelCompiledShader(compiledShader);
            return compiledShader;
         }
      }
   }

   private CompiledShaderPipeline compileRenderPipeline(RenderPipeline pipeline, BiFunction sourceRetriever) {
      CompiledShader compiledShader = this.compileShader(pipeline.getVertexShader(), ShaderType.VERTEX, pipeline.getShaderDefines(), sourceRetriever);
      CompiledShader compiledShader2 = this.compileShader(pipeline.getFragmentShader(), ShaderType.FRAGMENT, pipeline.getShaderDefines(), sourceRetriever);
      if (compiledShader == CompiledShader.INVALID_SHADER) {
         LOGGER.error("Couldn't compile pipeline {}: vertex shader {} was invalid", pipeline.getLocation(), pipeline.getVertexShader());
         return new CompiledShaderPipeline(pipeline, ShaderProgram.INVALID);
      } else if (compiledShader2 == CompiledShader.INVALID_SHADER) {
         LOGGER.error("Couldn't compile pipeline {}: fragment shader {} was invalid", pipeline.getLocation(), pipeline.getFragmentShader());
         return new CompiledShaderPipeline(pipeline, ShaderProgram.INVALID);
      } else {
         ShaderProgram shaderProgram;
         try {
            shaderProgram = ShaderProgram.create(compiledShader, compiledShader2, pipeline.getVertexFormat(), pipeline.getLocation().toString());
         } catch (ShaderLoader.LoadException var7) {
            LOGGER.error("Couldn't compile program for pipeline {}: {}", pipeline.getLocation(), var7);
            return new CompiledShaderPipeline(pipeline, ShaderProgram.INVALID);
         }

         shaderProgram.set(pipeline.getUniforms(), pipeline.getSamplers());
         this.debugLabelManager.labelShaderProgram(shaderProgram);
         return new CompiledShaderPipeline(pipeline, shaderProgram);
      }
   }

   public VertexBufferManager getVertexBufferManager() {
      return this.vertexBufferManager;
   }

   public GpuBufferManager getGpuBufferManager() {
      return this.gpuBufferManager;
   }

   // $FF: synthetic method
   public CompiledRenderPipeline precompilePipeline(final RenderPipeline renderPipeline, @Nullable final BiFunction biFunction) {
      return this.precompilePipeline(renderPipeline, biFunction);
   }

   @Environment(EnvType.CLIENT)
   static record ShaderKey(Identifier id, ShaderType type, Defines defines) {
      final Identifier id;
      final ShaderType type;
      final Defines defines;

      ShaderKey(Identifier identifier, ShaderType shaderType, Defines defines) {
         this.id = identifier;
         this.type = shaderType;
         this.defines = defines;
      }

      public String toString() {
         String var10000 = String.valueOf(this.id);
         String string = var10000 + " (" + String.valueOf(this.type) + ")";
         return !this.defines.isEmpty() ? string + " with " + String.valueOf(this.defines) : string;
      }

      public Identifier id() {
         return this.id;
      }

      public ShaderType type() {
         return this.type;
      }

      public Defines defines() {
         return this.defines;
      }
   }
}
