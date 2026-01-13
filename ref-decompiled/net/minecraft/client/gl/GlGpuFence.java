/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuFence
 *  com.mojang.blaze3d.opengl.GlStateManager
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.GlGpuFence
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.opengl.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class GlGpuFence
implements GpuFence {
    private long handle = GlStateManager._glFenceSync((int)37143, (int)0);

    public void close() {
        if (this.handle != 0L) {
            GlStateManager._glDeleteSync((long)this.handle);
            this.handle = 0L;
        }
    }

    public boolean awaitCompletion(long l) {
        if (this.handle == 0L) {
            return true;
        }
        int i = GlStateManager._glClientWaitSync((long)this.handle, (int)0, (long)l);
        if (i == 37147) {
            return false;
        }
        if (i == 37149) {
            throw new IllegalStateException("Failed to complete GPU fence: " + GlStateManager._getError());
        }
        return true;
    }
}

