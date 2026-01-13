/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ca.weblite.objc.Client
 *  ca.weblite.objc.NSObject
 *  com.sun.jna.Pointer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.glfw.GLFWNativeCocoa
 */
package net.minecraft.client.util;

import ca.weblite.objc.Client;
import ca.weblite.objc.NSObject;
import com.sun.jna.Pointer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Window;
import net.minecraft.resource.InputSupplier;
import org.lwjgl.glfw.GLFWNativeCocoa;

@Environment(value=EnvType.CLIENT)
public class MacWindowUtil {
    public static final boolean IS_MAC = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("mac");
    private static final int field_46537 = 8;
    private static final int FULLSCREEN_MASK = 16384;

    public static void toggleFullscreen(Window window) {
        MacWindowUtil.getCocoaWindow(window).filter(MacWindowUtil::isFullscreen).ifPresent(MacWindowUtil::toggleFullscreen);
    }

    public static void fixStyleMask(Window window) {
        MacWindowUtil.getCocoaWindow(window).ifPresent(windowHandle -> {
            long l = MacWindowUtil.getStyleMask(windowHandle);
            windowHandle.send("setStyleMask:", new Object[]{l & 0xFFFFFFFFFFFFFFF7L});
        });
    }

    private static Optional<NSObject> getCocoaWindow(Window window) {
        long l = GLFWNativeCocoa.glfwGetCocoaWindow((long)window.getHandle());
        if (l != 0L) {
            return Optional.of(new NSObject(new Pointer(l)));
        }
        return Optional.empty();
    }

    private static boolean isFullscreen(NSObject handle) {
        return (MacWindowUtil.getStyleMask(handle) & 0x4000L) != 0L;
    }

    private static long getStyleMask(NSObject handle) {
        return (Long)handle.sendRaw("styleMask", new Object[0]);
    }

    private static void toggleFullscreen(NSObject handle) {
        handle.send("toggleFullScreen:", new Object[]{Pointer.NULL});
    }

    public static void setApplicationIconImage(InputSupplier<InputStream> iconSupplier) throws IOException {
        try (InputStream inputStream = iconSupplier.get();){
            String string = Base64.getEncoder().encodeToString(inputStream.readAllBytes());
            Client client = Client.getInstance();
            Object object = client.sendProxy("NSData", "alloc", new Object[0]).send("initWithBase64Encoding:", new Object[]{string});
            Object object2 = client.sendProxy("NSImage", "alloc", new Object[0]).send("initWithData:", new Object[]{object});
            client.sendProxy("NSApplication", "sharedApplication", new Object[0]).send("setApplicationIconImage:", new Object[]{object2});
        }
    }
}
