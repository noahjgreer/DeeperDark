package net.minecraft.client.gl;

import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record CompiledShaderPipeline(RenderPipeline info, ShaderProgram program) implements CompiledRenderPipeline {
   public CompiledShaderPipeline(RenderPipeline renderPipeline, ShaderProgram shaderProgram) {
      this.info = renderPipeline;
      this.program = shaderProgram;
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
