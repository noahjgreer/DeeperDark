/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderPass$RenderObject
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat$IndexType
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.gl.CompiledShaderPipeline
 *  net.minecraft.client.gl.GlCommandEncoder
 *  net.minecraft.client.gl.GlSampler
 *  net.minecraft.client.gl.GpuSampler
 *  net.minecraft.client.gl.RenderPassImpl
 *  net.minecraft.client.gl.RenderPassImpl$SamplerUniform
 *  net.minecraft.client.gl.ScissorState
 *  net.minecraft.client.texture.GlTextureView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gl.CompiledShaderPipeline;
import net.minecraft.client.gl.GlCommandEncoder;
import net.minecraft.client.gl.GlSampler;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.RenderPassImpl;
import net.minecraft.client.gl.ScissorState;
import net.minecraft.client.texture.GlTextureView;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RenderPassImpl
implements RenderPass {
    protected static final int field_57866 = 1;
    public static final boolean IS_DEVELOPMENT = SharedConstants.isDevelopment;
    private final GlCommandEncoder resourceManager;
    private final boolean hasDepth;
    private boolean closed;
    protected @Nullable CompiledShaderPipeline pipeline;
    protected final @Nullable GpuBuffer[] vertexBuffers = new GpuBuffer[1];
    protected @Nullable GpuBuffer indexBuffer;
    protected VertexFormat.IndexType indexType = VertexFormat.IndexType.INT;
    private final ScissorState scissorState = new ScissorState();
    protected final HashMap<String, GpuBufferSlice> simpleUniforms = new HashMap();
    protected final HashMap<String, SamplerUniform> samplerUniforms = new HashMap();
    protected final Set<String> setSimpleUniforms = new HashSet();
    protected int debugGroupPushCount;

    public RenderPassImpl(GlCommandEncoder resourceManager, boolean hasDepth) {
        this.resourceManager = resourceManager;
        this.hasDepth = hasDepth;
    }

    public boolean hasDepth() {
        return this.hasDepth;
    }

    public void pushDebugGroup(Supplier<String> supplier) {
        if (this.closed) {
            throw new IllegalStateException("Can't use a closed render pass");
        }
        ++this.debugGroupPushCount;
        this.resourceManager.getBackend().getDebugLabelManager().pushDebugGroup(supplier);
    }

    public void popDebugGroup() {
        if (this.closed) {
            throw new IllegalStateException("Can't use a closed render pass");
        }
        if (this.debugGroupPushCount == 0) {
            throw new IllegalStateException("Can't pop more debug groups than was pushed!");
        }
        --this.debugGroupPushCount;
        this.resourceManager.getBackend().getDebugLabelManager().popDebugGroup();
    }

    public void setPipeline(RenderPipeline renderPipeline) {
        if (this.pipeline == null || this.pipeline.info() != renderPipeline) {
            this.setSimpleUniforms.addAll(this.simpleUniforms.keySet());
            this.setSimpleUniforms.addAll(this.samplerUniforms.keySet());
        }
        this.pipeline = this.resourceManager.getBackend().compilePipelineCached(renderPipeline);
    }

    public void bindTexture(String string, @Nullable GpuTextureView gpuTextureView, @Nullable GpuSampler gpuSampler) {
        if (gpuSampler == null) {
            this.samplerUniforms.remove(string);
        } else {
            this.samplerUniforms.put(string, new SamplerUniform((GlTextureView)gpuTextureView, (GlSampler)gpuSampler));
        }
        this.setSimpleUniforms.add(string);
    }

    public void setUniform(String string, GpuBuffer gpuBuffer) {
        this.simpleUniforms.put(string, gpuBuffer.slice());
        this.setSimpleUniforms.add(string);
    }

    public void setUniform(String string, GpuBufferSlice gpuBufferSlice) {
        int i = this.resourceManager.getBackend().getUniformOffsetAlignment();
        if (gpuBufferSlice.offset() % (long)i > 0L) {
            throw new IllegalArgumentException("Uniform buffer offset must be aligned to " + i);
        }
        this.simpleUniforms.put(string, gpuBufferSlice);
        this.setSimpleUniforms.add(string);
    }

    public void enableScissor(int i, int j, int k, int l) {
        this.scissorState.enable(i, j, k, l);
    }

    public void disableScissor() {
        this.scissorState.disable();
    }

    public boolean isScissorEnabled() {
        return this.scissorState.isEnabled();
    }

    public int getScissorX() {
        return this.scissorState.getX();
    }

    public int getScissorY() {
        return this.scissorState.getY();
    }

    public int getScissorWidth() {
        return this.scissorState.getWidth();
    }

    public int getScissorHeight() {
        return this.scissorState.getHeight();
    }

    public void setVertexBuffer(int i, GpuBuffer gpuBuffer) {
        if (i < 0 || i >= 1) {
            throw new IllegalArgumentException("Vertex buffer slot is out of range: " + i);
        }
        this.vertexBuffers[i] = gpuBuffer;
    }

    public void setIndexBuffer(@Nullable GpuBuffer gpuBuffer, VertexFormat.IndexType indexType) {
        this.indexBuffer = gpuBuffer;
        this.indexType = indexType;
    }

    public void drawIndexed(int i, int j, int k, int l) {
        if (this.closed) {
            throw new IllegalStateException("Can't use a closed render pass");
        }
        this.resourceManager.drawBoundObjectWithRenderPass(this, i, j, k, this.indexType, l);
    }

    public <T> void drawMultipleIndexed(Collection<RenderPass.RenderObject<T>> collection, @Nullable GpuBuffer gpuBuffer, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable VertexFormat.IndexType indexType, Collection<String> collection2, T object) {
        if (this.closed) {
            throw new IllegalStateException("Can't use a closed render pass");
        }
        this.resourceManager.drawObjectsWithRenderPass(this, collection, gpuBuffer, indexType, collection2, object);
    }

    public void draw(int i, int j) {
        if (this.closed) {
            throw new IllegalStateException("Can't use a closed render pass");
        }
        this.resourceManager.drawBoundObjectWithRenderPass(this, i, 0, j, null, 1);
    }

    public void close() {
        if (!this.closed) {
            if (this.debugGroupPushCount > 0) {
                throw new IllegalStateException("Render pass had debug groups left open!");
            }
            this.closed = true;
            this.resourceManager.closePass();
        }
    }
}

