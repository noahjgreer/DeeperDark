/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.render.LayeringTransform;
import net.minecraft.client.render.OutputTarget;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.TextureTransform;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class RenderSetup.Builder {
    private final RenderPipeline pipeline;
    private boolean useLightmap = false;
    private boolean useOverlay = false;
    private LayeringTransform layeringTransform = LayeringTransform.NO_LAYERING;
    private OutputTarget outputTarget = OutputTarget.MAIN_TARGET;
    private TextureTransform textureTransform = TextureTransform.DEFAULT_TEXTURING;
    private boolean hasCrumbling = false;
    private boolean translucent = false;
    private int expectedBufferSize = 1536;
    private RenderSetup.OutlineMode outlineMode = RenderSetup.OutlineMode.NONE;
    private final Map<String, RenderSetup.TextureSpec> textures = new HashMap<String, RenderSetup.TextureSpec>();

    RenderSetup.Builder(RenderPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public RenderSetup.Builder texture(String name, Identifier id) {
        this.textures.put(name, new RenderSetup.TextureSpec(id, () -> null));
        return this;
    }

    public RenderSetup.Builder texture(String name, Identifier id, @Nullable Supplier<GpuSampler> samplerSupplier) {
        this.textures.put(name, new RenderSetup.TextureSpec(id, (Supplier<GpuSampler>)Suppliers.memoize(() -> samplerSupplier == null ? null : (GpuSampler)samplerSupplier.get())));
        return this;
    }

    public RenderSetup.Builder useLightmap() {
        this.useLightmap = true;
        return this;
    }

    public RenderSetup.Builder useOverlay() {
        this.useOverlay = true;
        return this;
    }

    public RenderSetup.Builder crumbling() {
        this.hasCrumbling = true;
        return this;
    }

    public RenderSetup.Builder translucent() {
        this.translucent = true;
        return this;
    }

    public RenderSetup.Builder expectedBufferSize(int expectedBufferSize) {
        this.expectedBufferSize = expectedBufferSize;
        return this;
    }

    public RenderSetup.Builder layeringTransform(LayeringTransform layeringTransform) {
        this.layeringTransform = layeringTransform;
        return this;
    }

    public RenderSetup.Builder outputTarget(OutputTarget outputTarget) {
        this.outputTarget = outputTarget;
        return this;
    }

    public RenderSetup.Builder textureTransform(TextureTransform textureTransform) {
        this.textureTransform = textureTransform;
        return this;
    }

    public RenderSetup.Builder outlineMode(RenderSetup.OutlineMode outlineMode) {
        this.outlineMode = outlineMode;
        return this;
    }

    public RenderSetup build() {
        return new RenderSetup(this.pipeline, this.textures, this.useLightmap, this.useOverlay, this.layeringTransform, this.outputTarget, this.textureTransform, this.outlineMode, this.hasCrumbling, this.translucent, this.expectedBufferSize);
    }
}
