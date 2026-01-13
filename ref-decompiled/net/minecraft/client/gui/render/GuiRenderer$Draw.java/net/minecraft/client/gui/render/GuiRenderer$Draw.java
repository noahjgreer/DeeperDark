/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.texture.TextureSetup;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static final class GuiRenderer.Draw
extends Record {
    final GpuBuffer vertexBuffer;
    final int baseVertex;
    private final VertexFormat.DrawMode mode;
    final int indexCount;
    private final RenderPipeline pipeline;
    final TextureSetup textureSetup;
    private final @Nullable ScreenRect scissorArea;

    GuiRenderer.Draw(GpuBuffer vertexBuffer, int baseVertex, VertexFormat.DrawMode mode, int indexCount, RenderPipeline pipeline, TextureSetup textureSetup, @Nullable ScreenRect scissorArea) {
        this.vertexBuffer = vertexBuffer;
        this.baseVertex = baseVertex;
        this.mode = mode;
        this.indexCount = indexCount;
        this.pipeline = pipeline;
        this.textureSetup = textureSetup;
        this.scissorArea = scissorArea;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{GuiRenderer.Draw.class, "vertexBuffer;baseVertex;mode;indexCount;pipeline;textureSetup;scissorArea", "vertexBuffer", "baseVertex", "mode", "indexCount", "pipeline", "textureSetup", "scissorArea"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GuiRenderer.Draw.class, "vertexBuffer;baseVertex;mode;indexCount;pipeline;textureSetup;scissorArea", "vertexBuffer", "baseVertex", "mode", "indexCount", "pipeline", "textureSetup", "scissorArea"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GuiRenderer.Draw.class, "vertexBuffer;baseVertex;mode;indexCount;pipeline;textureSetup;scissorArea", "vertexBuffer", "baseVertex", "mode", "indexCount", "pipeline", "textureSetup", "scissorArea"}, this, object);
    }

    public GpuBuffer vertexBuffer() {
        return this.vertexBuffer;
    }

    public int baseVertex() {
        return this.baseVertex;
    }

    public VertexFormat.DrawMode mode() {
        return this.mode;
    }

    public int indexCount() {
        return this.indexCount;
    }

    public RenderPipeline pipeline() {
        return this.pipeline;
    }

    public TextureSetup textureSetup() {
        return this.textureSetup;
    }

    public @Nullable ScreenRect scissorArea() {
        return this.scissorArea;
    }
}
