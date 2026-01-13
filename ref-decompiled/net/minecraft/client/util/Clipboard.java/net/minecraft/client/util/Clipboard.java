/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallback
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Window;
import net.minecraft.text.TextVisitFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class Clipboard {
    public static final int GLFW_FORMAT_UNAVAILABLE = 65545;
    private final ByteBuffer clipboardBuffer = BufferUtils.createByteBuffer((int)8192);

    public String get(Window window, GLFWErrorCallbackI errorCallback) {
        GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)errorCallback);
        String string = GLFW.glfwGetClipboardString((long)window.getHandle());
        string = string != null ? TextVisitFactory.validateSurrogates(string) : "";
        GLFWErrorCallback gLFWErrorCallback2 = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)gLFWErrorCallback);
        if (gLFWErrorCallback2 != null) {
            gLFWErrorCallback2.free();
        }
        return string;
    }

    private static void set(Window window, ByteBuffer clipboardBuffer, byte[] content) {
        clipboardBuffer.clear();
        clipboardBuffer.put(content);
        clipboardBuffer.put((byte)0);
        clipboardBuffer.flip();
        GLFW.glfwSetClipboardString((long)window.getHandle(), (ByteBuffer)clipboardBuffer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void set(Window window, String string) {
        byte[] bs = string.getBytes(StandardCharsets.UTF_8);
        int i = bs.length + 1;
        if (i < this.clipboardBuffer.capacity()) {
            Clipboard.set(window, this.clipboardBuffer, bs);
        } else {
            ByteBuffer byteBuffer = MemoryUtil.memAlloc((int)i);
            try {
                Clipboard.set(window, byteBuffer, bs);
            }
            finally {
                MemoryUtil.memFree((Buffer)byteBuffer);
            }
        }
    }
}
