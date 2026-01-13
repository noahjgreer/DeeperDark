/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.shaders.ShaderType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class ShaderLoader.Cache
implements AutoCloseable {
    private final ShaderLoader.Definitions definitions;
    final Map<Identifier, Optional<PostEffectProcessor>> postEffectProcessors = new HashMap<Identifier, Optional<PostEffectProcessor>>();
    boolean errorHandled;

    ShaderLoader.Cache(ShaderLoader.Definitions definitions) {
        this.definitions = definitions;
    }

    public @Nullable PostEffectProcessor getOrLoadProcessor(Identifier id, Set<Identifier> availableExternalTargets) throws ShaderLoader.LoadException {
        Optional<PostEffectProcessor> optional = this.postEffectProcessors.get(id);
        if (optional != null) {
            return optional.orElse(null);
        }
        PostEffectProcessor postEffectProcessor = this.loadProcessor(id, availableExternalTargets);
        this.postEffectProcessors.put(id, Optional.of(postEffectProcessor));
        return postEffectProcessor;
    }

    private PostEffectProcessor loadProcessor(Identifier id, Set<Identifier> availableExternalTargets) throws ShaderLoader.LoadException {
        PostEffectPipeline postEffectPipeline = this.definitions.postChains.get(id);
        if (postEffectPipeline == null) {
            throw new ShaderLoader.LoadException("Could not find post chain with id: " + String.valueOf(id));
        }
        return PostEffectProcessor.parseEffect(postEffectPipeline, ShaderLoader.this.textureManager, availableExternalTargets, id, ShaderLoader.this.projectionMatrix);
    }

    @Override
    public void close() {
        this.postEffectProcessors.values().forEach(processor -> processor.ifPresent(PostEffectProcessor::close));
        this.postEffectProcessors.clear();
    }

    public @Nullable String getSource(Identifier id, ShaderType type) {
        return this.definitions.shaderSources.get(new ShaderLoader.ShaderSourceKey(id, type));
    }
}
