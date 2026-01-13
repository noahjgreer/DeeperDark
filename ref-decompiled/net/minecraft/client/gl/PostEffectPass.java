/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBuffer$MappedView
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.buffers.Std140Builder
 *  com.mojang.blaze3d.buffers.Std140SizeCalculator
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.ProjectionType
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.MappableRingBuffer
 *  net.minecraft.client.gl.PostEffectPass
 *  net.minecraft.client.gl.PostEffectPass$Sampler
 *  net.minecraft.client.gl.PostEffectPass$Target
 *  net.minecraft.client.gl.SamplerCache
 *  net.minecraft.client.gl.UniformValue
 *  net.minecraft.client.render.FrameGraphBuilder
 *  net.minecraft.client.render.FramePass
 *  net.minecraft.client.util.Handle
 *  net.minecraft.util.Identifier
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
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.SamplerCache;
import net.minecraft.client.gl.UniformValue;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
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
    private final Map<String, GpuBuffer> uniformBuffers = new HashMap();
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
                Std140Builder std140Builder = Std140Builder.onStack((MemoryStack)memoryStack, (int)i);
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
            RenderSystem.setProjectionMatrix((GpuBufferSlice)slice, (ProjectionType)ProjectionType.ORTHOGRAPHIC);
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            SamplerCache samplerCache = RenderSystem.getSamplerCache();
            List<Target> list = this.samplers.stream().map(sampler -> new Target(sampler.samplerName(), sampler.getTexture(handles), samplerCache.get(sampler.bilinear() ? FilterMode.LINEAR : FilterMode.NEAREST))).toList();
            try (GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(this.samplerInfoBuffer.getBlocking(), false, true);){
                Std140Builder std140Builder = Std140Builder.intoBuffer((ByteBuffer)mappedView.data());
                std140Builder.putVec2((float)framebuffer.textureWidth, (float)framebuffer.textureHeight);
                for (Target target : list) {
                    std140Builder.putVec2((float)target.view.getWidth(0), (float)target.view.getHeight(0));
                }
            }
            try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Post pass " + this.id, framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.useDepthAttachment ? framebuffer.getDepthAttachmentView() : null, OptionalDouble.empty());){
                renderPass.setPipeline(this.pipeline);
                RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
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
}

