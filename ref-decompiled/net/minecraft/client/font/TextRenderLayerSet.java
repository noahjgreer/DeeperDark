/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderLayerSet
 *  net.minecraft.client.font.TextRenderLayerSet$1
 *  net.minecraft.client.font.TextRenderer$TextLayerType
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderLayerSet;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record TextRenderLayerSet(RenderLayer normal, RenderLayer seeThrough, RenderLayer polygonOffset, RenderPipeline guiPipeline) {
    private final RenderLayer normal;
    private final RenderLayer seeThrough;
    private final RenderLayer polygonOffset;
    private final RenderPipeline guiPipeline;

    public TextRenderLayerSet(RenderLayer normal, RenderLayer seeThrough, RenderLayer polygonOffset, RenderPipeline guiPipeline) {
        this.normal = normal;
        this.seeThrough = seeThrough;
        this.polygonOffset = polygonOffset;
        this.guiPipeline = guiPipeline;
    }

    public static TextRenderLayerSet ofIntensity(Identifier textureId) {
        return new TextRenderLayerSet(RenderLayers.textIntensity((Identifier)textureId), RenderLayers.textIntensitySeeThrough((Identifier)textureId), RenderLayers.textIntensityPolygonOffset((Identifier)textureId), RenderPipelines.GUI_TEXT_INTENSITY);
    }

    public static TextRenderLayerSet of(Identifier textureId) {
        return new TextRenderLayerSet(RenderLayers.text((Identifier)textureId), RenderLayers.textSeeThrough((Identifier)textureId), RenderLayers.textPolygonOffset((Identifier)textureId), RenderPipelines.GUI_TEXT);
    }

    public RenderLayer getRenderLayer(TextRenderer.TextLayerType layerType) {
        return switch (1.field_34000[layerType.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> this.normal;
            case 2 -> this.seeThrough;
            case 3 -> this.polygonOffset;
        };
    }

    public RenderLayer normal() {
        return this.normal;
    }

    public RenderLayer seeThrough() {
        return this.seeThrough;
    }

    public RenderLayer polygonOffset() {
        return this.polygonOffset;
    }

    public RenderPipeline guiPipeline() {
        return this.guiPipeline;
    }
}

