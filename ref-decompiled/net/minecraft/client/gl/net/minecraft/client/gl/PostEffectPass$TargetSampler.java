/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record PostEffectPass.TargetSampler(String samplerName, Identifier targetId, boolean depthBuffer, boolean bilinear) implements PostEffectPass.Sampler
{
    private Handle<Framebuffer> getTarget(Map<Identifier, Handle<Framebuffer>> internalTargets) {
        Handle<Framebuffer> handle = internalTargets.get(this.targetId);
        if (handle == null) {
            throw new IllegalStateException("Missing handle for target " + String.valueOf(this.targetId));
        }
        return handle;
    }

    @Override
    public void preRender(FramePass pass, Map<Identifier, Handle<Framebuffer>> internalTargets) {
        pass.dependsOn(this.getTarget(internalTargets));
    }

    @Override
    public GpuTextureView getTexture(Map<Identifier, Handle<Framebuffer>> internalTargets) {
        GpuTextureView gpuTextureView;
        Handle<Framebuffer> handle = this.getTarget(internalTargets);
        Framebuffer framebuffer = handle.get();
        GpuTextureView gpuTextureView2 = gpuTextureView = this.depthBuffer ? framebuffer.getDepthAttachmentView() : framebuffer.getColorAttachmentView();
        if (gpuTextureView == null) {
            throw new IllegalStateException("Missing " + (this.depthBuffer ? "depth" : "color") + "texture for target " + String.valueOf(this.targetId));
        }
        return gpuTextureView;
    }
}
