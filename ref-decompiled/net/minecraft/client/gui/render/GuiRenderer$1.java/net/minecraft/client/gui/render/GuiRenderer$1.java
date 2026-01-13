/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3x2fc
 */
package net.minecraft.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.GlyphGuiElementRenderState;
import org.joml.Matrix3x2fc;

@Environment(value=EnvType.CLIENT)
class GuiRenderer.1
implements TextRenderer.GlyphDrawer {
    final /* synthetic */ Matrix3x2fc field_60739;
    final /* synthetic */ ScreenRect field_60740;

    GuiRenderer.1() {
        this.field_60739 = matrix3x2fc;
        this.field_60740 = screenRect;
    }

    @Override
    public void drawGlyph(TextDrawable.DrawnGlyphRect glyph) {
        this.draw(glyph);
    }

    @Override
    public void drawRectangle(TextDrawable rect) {
        this.draw(rect);
    }

    private void draw(TextDrawable drawable) {
        GuiRenderer.this.state.addPreparedTextElement(new GlyphGuiElementRenderState(this.field_60739, drawable, this.field_60740));
    }
}
