package net.minecraft.client.gl;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.lwjgl.opengl.GL31;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ShaderProgram implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static Set PREDEFINED_UNIFORMS = Sets.newHashSet(new String[]{"Projection", "Lighting", "Fog", "Globals"});
   public static ShaderProgram INVALID = new ShaderProgram(-1, "invalid");
   private final Map uniformsByName = new HashMap();
   private final int glRef;
   private final String debugLabel;

   private ShaderProgram(int glRef, String debugLabel) {
      this.glRef = glRef;
      this.debugLabel = debugLabel;
   }

   public static ShaderProgram create(CompiledShader vertexShader, CompiledShader fragmentShader, VertexFormat format, String name) throws ShaderLoader.LoadException {
      int i = GlStateManager.glCreateProgram();
      if (i <= 0) {
         throw new ShaderLoader.LoadException("Could not create shader program (returned program ID " + i + ")");
      } else {
         int j = 0;

         String string;
         for(Iterator var6 = format.getElementAttributeNames().iterator(); var6.hasNext(); ++j) {
            string = (String)var6.next();
            GlStateManager._glBindAttribLocation(i, j, string);
         }

         GlStateManager.glAttachShader(i, vertexShader.getHandle());
         GlStateManager.glAttachShader(i, fragmentShader.getHandle());
         GlStateManager.glLinkProgram(i);
         int k = GlStateManager.glGetProgrami(i, 35714);
         string = GlStateManager.glGetProgramInfoLog(i, 32768);
         if (k != 0 && !string.contains("Failed for unknown reason")) {
            if (!string.isEmpty()) {
               LOGGER.info("Info log when linking program containing VS {} and FS {}. Log output: {}", new Object[]{vertexShader.getId(), fragmentShader.getId(), string});
            }

            return new ShaderProgram(i, name);
         } else {
            String var10002 = String.valueOf(vertexShader.getId());
            throw new ShaderLoader.LoadException("Error encountered when linking program containing VS " + var10002 + " and FS " + String.valueOf(fragmentShader.getId()) + ". Log output: " + string);
         }
      }
   }

   public void set(List uniforms, List samplers) {
      int i = 0;
      int j = 0;
      Iterator var5 = uniforms.iterator();

      String string;
      while(var5.hasNext()) {
         RenderPipeline.UniformDescription uniformDescription = (RenderPipeline.UniformDescription)var5.next();
         string = uniformDescription.name();
         int k;
         int l;
         Object var10000;
         switch (uniformDescription.type()) {
            case UNIFORM_BUFFER:
               k = GL31.glGetUniformBlockIndex(this.glRef, string);
               if (k == -1) {
                  var10000 = null;
               } else {
                  l = i++;
                  GL31.glUniformBlockBinding(this.glRef, k, l);
                  var10000 = new GlUniform.UniformBuffer(l);
               }
               break;
            case TEXEL_BUFFER:
               k = GlStateManager._glGetUniformLocation(this.glRef, string);
               if (k == -1) {
                  LOGGER.warn("{} shader program does not use utb {} defined in the pipeline. This might be a bug.", this.debugLabel, string);
                  var10000 = null;
               } else {
                  l = j++;
                  var10000 = new GlUniform.TexelBuffer(k, l, (TextureFormat)Objects.requireNonNull(uniformDescription.textureFormat()));
               }
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         GlUniform glUniform = var10000;
         if (glUniform != null) {
            this.uniformsByName.put(string, glUniform);
         }
      }

      var5 = samplers.iterator();

      int n;
      while(var5.hasNext()) {
         String string2 = (String)var5.next();
         int m = GlStateManager._glGetUniformLocation(this.glRef, string2);
         if (m == -1) {
            LOGGER.warn("{} shader program does not use sampler {} defined in the pipeline. This might be a bug.", this.debugLabel, string2);
         } else {
            n = j++;
            this.uniformsByName.put(string2, new GlUniform.Sampler(m, n));
         }
      }

      int o = GlStateManager.glGetProgrami(this.glRef, 35382);

      for(int p = 0; p < o; ++p) {
         string = GL31.glGetActiveUniformBlockName(this.glRef, p);
         if (!this.uniformsByName.containsKey(string)) {
            if (!samplers.contains(string) && PREDEFINED_UNIFORMS.contains(string)) {
               n = i++;
               GL31.glUniformBlockBinding(this.glRef, p, n);
               this.uniformsByName.put(string, new GlUniform.UniformBuffer(n));
            } else {
               LOGGER.warn("Found unknown and unsupported uniform {} in {}", string, this.debugLabel);
            }
         }
      }

   }

   public void close() {
      this.uniformsByName.values().forEach(GlUniform::close);
      GlStateManager.glDeleteProgram(this.glRef);
   }

   @Nullable
   public GlUniform getUniform(String name) {
      RenderSystem.assertOnRenderThread();
      return (GlUniform)this.uniformsByName.get(name);
   }

   @VisibleForTesting
   public int getGlRef() {
      return this.glRef;
   }

   public String toString() {
      return this.debugLabel;
   }

   public String getDebugLabel() {
      return this.debugLabel;
   }

   public Map getUniforms() {
      return this.uniformsByName;
   }
}
