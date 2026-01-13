/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.FabricRenderPipeline$Builder
 */
package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.LogicOp;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderPipeline;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.util.Identifier;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public static class RenderPipeline.Builder
implements FabricRenderPipeline.Builder {
    private static int nextPipelineSortKey;
    private Optional<Identifier> location = Optional.empty();
    private Optional<Identifier> fragmentShader = Optional.empty();
    private Optional<Identifier> vertexShader = Optional.empty();
    private Optional<Defines.Builder> definesBuilder = Optional.empty();
    private Optional<List<String>> samplers = Optional.empty();
    private Optional<List<RenderPipeline.UniformDescription>> uniforms = Optional.empty();
    private Optional<DepthTestFunction> depthTestFunction = Optional.empty();
    private Optional<PolygonMode> polygonMode = Optional.empty();
    private Optional<Boolean> cull = Optional.empty();
    private Optional<Boolean> writeColor = Optional.empty();
    private Optional<Boolean> writeAlpha = Optional.empty();
    private Optional<Boolean> writeDepth = Optional.empty();
    private Optional<LogicOp> colorLogic = Optional.empty();
    private Optional<BlendFunction> blendFunction = Optional.empty();
    private Optional<VertexFormat> vertexFormat = Optional.empty();
    private Optional<VertexFormat.DrawMode> vertexFormatMode = Optional.empty();
    private float depthBiasScaleFactor;
    private float depthBiasConstant;

    RenderPipeline.Builder() {
    }

    public RenderPipeline.Builder withLocation(String location) {
        this.location = Optional.of(Identifier.ofVanilla(location));
        return this;
    }

    public RenderPipeline.Builder withLocation(Identifier location) {
        this.location = Optional.of(location);
        return this;
    }

    public RenderPipeline.Builder withFragmentShader(String fragmentShader) {
        this.fragmentShader = Optional.of(Identifier.ofVanilla(fragmentShader));
        return this;
    }

    public RenderPipeline.Builder withFragmentShader(Identifier fragmentShader) {
        this.fragmentShader = Optional.of(fragmentShader);
        return this;
    }

    public RenderPipeline.Builder withVertexShader(String string) {
        this.vertexShader = Optional.of(Identifier.ofVanilla(string));
        return this;
    }

    public RenderPipeline.Builder withVertexShader(Identifier vertexShader) {
        this.vertexShader = Optional.of(vertexShader);
        return this;
    }

    public RenderPipeline.Builder withShaderDefine(String flag) {
        if (this.definesBuilder.isEmpty()) {
            this.definesBuilder = Optional.of(Defines.builder());
        }
        this.definesBuilder.get().flag(flag);
        return this;
    }

    public RenderPipeline.Builder withShaderDefine(String name, int value) {
        if (this.definesBuilder.isEmpty()) {
            this.definesBuilder = Optional.of(Defines.builder());
        }
        this.definesBuilder.get().define(name, value);
        return this;
    }

    public RenderPipeline.Builder withShaderDefine(String name, float value) {
        if (this.definesBuilder.isEmpty()) {
            this.definesBuilder = Optional.of(Defines.builder());
        }
        this.definesBuilder.get().define(name, value);
        return this;
    }

    public RenderPipeline.Builder withSampler(String sampler) {
        if (this.samplers.isEmpty()) {
            this.samplers = Optional.of(new ArrayList());
        }
        this.samplers.get().add(sampler);
        return this;
    }

    public RenderPipeline.Builder withUniform(String name, UniformType type) {
        if (this.uniforms.isEmpty()) {
            this.uniforms = Optional.of(new ArrayList());
        }
        if (type == UniformType.TEXEL_BUFFER) {
            throw new IllegalArgumentException("Cannot use texel buffer without specifying texture format");
        }
        this.uniforms.get().add(new RenderPipeline.UniformDescription(name, type));
        return this;
    }

    public RenderPipeline.Builder withUniform(String name, UniformType type, TextureFormat format) {
        if (this.uniforms.isEmpty()) {
            this.uniforms = Optional.of(new ArrayList());
        }
        if (type != UniformType.TEXEL_BUFFER) {
            throw new IllegalArgumentException("Only texel buffer can specify texture format");
        }
        this.uniforms.get().add(new RenderPipeline.UniformDescription(name, format));
        return this;
    }

    public RenderPipeline.Builder withDepthTestFunction(DepthTestFunction depthTestFunction) {
        this.depthTestFunction = Optional.of(depthTestFunction);
        return this;
    }

    public RenderPipeline.Builder withPolygonMode(PolygonMode polygonMode) {
        this.polygonMode = Optional.of(polygonMode);
        return this;
    }

    public RenderPipeline.Builder withCull(boolean cull) {
        this.cull = Optional.of(cull);
        return this;
    }

    public RenderPipeline.Builder withBlend(BlendFunction blendFunction) {
        this.blendFunction = Optional.of(blendFunction);
        return this;
    }

    public RenderPipeline.Builder withoutBlend() {
        this.blendFunction = Optional.empty();
        return this;
    }

    public RenderPipeline.Builder withColorWrite(boolean writeColor) {
        this.writeColor = Optional.of(writeColor);
        this.writeAlpha = Optional.of(writeColor);
        return this;
    }

    public RenderPipeline.Builder withColorWrite(boolean writeColor, boolean writeAlpha) {
        this.writeColor = Optional.of(writeColor);
        this.writeAlpha = Optional.of(writeAlpha);
        return this;
    }

    public RenderPipeline.Builder withDepthWrite(boolean writeDepth) {
        this.writeDepth = Optional.of(writeDepth);
        return this;
    }

    @Deprecated
    public RenderPipeline.Builder withColorLogic(LogicOp colorLogic) {
        this.colorLogic = Optional.of(colorLogic);
        return this;
    }

    public RenderPipeline.Builder withVertexFormat(VertexFormat vertexFormat, VertexFormat.DrawMode vertexFormatMode) {
        this.vertexFormat = Optional.of(vertexFormat);
        this.vertexFormatMode = Optional.of(vertexFormatMode);
        return this;
    }

    public RenderPipeline.Builder withDepthBias(float depthBiasScaleFactor, float depthBiasConstant) {
        this.depthBiasScaleFactor = depthBiasScaleFactor;
        this.depthBiasConstant = depthBiasConstant;
        return this;
    }

    void withSnippet(RenderPipeline.Snippet snippet) {
        if (snippet.vertexShader.isPresent()) {
            this.vertexShader = snippet.vertexShader;
        }
        if (snippet.fragmentShader.isPresent()) {
            this.fragmentShader = snippet.fragmentShader;
        }
        if (snippet.shaderDefines.isPresent()) {
            if (this.definesBuilder.isEmpty()) {
                this.definesBuilder = Optional.of(Defines.builder());
            }
            Defines defines = snippet.shaderDefines.get();
            for (Map.Entry<String, String> entry : defines.values().entrySet()) {
                this.definesBuilder.get().define(entry.getKey(), entry.getValue());
            }
            for (String string : defines.flags()) {
                this.definesBuilder.get().flag(string);
            }
        }
        snippet.samplers.ifPresent(samplers -> {
            if (this.samplers.isPresent()) {
                this.samplers.get().addAll((Collection<String>)samplers);
            } else {
                this.samplers = Optional.of(new ArrayList(samplers));
            }
        });
        snippet.uniforms.ifPresent(uniforms -> {
            if (this.uniforms.isPresent()) {
                this.uniforms.get().addAll((Collection<RenderPipeline.UniformDescription>)uniforms);
            } else {
                this.uniforms = Optional.of(new ArrayList(uniforms));
            }
        });
        if (snippet.depthTestFunction.isPresent()) {
            this.depthTestFunction = snippet.depthTestFunction;
        }
        if (snippet.cull.isPresent()) {
            this.cull = snippet.cull;
        }
        if (snippet.writeColor.isPresent()) {
            this.writeColor = snippet.writeColor;
        }
        if (snippet.writeAlpha.isPresent()) {
            this.writeAlpha = snippet.writeAlpha;
        }
        if (snippet.writeDepth.isPresent()) {
            this.writeDepth = snippet.writeDepth;
        }
        if (snippet.colorLogic.isPresent()) {
            this.colorLogic = snippet.colorLogic;
        }
        if (snippet.blendFunction.isPresent()) {
            this.blendFunction = snippet.blendFunction;
        }
        if (snippet.vertexFormat.isPresent()) {
            this.vertexFormat = snippet.vertexFormat;
        }
        if (snippet.vertexFormatMode.isPresent()) {
            this.vertexFormatMode = snippet.vertexFormatMode;
        }
    }

    public RenderPipeline.Snippet buildSnippet() {
        return new RenderPipeline.Snippet(this.vertexShader, this.fragmentShader, this.definesBuilder.map(Defines.Builder::build), this.samplers.map(Collections::unmodifiableList), this.uniforms.map(Collections::unmodifiableList), this.blendFunction, this.depthTestFunction, this.polygonMode, this.cull, this.writeColor, this.writeAlpha, this.writeDepth, this.colorLogic, this.vertexFormat, this.vertexFormatMode);
    }

    public RenderPipeline build() {
        if (this.location.isEmpty()) {
            throw new IllegalStateException("Missing location");
        }
        if (this.vertexShader.isEmpty()) {
            throw new IllegalStateException("Missing vertex shader");
        }
        if (this.fragmentShader.isEmpty()) {
            throw new IllegalStateException("Missing fragment shader");
        }
        if (this.vertexFormat.isEmpty()) {
            throw new IllegalStateException("Missing vertex buffer format");
        }
        if (this.vertexFormatMode.isEmpty()) {
            throw new IllegalStateException("Missing vertex mode");
        }
        return new RenderPipeline(this.location.get(), this.vertexShader.get(), this.fragmentShader.get(), this.definesBuilder.orElse(Defines.builder()).build(), List.copyOf(this.samplers.orElse(new ArrayList())), this.uniforms.orElse(Collections.emptyList()), this.blendFunction, this.depthTestFunction.orElse(DepthTestFunction.LEQUAL_DEPTH_TEST), this.polygonMode.orElse(PolygonMode.FILL), this.cull.orElse(true), this.writeColor.orElse(true), this.writeAlpha.orElse(true), this.writeDepth.orElse(true), this.colorLogic.orElse(LogicOp.NONE), this.vertexFormat.get(), this.vertexFormatMode.get(), this.depthBiasScaleFactor, this.depthBiasConstant, nextPipelineSortKey++);
    }
}
