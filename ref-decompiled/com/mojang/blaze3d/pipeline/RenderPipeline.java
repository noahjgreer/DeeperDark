/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Builder
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.pipeline.RenderPipeline$UniformDescription
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.platform.LogicOp
 *  com.mojang.blaze3d.platform.PolygonMode
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$DrawMode
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.FabricRenderPipeline
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.gl.Defines
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.annotation.DeobfuscateClass
 */
package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.LogicOp;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderPipeline;
import net.minecraft.SharedConstants;
import net.minecraft.client.gl.Defines;
import net.minecraft.util.Identifier;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public class RenderPipeline
implements FabricRenderPipeline {
    private final Identifier location;
    private final Identifier vertexShader;
    private final Identifier fragmentShader;
    private final Defines shaderDefines;
    private final List<String> samplers;
    private final List<UniformDescription> uniforms;
    private final DepthTestFunction depthTestFunction;
    private final PolygonMode polygonMode;
    private final boolean cull;
    private final LogicOp colorLogic;
    private final Optional<BlendFunction> blendFunction;
    private final boolean writeColor;
    private final boolean writeAlpha;
    private final boolean writeDepth;
    private final VertexFormat vertexFormat;
    private final VertexFormat.DrawMode vertexFormatMode;
    private final float depthBiasScaleFactor;
    private final float depthBiasConstant;
    private final int sortKey;
    private static int sortKeySeed;

    protected RenderPipeline(Identifier location, Identifier vertexShader, Identifier fragmentShader, Defines shaderDefines, List<String> samplers, List<UniformDescription> uniforms, Optional<BlendFunction> blendFunction, DepthTestFunction depthTestFunction, PolygonMode polygonMode, boolean cull, boolean writeColor, boolean writeAlpha, boolean writeDepth, LogicOp colorLogic, VertexFormat vertexFormat, VertexFormat.DrawMode vertexFormatMode, float depthBiasScaleFactor, float depthBiasConstant, int sortKey) {
        this.location = location;
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        this.shaderDefines = shaderDefines;
        this.samplers = samplers;
        this.uniforms = uniforms;
        this.depthTestFunction = depthTestFunction;
        this.polygonMode = polygonMode;
        this.cull = cull;
        this.blendFunction = blendFunction;
        this.writeColor = writeColor;
        this.writeAlpha = writeAlpha;
        this.writeDepth = writeDepth;
        this.colorLogic = colorLogic;
        this.vertexFormat = vertexFormat;
        this.vertexFormatMode = vertexFormatMode;
        this.depthBiasScaleFactor = depthBiasScaleFactor;
        this.depthBiasConstant = depthBiasConstant;
        this.sortKey = sortKey;
    }

    public int getSortKey() {
        return SharedConstants.SHUFFLE_UI_RENDERING_ORDER ? super.hashCode() * (sortKeySeed + 1) : this.sortKey;
    }

    public static void updateSortKeySeed() {
        sortKeySeed = Math.round(100000.0f * (float)Math.random());
    }

    public String toString() {
        return this.location.toString();
    }

    public DepthTestFunction getDepthTestFunction() {
        return this.depthTestFunction;
    }

    public PolygonMode getPolygonMode() {
        return this.polygonMode;
    }

    public boolean isCull() {
        return this.cull;
    }

    public LogicOp getColorLogic() {
        return this.colorLogic;
    }

    public Optional<BlendFunction> getBlendFunction() {
        return this.blendFunction;
    }

    public boolean isWriteColor() {
        return this.writeColor;
    }

    public boolean isWriteAlpha() {
        return this.writeAlpha;
    }

    public boolean isWriteDepth() {
        return this.writeDepth;
    }

    public float getDepthBiasScaleFactor() {
        return this.depthBiasScaleFactor;
    }

    public float getDepthBiasConstant() {
        return this.depthBiasConstant;
    }

    public Identifier getLocation() {
        return this.location;
    }

    public VertexFormat getVertexFormat() {
        return this.vertexFormat;
    }

    public VertexFormat.DrawMode getVertexFormatMode() {
        return this.vertexFormatMode;
    }

    public Identifier getVertexShader() {
        return this.vertexShader;
    }

    public Identifier getFragmentShader() {
        return this.fragmentShader;
    }

    public Defines getShaderDefines() {
        return this.shaderDefines;
    }

    public List<String> getSamplers() {
        return this.samplers;
    }

    public List<UniformDescription> getUniforms() {
        return this.uniforms;
    }

    public boolean wantsDepthTexture() {
        return this.depthTestFunction != DepthTestFunction.NO_DEPTH_TEST || this.depthBiasConstant != 0.0f || this.depthBiasScaleFactor != 0.0f || this.writeDepth;
    }

    public static Builder builder(Snippet ... snippets) {
        Builder builder = new Builder();
        for (Snippet snippet : snippets) {
            builder.withSnippet(snippet);
        }
        return builder;
    }
}

