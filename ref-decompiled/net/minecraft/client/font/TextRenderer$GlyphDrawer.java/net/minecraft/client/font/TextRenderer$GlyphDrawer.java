/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.EmptyGlyphRect;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public static interface TextRenderer.GlyphDrawer {
    public static TextRenderer.GlyphDrawer drawing(final VertexConsumerProvider vertexConsumers, final Matrix4f matrix, final TextRenderer.TextLayerType layerType, final int light) {
        return new TextRenderer.GlyphDrawer(){

            @Override
            public void drawGlyph(TextDrawable.DrawnGlyphRect glyph) {
                this.draw(glyph);
            }

            @Override
            public void drawRectangle(TextDrawable rect) {
                this.draw(rect);
            }

            private void draw(TextDrawable glyph) {
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(glyph.getRenderLayer(layerType));
                glyph.render(matrix, vertexConsumer, light, false);
            }
        };
    }

    default public void drawGlyph(TextDrawable.DrawnGlyphRect glyph) {
    }

    default public void drawRectangle(TextDrawable rect) {
    }

    default public void drawEmptyGlyphRect(EmptyGlyphRect rect) {
    }
}
