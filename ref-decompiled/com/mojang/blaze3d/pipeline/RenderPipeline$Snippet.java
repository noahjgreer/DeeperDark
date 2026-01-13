/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.FabricRenderPipeline$Snippet
 */
package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.LogicOp;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderPipeline;
import net.minecraft.client.gl.Defines;
import net.minecraft.util.Identifier;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public static final class RenderPipeline.Snippet
extends Record
implements FabricRenderPipeline.Snippet {
    final Optional<Identifier> vertexShader;
    final Optional<Identifier> fragmentShader;
    final Optional<Defines> shaderDefines;
    final Optional<List<String>> samplers;
    final Optional<List<RenderPipeline.UniformDescription>> uniforms;
    final Optional<BlendFunction> blendFunction;
    final Optional<DepthTestFunction> depthTestFunction;
    private final Optional<PolygonMode> polygonMode;
    final Optional<Boolean> cull;
    final Optional<Boolean> writeColor;
    final Optional<Boolean> writeAlpha;
    final Optional<Boolean> writeDepth;
    final Optional<LogicOp> colorLogic;
    final Optional<VertexFormat> vertexFormat;
    final Optional<VertexFormat.DrawMode> vertexFormatMode;

    public RenderPipeline.Snippet(Optional<Identifier> vertexShader, Optional<Identifier> fragmentShader, Optional<Defines> shaderDefines, Optional<List<String>> samplers, Optional<List<RenderPipeline.UniformDescription>> uniforms, Optional<BlendFunction> blendFunction, Optional<DepthTestFunction> depthTestFunction, Optional<PolygonMode> polygonMode, Optional<Boolean> cull, Optional<Boolean> writeColor, Optional<Boolean> writeAlpha, Optional<Boolean> writeDepth, Optional<LogicOp> colorLogic, Optional<VertexFormat> vertexFormat, Optional<VertexFormat.DrawMode> vertexFormatMode) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        this.shaderDefines = shaderDefines;
        this.samplers = samplers;
        this.uniforms = uniforms;
        this.blendFunction = blendFunction;
        this.depthTestFunction = depthTestFunction;
        this.polygonMode = polygonMode;
        this.cull = cull;
        this.writeColor = writeColor;
        this.writeAlpha = writeAlpha;
        this.writeDepth = writeDepth;
        this.colorLogic = colorLogic;
        this.vertexFormat = vertexFormat;
        this.vertexFormatMode = vertexFormatMode;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RenderPipeline.Snippet.class, "vertexShader;fragmentShader;shaderDefines;samplers;uniforms;blendFunction;depthTestFunction;polygonMode;cull;writeColor;writeAlpha;writeDepth;colorLogic;vertexFormat;vertexFormatMode", "vertexShader", "fragmentShader", "shaderDefines", "samplers", "uniforms", "blendFunction", "depthTestFunction", "polygonMode", "cull", "writeColor", "writeAlpha", "writeDepth", "colorLogic", "vertexFormat", "vertexFormatMode"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RenderPipeline.Snippet.class, "vertexShader;fragmentShader;shaderDefines;samplers;uniforms;blendFunction;depthTestFunction;polygonMode;cull;writeColor;writeAlpha;writeDepth;colorLogic;vertexFormat;vertexFormatMode", "vertexShader", "fragmentShader", "shaderDefines", "samplers", "uniforms", "blendFunction", "depthTestFunction", "polygonMode", "cull", "writeColor", "writeAlpha", "writeDepth", "colorLogic", "vertexFormat", "vertexFormatMode"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RenderPipeline.Snippet.class, "vertexShader;fragmentShader;shaderDefines;samplers;uniforms;blendFunction;depthTestFunction;polygonMode;cull;writeColor;writeAlpha;writeDepth;colorLogic;vertexFormat;vertexFormatMode", "vertexShader", "fragmentShader", "shaderDefines", "samplers", "uniforms", "blendFunction", "depthTestFunction", "polygonMode", "cull", "writeColor", "writeAlpha", "writeDepth", "colorLogic", "vertexFormat", "vertexFormatMode"}, this, object);
    }

    public Optional<Identifier> vertexShader() {
        return this.vertexShader;
    }

    public Optional<Identifier> fragmentShader() {
        return this.fragmentShader;
    }

    public Optional<Defines> shaderDefines() {
        return this.shaderDefines;
    }

    public Optional<List<String>> samplers() {
        return this.samplers;
    }

    public Optional<List<RenderPipeline.UniformDescription>> uniforms() {
        return this.uniforms;
    }

    public Optional<BlendFunction> blendFunction() {
        return this.blendFunction;
    }

    public Optional<DepthTestFunction> depthTestFunction() {
        return this.depthTestFunction;
    }

    public Optional<PolygonMode> polygonMode() {
        return this.polygonMode;
    }

    public Optional<Boolean> cull() {
        return this.cull;
    }

    public Optional<Boolean> writeColor() {
        return this.writeColor;
    }

    public Optional<Boolean> writeAlpha() {
        return this.writeAlpha;
    }

    public Optional<Boolean> writeDepth() {
        return this.writeDepth;
    }

    public Optional<LogicOp> colorLogic() {
        return this.colorLogic;
    }

    public Optional<VertexFormat> vertexFormat() {
        return this.vertexFormat;
    }

    public Optional<VertexFormat.DrawMode> vertexFormatMode() {
        return this.vertexFormatMode;
    }
}
