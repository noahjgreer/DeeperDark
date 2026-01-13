/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static class WindowFramebuffer.Size {
    public final int width;
    public final int height;

    WindowFramebuffer.Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    static List<WindowFramebuffer.Size> findCompatible(int width, int height) {
        RenderSystem.assertOnRenderThread();
        int i = RenderSystem.getDevice().getMaxTextureSize();
        if (width <= 0 || width > i || height <= 0 || height > i) {
            return ImmutableList.of((Object)DEFAULT);
        }
        return ImmutableList.of((Object)new WindowFramebuffer.Size(width, height), (Object)DEFAULT);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        WindowFramebuffer.Size size = (WindowFramebuffer.Size)o;
        return this.width == size.width && this.height == size.height;
    }

    public int hashCode() {
        return Objects.hash(this.width, this.height);
    }

    public String toString() {
        return this.width + "x" + this.height;
    }
}
