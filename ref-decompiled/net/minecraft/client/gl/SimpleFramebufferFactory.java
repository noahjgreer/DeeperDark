/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.SimpleFramebuffer
 *  net.minecraft.client.gl.SimpleFramebufferFactory
 *  net.minecraft.client.util.ClosableFactory
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.util.ClosableFactory;

@Environment(value=EnvType.CLIENT)
public record SimpleFramebufferFactory(int width, int height, boolean useDepth, int clearColor) implements ClosableFactory<Framebuffer>
{
    private final int width;
    private final int height;
    private final boolean useDepth;
    private final int clearColor;

    public SimpleFramebufferFactory(int width, int height, boolean useDepth, int clearColor) {
        this.width = width;
        this.height = height;
        this.useDepth = useDepth;
        this.clearColor = clearColor;
    }

    public Framebuffer create() {
        return new SimpleFramebuffer(null, this.width, this.height, this.useDepth);
    }

    public void prepare(Framebuffer framebuffer) {
        if (this.useDepth) {
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(framebuffer.getColorAttachment(), this.clearColor, framebuffer.getDepthAttachment(), 1.0);
        } else {
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(framebuffer.getColorAttachment(), this.clearColor);
        }
    }

    public void close(Framebuffer framebuffer) {
        framebuffer.delete();
    }

    public boolean equals(ClosableFactory<?> factory) {
        if (factory instanceof SimpleFramebufferFactory) {
            SimpleFramebufferFactory simpleFramebufferFactory = (SimpleFramebufferFactory)factory;
            return this.width == simpleFramebufferFactory.width && this.height == simpleFramebufferFactory.height && this.useDepth == simpleFramebufferFactory.useDepth;
        }
        return false;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public boolean useDepth() {
        return this.useDepth;
    }

    public int clearColor() {
        return this.clearColor;
    }

    public /* synthetic */ void close(Object value) {
        this.close((Framebuffer)value);
    }

    public /* synthetic */ void prepare(Object value) {
        this.prepare((Framebuffer)value);
    }

    public /* synthetic */ Object create() {
        return this.create();
    }
}

