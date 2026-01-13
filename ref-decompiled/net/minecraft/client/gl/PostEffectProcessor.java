/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Builder
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.PostEffectPass
 *  net.minecraft.client.gl.PostEffectPass$TargetSampler
 *  net.minecraft.client.gl.PostEffectPass$TextureSampler
 *  net.minecraft.client.gl.PostEffectPipeline
 *  net.minecraft.client.gl.PostEffectPipeline$Input
 *  net.minecraft.client.gl.PostEffectPipeline$Pass
 *  net.minecraft.client.gl.PostEffectPipeline$TargetSampler
 *  net.minecraft.client.gl.PostEffectPipeline$Targets
 *  net.minecraft.client.gl.PostEffectPipeline$TextureSampler
 *  net.minecraft.client.gl.PostEffectProcessor
 *  net.minecraft.client.gl.PostEffectProcessor$FramebufferSet
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gl.ShaderLoader$LoadException
 *  net.minecraft.client.gl.SimpleFramebufferFactory
 *  net.minecraft.client.gl.UniformType
 *  net.minecraft.client.render.FrameGraphBuilder
 *  net.minecraft.client.render.ProjectionMatrix2
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.client.util.ClosableFactory
 *  net.minecraft.client.util.Handle
 *  net.minecraft.client.util.ObjectAllocator
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.gl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.ProjectionMatrix2;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class PostEffectProcessor
implements AutoCloseable {
    public static final Identifier MAIN = Identifier.ofVanilla((String)"main");
    private final List<PostEffectPass> passes;
    private final Map<Identifier, PostEffectPipeline.Targets> internalTargets;
    private final Set<Identifier> externalTargets;
    private final Map<Identifier, Framebuffer> framebuffers = new HashMap();
    private final ProjectionMatrix2 projectionMatrix;

    private PostEffectProcessor(List<PostEffectPass> passes, Map<Identifier, PostEffectPipeline.Targets> internalTargets, Set<Identifier> externalTargets, ProjectionMatrix2 projectionMatrix) {
        this.passes = passes;
        this.internalTargets = internalTargets;
        this.externalTargets = externalTargets;
        this.projectionMatrix = projectionMatrix;
    }

    public static PostEffectProcessor parseEffect(PostEffectPipeline pipeline, TextureManager textureManager, Set<Identifier> availableExternalTargets, Identifier id, ProjectionMatrix2 projectionMatrix) throws ShaderLoader.LoadException {
        Stream stream = pipeline.passes().stream().flatMap(PostEffectPipeline.Pass::streamTargets);
        Set set = stream.filter(target -> !pipeline.internalTargets().containsKey(target)).collect(Collectors.toSet());
        Sets.SetView set2 = Sets.difference(set, availableExternalTargets);
        if (!set2.isEmpty()) {
            throw new ShaderLoader.LoadException("Referenced external targets are not available in this context: " + String.valueOf(set2));
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        for (int i = 0; i < pipeline.passes().size(); ++i) {
            PostEffectPipeline.Pass pass = (PostEffectPipeline.Pass)pipeline.passes().get(i);
            builder.add((Object)PostEffectProcessor.parsePass((TextureManager)textureManager, (PostEffectPipeline.Pass)pass, (Identifier)id.withSuffixedPath("/" + i)));
        }
        return new PostEffectProcessor((List)builder.build(), pipeline.internalTargets(), set, projectionMatrix);
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static PostEffectPass parsePass(TextureManager textureManager, PostEffectPipeline.Pass pass, Identifier id) throws ShaderLoader.LoadException {
        RenderPipeline.Builder builder = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RenderPipelines.POST_EFFECT_PROCESSOR_SNIPPET}).withFragmentShader(pass.fragmentShaderId()).withVertexShader(pass.vertexShaderId()).withLocation(id);
        for (PostEffectPipeline.Input input : pass.inputs()) {
            builder.withSampler(input.samplerName() + "Sampler");
        }
        builder.withUniform("SamplerInfo", UniformType.UNIFORM_BUFFER);
        for (String string : pass.uniforms().keySet()) {
            builder.withUniform(string, UniformType.UNIFORM_BUFFER);
        }
        RenderPipeline renderPipeline = builder.build();
        ArrayList<Object> list = new ArrayList<Object>();
        Iterator iterator = pass.inputs().iterator();
        block9: while (iterator.hasNext()) {
            Object object;
            PostEffectPipeline.Input input;
            PostEffectPipeline.Input input2 = (PostEffectPipeline.Input)iterator.next();
            Objects.requireNonNull(input2);
            int n = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{PostEffectPipeline.TextureSampler.class, PostEffectPipeline.TargetSampler.class}, (Object)input, n)) {
                default: {
                    throw new MatchException(null, null);
                }
                case 0: {
                    boolean bl3;
                    boolean j;
                    boolean i;
                    Object identifier;
                    Object string2;
                    PostEffectPipeline.TextureSampler textureSampler = (PostEffectPipeline.TextureSampler)input;
                    try {
                        boolean bl2;
                        string2 = object = textureSampler.samplerName();
                        identifier = object = textureSampler.location();
                        i = bl2 = textureSampler.width();
                        j = bl2 = textureSampler.height();
                        bl3 = bl2 = (boolean)textureSampler.bilinear();
                    }
                    catch (Throwable throwable) {
                        throw new MatchException(throwable.toString(), throwable);
                    }
                    AbstractTexture abstractTexture = textureManager.getTexture(identifier.withPath(name -> "textures/effect/" + name + ".png"));
                    list.add(new PostEffectPass.TextureSampler((String)string2, abstractTexture, i ? 1 : 0, j ? 1 : 0, bl3));
                    continue block9;
                }
                case 1: 
            }
            object = (PostEffectPipeline.TargetSampler)input;
            {
                boolean bl;
                String string;
                String string3 = string = object.samplerName();
                String identifier2 = string = object.targetId();
                boolean bl2 = bl = object.useDepthBuffer();
                boolean bl3 = bl = object.bilinear();
                list.add(new PostEffectPass.TargetSampler(string3, (Identifier)identifier2, bl2, bl3));
            }
        }
        return new PostEffectPass(renderPipeline, pass.outputTarget(), pass.uniforms(), list);
    }

    public void render(FrameGraphBuilder builder, int textureWidth, int textureHeight, FramebufferSet framebufferSet) {
        GpuBufferSlice gpuBufferSlice = this.projectionMatrix.set((float)textureWidth, (float)textureHeight);
        HashMap<Identifier, Handle> map = new HashMap<Identifier, Handle>(this.internalTargets.size() + this.externalTargets.size());
        for (Identifier identifier : this.externalTargets) {
            map.put(identifier, framebufferSet.getOrThrow(identifier));
        }
        for (Map.Entry entry : this.internalTargets.entrySet()) {
            Identifier identifier2 = (Identifier)entry.getKey();
            PostEffectPipeline.Targets targets = (PostEffectPipeline.Targets)entry.getValue();
            SimpleFramebufferFactory simpleFramebufferFactory = new SimpleFramebufferFactory(targets.width().orElse(textureWidth).intValue(), targets.height().orElse(textureHeight).intValue(), true, targets.clearColor());
            if (targets.persistent()) {
                Framebuffer framebuffer = this.createFramebuffer(identifier2, simpleFramebufferFactory);
                map.put(identifier2, builder.createObjectNode(identifier2.toString(), (Object)framebuffer));
                continue;
            }
            map.put(identifier2, builder.createResourceHandle(identifier2.toString(), (ClosableFactory)simpleFramebufferFactory));
        }
        for (PostEffectPass postEffectPass : this.passes) {
            postEffectPass.render(builder, map, gpuBufferSlice);
        }
        for (Identifier identifier : this.externalTargets) {
            framebufferSet.set(identifier, (Handle)map.get(identifier));
        }
    }

    @Deprecated
    public void render(Framebuffer framebuffer, ObjectAllocator objectAllocator) {
        FrameGraphBuilder frameGraphBuilder = new FrameGraphBuilder();
        FramebufferSet framebufferSet = FramebufferSet.singleton((Identifier)MAIN, (Handle)frameGraphBuilder.createObjectNode("main", (Object)framebuffer));
        this.render(frameGraphBuilder, framebuffer.textureWidth, framebuffer.textureHeight, framebufferSet);
        frameGraphBuilder.run(objectAllocator);
    }

    private Framebuffer createFramebuffer(Identifier id, SimpleFramebufferFactory factory) {
        Framebuffer framebuffer = (Framebuffer)this.framebuffers.get(id);
        if (framebuffer == null || framebuffer.textureWidth != factory.width() || framebuffer.textureHeight != factory.height()) {
            if (framebuffer != null) {
                framebuffer.delete();
            }
            framebuffer = factory.create();
            factory.prepare(framebuffer);
            this.framebuffers.put(id, framebuffer);
        }
        return framebuffer;
    }

    @Override
    public void close() {
        this.framebuffers.values().forEach(Framebuffer::delete);
        this.framebuffers.clear();
        for (PostEffectPass postEffectPass : this.passes) {
            postEffectPass.close();
        }
    }
}

