/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.CompiledRenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.CompiledShaderPipeline
 *  net.minecraft.client.gl.ShaderProgram
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.ShaderProgram;

@Environment(value=EnvType.CLIENT)
public record CompiledShaderPipeline(RenderPipeline info, ShaderProgram program) implements CompiledRenderPipeline
{
    private final RenderPipeline info;
    private final ShaderProgram program;

    public CompiledShaderPipeline(RenderPipeline info, ShaderProgram program) {
        this.info = info;
        this.program = program;
    }

    public boolean isValid() {
        return this.program != ShaderProgram.INVALID;
    }

    public RenderPipeline info() {
        return this.info;
    }

    public ShaderProgram program() {
        return this.program;
    }
}

