/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.SamplerCache;
import net.minecraft.client.gl.UniformValue;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public class PostEffectPass
implements AutoCloseable {
    private static final int SIZE = new Std140SizeCalculator().putVec2().get();
    private final String id;
    private final RenderPipeline pipeline;
    private final Identifier outputTargetId;
    private final Map<String, GpuBuffer> uniformBuffers = new HashMap<String, GpuBuffer>();
    private final MappableRingBuffer samplerInfoBuffer;
    private final List<Sampler> samplers;

    public PostEffectPass(RenderPipeline pipeline, Identifier outputTargetId, Map<String, List<UniformValue>> uniforms, List<Sampler> samplers) {
        this.pipeline = pipeline;
        this.id = pipeline.getLocation().toString();
        this.outputTargetId = outputTargetId;
        this.samplers = samplers;
        for (Map.Entry<String, List<UniformValue>> entry : uniforms.entrySet()) {
            List<UniformValue> list = entry.getValue();
            if (list.isEmpty()) continue;
            Std140SizeCalculator std140SizeCalculator = new Std140SizeCalculator();
            for (UniformValue uniformValue : list) {
                uniformValue.addSize(std140SizeCalculator);
            }
            int i = std140SizeCalculator.get();
            MemoryStack memoryStack = MemoryStack.stackPush();
            try {
                Std140Builder std140Builder = Std140Builder.onStack(memoryStack, i);
                for (UniformValue uniformValue2 : list) {
                    uniformValue2.write(std140Builder);
                }
                this.uniformBuffers.put(entry.getKey(), RenderSystem.getDevice().createBuffer(() -> this.id + " / " + (String)entry.getKey(), 128, std140Builder.get()));
            }
            finally {
                if (memoryStack == null) continue;
                memoryStack.close();
            }
        }
        this.samplerInfoBuffer = new MappableRingBuffer(() -> this.id + " SamplerInfo", 130, (samplers.size() + 1) * SIZE);
    }

    public void render(FrameGraphBuilder builder, Map<Identifier, Handle<Framebuffer>> handles, GpuBufferSlice slice) {
        FramePass framePass = builder.createPass(this.id);
        for (Sampler sampler : this.samplers) {
            sampler.preRender(framePass, handles);
        }
        Handle handle2 = handles.computeIfPresent(this.outputTargetId, (id, handle) -> framePass.transfer(handle));
        if (handle2 == null) {
            throw new IllegalStateException("Missing handle for target " + String.valueOf(this.outputTargetId));
        }
        framePass.setRenderer(() -> {
            Framebuffer framebuffer = (Framebuffer)handle2.get();
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(slice, ProjectionType.ORTHOGRAPHIC);
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            SamplerCache samplerCache = RenderSystem.getSamplerCache();
            List<Target> list = this.samplers.stream().map(sampler -> new Target(sampler.samplerName(), sampler.getTexture(handles), samplerCache.get(sampler.bilinear() ? FilterMode.LINEAR : FilterMode.NEAREST))).toList();
            try (GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(this.samplerInfoBuffer.getBlocking(), false, true);){
                Iterator<Target> std140Builder = Std140Builder.intoBuffer(mappedView.data());
                ((Std140Builder)((Object)std140Builder)).putVec2(framebuffer.textureWidth, framebuffer.textureHeight);
                for (Target target : list) {
                    ((Std140Builder)((Object)std140Builder)).putVec2(target.view.getWidth(0), target.view.getHeight(0));
                }
            }
            try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Post pass " + this.id, framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.useDepthAttachment ? framebuffer.getDepthAttachmentView() : null, OptionalDouble.empty());){
                renderPass.setPipeline(this.pipeline);
                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setUniform("SamplerInfo", this.samplerInfoBuffer.getBlocking());
                for (Map.Entry entry : this.uniformBuffers.entrySet()) {
                    renderPass.setUniform((String)entry.getKey(), (GpuBuffer)entry.getValue());
                }
                for (Target target : list) {
                    renderPass.bindTexture(target.samplerName() + "Sampler", target.view(), target.sampler());
                }
                renderPass.draw(0, 3);
            }
            this.samplerInfoBuffer.rotate();
            RenderSystem.restoreProjectionMatrix();
            for (Sampler sampler2 : this.samplers) {
                sampler2.postRender(handles);
            }
        });
    }

    @Override
    public void close() {
        for (GpuBuffer gpuBuffer : this.uniformBuffers.values()) {
            gpuBuffer.close();
        }
        this.samplerInfoBuffer.close();
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Sampler {
        public void preRender(FramePass var1, Map<Identifier, Handle<Framebuffer>> var2);

        default public void postRender(Map<Identifier, Handle<Framebuffer>> internalTargets) {
        }

        public GpuTextureView getTexture(Map<Identifier, Handle<Framebuffer>> var1);

        public String samplerName();

        public boolean bilinear();
    }

    @Environment(value=EnvType.CLIENT)
    static final class Target
    extends Record {
        private final String samplerName;
        final GpuTextureView view;
        private final GpuSampler sampler;

        Target(String samplerName, GpuTextureView view, GpuSampler sampler) {
            this.samplerName = samplerName;
            this.view = view;
            this.sampler = sampler;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Target.class, "samplerName;view;sampler", "samplerName", "view", "sampler"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Target.class, "samplerName;view;sampler", "samplerName", "view", "sampler"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Target.class, "samplerName;view;sampler", "samplerName", "view", "sampler"}, this, object);
        }

        public String samplerName() {
            return this.samplerName;
        }

        public GpuTextureView view() {
            return this.view;
        }

        public GpuSampler sampler() {
            return this.sampler;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record TargetSampler(String samplerName, Identifier targetId, boolean depthBuffer, boolean bilinear) implements Sampler
    {
        private Handle<Framebuffer> getTarget(Map<Identifier, Handle<Framebuffer>> internalTargets) {
            Handle<Framebuffer> handle = internalTargets.get(this.targetId);
            if (handle == null) {
                throw new IllegalStateException("Missing handle for target " + String.valueOf(this.targetId));
            }
            return handle;
        }

        @Override
        public void preRender(FramePass pass, Map<Identifier, Handle<Framebuffer>> internalTargets) {
            pass.dependsOn(this.getTarget(internalTargets));
        }

        @Override
        public GpuTextureView getTexture(Map<Identifier, Handle<Framebuffer>> internalTargets) {
            GpuTextureView gpuTextureView;
            Handle<Framebuffer> handle = this.getTarget(internalTargets);
            Framebuffer framebuffer = handle.get();
            GpuTextureView gpuTextureView2 = gpuTextureView = this.depthBuffer ? framebuffer.getDepthAttachmentView() : framebuffer.getColorAttachmentView();
            if (gpuTextureView == null) {
                throw new IllegalStateException("Missing " + (this.depthBuffer ? "depth" : "color") + "texture for target " + String.valueOf(this.targetId));
            }
            return gpuTextureView;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record TextureSampler(String samplerName, AbstractTexture texture, int width, int height, boolean bilinear) implements Sampler
    {
        @Override
        public void preRender(FramePass pass, Map<Identifier, Handle<Framebuffer>> internalTargets) {
        }

        @Override
        public GpuTextureView getTexture(Map<Identifier, Handle<Framebuffer>> internalTargets) {
            return this.texture.getGlTextureView();
        }
    }
}
