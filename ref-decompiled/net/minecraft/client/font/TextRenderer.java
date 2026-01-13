/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ibm.icu.text.ArabicShaping
 *  com.ibm.icu.text.ArabicShapingException
 *  com.ibm.icu.text.Bidi
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.BakedGlyph
 *  net.minecraft.client.font.GlyphProvider
 *  net.minecraft.client.font.TextDrawable$DrawnGlyphRect
 *  net.minecraft.client.font.TextHandler
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.font.TextRenderer$Drawer
 *  net.minecraft.client.font.TextRenderer$GlyphDrawable
 *  net.minecraft.client.font.TextRenderer$GlyphDrawer
 *  net.minecraft.client.font.TextRenderer$GlyphsProvider
 *  net.minecraft.client.font.TextRenderer$TextLayerType
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.text.CharacterVisitor
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Style
 *  net.minecraft.text.StyleSpriteSource
 *  net.minecraft.text.Text
 *  net.minecraft.text.TextVisitFactory
 *  net.minecraft.util.Language
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 *  org.joml.Matrix4f
 */
package net.minecraft.client.font;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.GlyphProvider;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

/*
 * Exception performing whole class analysis ignored.
 */
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
        glyphDrawable.draw(GlyphDrawer.drawing((VertexConsumerProvider)vertexConsumers, (Matrix4f)matrix, (TextLayerType)layerType, (int)light));
    }

    public void draw(Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
        GlyphDrawable glyphDrawable = this.prepare(text.asOrderedText(), x, y, color, shadow, false, backgroundColor);
        glyphDrawable.draw(GlyphDrawer.drawing((VertexConsumerProvider)vertexConsumers, (Matrix4f)matrix, (TextLayerType)layerType, (int)light));
    }

    public void draw(OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
        GlyphDrawable glyphDrawable = this.prepare(text, x, y, color, shadow, false, backgroundColor);
        glyphDrawable.draw(GlyphDrawer.drawing((VertexConsumerProvider)vertexConsumers, (Matrix4f)matrix, (TextLayerType)layerType, (int)light));
    }

    public void drawWithOutline(OrderedText text, float x, float y, int color, int outlineColor, Matrix4f matrix, VertexConsumerProvider vertexConsumers, int light) {
        Drawer drawer = new Drawer(this, 0.0f, 0.0f, outlineColor, false, false);
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
        GlyphDrawer glyphDrawer = GlyphDrawer.drawing((VertexConsumerProvider)vertexConsumers, (Matrix4f)matrix, (TextLayerType)TextLayerType.NORMAL, (int)light);
        for (TextDrawable.DrawnGlyphRect drawnGlyphRect : drawer.drawnGlyphs) {
            glyphDrawer.drawGlyph(drawnGlyphRect);
        }
        Drawer drawer2 = new Drawer(this, x, y, color, false, true);
        text.accept((CharacterVisitor)drawer2);
        drawer2.draw(GlyphDrawer.drawing((VertexConsumerProvider)vertexConsumers, (Matrix4f)matrix, (TextLayerType)TextLayerType.POLYGON_OFFSET, (int)light));
    }

    BakedGlyph getGlyph(int codePoint, Style style) {
        GlyphProvider glyphProvider = this.getGlyphs(style.getFont());
        BakedGlyph bakedGlyph = glyphProvider.get(codePoint);
        if (style.isObfuscated() && codePoint != 32) {
            int i = MathHelper.ceil((float)bakedGlyph.getMetrics().getAdvance(false));
            bakedGlyph = glyphProvider.getObfuscated(this.random, i);
        }
        return bakedGlyph;
    }

    public GlyphDrawable prepare(String string, float x, float y, int color, boolean shadow, int backgroundColor) {
        if (this.isRightToLeft()) {
            string = this.mirror(string);
        }
        Drawer drawer = new Drawer(this, x, y, color, backgroundColor, shadow, false);
        TextVisitFactory.visitFormatted((String)string, (Style)Style.EMPTY, (CharacterVisitor)drawer);
        return drawer;
    }

    public GlyphDrawable prepare(OrderedText text, float x, float y, int color, boolean shadow, boolean trackEmpty, int backgroundColor) {
        Drawer drawer = new Drawer(this, x, y, color, backgroundColor, shadow, trackEmpty);
        text.accept((CharacterVisitor)drawer);
        return drawer;
    }

    public int getWidth(String text) {
        return MathHelper.ceil((float)this.handler.getWidth(text));
    }

    public int getWidth(StringVisitable text) {
        return MathHelper.ceil((float)this.handler.getWidth(text));
    }

    public int getWidth(OrderedText text) {
        return MathHelper.ceil((float)this.handler.getWidth(text));
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
}

