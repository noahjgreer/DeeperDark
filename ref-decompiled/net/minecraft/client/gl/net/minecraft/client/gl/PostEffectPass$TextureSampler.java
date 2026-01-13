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
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record PostEffectPass.TextureSampler(String samplerName, AbstractTexture texture, int width, int height, boolean bilinear) implements PostEffectPass.Sampler
{
    @Override
    public void preRender(FramePass pass, Map<Identifier, Handle<Framebuffer>> internalTargets) {
    }

    @Override
    public GpuTextureView getTexture(Map<Identifier, Handle<Framebuffer>> internalTargets) {
        return this.texture.getGlTextureView();
    }
}
