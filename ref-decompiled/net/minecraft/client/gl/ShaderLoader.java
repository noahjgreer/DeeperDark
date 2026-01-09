package net.minecraft.client.gl;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.ProjectionMatrix2;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.path.PathUtil;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ShaderLoader extends SinglePreparationResourceReloader implements AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final int field_53936 = 32768;
   public static final String SHADERS_PATH = "shaders";
   private static final String INCLUDE_PATH = "shaders/include/";
   private static final ResourceFinder POST_EFFECT_FINDER = ResourceFinder.json("post_effect");
   final TextureManager textureManager;
   private final Consumer onError;
   private Cache cache;
   final ProjectionMatrix2 projectionMatrix;

   public ShaderLoader(TextureManager textureManager, Consumer onError) {
      this.cache = new Cache(ShaderLoader.Definitions.EMPTY);
      this.projectionMatrix = new ProjectionMatrix2("post", 0.1F, 1000.0F, false);
      this.textureManager = textureManager;
      this.onError = onError;
   }

   protected Definitions prepare(ResourceManager resourceManager, Profiler profiler) {
      ImmutableMap.Builder builder = ImmutableMap.builder();
      Map map = resourceManager.findResources("shaders", ShaderLoader::isShaderSource);
      Iterator var5 = map.entrySet().iterator();

      while(var5.hasNext()) {
         Map.Entry entry = (Map.Entry)var5.next();
         Identifier identifier = (Identifier)entry.getKey();
         ShaderType shaderType = ShaderType.byLocation(identifier);
         if (shaderType != null) {
            loadShaderSource(identifier, (Resource)entry.getValue(), shaderType, map, builder);
         }
      }

      ImmutableMap.Builder builder2 = ImmutableMap.builder();
      Iterator var10 = POST_EFFECT_FINDER.findResources(resourceManager).entrySet().iterator();

      while(var10.hasNext()) {
         Map.Entry entry2 = (Map.Entry)var10.next();
         loadPostEffect((Identifier)entry2.getKey(), (Resource)entry2.getValue(), builder2);
      }

      return new Definitions(builder.build(), builder2.build());
   }

   private static void loadShaderSource(Identifier id, Resource resource, ShaderType type, Map allResources, ImmutableMap.Builder builder) {
      Identifier identifier = type.idConverter().toResourceId(id);
      GlImportProcessor glImportProcessor = createImportProcessor(allResources, id);

      try {
         Reader reader = resource.getReader();

         try {
            String string = IOUtils.toString(reader);
            builder.put(new ShaderSourceKey(identifier, type), String.join("", glImportProcessor.readSource(string)));
         } catch (Throwable var11) {
            if (reader != null) {
               try {
                  reader.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (reader != null) {
            reader.close();
         }
      } catch (IOException var12) {
         LOGGER.error("Failed to load shader source at {}", id, var12);
      }

   }

   private static GlImportProcessor createImportProcessor(final Map allResources, Identifier id) {
      final Identifier identifier = id.withPath(PathUtil::getPosixFullPath);
      return new GlImportProcessor() {
         private final Set processed = new ObjectArraySet();

         public String loadImport(boolean inline, String name) {
            Identifier identifierx;
            try {
               if (inline) {
                  identifierx = identifier.withPath((path) -> {
                     return PathUtil.normalizeToPosix(path + name);
                  });
               } else {
                  identifierx = Identifier.of(name).withPrefixedPath("shaders/include/");
               }
            } catch (InvalidIdentifierException var8) {
               ShaderLoader.LOGGER.error("Malformed GLSL import {}: {}", name, var8.getMessage());
               return "#error " + var8.getMessage();
            }

            if (!this.processed.add(identifierx)) {
               return null;
            } else {
               try {
                  Reader reader = ((Resource)allResources.get(identifierx)).getReader();

                  String var5;
                  try {
                     var5 = IOUtils.toString(reader);
                  } catch (Throwable var9) {
                     if (reader != null) {
                        try {
                           reader.close();
                        } catch (Throwable var7) {
                           var9.addSuppressed(var7);
                        }
                     }

                     throw var9;
                  }

                  if (reader != null) {
                     reader.close();
                  }

                  return var5;
               } catch (IOException var10) {
                  ShaderLoader.LOGGER.error("Could not open GLSL import {}: {}", identifierx, var10.getMessage());
                  return "#error " + var10.getMessage();
               }
            }
         }
      };
   }

   private static void loadPostEffect(Identifier id, Resource resource, ImmutableMap.Builder builder) {
      Identifier identifier = POST_EFFECT_FINDER.toResourceId(id);

      try {
         Reader reader = resource.getReader();

         try {
            JsonElement jsonElement = StrictJsonParser.parse((Reader)reader);
            builder.put(identifier, (PostEffectPipeline)PostEffectPipeline.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonSyntaxException::new));
         } catch (Throwable var8) {
            if (reader != null) {
               try {
                  reader.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (reader != null) {
            reader.close();
         }
      } catch (JsonParseException | IOException var9) {
         LOGGER.error("Failed to parse post chain at {}", id, var9);
      }

   }

   private static boolean isShaderSource(Identifier id) {
      return ShaderType.byLocation(id) != null || id.getPath().endsWith(".glsl");
   }

   protected void apply(Definitions definitions, ResourceManager resourceManager, Profiler profiler) {
      Cache cache = new Cache(definitions);
      Set set = new HashSet(RenderPipelines.getAll());
      List list = new ArrayList();
      GpuDevice gpuDevice = RenderSystem.getDevice();
      gpuDevice.clearPipelineCache();
      Iterator var8 = set.iterator();

      while(var8.hasNext()) {
         RenderPipeline renderPipeline = (RenderPipeline)var8.next();
         Objects.requireNonNull(cache);
         CompiledRenderPipeline compiledRenderPipeline = gpuDevice.precompilePipeline(renderPipeline, cache::getSource);
         if (!compiledRenderPipeline.isValid()) {
            list.add(renderPipeline.getLocation());
         }
      }

      if (!list.isEmpty()) {
         gpuDevice.clearPipelineCache();
         Stream var10002 = list.stream().map((identifier) -> {
            return " - " + String.valueOf(identifier);
         });
         throw new RuntimeException("Failed to load required shader programs:\n" + (String)var10002.collect(Collectors.joining("\n")));
      } else {
         this.cache.close();
         this.cache = cache;
      }
   }

   public String getName() {
      return "Shader Loader";
   }

   private void handleError(Exception exception) {
      if (!this.cache.errorHandled) {
         this.onError.accept(exception);
         this.cache.errorHandled = true;
      }
   }

   @Nullable
   public PostEffectProcessor loadPostEffect(Identifier id, Set availableExternalTargets) {
      try {
         return this.cache.getOrLoadProcessor(id, availableExternalTargets);
      } catch (LoadException var4) {
         LOGGER.error("Failed to load post chain: {}", id, var4);
         this.cache.postEffectProcessors.put(id, Optional.empty());
         this.handleError(var4);
         return null;
      }
   }

   public void close() {
      this.cache.close();
      this.projectionMatrix.close();
   }

   public String getSource(Identifier id, ShaderType type) {
      return this.cache.getSource(id, type);
   }

   // $FF: synthetic method
   protected Object prepare(final ResourceManager manager, final Profiler profiler) {
      return this.prepare(manager, profiler);
   }

   @Environment(EnvType.CLIENT)
   class Cache implements AutoCloseable {
      private final Definitions definitions;
      final Map postEffectProcessors = new HashMap();
      boolean errorHandled;

      Cache(final Definitions definitions) {
         this.definitions = definitions;
      }

      @Nullable
      public PostEffectProcessor getOrLoadProcessor(Identifier id, Set availableExternalTargets) throws LoadException {
         Optional optional = (Optional)this.postEffectProcessors.get(id);
         if (optional != null) {
            return (PostEffectProcessor)optional.orElse((Object)null);
         } else {
            PostEffectProcessor postEffectProcessor = this.loadProcessor(id, availableExternalTargets);
            this.postEffectProcessors.put(id, Optional.of(postEffectProcessor));
            return postEffectProcessor;
         }
      }

      private PostEffectProcessor loadProcessor(Identifier id, Set availableExternalTargets) throws LoadException {
         PostEffectPipeline postEffectPipeline = (PostEffectPipeline)this.definitions.postChains.get(id);
         if (postEffectPipeline == null) {
            throw new LoadException("Could not find post chain with id: " + String.valueOf(id));
         } else {
            return PostEffectProcessor.parseEffect(postEffectPipeline, ShaderLoader.this.textureManager, availableExternalTargets, id, ShaderLoader.this.projectionMatrix);
         }
      }

      public void close() {
         this.postEffectProcessors.values().forEach((processor) -> {
            processor.ifPresent(PostEffectProcessor::close);
         });
         this.postEffectProcessors.clear();
      }

      public String getSource(Identifier id, ShaderType type) {
         return (String)this.definitions.shaderSources.get(new ShaderSourceKey(id, type));
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Definitions(Map shaderSources, Map postChains) {
      final Map shaderSources;
      final Map postChains;
      public static final Definitions EMPTY = new Definitions(Map.of(), Map.of());

      public Definitions(Map map, Map map2) {
         this.shaderSources = map;
         this.postChains = map2;
      }

      public Map shaderSources() {
         return this.shaderSources;
      }

      public Map postChains() {
         return this.postChains;
      }
   }

   @Environment(EnvType.CLIENT)
   private static record ShaderSourceKey(Identifier id, ShaderType type) {
      ShaderSourceKey(Identifier identifier, ShaderType shaderType) {
         this.id = identifier;
         this.type = shaderType;
      }

      public String toString() {
         String var10000 = String.valueOf(this.id);
         return var10000 + " (" + String.valueOf(this.type) + ")";
      }

      public Identifier id() {
         return this.id;
      }

      public ShaderType type() {
         return this.type;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class LoadException extends Exception {
      public LoadException(String message) {
         super(message);
      }
   }
}
