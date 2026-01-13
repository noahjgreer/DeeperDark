/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.GpuSampler
 *  net.minecraft.client.render.LayeringTransform
 *  net.minecraft.client.render.OutputTarget
 *  net.minecraft.client.render.RenderSetup
 *  net.minecraft.client.render.RenderSetup$Builder
 *  net.minecraft.client.render.RenderSetup$OutlineMode
 *  net.minecraft.client.render.RenderSetup$Texture
 *  net.minecraft.client.render.RenderSetup$TextureSpec
 *  net.minecraft.client.render.TextureTransform
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.texture.TextureManager
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.render.LayeringTransform;
import net.minecraft.client.render.OutputTarget;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.TextureTransform;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;

@Environment(value=EnvType.CLIENT)
public final class RenderSetup {
    final RenderPipeline pipeline;
    final Map<String, TextureSpec> textures;
    final TextureTransform textureTransform;
    final OutputTarget outputTarget;
    final OutlineMode outlineMode;
    final boolean useLightmap;
    final boolean useOverlay;
    final boolean hasCrumbling;
    final boolean translucent;
    final int expectedBufferSize;
    final LayeringTransform layeringTransform;

    RenderSetup(RenderPipeline pipeline, Map<String, TextureSpec> textures, boolean useLightmap, boolean useOverlay, LayeringTransform layeringTransform, OutputTarget outputTarget, TextureTransform textureTransform, OutlineMode outlineMode, boolean hasCrumbling, boolean translucent, int expectedBufferSize) {
        this.pipeline = pipeline;
        this.textures = textures;
        this.outputTarget = outputTarget;
        this.textureTransform = textureTransform;
        this.useLightmap = useLightmap;
        this.useOverlay = useOverlay;
        this.outlineMode = outlineMode;
        this.layeringTransform = layeringTransform;
        this.hasCrumbling = hasCrumbling;
        this.translucent = translucent;
        this.expectedBufferSize = expectedBufferSize;
    }

    public String toString() {
        return "RenderSetup[layeringTransform=" + String.valueOf(this.layeringTransform) + ", textureTransform=" + String.valueOf(this.textureTransform) + ", textures=" + String.valueOf(this.textures) + ", outlineProperty=" + String.valueOf(this.outlineMode) + ", useLightmap=" + this.useLightmap + ", useOverlay=" + this.useOverlay + "]";
    }

    public static Builder builder(RenderPipeline renderPipeline) {
        return new Builder(renderPipeline);
    }

    public Map<String, Texture> resolveTextures() {
        if (this.textures.isEmpty() && !this.useOverlay && !this.useLightmap) {
            return Collections.emptyMap();
        }
        HashMap<String, Texture> map = new HashMap<String, Texture>();
        if (this.useOverlay) {
            map.put("Sampler1", new Texture(MinecraftClient.getInstance().gameRenderer.getOverlayTexture().getTextureView(), RenderSystem.getSamplerCache().get(FilterMode.LINEAR)));
        }
        if (this.useLightmap) {
            map.put("Sampler2", new Texture(MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().getGlTextureView(), RenderSystem.getSamplerCache().get(FilterMode.LINEAR)));
        }
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        for (Map.Entry entry : this.textures.entrySet()) {
            AbstractTexture abstractTexture = textureManager.getTexture(((TextureSpec)entry.getValue()).location);
            GpuSampler gpuSampler = (GpuSampler)((TextureSpec)entry.getValue()).sampler().get();
            map.put((String)entry.getKey(), new Texture(abstractTexture.getGlTextureView(), gpuSampler != null ? gpuSampler : abstractTexture.getSampler()));
        }
        return map;
    }
}

