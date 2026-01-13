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
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
static class TextRenderer.GlyphDrawer.1
implements TextRenderer.GlyphDrawer {
    final /* synthetic */ VertexConsumerProvider field_60695;
    final /* synthetic */ TextRenderer.TextLayerType field_60696;
    final /* synthetic */ Matrix4f field_60697;
    final /* synthetic */ int field_60698;

    TextRenderer.GlyphDrawer.1() {
        this.field_60695 = vertexConsumerProvider;
        this.field_60696 = textLayerType;
        this.field_60697 = matrix4f;
        this.field_60698 = i;
    }

    @Override
    public void drawGlyph(TextDrawable.DrawnGlyphRect glyph) {
        this.draw(glyph);
    }

    @Override
    public void drawRectangle(TextDrawable rect) {
        this.draw(rect);
    }

    private void draw(TextDrawable glyph) {
        VertexConsumer vertexConsumer = this.field_60695.getBuffer(glyph.getRenderLayer(this.field_60696));
        glyph.render(this.field_60697, vertexConsumer, this.field_60698, false);
    }
}
