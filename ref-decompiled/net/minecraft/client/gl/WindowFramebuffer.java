/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.TextureFormat
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.WindowFramebuffer
 *  net.minecraft.client.gl.WindowFramebuffer$Size
 *  net.minecraft.client.util.TextureAllocationException
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.WindowFramebuffer;
import net.minecraft.client.util.TextureAllocationException;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class WindowFramebuffer
extends Framebuffer {
    public static final int DEFAULT_WIDTH = 854;
    public static final int DEFAULT_HEIGHT = 480;
    static final Size DEFAULT = new Size(854, 480);

    public WindowFramebuffer(int width, int height) {
        super("Main", true);
        this.init(width, height);
    }

    private void init(int width, int height) {
        Size size = this.findSuitableSize(width, height);
        if (this.colorAttachment == null || this.depthAttachment == null) {
            throw new IllegalStateException("Missing color and/or depth textures");
        }
        this.textureWidth = size.width;
        this.textureHeight = size.height;
    }

    private Size findSuitableSize(int width, int height) {
        RenderSystem.assertOnRenderThread();
        for (Size size : Size.findCompatible((int)width, (int)height)) {
            if (this.colorAttachment != null) {
                this.colorAttachment.close();
                this.colorAttachment = null;
            }
            if (this.colorAttachmentView != null) {
                this.colorAttachmentView.close();
                this.colorAttachmentView = null;
            }
            if (this.depthAttachment != null) {
                this.depthAttachment.close();
                this.depthAttachment = null;
            }
            if (this.depthAttachmentView != null) {
                this.depthAttachmentView.close();
                this.depthAttachmentView = null;
            }
            this.colorAttachment = this.createColorAttachment(size);
            this.depthAttachment = this.createDepthAttachment(size);
            if (this.colorAttachment == null || this.depthAttachment == null) continue;
            this.colorAttachmentView = RenderSystem.getDevice().createTextureView(this.colorAttachment);
            this.depthAttachmentView = RenderSystem.getDevice().createTextureView(this.depthAttachment);
            return size;
        }
        throw new RuntimeException("Unrecoverable GL_OUT_OF_MEMORY (" + (this.colorAttachment == null ? "missing color" : "have color") + ", " + (this.depthAttachment == null ? "missing depth" : "have depth") + ")");
    }

    private @Nullable GpuTexture createColorAttachment(Size size) {
        try {
            return RenderSystem.getDevice().createTexture(() -> this.name + " / Color", 15, TextureFormat.RGBA8, size.width, size.height, 1, 1);
        }
        catch (TextureAllocationException textureAllocationException) {
            return null;
        }
    }

    private @Nullable GpuTexture createDepthAttachment(Size size) {
        try {
            return RenderSystem.getDevice().createTexture(() -> this.name + " / Depth", 15, TextureFormat.DEPTH32, size.width, size.height, 1, 1);
        }
        catch (TextureAllocationException textureAllocationException) {
            return null;
        }
    }
}

