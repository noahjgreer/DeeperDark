/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.EmptyGlyphRect;
import net.minecraft.client.font.GlyphRect;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.font.TextRenderer;

@Environment(value=EnvType.CLIENT)
static class DrawnTextConsumer.1
implements TextRenderer.GlyphDrawer {
    final /* synthetic */ float field_63834;
    final /* synthetic */ float field_63835;
    final /* synthetic */ Consumer field_63836;

    DrawnTextConsumer.1() {
        this.field_63834 = f;
        this.field_63835 = g;
        this.field_63836 = consumer;
    }

    @Override
    public void drawGlyph(TextDrawable.DrawnGlyphRect glyph) {
        this.addGlyphInternal(glyph);
    }

    @Override
    public void drawEmptyGlyphRect(EmptyGlyphRect rect) {
        this.addGlyphInternal(rect);
    }

    private void addGlyphInternal(GlyphRect glyph) {
        if (DrawnTextConsumer.isWithinBounds(this.field_63834, this.field_63835, glyph.getLeft(), glyph.getTop(), glyph.getRight(), glyph.getBottom())) {
            this.field_63836.accept(glyph.style());
        }
    }
}
