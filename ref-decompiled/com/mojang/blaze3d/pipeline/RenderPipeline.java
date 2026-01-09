package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.LogicOp;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.util.Identifier;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public class RenderPipeline {
   private final Identifier location;
   private final Identifier vertexShader;
   private final Identifier fragmentShader;
   private final Defines shaderDefines;
   private final List samplers;
   private final List uniforms;
   private final DepthTestFunction depthTestFunction;
   private final PolygonMode polygonMode;
   private final boolean cull;
   private final LogicOp colorLogic;
   private final Optional blendFunction;
   private final boolean writeColor;
   private final boolean writeAlpha;
   private final boolean writeDepth;
   private final VertexFormat vertexFormat;
   private final VertexFormat.DrawMode vertexFormatMode;
   private final float depthBiasScaleFactor;
   private final float depthBiasConstant;
   private final int sortKey;
   private static int sortKeySeed;

   protected RenderPipeline(Identifier location, Identifier vertexShader, Identifier fragmentShader, Defines shaderDefines, List samplers, List uniforms, Optional blendFunction, DepthTestFunction depthTestFunction, PolygonMode polygonMode, boolean cull, boolean writeColor, boolean writeAlpha, boolean writeDepth, LogicOp colorLogic, VertexFormat vertexFormat, VertexFormat.DrawMode vertexFormatMode, float depthBiasScaleFactor, float depthBiasConstant, int sortKey) {
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
      return this.sortKey;
   }

   public static void updateSortKeySeed() {
      sortKeySeed = Math.round(100000.0F * (float)Math.random());
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

   public Optional getBlendFunction() {
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

   public List getSamplers() {
      return this.samplers;
   }

   public List getUniforms() {
      return this.uniforms;
   }

   public boolean wantsDepthTexture() {
      return this.depthTestFunction != DepthTestFunction.NO_DEPTH_TEST || this.depthBiasConstant != 0.0F || this.depthBiasScaleFactor != 0.0F || this.writeDepth;
   }

   public static Builder builder(Snippet... snippets) {
      Builder builder = new Builder();
      Snippet[] var2 = snippets;
      int var3 = snippets.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Snippet snippet = var2[var4];
         builder.withSnippet(snippet);
      }

      return builder;
   }

   @Environment(EnvType.CLIENT)
   @DeobfuscateClass
   public static class Builder {
      private static int nextPipelineSortKey;
      private Optional location = Optional.empty();
      private Optional fragmentShader = Optional.empty();
      private Optional vertexShader = Optional.empty();
      private Optional definesBuilder = Optional.empty();
      private Optional samplers = Optional.empty();
      private Optional uniforms = Optional.empty();
      private Optional depthTestFunction = Optional.empty();
      private Optional polygonMode = Optional.empty();
      private Optional cull = Optional.empty();
      private Optional writeColor = Optional.empty();
      private Optional writeAlpha = Optional.empty();
      private Optional writeDepth = Optional.empty();
      private Optional colorLogic = Optional.empty();
      private Optional blendFunction = Optional.empty();
      private Optional vertexFormat = Optional.empty();
      private Optional vertexFormatMode = Optional.empty();
      private float depthBiasScaleFactor;
      private float depthBiasConstant;

      Builder() {
      }

      public Builder withLocation(String location) {
         this.location = Optional.of(Identifier.ofVanilla(location));
         return this;
      }

      public Builder withLocation(Identifier location) {
         this.location = Optional.of(location);
         return this;
      }

      public Builder withFragmentShader(String fragmentShader) {
         this.fragmentShader = Optional.of(Identifier.ofVanilla(fragmentShader));
         return this;
      }

      public Builder withFragmentShader(Identifier fragmentShader) {
         this.fragmentShader = Optional.of(fragmentShader);
         return this;
      }

      public Builder withVertexShader(String string) {
         this.vertexShader = Optional.of(Identifier.ofVanilla(string));
         return this;
      }

      public Builder withVertexShader(Identifier vertexShader) {
         this.vertexShader = Optional.of(vertexShader);
         return this;
      }

      public Builder withShaderDefine(String flag) {
         if (this.definesBuilder.isEmpty()) {
            this.definesBuilder = Optional.of(Defines.builder());
         }

         ((Defines.Builder)this.definesBuilder.get()).flag(flag);
         return this;
      }

      public Builder withShaderDefine(String name, int value) {
         if (this.definesBuilder.isEmpty()) {
            this.definesBuilder = Optional.of(Defines.builder());
         }

         ((Defines.Builder)this.definesBuilder.get()).define(name, value);
         return this;
      }

      public Builder withShaderDefine(String name, float value) {
         if (this.definesBuilder.isEmpty()) {
            this.definesBuilder = Optional.of(Defines.builder());
         }

         ((Defines.Builder)this.definesBuilder.get()).define(name, value);
         return this;
      }

      public Builder withSampler(String sampler) {
         if (this.samplers.isEmpty()) {
            this.samplers = Optional.of(new ArrayList());
         }

         ((List)this.samplers.get()).add(sampler);
         return this;
      }

      public Builder withUniform(String name, UniformType type) {
         if (this.uniforms.isEmpty()) {
            this.uniforms = Optional.of(new ArrayList());
         }

         if (type == UniformType.TEXEL_BUFFER) {
            throw new IllegalArgumentException("Cannot use texel buffer without specifying texture format");
         } else {
            ((List)this.uniforms.get()).add(new UniformDescription(name, type));
            return this;
         }
      }

      public Builder withUniform(String name, UniformType type, TextureFormat format) {
         if (this.uniforms.isEmpty()) {
            this.uniforms = Optional.of(new ArrayList());
         }

         if (type != UniformType.TEXEL_BUFFER) {
            throw new IllegalArgumentException("Only texel buffer can specify texture format");
         } else {
            ((List)this.uniforms.get()).add(new UniformDescription(name, format));
            return this;
         }
      }

      public Builder withDepthTestFunction(DepthTestFunction depthTestFunction) {
         this.depthTestFunction = Optional.of(depthTestFunction);
         return this;
      }

      public Builder withPolygonMode(PolygonMode polygonMode) {
         this.polygonMode = Optional.of(polygonMode);
         return this;
      }

      public Builder withCull(boolean cull) {
         this.cull = Optional.of(cull);
         return this;
      }

      public Builder withBlend(BlendFunction blendFunction) {
         this.blendFunction = Optional.of(blendFunction);
         return this;
      }

      public Builder withoutBlend() {
         this.blendFunction = Optional.empty();
         return this;
      }

      public Builder withColorWrite(boolean writeColor) {
         this.writeColor = Optional.of(writeColor);
         this.writeAlpha = Optional.of(writeColor);
         return this;
      }

      public Builder withColorWrite(boolean writeColor, boolean writeAlpha) {
         this.writeColor = Optional.of(writeColor);
         this.writeAlpha = Optional.of(writeAlpha);
         return this;
      }

      public Builder withDepthWrite(boolean writeDepth) {
         this.writeDepth = Optional.of(writeDepth);
         return this;
      }

      /** @deprecated */
      @Deprecated
      public Builder withColorLogic(LogicOp colorLogic) {
         this.colorLogic = Optional.of(colorLogic);
         return this;
      }

      public Builder withVertexFormat(VertexFormat vertexFormat, VertexFormat.DrawMode vertexFormatMode) {
         this.vertexFormat = Optional.of(vertexFormat);
         this.vertexFormatMode = Optional.of(vertexFormatMode);
         return this;
      }

      public Builder withDepthBias(float depthBiasScaleFactor, float depthBiasConstant) {
         this.depthBiasScaleFactor = depthBiasScaleFactor;
         this.depthBiasConstant = depthBiasConstant;
         return this;
      }

      void withSnippet(Snippet snippet) {
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

            Defines defines = (Defines)snippet.shaderDefines.get();
            Iterator var3 = defines.values().entrySet().iterator();

            while(var3.hasNext()) {
               Map.Entry entry = (Map.Entry)var3.next();
               ((Defines.Builder)this.definesBuilder.get()).define((String)entry.getKey(), (String)entry.getValue());
            }

            var3 = defines.flags().iterator();

            while(var3.hasNext()) {
               String string = (String)var3.next();
               ((Defines.Builder)this.definesBuilder.get()).flag(string);
            }
         }

         snippet.samplers.ifPresent((samplers) -> {
            if (this.samplers.isPresent()) {
               ((List)this.samplers.get()).addAll(samplers);
            } else {
               this.samplers = Optional.of(new ArrayList(samplers));
            }

         });
         snippet.uniforms.ifPresent((uniforms) -> {
            if (this.uniforms.isPresent()) {
               ((List)this.uniforms.get()).addAll(uniforms);
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

      public Snippet buildSnippet() {
         return new Snippet(this.vertexShader, this.fragmentShader, this.definesBuilder.map(Defines.Builder::build), this.samplers.map(Collections::unmodifiableList), this.uniforms.map(Collections::unmodifiableList), this.blendFunction, this.depthTestFunction, this.polygonMode, this.cull, this.writeColor, this.writeAlpha, this.writeDepth, this.colorLogic, this.vertexFormat, this.vertexFormatMode);
      }

      public RenderPipeline build() {
         if (this.location.isEmpty()) {
            throw new IllegalStateException("Missing location");
         } else if (this.vertexShader.isEmpty()) {
            throw new IllegalStateException("Missing vertex shader");
         } else if (this.fragmentShader.isEmpty()) {
            throw new IllegalStateException("Missing fragment shader");
         } else if (this.vertexFormat.isEmpty()) {
            throw new IllegalStateException("Missing vertex buffer format");
         } else if (this.vertexFormatMode.isEmpty()) {
            throw new IllegalStateException("Missing vertex mode");
         } else {
            return new RenderPipeline((Identifier)this.location.get(), (Identifier)this.vertexShader.get(), (Identifier)this.fragmentShader.get(), ((Defines.Builder)this.definesBuilder.orElse(Defines.builder())).build(), List.copyOf((Collection)this.samplers.orElse(new ArrayList())), (List)this.uniforms.orElse(Collections.emptyList()), this.blendFunction, (DepthTestFunction)this.depthTestFunction.orElse(DepthTestFunction.LEQUAL_DEPTH_TEST), (PolygonMode)this.polygonMode.orElse(PolygonMode.FILL), (Boolean)this.cull.orElse(true), (Boolean)this.writeColor.orElse(true), (Boolean)this.writeAlpha.orElse(true), (Boolean)this.writeDepth.orElse(true), (LogicOp)this.colorLogic.orElse(LogicOp.NONE), (VertexFormat)this.vertexFormat.get(), (VertexFormat.DrawMode)this.vertexFormatMode.get(), this.depthBiasScaleFactor, this.depthBiasConstant, nextPipelineSortKey++);
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @DeobfuscateClass
   public static record Snippet(Optional vertexShader, Optional fragmentShader, Optional shaderDefines, Optional samplers, Optional uniforms, Optional blendFunction, Optional depthTestFunction, Optional polygonMode, Optional cull, Optional writeColor, Optional writeAlpha, Optional writeDepth, Optional colorLogic, Optional vertexFormat, Optional vertexFormatMode) {
      final Optional vertexShader;
      final Optional fragmentShader;
      final Optional shaderDefines;
      final Optional samplers;
      final Optional uniforms;
      final Optional blendFunction;
      final Optional depthTestFunction;
      final Optional cull;
      final Optional writeColor;
      final Optional writeAlpha;
      final Optional writeDepth;
      final Optional colorLogic;
      final Optional vertexFormat;
      final Optional vertexFormatMode;

      public Snippet(Optional optional, Optional optional2, Optional optional3, Optional optional4, Optional optional5, Optional optional6, Optional optional7, Optional optional8, Optional optional9, Optional optional10, Optional optional11, Optional optional12, Optional optional13, Optional optional14, Optional optional15) {
         this.vertexShader = optional;
         this.fragmentShader = optional2;
         this.shaderDefines = optional3;
         this.samplers = optional4;
         this.uniforms = optional5;
         this.blendFunction = optional6;
         this.depthTestFunction = optional7;
         this.polygonMode = optional8;
         this.cull = optional9;
         this.writeColor = optional10;
         this.writeAlpha = optional11;
         this.writeDepth = optional12;
         this.colorLogic = optional13;
         this.vertexFormat = optional14;
         this.vertexFormatMode = optional15;
      }

      public Optional vertexShader() {
         return this.vertexShader;
      }

      public Optional fragmentShader() {
         return this.fragmentShader;
      }

      public Optional shaderDefines() {
         return this.shaderDefines;
      }

      public Optional samplers() {
         return this.samplers;
      }

      public Optional uniforms() {
         return this.uniforms;
      }

      public Optional blendFunction() {
         return this.blendFunction;
      }

      public Optional depthTestFunction() {
         return this.depthTestFunction;
      }

      public Optional polygonMode() {
         return this.polygonMode;
      }

      public Optional cull() {
         return this.cull;
      }

      public Optional writeColor() {
         return this.writeColor;
      }

      public Optional writeAlpha() {
         return this.writeAlpha;
      }

      public Optional writeDepth() {
         return this.writeDepth;
      }

      public Optional colorLogic() {
         return this.colorLogic;
      }

      public Optional vertexFormat() {
         return this.vertexFormat;
      }

      public Optional vertexFormatMode() {
         return this.vertexFormatMode;
      }
   }

   @Environment(EnvType.CLIENT)
   @DeobfuscateClass
   public static record UniformDescription(String name, UniformType type, @Nullable TextureFormat textureFormat) {
      public UniformDescription(String name, UniformType type) {
         this(name, type, (TextureFormat)null);
         if (type == UniformType.TEXEL_BUFFER) {
            throw new IllegalArgumentException("Texel buffer needs a texture format");
         }
      }

      public UniformDescription(String name, TextureFormat format) {
         this(name, UniformType.TEXEL_BUFFER, format);
      }

      public UniformDescription(String string, UniformType uniformType, @Nullable TextureFormat textureFormat) {
         this.name = string;
         this.type = uniformType;
         this.textureFormat = textureFormat;
      }

      public String name() {
         return this.name;
      }

      public UniformType type() {
         return this.type;
      }

      @Nullable
      public TextureFormat textureFormat() {
         return this.textureFormat;
      }
   }
}
