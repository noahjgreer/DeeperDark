/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.cursor.Cursor
 *  net.minecraft.client.util.Window
 *  org.lwjgl.glfw.GLFW
 */
package net.minecraft.client.gui.cursor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public class Cursor {
    public static final Cursor DEFAULT = new Cursor("default", 0L);
    private final String name;
    private final long handle;

    private Cursor(String name, long handle) {
        this.name = name;
        this.handle = handle;
    }

    public void applyTo(Window window) {
        GLFW.glfwSetCursor((long)window.getHandle(), (long)this.handle);
    }

    public String toString() {
        return this.name;
    }

    public static Cursor createStandard(int handle, String name, Cursor fallback) {
        long l = GLFW.glfwCreateStandardCursor((int)handle);
        if (l == 0L) {
            return fallback;
        }
        return new Cursor(name, l);
    }
}

