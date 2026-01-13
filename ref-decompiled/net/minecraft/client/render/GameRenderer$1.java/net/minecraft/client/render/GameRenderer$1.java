/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.EmptyGlyphRect;
import net.minecraft.client.font.GlyphRect;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.render.state.ColoredQuadGuiElementRenderState;
import net.minecraft.client.gui.render.state.TextGuiElementRenderState;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.text.Style;
import net.minecraft.util.math.ColorHelper;

@Environment(value=EnvType.CLIENT)
class GameRenderer.1
implements TextRenderer.GlyphDrawer {
    private int index;
    final /* synthetic */ TextGuiElementRenderState field_63912;

    GameRenderer.1() {
        this.field_63912 = textGuiElementRenderState;
    }

    @Override
    public void drawGlyph(TextDrawable.DrawnGlyphRect glyph) {
        this.addGlyph(glyph, false);
    }

    @Override
    public void drawEmptyGlyphRect(EmptyGlyphRect rect) {
        this.addGlyph(rect, true);
    }

    private void addGlyph(GlyphRect glyph, boolean empty) {
        int i = (empty ? 128 : 255) - (this.index++ & 1) * 64;
        Style style = glyph.style();
        int j = style.getClickEvent() != null ? i : 0;
        int k = style.getHoverEvent() != null ? i : 0;
        int l = j == 0 || k == 0 ? i : 0;
        int m = ColorHelper.getArgb(128, j, k, l);
        GameRenderer.this.guiState.addSimpleElement(new ColoredQuadGuiElementRenderState(RenderPipelines.GUI, TextureSetup.empty(), this.field_63912.matrix, (int)glyph.getLeft(), (int)glyph.getTop(), (int)glyph.getRight(), (int)glyph.getBottom(), m, m, this.field_63912.clipBounds));
    }
}
