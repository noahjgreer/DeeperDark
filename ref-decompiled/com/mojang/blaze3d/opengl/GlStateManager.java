/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.jtracy.Plot
 *  com.mojang.jtracy.TracyClient
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.opengl.GL15
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL20C
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GL32
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.jtracy.Plot;
import com.mojang.jtracy.TracyClient;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.MacWindowUtil;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public class GlStateManager {
    private static final Plot PLOT_TEXTURES = TracyClient.createPlot((String)"GPU Textures");
    private static int numTextures = 0;
    private static final Plot PLOT_BUFFERS = TracyClient.createPlot((String)"GPU Buffers");
    private static int numBuffers = 0;
    private static final BlendFuncState BLEND = new BlendFuncState();
    private static final DepthTestState DEPTH = new DepthTestState();
    private static final CullFaceState CULL = new CullFaceState();
    private static final PolygonOffsetState POLY_OFFSET = new PolygonOffsetState();
    private static final LogicOpState COLOR_LOGIC = new LogicOpState();
    private static final ScissorTestState SCISSOR = new ScissorTestState();
    private static int activeTexture;
    private static final int TEXTURE_COUNT = 12;
    private static final Texture2DState[] TEXTURES;
    private static final ColorMask COLOR_MASK;
    private static int readFbo;
    private static int writeFbo;

    public static void _disableScissorTest() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.SCISSOR.capState.disable();
    }

    public static void _enableScissorTest() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.SCISSOR.capState.enable();
    }

    public static void _scissorBox(int x, int y, int width, int height) {
        RenderSystem.assertOnRenderThread();
        GL20.glScissor((int)x, (int)y, (int)width, (int)height);
    }

    public static void _disableDepthTest() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.DEPTH.capState.disable();
    }

    public static void _enableDepthTest() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.DEPTH.capState.enable();
    }

    public static void _depthFunc(int func) {
        RenderSystem.assertOnRenderThread();
        if (func != GlStateManager.DEPTH.func) {
            GlStateManager.DEPTH.func = func;
            GL11.glDepthFunc((int)func);
        }
    }

    public static void _depthMask(boolean mask) {
        RenderSystem.assertOnRenderThread();
        if (mask != GlStateManager.DEPTH.mask) {
            GlStateManager.DEPTH.mask = mask;
            GL11.glDepthMask((boolean)mask);
        }
    }

    public static void _disableBlend() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.BLEND.capState.disable();
    }

    public static void _enableBlend() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.BLEND.capState.enable();
    }

    public static void _blendFuncSeparate(int srcFactorRGB, int dstFactorRgb, int srcFactorAlpha, int dstFactorAlpha) {
        RenderSystem.assertOnRenderThread();
        if (srcFactorRGB != GlStateManager.BLEND.srcFactorRgb || dstFactorRgb != GlStateManager.BLEND.dstFactorRgb || srcFactorAlpha != GlStateManager.BLEND.srcFactorAlpha || dstFactorAlpha != GlStateManager.BLEND.dstFactorAlpha) {
            GlStateManager.BLEND.srcFactorRgb = srcFactorRGB;
            GlStateManager.BLEND.dstFactorRgb = dstFactorRgb;
            GlStateManager.BLEND.srcFactorAlpha = srcFactorAlpha;
            GlStateManager.BLEND.dstFactorAlpha = dstFactorAlpha;
            GlStateManager.glBlendFuncSeparate(srcFactorRGB, dstFactorRgb, srcFactorAlpha, dstFactorAlpha);
        }
    }

    public static int glGetProgrami(int program, int pname) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetProgrami((int)program, (int)pname);
    }

    public static void glAttachShader(int program, int shader) {
        RenderSystem.assertOnRenderThread();
        GL20.glAttachShader((int)program, (int)shader);
    }

    public static void glDeleteShader(int shader) {
        RenderSystem.assertOnRenderThread();
        GL20.glDeleteShader((int)shader);
    }

    public static int glCreateShader(int type) {
        RenderSystem.assertOnRenderThread();
        return GL20.glCreateShader((int)type);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void glShaderSource(int shader, String source) {
        RenderSystem.assertOnRenderThread();
        byte[] bs = source.getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBuffer = MemoryUtil.memAlloc((int)(bs.length + 1));
        byteBuffer.put(bs);
        byteBuffer.put((byte)0);
        byteBuffer.flip();
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
            pointerBuffer.put(byteBuffer);
            GL20C.nglShaderSource((int)shader, (int)1, (long)pointerBuffer.address0(), (long)0L);
        }
        finally {
            MemoryUtil.memFree((Buffer)byteBuffer);
        }
    }

    public static void glCompileShader(int shader) {
        RenderSystem.assertOnRenderThread();
        GL20.glCompileShader((int)shader);
    }

    public static int glGetShaderi(int shader, int pname) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetShaderi((int)shader, (int)pname);
    }

    public static void _glUseProgram(int program) {
        RenderSystem.assertOnRenderThread();
        GL20.glUseProgram((int)program);
    }

    public static int glCreateProgram() {
        RenderSystem.assertOnRenderThread();
        return GL20.glCreateProgram();
    }

    public static void glDeleteProgram(int program) {
        RenderSystem.assertOnRenderThread();
        GL20.glDeleteProgram((int)program);
    }

    public static void glLinkProgram(int program) {
        RenderSystem.assertOnRenderThread();
        GL20.glLinkProgram((int)program);
    }

    public static int _glGetUniformLocation(int program, CharSequence name) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetUniformLocation((int)program, (CharSequence)name);
    }

    public static void _glUniform1i(int location, int value) {
        RenderSystem.assertOnRenderThread();
        GL20.glUniform1i((int)location, (int)value);
    }

    public static void _glBindAttribLocation(int program, int index, CharSequence name) {
        RenderSystem.assertOnRenderThread();
        GL20.glBindAttribLocation((int)program, (int)index, (CharSequence)name);
    }

    public static void incrementTrackedBuffers() {
        PLOT_BUFFERS.setValue((double)(++numBuffers));
    }

    public static int _glGenBuffers() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.incrementTrackedBuffers();
        return GL15.glGenBuffers();
    }

    public static int _glGenVertexArrays() {
        RenderSystem.assertOnRenderThread();
        return GL30.glGenVertexArrays();
    }

    public static void _glBindBuffer(int target, int buffer) {
        RenderSystem.assertOnRenderThread();
        GL15.glBindBuffer((int)target, (int)buffer);
    }

    public static void _glBindVertexArray(int array) {
        RenderSystem.assertOnRenderThread();
        GL30.glBindVertexArray((int)array);
    }

    public static void _glBufferData(int target, ByteBuffer data, int usage) {
        RenderSystem.assertOnRenderThread();
        GL15.glBufferData((int)target, (ByteBuffer)data, (int)usage);
    }

    public static void _glBufferSubData(int target, long offset, ByteBuffer data) {
        RenderSystem.assertOnRenderThread();
        GL15.glBufferSubData((int)target, (long)offset, (ByteBuffer)data);
    }

    public static void _glBufferData(int target, long size, int usage) {
        RenderSystem.assertOnRenderThread();
        GL15.glBufferData((int)target, (long)size, (int)usage);
    }

    public static @Nullable ByteBuffer _glMapBufferRange(int target, long offset, long range, int access) {
        RenderSystem.assertOnRenderThread();
        return GL30.glMapBufferRange((int)target, (long)offset, (long)range, (int)access);
    }

    public static void _glUnmapBuffer(int target) {
        RenderSystem.assertOnRenderThread();
        GL15.glUnmapBuffer((int)target);
    }

    public static void _glDeleteBuffers(int buffer) {
        RenderSystem.assertOnRenderThread();
        PLOT_BUFFERS.setValue((double)(--numBuffers));
        GL15.glDeleteBuffers((int)buffer);
    }

    public static void _glBindFramebuffer(int target, int framebuffer) {
        if ((target == 36008 || target == 36160) && readFbo != framebuffer) {
            GL30.glBindFramebuffer((int)36008, (int)framebuffer);
            readFbo = framebuffer;
        }
        if ((target == 36009 || target == 36160) && writeFbo != framebuffer) {
            GL30.glBindFramebuffer((int)36009, (int)framebuffer);
            writeFbo = framebuffer;
        }
    }

    public static int getFrameBuffer(int target) {
        if (target == 36008) {
            return readFbo;
        }
        if (target == 36009) {
            return writeFbo;
        }
        return 0;
    }

    public static void _glBlitFrameBuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        RenderSystem.assertOnRenderThread();
        GL30.glBlitFramebuffer((int)srcX0, (int)srcY0, (int)srcX1, (int)srcY1, (int)dstX0, (int)dstY0, (int)dstX1, (int)dstY1, (int)mask, (int)filter);
    }

    public static void _glDeleteFramebuffers(int framebuffer) {
        RenderSystem.assertOnRenderThread();
        GL30.glDeleteFramebuffers((int)framebuffer);
        if (readFbo == framebuffer) {
            readFbo = 0;
        }
        if (writeFbo == framebuffer) {
            writeFbo = 0;
        }
    }

    public static int glGenFramebuffers() {
        RenderSystem.assertOnRenderThread();
        return GL30.glGenFramebuffers();
    }

    public static void _glFramebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
        RenderSystem.assertOnRenderThread();
        GL30.glFramebufferTexture2D((int)target, (int)attachment, (int)textureTarget, (int)texture, (int)level);
    }

    public static void glBlendFuncSeparate(int srcFactorRgb, int dstFactorRgb, int srcFactorAlpha, int dstFactorAlpha) {
        RenderSystem.assertOnRenderThread();
        GL14.glBlendFuncSeparate((int)srcFactorRgb, (int)dstFactorRgb, (int)srcFactorAlpha, (int)dstFactorAlpha);
    }

    public static String glGetShaderInfoLog(int shader, int maxLength) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetShaderInfoLog((int)shader, (int)maxLength);
    }

    public static String glGetProgramInfoLog(int program, int maxLength) {
        RenderSystem.assertOnRenderThread();
        return GL20.glGetProgramInfoLog((int)program, (int)maxLength);
    }

    public static void _enableCull() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.CULL.capState.enable();
    }

    public static void _disableCull() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.CULL.capState.disable();
    }

    public static void _polygonMode(int face, int mode) {
        RenderSystem.assertOnRenderThread();
        GL11.glPolygonMode((int)face, (int)mode);
    }

    public static void _enablePolygonOffset() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.POLY_OFFSET.capFill.enable();
    }

    public static void _disablePolygonOffset() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.POLY_OFFSET.capFill.disable();
    }

    public static void _polygonOffset(float factor, float units) {
        RenderSystem.assertOnRenderThread();
        if (factor != GlStateManager.POLY_OFFSET.factor || units != GlStateManager.POLY_OFFSET.units) {
            GlStateManager.POLY_OFFSET.factor = factor;
            GlStateManager.POLY_OFFSET.units = units;
            GL11.glPolygonOffset((float)factor, (float)units);
        }
    }

    public static void _enableColorLogicOp() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.COLOR_LOGIC.capState.enable();
    }

    public static void _disableColorLogicOp() {
        RenderSystem.assertOnRenderThread();
        GlStateManager.COLOR_LOGIC.capState.disable();
    }

    public static void _logicOp(int op) {
        RenderSystem.assertOnRenderThread();
        if (op != GlStateManager.COLOR_LOGIC.op) {
            GlStateManager.COLOR_LOGIC.op = op;
            GL11.glLogicOp((int)op);
        }
    }

    public static void _activeTexture(int texture) {
        RenderSystem.assertOnRenderThread();
        if (activeTexture != texture - 33984) {
            activeTexture = texture - 33984;
            GL13.glActiveTexture((int)texture);
        }
    }

    public static void _texParameter(int target, int pname, int param) {
        RenderSystem.assertOnRenderThread();
        GL11.glTexParameteri((int)target, (int)pname, (int)param);
    }

    public static int _getTexLevelParameter(int target, int level, int pname) {
        return GL11.glGetTexLevelParameteri((int)target, (int)level, (int)pname);
    }

    public static int _genTexture() {
        RenderSystem.assertOnRenderThread();
        PLOT_TEXTURES.setValue((double)(++numTextures));
        return GL11.glGenTextures();
    }

    public static void _deleteTexture(int texture) {
        RenderSystem.assertOnRenderThread();
        GL11.glDeleteTextures((int)texture);
        for (Texture2DState texture2DState : TEXTURES) {
            if (texture2DState.boundTexture != texture) continue;
            texture2DState.boundTexture = -1;
        }
        PLOT_TEXTURES.setValue((double)(--numTextures));
    }

    public static void _bindTexture(int texture) {
        RenderSystem.assertOnRenderThread();
        if (texture != GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture) {
            GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture = texture;
            GL11.glBindTexture((int)3553, (int)texture);
        }
    }

    public static void _texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, @Nullable ByteBuffer byteBuffer) {
        RenderSystem.assertOnRenderThread();
        GL11.glTexImage2D((int)target, (int)level, (int)internalFormat, (int)width, (int)height, (int)border, (int)format, (int)type, (ByteBuffer)byteBuffer);
    }

    public static void _texSubImage2D(int target, int level, int offsetX, int offsetY, int width, int height, int format, int type, long pixels) {
        RenderSystem.assertOnRenderThread();
        GL11.glTexSubImage2D((int)target, (int)level, (int)offsetX, (int)offsetY, (int)width, (int)height, (int)format, (int)type, (long)pixels);
    }

    public static void _texSubImage2D(int target, int level, int offsetX, int offsetY, int width, int height, int format, int type, ByteBuffer byteBuffer) {
        RenderSystem.assertOnRenderThread();
        GL11.glTexSubImage2D((int)target, (int)level, (int)offsetX, (int)offsetY, (int)width, (int)height, (int)format, (int)type, (ByteBuffer)byteBuffer);
    }

    public static void _viewport(int x, int y, int width, int height) {
        GL11.glViewport((int)x, (int)y, (int)width, (int)height);
    }

    public static void _colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        RenderSystem.assertOnRenderThread();
        if (red != GlStateManager.COLOR_MASK.red || green != GlStateManager.COLOR_MASK.green || blue != GlStateManager.COLOR_MASK.blue || alpha != GlStateManager.COLOR_MASK.alpha) {
            GlStateManager.COLOR_MASK.red = red;
            GlStateManager.COLOR_MASK.green = green;
            GlStateManager.COLOR_MASK.blue = blue;
            GlStateManager.COLOR_MASK.alpha = alpha;
            GL11.glColorMask((boolean)red, (boolean)green, (boolean)blue, (boolean)alpha);
        }
    }

    public static void _clear(int mask) {
        RenderSystem.assertOnRenderThread();
        GL11.glClear((int)mask);
        if (MacWindowUtil.IS_MAC) {
            GlStateManager._getError();
        }
    }

    public static void _vertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
        RenderSystem.assertOnRenderThread();
        GL20.glVertexAttribPointer((int)index, (int)size, (int)type, (boolean)normalized, (int)stride, (long)pointer);
    }

    public static void _vertexAttribIPointer(int index, int size, int type, int stride, long pointer) {
        RenderSystem.assertOnRenderThread();
        GL30.glVertexAttribIPointer((int)index, (int)size, (int)type, (int)stride, (long)pointer);
    }

    public static void _enableVertexAttribArray(int index) {
        RenderSystem.assertOnRenderThread();
        GL20.glEnableVertexAttribArray((int)index);
    }

    public static void _drawElements(int mode, int type, int count, long indices) {
        RenderSystem.assertOnRenderThread();
        GL11.glDrawElements((int)mode, (int)type, (int)count, (long)indices);
    }

    public static void _drawArrays(int mode, int first, int count) {
        RenderSystem.assertOnRenderThread();
        GL11.glDrawArrays((int)mode, (int)first, (int)count);
    }

    public static void _pixelStore(int pname, int param) {
        RenderSystem.assertOnRenderThread();
        GL11.glPixelStorei((int)pname, (int)param);
    }

    public static void _readPixels(int x, int y, int width, int height, int format, int type, long pixels) {
        RenderSystem.assertOnRenderThread();
        GL11.glReadPixels((int)x, (int)y, (int)width, (int)height, (int)format, (int)type, (long)pixels);
    }

    public static int _getError() {
        RenderSystem.assertOnRenderThread();
        return GL11.glGetError();
    }

    public static void clearGlErrors() {
        RenderSystem.assertOnRenderThread();
        while (GL11.glGetError() != 0) {
        }
    }

    public static String _getString(int name) {
        RenderSystem.assertOnRenderThread();
        return GL11.glGetString((int)name);
    }

    public static int _getInteger(int pname) {
        RenderSystem.assertOnRenderThread();
        return GL11.glGetInteger((int)pname);
    }

    public static long _glFenceSync(int condition, int flags) {
        RenderSystem.assertOnRenderThread();
        return GL32.glFenceSync((int)condition, (int)flags);
    }

    public static int _glClientWaitSync(long sync, int flags, long timeout) {
        RenderSystem.assertOnRenderThread();
        return GL32.glClientWaitSync((long)sync, (int)flags, (long)timeout);
    }

    public static void _glDeleteSync(long sync) {
        RenderSystem.assertOnRenderThread();
        GL32.glDeleteSync((long)sync);
    }

    static {
        TEXTURES = (Texture2DState[])IntStream.range(0, 12).mapToObj(index -> new Texture2DState()).toArray(Texture2DState[]::new);
        COLOR_MASK = new ColorMask();
    }

    @Environment(value=EnvType.CLIENT)
    static class ScissorTestState {
        public final CapabilityTracker capState = new CapabilityTracker(3089);

        ScissorTestState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CapabilityTracker {
        private final int cap;
        private boolean state;

        public CapabilityTracker(int cap) {
            this.cap = cap;
        }

        public void disable() {
            this.setState(false);
        }

        public void enable() {
            this.setState(true);
        }

        public void setState(boolean state) {
            RenderSystem.assertOnRenderThread();
            if (state != this.state) {
                this.state = state;
                if (state) {
                    GL11.glEnable((int)this.cap);
                } else {
                    GL11.glDisable((int)this.cap);
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class DepthTestState {
        public final CapabilityTracker capState = new CapabilityTracker(2929);
        public boolean mask = true;
        public int func = 513;

        DepthTestState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class BlendFuncState {
        public final CapabilityTracker capState = new CapabilityTracker(3042);
        public int srcFactorRgb = 1;
        public int dstFactorRgb = 0;
        public int srcFactorAlpha = 1;
        public int dstFactorAlpha = 0;

        BlendFuncState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CullFaceState {
        public final CapabilityTracker capState = new CapabilityTracker(2884);

        CullFaceState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class PolygonOffsetState {
        public final CapabilityTracker capFill = new CapabilityTracker(32823);
        public float factor;
        public float units;

        PolygonOffsetState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class LogicOpState {
        public final CapabilityTracker capState = new CapabilityTracker(3058);
        public int op = 5379;

        LogicOpState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Texture2DState {
        public int boundTexture;

        Texture2DState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ColorMask {
        public boolean red = true;
        public boolean green = true;
        public boolean blue = true;
        public boolean alpha = true;

        ColorMask() {
        }
    }
}
