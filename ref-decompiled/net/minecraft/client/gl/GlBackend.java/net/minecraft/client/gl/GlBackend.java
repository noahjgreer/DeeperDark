/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GLCapabilities
 *  org.slf4j.Logger
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.BufferManager;
import net.minecraft.client.gl.CompiledShader;
import net.minecraft.client.gl.CompiledShaderPipeline;
import net.minecraft.client.gl.DebugLabelManager;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.GlCommandEncoder;
import net.minecraft.client.gl.GlDebug;
import net.minecraft.client.gl.GlGpuBuffer;
import net.minecraft.client.gl.GlImportProcessor;
import net.minecraft.client.gl.GlSampler;
import net.minecraft.client.gl.GpuBufferManager;
import net.minecraft.client.gl.GpuDeviceInfo;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderSourceGetter;
import net.minecraft.client.gl.VertexBufferManager;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.GlTextureView;
import net.minecraft.client.util.TextureAllocationException;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class GlBackend
implements GpuDevice {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected static boolean allowGlArbVABinding = true;
    protected static boolean allowGlKhrDebug = true;
    protected static boolean allowExtDebugLabel = true;
    protected static boolean allowGlArbDebugOutput = true;
    protected static boolean allowGlArbDirectAccess = true;
    protected static boolean allowGlBufferStorage = true;
    private final CommandEncoder commandEncoder;
    private final @Nullable GlDebug glDebug;
    private final DebugLabelManager debugLabelManager;
    private final int maxTextureSize;
    private final BufferManager bufferManager;
    private final ShaderSourceGetter defaultShaderSourceGetter;
    private final Map<RenderPipeline, CompiledShaderPipeline> pipelineCompileCache = new IdentityHashMap<RenderPipeline, CompiledShaderPipeline>();
    private final Map<ShaderKey, CompiledShader> shaderCompileCache = new HashMap<ShaderKey, CompiledShader>();
    private final VertexBufferManager vertexBufferManager;
    private final GpuBufferManager gpuBufferManager;
    private final Set<String> usedGlCapabilities = new HashSet<String>();
    private final int uniformOffsetAlignment;
    private final int maxSupportedAnisotropy;

    public GlBackend(long contextId, int debugVerbosity, boolean sync, ShaderSourceGetter defaultShaderSourceGetter, boolean renderDebugLabels) {
        GLFW.glfwMakeContextCurrent((long)contextId);
        GLCapabilities gLCapabilities = GL.createCapabilities();
        int i = GlBackend.determineMaxTextureSize();
        GLFW.glfwSetWindowSizeLimits((long)contextId, (int)-1, (int)-1, (int)i, (int)i);
        GpuDeviceInfo gpuDeviceInfo = GpuDeviceInfo.get(this);
        this.glDebug = GlDebug.enableDebug(debugVerbosity, sync, this.usedGlCapabilities);
        this.debugLabelManager = DebugLabelManager.create(gLCapabilities, renderDebugLabels, this.usedGlCapabilities);
        this.vertexBufferManager = VertexBufferManager.create(gLCapabilities, this.debugLabelManager, this.usedGlCapabilities);
        this.gpuBufferManager = GpuBufferManager.create(gLCapabilities, this.usedGlCapabilities);
        this.bufferManager = BufferManager.create(gLCapabilities, this.usedGlCapabilities, gpuDeviceInfo);
        this.maxTextureSize = i;
        this.defaultShaderSourceGetter = defaultShaderSourceGetter;
        this.commandEncoder = new GlCommandEncoder(this);
        this.uniformOffsetAlignment = GL11.glGetInteger((int)35380);
        GL11.glEnable((int)34895);
        GL11.glEnable((int)34370);
        if (gLCapabilities.GL_EXT_texture_filter_anisotropic) {
            this.maxSupportedAnisotropy = MathHelper.floor(GL11.glGetFloat((int)34047));
            this.usedGlCapabilities.add("GL_EXT_texture_filter_anisotropic");
        } else {
            this.maxSupportedAnisotropy = 1;
        }
    }

    public DebugLabelManager getDebugLabelManager() {
        return this.debugLabelManager;
    }

    @Override
    public CommandEncoder createCommandEncoder() {
        return this.commandEncoder;
    }

    @Override
    public int getMaxSupportedAnisotropy() {
        return this.maxSupportedAnisotropy;
    }

    @Override
    public GpuSampler createSampler(AddressMode addressMode, AddressMode addressMode2, FilterMode filterMode, FilterMode filterMode2, int i, OptionalDouble optionalDouble) {
        if (i < 1 || i > this.maxSupportedAnisotropy) {
            throw new IllegalArgumentException("maxAnisotropy out of range; must be >= 1 and <= " + this.getMaxSupportedAnisotropy() + ", but was " + i);
        }
        return new GlSampler(addressMode, addressMode2, filterMode, filterMode2, i, optionalDouble);
    }

    @Override
    public GpuTexture createTexture(@Nullable Supplier<String> supplier, @GpuTexture.Usage int i, TextureFormat textureFormat, int j, int k, int l, int m) {
        return this.createTexture(this.debugLabelManager.isUsable() && supplier != null ? supplier.get() : null, i, textureFormat, j, k, l, m);
    }

    @Override
    public GpuTexture createTexture(@Nullable String string, @GpuTexture.Usage int i, TextureFormat textureFormat, int j, int k, int l, int m) {
        int r;
        int o;
        boolean bl;
        if (m < 1) {
            throw new IllegalArgumentException("mipLevels must be at least 1");
        }
        if (l < 1) {
            throw new IllegalArgumentException("depthOrLayers must be at least 1");
        }
        boolean bl2 = bl = (i & 0x10) != 0;
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
        if (bl) {
            GL11.glBindTexture((int)34067, (int)n);
            o = 34067;
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
        if (bl) {
            for (int p : GlConst.CUBEMAP_TARGETS) {
                for (int q = 0; q < m; ++q) {
                    GlStateManager._texImage2D(p, q, GlConst.toGlInternalId(textureFormat), j >> q, k >> q, 0, GlConst.toGlExternalId(textureFormat), GlConst.toGlType(textureFormat), null);
                }
            }
        } else {
            for (int r2 = 0; r2 < m; ++r2) {
                GlStateManager._texImage2D(o, r2, GlConst.toGlInternalId(textureFormat), j >> r2, k >> r2, 0, GlConst.toGlExternalId(textureFormat), GlConst.toGlType(textureFormat), null);
            }
        }
        if ((r = GlStateManager._getError()) == 1285) {
            throw new TextureAllocationException("Could not allocate texture of " + j + "x" + k + " for " + string);
        }
        if (r != 0) {
            throw new IllegalStateException("OpenGL error " + r);
        }
        GlTexture glTexture = new GlTexture(i, string, textureFormat, j, k, l, m, n);
        this.debugLabelManager.labelGlTexture(glTexture);
        return glTexture;
    }

    @Override
    public GpuTextureView createTextureView(GpuTexture gpuTexture) {
        return this.createTextureView(gpuTexture, 0, gpuTexture.getMipLevels());
    }

    @Override
    public GpuTextureView createTextureView(GpuTexture gpuTexture, int i, int j) {
        if (gpuTexture.isClosed()) {
            throw new IllegalArgumentException("Can't create texture view with closed texture");
        }
        if (i < 0 || i + j > gpuTexture.getMipLevels()) {
            throw new IllegalArgumentException(j + " mip levels starting from " + i + " would be out of range for texture with only " + gpuTexture.getMipLevels() + " mip levels");
        }
        return new GlTextureView((GlTexture)gpuTexture, i, j);
    }

    @Override
    public GpuBuffer createBuffer(@Nullable Supplier<String> supplier, @GpuBuffer.Usage int i, long l) {
        if (l <= 0L) {
            throw new IllegalArgumentException("Buffer size must be greater than zero");
        }
        GlStateManager.clearGlErrors();
        GlGpuBuffer glGpuBuffer = this.gpuBufferManager.createBuffer(this.bufferManager, supplier, i, l);
        int j = GlStateManager._getError();
        if (j == 1285) {
            throw new TextureAllocationException("Could not allocate buffer of " + l + " for " + String.valueOf(supplier));
        }
        if (j != 0) {
            throw new IllegalStateException("OpenGL error " + j);
        }
        this.debugLabelManager.labelGlGpuBuffer(glGpuBuffer);
        return glGpuBuffer;
    }

    @Override
    public GpuBuffer createBuffer(@Nullable Supplier<String> supplier, @GpuBuffer.Usage int i, ByteBuffer byteBuffer) {
        if (!byteBuffer.hasRemaining()) {
            throw new IllegalArgumentException("Buffer source must not be empty");
        }
        GlStateManager.clearGlErrors();
        long l = byteBuffer.remaining();
        GlGpuBuffer glGpuBuffer = this.gpuBufferManager.createBuffer(this.bufferManager, supplier, i, byteBuffer);
        int j = GlStateManager._getError();
        if (j == 1285) {
            throw new TextureAllocationException("Could not allocate buffer of " + l + " for " + String.valueOf(supplier));
        }
        if (j != 0) {
            throw new IllegalStateException("OpenGL error " + j);
        }
        this.debugLabelManager.labelGlGpuBuffer(glGpuBuffer);
        return glGpuBuffer;
    }

    @Override
    public String getImplementationInformation() {
        if (GLFW.glfwGetCurrentContext() == 0L) {
            return "NO CONTEXT";
        }
        return GlStateManager._getString(7937) + " GL version " + GlStateManager._getString(7938) + ", " + GlStateManager._getString(7936);
    }

    @Override
    public List<String> getLastDebugMessages() {
        return this.glDebug == null ? Collections.emptyList() : this.glDebug.collectDebugMessages();
    }

    @Override
    public boolean isDebuggingEnabled() {
        return this.glDebug != null;
    }

    @Override
    public String getRenderer() {
        return GlStateManager._getString(7937);
    }

    @Override
    public String getVendor() {
        return GlStateManager._getString(7936);
    }

    @Override
    public String getBackendName() {
        return "OpenGL";
    }

    @Override
    public String getVersion() {
        return GlStateManager._getString(7938);
    }

    private static int determineMaxTextureSize() {
        int j;
        int i = GlStateManager._getInteger(3379);
        for (j = Math.max(32768, i); j >= 1024; j >>= 1) {
            GlStateManager._texImage2D(32868, 0, 6408, j, j, 0, 6408, 5121, null);
            int k = GlStateManager._getTexLevelParameter(32868, 0, 4096);
            if (k == 0) continue;
            return j;
        }
        j = Math.max(i, 1024);
        LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", (Object)j);
        return j;
    }

    @Override
    public int getMaxTextureSize() {
        return this.maxTextureSize;
    }

    @Override
    public int getUniformOffsetAlignment() {
        return this.uniformOffsetAlignment;
    }

    @Override
    public void clearPipelineCache() {
        for (CompiledShaderPipeline compiledShaderPipeline : this.pipelineCompileCache.values()) {
            if (compiledShaderPipeline.program() == ShaderProgram.INVALID) continue;
            compiledShaderPipeline.program().close();
        }
        this.pipelineCompileCache.clear();
        for (CompiledShader compiledShader : this.shaderCompileCache.values()) {
            if (compiledShader == CompiledShader.INVALID_SHADER) continue;
            compiledShader.close();
        }
        this.shaderCompileCache.clear();
        String string = GlStateManager._getString(7937);
        if (string.contains("AMD")) {
            GlBackend.applyAmdCleanupHack();
        }
    }

    private static void applyAmdCleanupHack() {
        int i = GlStateManager.glCreateShader(35633);
        int j = GlStateManager.glCreateProgram();
        GlStateManager.glAttachShader(j, i);
        GlStateManager.glDeleteShader(i);
        GlStateManager.glDeleteProgram(j);
    }

    @Override
    public List<String> getEnabledExtensions() {
        return new ArrayList<String>(this.usedGlCapabilities);
    }

    @Override
    public void close() {
        this.clearPipelineCache();
    }

    public BufferManager getBufferManager() {
        return this.bufferManager;
    }

    protected CompiledShaderPipeline compilePipelineCached(RenderPipeline pipeline) {
        return this.pipelineCompileCache.computeIfAbsent(pipeline, p -> this.compileRenderPipeline((RenderPipeline)p, this.defaultShaderSourceGetter));
    }

    protected CompiledShader compileShader(Identifier id, ShaderType type, Defines defines, ShaderSourceGetter sourceGetter) {
        ShaderKey shaderKey = new ShaderKey(id, type, defines);
        return this.shaderCompileCache.computeIfAbsent(shaderKey, key -> this.compileShader((ShaderKey)key, sourceGetter));
    }

    @Override
    public CompiledShaderPipeline precompilePipeline(RenderPipeline renderPipeline, @Nullable ShaderSourceGetter shaderSourceGetter) {
        ShaderSourceGetter shaderSourceGetter2 = shaderSourceGetter == null ? this.defaultShaderSourceGetter : shaderSourceGetter;
        return this.pipelineCompileCache.computeIfAbsent(renderPipeline, p -> this.compileRenderPipeline((RenderPipeline)p, shaderSourceGetter2));
    }

    private CompiledShader compileShader(ShaderKey key, ShaderSourceGetter sourceGetter) {
        String string = sourceGetter.get(key.id, key.type);
        if (string == null) {
            LOGGER.error("Couldn't find source for {} shader ({})", (Object)key.type, (Object)key.id);
            return CompiledShader.INVALID_SHADER;
        }
        String string2 = GlImportProcessor.addDefines(string, key.defines);
        int i = GlStateManager.glCreateShader(GlConst.toGl(key.type));
        GlStateManager.glShaderSource(i, string2);
        GlStateManager.glCompileShader(i);
        if (GlStateManager.glGetShaderi(i, 35713) == 0) {
            String string3 = StringUtils.trim((String)GlStateManager.glGetShaderInfoLog(i, 32768));
            LOGGER.error("Couldn't compile {} shader ({}): {}", new Object[]{key.type.getName(), key.id, string3});
            return CompiledShader.INVALID_SHADER;
        }
        CompiledShader compiledShader = new CompiledShader(i, key.id, key.type);
        this.debugLabelManager.labelCompiledShader(compiledShader);
        return compiledShader;
    }

    private ShaderProgram compileProgram(RenderPipeline pipeline, ShaderSourceGetter sourceGetter) {
        CompiledShader compiledShader = this.compileShader(pipeline.getVertexShader(), ShaderType.VERTEX, pipeline.getShaderDefines(), sourceGetter);
        CompiledShader compiledShader2 = this.compileShader(pipeline.getFragmentShader(), ShaderType.FRAGMENT, pipeline.getShaderDefines(), sourceGetter);
        if (compiledShader == CompiledShader.INVALID_SHADER) {
            LOGGER.error("Couldn't compile pipeline {}: vertex shader {} was invalid", (Object)pipeline.getLocation(), (Object)pipeline.getVertexShader());
            return ShaderProgram.INVALID;
        }
        if (compiledShader2 == CompiledShader.INVALID_SHADER) {
            LOGGER.error("Couldn't compile pipeline {}: fragment shader {} was invalid", (Object)pipeline.getLocation(), (Object)pipeline.getFragmentShader());
            return ShaderProgram.INVALID;
        }
        try {
            ShaderProgram shaderProgram = ShaderProgram.create(compiledShader, compiledShader2, pipeline.getVertexFormat(), pipeline.getLocation().toString());
            shaderProgram.set(pipeline.getUniforms(), pipeline.getSamplers());
            this.debugLabelManager.labelShaderProgram(shaderProgram);
            return shaderProgram;
        }
        catch (ShaderLoader.LoadException loadException) {
            LOGGER.error("Couldn't compile program for pipeline {}: {}", (Object)pipeline.getLocation(), (Object)loadException);
            return ShaderProgram.INVALID;
        }
    }

    private CompiledShaderPipeline compileRenderPipeline(RenderPipeline pipeline, ShaderSourceGetter sourceGetter) {
        return new CompiledShaderPipeline(pipeline, this.compileProgram(pipeline, sourceGetter));
    }

    public VertexBufferManager getVertexBufferManager() {
        return this.vertexBufferManager;
    }

    public GpuBufferManager getGpuBufferManager() {
        return this.gpuBufferManager;
    }

    @Override
    public /* synthetic */ CompiledRenderPipeline precompilePipeline(RenderPipeline renderPipeline, @Nullable ShaderSourceGetter shaderSourceGetter) {
        return this.precompilePipeline(renderPipeline, shaderSourceGetter);
    }

    @Environment(value=EnvType.CLIENT)
    static final class ShaderKey
    extends Record {
        final Identifier id;
        final ShaderType type;
        final Defines defines;

        ShaderKey(Identifier id, ShaderType type, Defines defines) {
            this.id = id;
            this.type = type;
            this.defines = defines;
        }

        @Override
        public String toString() {
            String string = String.valueOf(this.id) + " (" + String.valueOf((Object)this.type) + ")";
            if (!this.defines.isEmpty()) {
                return string + " with " + String.valueOf(this.defines);
            }
            return string;
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ShaderKey.class, "id;type;defines", "id", "type", "defines"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ShaderKey.class, "id;type;defines", "id", "type", "defines"}, this, object);
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
