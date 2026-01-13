/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ScissorState;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.RenderSetup;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Environment(value=EnvType.CLIENT)
public class RenderLayer {
    private static final int field_64012 = 0x100000;
    public static final int field_64008 = 0x400000;
    public static final int field_64009 = 786432;
    public static final int field_64010 = 1536;
    private final RenderSetup renderSetup;
    private final Optional<RenderLayer> affectedOutline;
    protected final String name;

    private RenderLayer(String name, RenderSetup renderSetup) {
        this.name = name;
        this.renderSetup = renderSetup;
        this.affectedOutline = renderSetup.outlineMode == RenderSetup.OutlineMode.AFFECTS_OUTLINE ? renderSetup.textures.values().stream().findFirst().map(texture -> RenderLayers.OUTLINE.apply(texture.location(), renderSetup.pipeline.isCull())) : Optional.empty();
    }

    public static RenderLayer of(String name, RenderSetup renderSetup) {
        return new RenderLayer(name, renderSetup);
    }

    public String toString() {
        return "RenderType[" + this.name + ":" + String.valueOf(this.renderSetup) + "]";
    }

    public void draw(BuiltBuffer buffer) {
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        Consumer<Matrix4fStack> consumer = this.renderSetup.layeringTransform.getTransform();
        if (consumer != null) {
            matrix4fStack.pushMatrix();
            consumer.accept(matrix4fStack);
        }
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)this.renderSetup.textureTransform.getTransformSupplier());
        Map<String, RenderSetup.Texture> map = this.renderSetup.resolveTextures();
        try (BuiltBuffer builtBuffer = buffer;){
            GpuTextureView gpuTextureView;
            VertexFormat.IndexType indexType;
            GpuBuffer gpuBuffer2;
            GpuBuffer gpuBuffer = this.renderSetup.pipeline.getVertexFormat().uploadImmediateVertexBuffer(buffer.getBuffer());
            if (buffer.getSortedBuffer() == null) {
                RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(buffer.getDrawParameters().mode());
                gpuBuffer2 = shapeIndexBuffer.getIndexBuffer(buffer.getDrawParameters().indexCount());
                indexType = shapeIndexBuffer.getIndexType();
            } else {
                gpuBuffer2 = this.renderSetup.pipeline.getVertexFormat().uploadImmediateIndexBuffer(buffer.getSortedBuffer());
                indexType = buffer.getDrawParameters().indexType();
            }
            Framebuffer framebuffer = this.renderSetup.outputTarget.getFramebuffer();
            GpuTextureView gpuTextureView2 = gpuTextureView = RenderSystem.outputColorTextureOverride != null ? RenderSystem.outputColorTextureOverride : framebuffer.getColorAttachmentView();
            GpuTextureView gpuTextureView22 = framebuffer.useDepthAttachment ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : framebuffer.getDepthAttachmentView()) : null;
            try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Immediate draw for " + this.name, gpuTextureView, OptionalInt.empty(), gpuTextureView22, OptionalDouble.empty());){
                renderPass.setPipeline(this.renderSetup.pipeline);
                ScissorState scissorState = RenderSystem.getScissorStateForRenderTypeDraws();
                if (scissorState.isEnabled()) {
                    renderPass.enableScissor(scissorState.getX(), scissorState.getY(), scissorState.getWidth(), scissorState.getHeight());
                }
                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
                renderPass.setVertexBuffer(0, gpuBuffer);
                for (Map.Entry<String, RenderSetup.Texture> entry : map.entrySet()) {
                    renderPass.bindTexture(entry.getKey(), entry.getValue().textureView(), entry.getValue().sampler());
                }
                renderPass.setIndexBuffer(gpuBuffer2, indexType);
                renderPass.drawIndexed(0, 0, buffer.getDrawParameters().indexCount(), 1);
            }
        }
        if (consumer != null) {
            matrix4fStack.popMatrix();
        }
    }

    public int getExpectedBufferSize() {
        return this.renderSetup.expectedBufferSize;
    }

    public VertexFormat getVertexFormat() {
        return this.renderSetup.pipeline.getVertexFormat();
    }

    public VertexFormat.DrawMode getDrawMode() {
        return this.renderSetup.pipeline.getVertexFormatMode();
    }

    public Optional<RenderLayer> getAffectedOutline() {
        return this.affectedOutline;
    }

    public boolean isOutline() {
        return this.renderSetup.outlineMode == RenderSetup.OutlineMode.IS_OUTLINE;
    }

    public RenderPipeline getRenderPipeline() {
        return this.renderSetup.pipeline;
    }

    public boolean hasCrumbling() {
        return this.renderSetup.hasCrumbling;
    }

    public boolean areVerticesNotShared() {
        return !this.getDrawMode().shareVertices;
    }

    public boolean isTranslucent() {
        return this.renderSetup.translucent;
    }
}
