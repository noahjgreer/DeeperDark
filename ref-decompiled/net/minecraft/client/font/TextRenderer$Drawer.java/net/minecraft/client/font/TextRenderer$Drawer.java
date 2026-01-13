/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.EmptyGlyphRect;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class TextRenderer.Drawer
implements CharacterVisitor,
TextRenderer.GlyphDrawable {
    private final boolean shadow;
    private final int color;
    private final int backgroundColor;
    private final boolean trackEmpty;
    float x;
    float y;
    private float minX = Float.MAX_VALUE;
    private float minY = Float.MAX_VALUE;
    private float maxX = -3.4028235E38f;
    private float maxY = -3.4028235E38f;
    private float minBackgroundX = Float.MAX_VALUE;
    private float minBackgroundY = Float.MAX_VALUE;
    private float maxBackgroundX = -3.4028235E38f;
    private float maxBackgroundY = -3.4028235E38f;
    final List<TextDrawable.DrawnGlyphRect> drawnGlyphs = new ArrayList<TextDrawable.DrawnGlyphRect>();
    private @Nullable List<TextDrawable> rectangles;
    private @Nullable List<EmptyGlyphRect> emptyGlyphRects;

    public TextRenderer.Drawer(float x, float y, int color, boolean shadow, boolean trackEmpty) {
        this(x, y, color, 0, shadow, trackEmpty);
    }

    public TextRenderer.Drawer(float x, float y, int color, int backgroundColor, boolean shadow, boolean trackEmpty) {
        this.x = x;
        this.y = y;
        this.shadow = shadow;
        this.color = color;
        this.backgroundColor = backgroundColor;
        this.trackEmpty = trackEmpty;
        this.updateBackgroundBounds(x, y, 0.0f);
    }

    private void updateTextBounds(float minX, float minY, float maxX, float maxY) {
        this.minX = Math.min(this.minX, minX);
        this.minY = Math.min(this.minY, minY);
        this.maxX = Math.max(this.maxX, maxX);
        this.maxY = Math.max(this.maxY, maxY);
    }

    private void updateBackgroundBounds(float x, float y, float width) {
        if (ColorHelper.getAlpha(this.backgroundColor) == 0) {
            return;
        }
        this.minBackgroundX = Math.min(this.minBackgroundX, x - 1.0f);
        this.minBackgroundY = Math.min(this.minBackgroundY, y - 1.0f);
        this.maxBackgroundX = Math.max(this.maxBackgroundX, x + width);
        this.maxBackgroundY = Math.max(this.maxBackgroundY, y + 9.0f);
        this.updateTextBounds(this.minBackgroundX, this.minBackgroundY, this.maxBackgroundX, this.maxBackgroundY);
    }

    private void addGlyph(TextDrawable.DrawnGlyphRect glyph) {
        this.drawnGlyphs.add(glyph);
        this.updateTextBounds(glyph.getEffectiveMinX(), glyph.getEffectiveMinY(), glyph.getEffectiveMaxX(), glyph.getEffectiveMaxY());
    }

    private void addRectangle(TextDrawable rectangle) {
        if (this.rectangles == null) {
            this.rectangles = new ArrayList<TextDrawable>();
        }
        this.rectangles.add(rectangle);
        this.updateTextBounds(rectangle.getEffectiveMinX(), rectangle.getEffectiveMinY(), rectangle.getEffectiveMaxX(), rectangle.getEffectiveMaxY());
    }

    private void addEmptyGlyphRect(EmptyGlyphRect rect) {
        if (this.emptyGlyphRects == null) {
            this.emptyGlyphRects = new ArrayList<EmptyGlyphRect>();
        }
        this.emptyGlyphRects.add(rect);
    }

    @Override
    public boolean accept(int i, Style style, int j) {
        BakedGlyph bakedGlyph = TextRenderer.this.getGlyph(j, style);
        return this.accept(i, style, bakedGlyph);
    }

    public boolean accept(int index, Style style, BakedGlyph glyph) {
        float h;
        GlyphMetrics glyphMetrics = glyph.getMetrics();
        boolean bl = style.isBold();
        TextColor textColor = style.getColor();
        int i = this.getRenderColor(textColor);
        int j = this.getShadowColor(style, i);
        float f = glyphMetrics.getAdvance(bl);
        float g = index == 0 ? this.x - 1.0f : this.x;
        float k = bl ? glyphMetrics.getBoldOffset() : 0.0f;
        TextDrawable.DrawnGlyphRect drawnGlyphRect = glyph.create(this.x, this.y, i, j, style, k, h = glyphMetrics.getShadowOffset());
        if (drawnGlyphRect != null) {
            this.addGlyph(drawnGlyphRect);
        } else if (this.trackEmpty) {
            this.addEmptyGlyphRect(new EmptyGlyphRect(this.x, this.y, f, 7.0f, 9.0f, style));
        }
        this.updateBackgroundBounds(this.x, this.y, f);
        if (style.isStrikethrough()) {
            this.addRectangle(TextRenderer.this.fonts.getRectangleGlyph().create(g, this.y + 4.5f - 1.0f, this.x + f, this.y + 4.5f, 0.01f, i, j, h));
        }
        if (style.isUnderlined()) {
            this.addRectangle(TextRenderer.this.fonts.getRectangleGlyph().create(g, this.y + 9.0f - 1.0f, this.x + f, this.y + 9.0f, 0.01f, i, j, h));
        }
        this.x += f;
        return true;
    }

    @Override
    public void draw(TextRenderer.GlyphDrawer glyphDrawer) {
        if (ColorHelper.getAlpha(this.backgroundColor) != 0) {
            glyphDrawer.drawRectangle(TextRenderer.this.fonts.getRectangleGlyph().create(this.minBackgroundX, this.minBackgroundY, this.maxBackgroundX, this.maxBackgroundY, -0.01f, this.backgroundColor, 0, 0.0f));
        }
        for (TextDrawable.DrawnGlyphRect drawnGlyphRect : this.drawnGlyphs) {
            glyphDrawer.drawGlyph(drawnGlyphRect);
        }
        if (this.rectangles != null) {
            for (TextDrawable textDrawable : this.rectangles) {
                glyphDrawer.drawRectangle(textDrawable);
            }
        }
        if (this.emptyGlyphRects != null) {
            for (EmptyGlyphRect emptyGlyphRect : this.emptyGlyphRects) {
                glyphDrawer.drawEmptyGlyphRect(emptyGlyphRect);
            }
        }
    }

    private int getRenderColor(@Nullable TextColor override) {
        if (override != null) {
            int i = ColorHelper.getAlpha(this.color);
            int j = override.getRgb();
            return ColorHelper.withAlpha(i, j);
        }
        return this.color;
    }

    private int getShadowColor(Style style, int textColor) {
        Integer integer = style.getShadowColor();
        if (integer != null) {
            float f = ColorHelper.getAlphaFloat(textColor);
            float g = ColorHelper.getAlphaFloat(integer);
            if (f != 1.0f) {
                return ColorHelper.withAlpha(ColorHelper.channelFromFloat(f * g), (int)integer);
            }
            return integer;
        }
        if (this.shadow) {
            return ColorHelper.scaleRgb(textColor, 0.25f);
        }
        return 0;
    }

    @Override
    public @Nullable ScreenRect getScreenRect() {
        if (this.minX >= this.maxX || this.minY >= this.maxY) {
            return null;
        }
        int i = MathHelper.floor(this.minX);
        int j = MathHelper.floor(this.minY);
        int k = MathHelper.ceil(this.maxX);
        int l = MathHelper.ceil(this.maxY);
        return new ScreenRect(i, j, k - i, l - j);
    }
}
