/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.blaze3d.pipeline.CompiledRenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.shaders.ShaderType
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.GlImportProcessor
 *  net.minecraft.client.gl.PostEffectPipeline
 *  net.minecraft.client.gl.PostEffectProcessor
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gl.ShaderLoader
 *  net.minecraft.client.gl.ShaderLoader$Cache
 *  net.minecraft.client.gl.ShaderLoader$Definitions
 *  net.minecraft.client.gl.ShaderLoader$LoadException
 *  net.minecraft.client.gl.ShaderLoader$ShaderSourceKey
 *  net.minecraft.client.render.ProjectionMatrix2
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.resource.Resource
 *  net.minecraft.resource.ResourceFinder
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.resource.SinglePreparationResourceReloader
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.StrictJsonParser
 *  net.minecraft.util.path.PathUtil
 *  net.minecraft.util.profiler.Profiler
 *  org.apache.commons.io.IOUtils
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
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
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlImportProcessor;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.render.ProjectionMatrix2;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.path.PathUtil;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ShaderLoader
extends SinglePreparationResourceReloader<Definitions>
implements AutoCloseable {
    static final Logger LOGGER = LogUtils.getLogger();
    public static final int field_53936 = 32768;
    public static final String SHADERS_PATH = "shaders";
    private static final String INCLUDE_PATH = "shaders/include/";
    private static final ResourceFinder POST_EFFECT_FINDER = ResourceFinder.json((String)"post_effect");
    final TextureManager textureManager;
    private final Consumer<Exception> onError;
    private Cache cache = new Cache(this, Definitions.EMPTY);
    final ProjectionMatrix2 projectionMatrix = new ProjectionMatrix2("post", 0.1f, 1000.0f, false);

    public ShaderLoader(TextureManager textureManager, Consumer<Exception> onError) {
        this.textureManager = textureManager;
        this.onError = onError;
    }

    protected Definitions prepare(ResourceManager resourceManager, Profiler profiler) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        Map map = resourceManager.findResources("shaders", ShaderLoader::isShaderSource);
        for (Map.Entry entry : map.entrySet()) {
            Identifier identifier = (Identifier)entry.getKey();
            ShaderType shaderType = ShaderType.byLocation((Identifier)identifier);
            if (shaderType == null) continue;
            ShaderLoader.loadShaderSource((Identifier)identifier, (Resource)((Resource)entry.getValue()), (ShaderType)shaderType, (Map)map, (ImmutableMap.Builder)builder);
        }
        ImmutableMap.Builder builder2 = ImmutableMap.builder();
        for (Map.Entry entry2 : POST_EFFECT_FINDER.findResources(resourceManager).entrySet()) {
            ShaderLoader.loadPostEffect((Identifier)((Identifier)entry2.getKey()), (Resource)((Resource)entry2.getValue()), (ImmutableMap.Builder)builder2);
        }
        return new Definitions((Map)builder.build(), (Map)builder2.build());
    }

    private static void loadShaderSource(Identifier id, Resource resource, ShaderType type, Map<Identifier, Resource> allResources, ImmutableMap.Builder<ShaderSourceKey, String> builder) {
        Identifier identifier = type.idConverter().toResourceId(id);
        GlImportProcessor glImportProcessor = ShaderLoader.createImportProcessor(allResources, (Identifier)id);
        try (BufferedReader reader = resource.getReader();){
            String string = IOUtils.toString((Reader)reader);
            builder.put((Object)new ShaderSourceKey(identifier, type), (Object)String.join((CharSequence)"", glImportProcessor.readSource(string)));
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to load shader source at {}", (Object)id, (Object)iOException);
        }
    }

    private static GlImportProcessor createImportProcessor(Map<Identifier, Resource> allResources, Identifier id) {
        Identifier identifier = id.withPath(PathUtil::getPosixFullPath);
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    private static void loadPostEffect(Identifier id, Resource resource, ImmutableMap.Builder<Identifier, PostEffectPipeline> builder) {
        Identifier identifier = POST_EFFECT_FINDER.toResourceId(id);
        try (BufferedReader reader = resource.getReader();){
            JsonElement jsonElement = StrictJsonParser.parse((Reader)reader);
            builder.put((Object)identifier, (Object)((PostEffectPipeline)PostEffectPipeline.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement).getOrThrow(JsonSyntaxException::new)));
        }
        catch (JsonParseException | IOException exception) {
            LOGGER.error("Failed to parse post chain at {}", (Object)id, (Object)exception);
        }
    }

    private static boolean isShaderSource(Identifier id) {
        return ShaderType.byLocation((Identifier)id) != null || id.getPath().endsWith(".glsl");
    }

    protected void apply(Definitions definitions, ResourceManager resourceManager, Profiler profiler) {
        Cache cache = new Cache(this, definitions);
        HashSet set = new HashSet(RenderPipelines.getAll());
        ArrayList<Identifier> list = new ArrayList<Identifier>();
        GpuDevice gpuDevice = RenderSystem.getDevice();
        gpuDevice.clearPipelineCache();
        for (RenderPipeline renderPipeline : set) {
            CompiledRenderPipeline compiledRenderPipeline = gpuDevice.precompilePipeline(renderPipeline, (arg_0, arg_1) -> ((Cache)cache).getSource(arg_0, arg_1));
            if (compiledRenderPipeline.isValid()) continue;
            list.add(renderPipeline.getLocation());
        }
        if (!list.isEmpty()) {
            gpuDevice.clearPipelineCache();
            throw new RuntimeException("Failed to load required shader programs:\n" + list.stream().map(id -> " - " + String.valueOf(id)).collect(Collectors.joining("\n")));
        }
        this.cache.close();
        this.cache = cache;
    }

    public String getName() {
        return "Shader Loader";
    }

    private void handleError(Exception exception) {
        if (this.cache.errorHandled) {
            return;
        }
        this.onError.accept(exception);
        this.cache.errorHandled = true;
    }

    public @Nullable PostEffectProcessor loadPostEffect(Identifier id, Set<Identifier> availableExternalTargets) {
        try {
            return this.cache.getOrLoadProcessor(id, availableExternalTargets);
        }
        catch (LoadException loadException) {
            LOGGER.error("Failed to load post chain: {}", (Object)id, (Object)loadException);
            this.cache.postEffectProcessors.put(id, Optional.empty());
            this.handleError((Exception)((Object)loadException));
            return null;
        }
    }

    @Override
    public void close() {
        this.cache.close();
        this.projectionMatrix.close();
    }

    public @Nullable String getSource(Identifier id, ShaderType type) {
        return this.cache.getSource(id, type);
    }

    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }
}

