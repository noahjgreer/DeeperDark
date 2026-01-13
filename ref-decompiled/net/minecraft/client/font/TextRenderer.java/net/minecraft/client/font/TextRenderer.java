/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ibm.icu.text.ArabicShaping
 *  com.ibm.icu.text.ArabicShapingException
 *  com.ibm.icu.text.Bidi
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.EffectGlyph;
import net.minecraft.client.font.EmptyGlyphRect;
import net.minecraft.client.font.GlyphMetrics;
import net.minecraft.client.font.GlyphProvider;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Language;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TextRenderer {
    private static final float Z_INDEX = 0.01f;
    private static final float field_60693 = 0.01f;
    private static final float field_60694 = -0.01f;
    public static final float FORWARD_SHIFT = 0.03f;
    public final int fontHeight = 9;
    private final Random random = Random.create();
    final GlyphsProvider fonts;
    private final TextHandler handler;

    public TextRenderer(GlyphsProvider fonts) {
        this.fonts = fonts;
        this.handler = new TextHandler((codePoint, style) -> this.getGlyphs(style.getFont()).get(codePoint).getMetrics().getAdvance(style.isBold()));
    }

    private GlyphProvider getGlyphs(StyleSpriteSource source) {
        return this.fonts.getGlyphs(source);
    }

    public String mirror(String text) {
        try {
            Bidi bidi = new Bidi(new ArabicShaping(8).shape(text), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        }
        catch (ArabicShapingException arabicShapingException) {
            return text;
        }
    }

    public void draw(String string, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
        GlyphDrawable glyphDrawable = this.prepare(string, x, y, color, shadow, backgroundColor);
        glyphDrawable.draw(GlyphDrawer.drawing(vertexConsumers, matrix, layerType, light));
    }

    public void draw(Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
        GlyphDrawable glyphDrawable = this.prepare(text.asOrderedText(), x, y, color, shadow, false, backgroundColor);
        glyphDrawable.draw(GlyphDrawer.drawing(vertexConsumers, matrix, layerType, light));
    }

    public void draw(OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
        GlyphDrawable glyphDrawable = this.prepare(text, x, y, color, shadow, false, backgroundColor);
        glyphDrawable.draw(GlyphDrawer.drawing(vertexConsumers, matrix, layerType, light));
    }

    public void drawWithOutline(OrderedText text, float x, float y, int color, int outlineColor, Matrix4f matrix, VertexConsumerProvider vertexConsumers, int light) {
        Drawer drawer = new Drawer(0.0f, 0.0f, outlineColor, false, false);
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (i == 0 && j == 0) continue;
                float[] fs = new float[]{x};
                int k = i;
                int l = j;
                text.accept((index, style, codePoint) -> {
                    boolean bl = style.isBold();
                    BakedGlyph bakedGlyph = this.getGlyph(codePoint, style);
                    drawer.x = fs[0] + (float)k * bakedGlyph.getMetrics().getShadowOffset();
                    drawer.y = y + (float)l * bakedGlyph.getMetrics().getShadowOffset();
                    fs[0] = fs[0] + bakedGlyph.getMetrics().getAdvance(bl);
                    return drawer.accept(index, style.withColor(outlineColor), bakedGlyph);
                });
            }
        }
        GlyphDrawer glyphDrawer = GlyphDrawer.drawing(vertexConsumers, matrix, TextLayerType.NORMAL, light);
        for (TextDrawable.DrawnGlyphRect drawnGlyphRect : drawer.drawnGlyphs) {
            glyphDrawer.drawGlyph(drawnGlyphRect);
        }
        Drawer drawer2 = new Drawer(x, y, color, false, true);
        text.accept(drawer2);
        drawer2.draw(GlyphDrawer.drawing(vertexConsumers, matrix, TextLayerType.POLYGON_OFFSET, light));
    }

    BakedGlyph getGlyph(int codePoint, Style style) {
        GlyphProvider glyphProvider = this.getGlyphs(style.getFont());
        BakedGlyph bakedGlyph = glyphProvider.get(codePoint);
        if (style.isObfuscated() && codePoint != 32) {
            int i = MathHelper.ceil(bakedGlyph.getMetrics().getAdvance(false));
            bakedGlyph = glyphProvider.getObfuscated(this.random, i);
        }
        return bakedGlyph;
    }

    public GlyphDrawable prepare(String string, float x, float y, int color, boolean shadow, int backgroundColor) {
        if (this.isRightToLeft()) {
            string = this.mirror(string);
        }
        Drawer drawer = new Drawer(x, y, color, backgroundColor, shadow, false);
        TextVisitFactory.visitFormatted(string, Style.EMPTY, (CharacterVisitor)drawer);
        return drawer;
    }

    public GlyphDrawable prepare(OrderedText text, float x, float y, int color, boolean shadow, boolean trackEmpty, int backgroundColor) {
        Drawer drawer = new Drawer(x, y, color, backgroundColor, shadow, trackEmpty);
        text.accept(drawer);
        return drawer;
    }

    public int getWidth(String text) {
        return MathHelper.ceil(this.handler.getWidth(text));
    }

    public int getWidth(StringVisitable text) {
        return MathHelper.ceil(this.handler.getWidth(text));
    }

    public int getWidth(OrderedText text) {
        return MathHelper.ceil(this.handler.getWidth(text));
    }

    public String trimToWidth(String text, int maxWidth, boolean backwards) {
        return backwards ? this.handler.trimToWidthBackwards(text, maxWidth, Style.EMPTY) : this.handler.trimToWidth(text, maxWidth, Style.EMPTY);
    }

    public String trimToWidth(String text, int maxWidth) {
        return this.handler.trimToWidth(text, maxWidth, Style.EMPTY);
    }

    public StringVisitable trimToWidth(StringVisitable text, int width) {
        return this.handler.trimToWidth(text, width, Style.EMPTY);
    }

    public int getWrappedLinesHeight(StringVisitable text, int maxWidth) {
        return 9 * this.handler.wrapLines(text, maxWidth, Style.EMPTY).size();
    }

    public List<OrderedText> wrapLines(StringVisitable text, int width) {
        return Language.getInstance().reorder(this.handler.wrapLines(text, width, Style.EMPTY));
    }

    public List<StringVisitable> wrapLinesWithoutLanguage(StringVisitable text, int width) {
        return this.handler.wrapLines(text, width, Style.EMPTY);
    }

    public boolean isRightToLeft() {
        return Language.getInstance().isRightToLeft();
    }

    public TextHandler getTextHandler() {
        return this.handler;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface GlyphsProvider {
        public GlyphProvider getGlyphs(StyleSpriteSource var1);

        public EffectGlyph getRectangleGlyph();
    }

    @Environment(value=EnvType.CLIENT)
    public static interface GlyphDrawable {
        public void draw(GlyphDrawer var1);

        public @Nullable ScreenRect getScreenRect();
    }

    @Environment(value=EnvType.CLIENT)
    public static interface GlyphDrawer {
        public static GlyphDrawer drawing(final VertexConsumerProvider vertexConsumers, final Matrix4f matrix, final TextLayerType layerType, final int light) {
            return new GlyphDrawer(){

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

    @Environment(value=EnvType.CLIENT)
    public static final class TextLayerType
    extends Enum<TextLayerType> {
        public static final /* enum */ TextLayerType NORMAL = new TextLayerType();
        public static final /* enum */ TextLayerType SEE_THROUGH = new TextLayerType();
        public static final /* enum */ TextLayerType POLYGON_OFFSET = new TextLayerType();
        private static final /* synthetic */ TextLayerType[] field_33996;

        public static TextLayerType[] values() {
            return (TextLayerType[])field_33996.clone();
        }

        public static TextLayerType valueOf(String string) {
            return Enum.valueOf(TextLayerType.class, string);
        }

        private static /* synthetic */ TextLayerType[] method_37344() {
            return new TextLayerType[]{NORMAL, SEE_THROUGH, POLYGON_OFFSET};
        }

        static {
            field_33996 = TextLayerType.method_37344();
        }
    }

    @Environment(value=EnvType.CLIENT)
    class Drawer
    implements CharacterVisitor,
    GlyphDrawable {
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

        public Drawer(float x, float y, int color, boolean shadow, boolean trackEmpty) {
            this(x, y, color, 0, shadow, trackEmpty);
        }

        public Drawer(float x, float y, int color, int backgroundColor, boolean shadow, boolean trackEmpty) {
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
        public void draw(GlyphDrawer glyphDrawer) {
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
}
