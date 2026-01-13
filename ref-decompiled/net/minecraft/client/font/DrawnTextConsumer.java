/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.Alignment
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.font.DrawnTextConsumer$Transformation
 *  net.minecraft.client.font.TextRenderer$GlyphDrawer
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.TextGuiElementRenderState
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.MathHelper
 *  org.joml.Matrix3x2f
 *  org.joml.Vector2f
 */
package net.minecraft.client.font;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.TextGuiElementRenderState;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

@Environment(value=EnvType.CLIENT)
public interface DrawnTextConsumer {
    public static final double MARQUEE_PERIOD_PER_EXCESS_WIDTH = 0.5;
    public static final double MARQUEE_MIN_PERIOD = 3.0;

    public Transformation getTransformation();

    public void setTransformation(Transformation var1);

    default public void text(int x, int y, OrderedText text) {
        this.text(Alignment.LEFT, x, y, this.getTransformation(), text);
    }

    default public void text(int x, int y, Text text) {
        this.text(Alignment.LEFT, x, y, this.getTransformation(), text.asOrderedText());
    }

    default public void text(Alignment alignment, int x, int y, Transformation transformation, Text text) {
        this.text(alignment, x, y, transformation, text.asOrderedText());
    }

    public void text(Alignment var1, int var2, int var3, Transformation var4, OrderedText var5);

    default public void text(Alignment alignment, int x, int y, Text text) {
        this.text(alignment, x, y, text.asOrderedText());
    }

    default public void text(Alignment alignment, int x, int y, OrderedText text) {
        this.text(alignment, x, y, this.getTransformation(), text);
    }

    public void marqueedText(Text var1, int var2, int var3, int var4, int var5, int var6, Transformation var7);

    default public void marqueedText(Text text, int x, int left, int right, int top, int bottom) {
        this.marqueedText(text, x, left, right, top, bottom, this.getTransformation());
    }

    default public void text(Text text, int left, int right, int top, int bottom) {
        this.marqueedText(text, (left + right) / 2, left, right, top, bottom);
    }

    default public void marqueedText(Text text, int x, int left, int right, int top, int bottom, int width, int lineHeight, Transformation transformation) {
        int i = (top + bottom - lineHeight) / 2 + 1;
        int j = right - left;
        if (width > j) {
            int k = width - j;
            double d = (double)Util.getMeasuringTimeMs() / 1000.0;
            double e = Math.max((double)k * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(Math.PI * 2 * d / e)) / 2.0 + 0.5;
            double g = MathHelper.lerp((double)f, (double)0.0, (double)k);
            Transformation transformation2 = transformation.withScissor(left, right, top, bottom);
            this.text(Alignment.LEFT, left - (int)g, i, transformation2, text.asOrderedText());
        } else {
            int k = MathHelper.clamp((int)x, (int)(left + width / 2), (int)(right - width / 2));
            this.text(Alignment.CENTER, k, i, text);
        }
    }

    public static void handleHover(TextGuiElementRenderState renderState, float mouseX, float mouseY, Consumer<Style> styleCallback) {
        ScreenRect screenRect = renderState.bounds();
        if (screenRect == null || !screenRect.contains((int)mouseX, (int)mouseY)) {
            return;
        }
        Vector2f vector2fc = renderState.matrix.invert(new Matrix3x2f()).transformPosition(new Vector2f(mouseX, mouseY));
        float f = vector2fc.x();
        float g = vector2fc.y();
        renderState.prepare().draw((TextRenderer.GlyphDrawer)new /* Unavailable Anonymous Inner Class!! */);
    }

    public static boolean isWithinBounds(float x, float y, float left, float top, float right, float bottom) {
        return x >= left && x < right && y >= top && y < bottom;
    }
}

