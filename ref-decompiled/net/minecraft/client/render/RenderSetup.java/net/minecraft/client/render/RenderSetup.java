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
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.render.LayeringTransform;
import net.minecraft.client.render.OutputTarget;
import net.minecraft.client.render.TextureTransform;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

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
        return "RenderSetup[layeringTransform=" + String.valueOf(this.layeringTransform) + ", textureTransform=" + String.valueOf(this.textureTransform) + ", textures=" + String.valueOf(this.textures) + ", outlineProperty=" + String.valueOf((Object)this.outlineMode) + ", useLightmap=" + this.useLightmap + ", useOverlay=" + this.useOverlay + "]";
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
        for (Map.Entry<String, TextureSpec> entry : this.textures.entrySet()) {
            AbstractTexture abstractTexture = textureManager.getTexture(entry.getValue().location);
            GpuSampler gpuSampler = entry.getValue().sampler().get();
            map.put(entry.getKey(), new Texture(abstractTexture.getGlTextureView(), gpuSampler != null ? gpuSampler : abstractTexture.getSampler()));
        }
        return map;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class OutlineMode
    extends Enum<OutlineMode> {
        public static final /* enum */ OutlineMode NONE = new OutlineMode("none");
        public static final /* enum */ OutlineMode IS_OUTLINE = new OutlineMode("is_outline");
        public static final /* enum */ OutlineMode AFFECTS_OUTLINE = new OutlineMode("affects_outline");
        private final String name;
        private static final /* synthetic */ OutlineMode[] field_21856;

        public static OutlineMode[] values() {
            return (OutlineMode[])field_21856.clone();
        }

        public static OutlineMode valueOf(String string) {
            return Enum.valueOf(OutlineMode.class, string);
        }

        private OutlineMode(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        private static /* synthetic */ OutlineMode[] method_36916() {
            return new OutlineMode[]{NONE, IS_OUTLINE, AFFECTS_OUTLINE};
        }

        static {
            field_21856 = OutlineMode.method_36916();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private final RenderPipeline pipeline;
        private boolean useLightmap = false;
        private boolean useOverlay = false;
        private LayeringTransform layeringTransform = LayeringTransform.NO_LAYERING;
        private OutputTarget outputTarget = OutputTarget.MAIN_TARGET;
        private TextureTransform textureTransform = TextureTransform.DEFAULT_TEXTURING;
        private boolean hasCrumbling = false;
        private boolean translucent = false;
        private int expectedBufferSize = 1536;
        private OutlineMode outlineMode = OutlineMode.NONE;
        private final Map<String, TextureSpec> textures = new HashMap<String, TextureSpec>();

        Builder(RenderPipeline pipeline) {
            this.pipeline = pipeline;
        }

        public Builder texture(String name, Identifier id) {
            this.textures.put(name, new TextureSpec(id, () -> null));
            return this;
        }

        public Builder texture(String name, Identifier id, @Nullable Supplier<GpuSampler> samplerSupplier) {
            this.textures.put(name, new TextureSpec(id, (Supplier<GpuSampler>)Suppliers.memoize(() -> samplerSupplier == null ? null : (GpuSampler)samplerSupplier.get())));
            return this;
        }

        public Builder useLightmap() {
            this.useLightmap = true;
            return this;
        }

        public Builder useOverlay() {
            this.useOverlay = true;
            return this;
        }

        public Builder crumbling() {
            this.hasCrumbling = true;
            return this;
        }

        public Builder translucent() {
            this.translucent = true;
            return this;
        }

        public Builder expectedBufferSize(int expectedBufferSize) {
            this.expectedBufferSize = expectedBufferSize;
            return this;
        }

        public Builder layeringTransform(LayeringTransform layeringTransform) {
            this.layeringTransform = layeringTransform;
            return this;
        }

        public Builder outputTarget(OutputTarget outputTarget) {
            this.outputTarget = outputTarget;
            return this;
        }

        public Builder textureTransform(TextureTransform textureTransform) {
            this.textureTransform = textureTransform;
            return this;
        }

        public Builder outlineMode(OutlineMode outlineMode) {
            this.outlineMode = outlineMode;
            return this;
        }

        public RenderSetup build() {
            return new RenderSetup(this.pipeline, this.textures, this.useLightmap, this.useOverlay, this.layeringTransform, this.outputTarget, this.textureTransform, this.outlineMode, this.hasCrumbling, this.translucent, this.expectedBufferSize);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Texture(GpuTextureView textureView, GpuSampler sampler) {
    }

    @Environment(value=EnvType.CLIENT)
    static final class TextureSpec
    extends Record {
        final Identifier location;
        private final Supplier<@Nullable GpuSampler> sampler;

        TextureSpec(Identifier location, Supplier<@Nullable GpuSampler> sampler) {
            this.location = location;
            this.sampler = sampler;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{TextureSpec.class, "location;sampler", "location", "sampler"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TextureSpec.class, "location;sampler", "location", "sampler"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TextureSpec.class, "location;sampler", "location", "sampler"}, this, object);
        }

        public Identifier location() {
            return this.location;
        }

        public Supplier<@Nullable GpuSampler> sampler() {
            return this.sampler;
        }
    }
}
