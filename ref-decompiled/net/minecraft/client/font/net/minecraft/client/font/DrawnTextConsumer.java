/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fc
 *  org.joml.Vector2f
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.EmptyGlyphRect;
import net.minecraft.client.font.GlyphRect;
import net.minecraft.client.font.TextDrawable;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.TextGuiElementRenderState;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;
import org.jspecify.annotations.Nullable;

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
            double g = MathHelper.lerp(f, 0.0, (double)k);
            Transformation transformation2 = transformation.withScissor(left, right, top, bottom);
            this.text(Alignment.LEFT, left - (int)g, i, transformation2, text.asOrderedText());
        } else {
            int k = MathHelper.clamp(x, left + width / 2, right - width / 2);
            this.text(Alignment.CENTER, k, i, text);
        }
    }

    public static void handleHover(TextGuiElementRenderState renderState, float mouseX, float mouseY, final Consumer<Style> styleCallback) {
        ScreenRect screenRect = renderState.bounds();
        if (screenRect == null || !screenRect.contains((int)mouseX, (int)mouseY)) {
            return;
        }
        Vector2f vector2fc = renderState.matrix.invert(new Matrix3x2f()).transformPosition(new Vector2f(mouseX, mouseY));
        final float f = vector2fc.x();
        final float g = vector2fc.y();
        renderState.prepare().draw(new TextRenderer.GlyphDrawer(){

            @Override
            public void drawGlyph(TextDrawable.DrawnGlyphRect glyph) {
                this.addGlyphInternal(glyph);
            }

            @Override
            public void drawEmptyGlyphRect(EmptyGlyphRect rect) {
                this.addGlyphInternal(rect);
            }

            private void addGlyphInternal(GlyphRect glyph) {
                if (DrawnTextConsumer.isWithinBounds(f, g, glyph.getLeft(), glyph.getTop(), glyph.getRight(), glyph.getBottom())) {
                    styleCallback.accept(glyph.style());
                }
            }
        });
    }

    public static boolean isWithinBounds(float x, float y, float left, float top, float right, float bottom) {
        return x >= left && x < right && y >= top && y < bottom;
    }

    @Environment(value=EnvType.CLIENT)
    public record Transformation(Matrix3x2fc pose, float opacity, @Nullable ScreenRect scissor) {
        public Transformation(Matrix3x2fc pose) {
            this(pose, 1.0f, null);
        }

        public Transformation withPose(Matrix3x2fc pose) {
            return new Transformation(pose, this.opacity, this.scissor);
        }

        public Transformation scaled(float scale) {
            return this.withPose((Matrix3x2fc)this.pose.scale(scale, scale, new Matrix3x2f()));
        }

        public Transformation withOpacity(float opacity) {
            if (this.opacity == opacity) {
                return this;
            }
            return new Transformation(this.pose, opacity, this.scissor);
        }

        public Transformation withScissor(ScreenRect scissor) {
            if (scissor.equals(this.scissor)) {
                return this;
            }
            return new Transformation(this.pose, this.opacity, scissor);
        }

        public Transformation withScissor(int left, int right, int top, int bottom) {
            ScreenRect screenRect = new ScreenRect(left, top, right - left, bottom - top).transform(this.pose);
            if (this.scissor != null) {
                screenRect = Objects.requireNonNullElse(this.scissor.intersection(screenRect), ScreenRect.empty());
            }
            return this.withScissor(screenRect);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class ClickHandler
    implements DrawnTextConsumer {
        private static final Transformation DEFAULT_TRANSFORMATION = new Transformation((Matrix3x2fc)new Matrix3x2f());
        private final TextRenderer textRenderer;
        private final int clickX;
        private final int clickY;
        private Transformation transformation = DEFAULT_TRANSFORMATION;
        private boolean insert;
        private @Nullable Style style;
        private final Consumer<Style> setStyleCallback = style -> {
            if (style.getClickEvent() != null || this.insert && style.getInsertion() != null) {
                this.style = style;
            }
        };

        public ClickHandler(TextRenderer textRenderer, int clickX, int clickY) {
            this.textRenderer = textRenderer;
            this.clickX = clickX;
            this.clickY = clickY;
        }

        @Override
        public Transformation getTransformation() {
            return this.transformation;
        }

        @Override
        public void setTransformation(Transformation transformation) {
            this.transformation = transformation;
        }

        @Override
        public void text(Alignment alignment, int x, int y, Transformation transformation, OrderedText text) {
            int i = alignment.getAdjustedX(x, this.textRenderer, text);
            TextGuiElementRenderState textGuiElementRenderState = new TextGuiElementRenderState(this.textRenderer, text, transformation.pose(), i, y, ColorHelper.getWhite(transformation.opacity()), 0, true, true, transformation.scissor());
            DrawnTextConsumer.handleHover(textGuiElementRenderState, this.clickX, this.clickY, this.setStyleCallback);
        }

        @Override
        public void marqueedText(Text text, int x, int left, int right, int top, int bottom, Transformation transformation) {
            int i = this.textRenderer.getWidth(text);
            int j = this.textRenderer.fontHeight;
            this.marqueedText(text, x, left, right, top, bottom, i, j, transformation);
        }

        public ClickHandler insert(boolean insert) {
            this.insert = insert;
            return this;
        }

        public @Nullable Style getStyle() {
            return this.style;
        }
    }
}
