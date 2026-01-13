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
import net.minecraft.client.render.FramePass;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public static interface PostEffectPass.Sampler {
    public void preRender(FramePass var1, Map<Identifier, Handle<Framebuffer>> var2);

    default public void postRender(Map<Identifier, Handle<Framebuffer>> internalTargets) {
    }

    public GpuTextureView getTexture(Map<Identifier, Handle<Framebuffer>> var1);

    public String samplerName();

    public boolean bilinear();
}
