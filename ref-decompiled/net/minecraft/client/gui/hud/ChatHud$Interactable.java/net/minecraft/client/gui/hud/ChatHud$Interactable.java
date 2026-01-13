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
package net.minecraft.client.gui.hud;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class ChatHud.Interactable
implements ChatHud.Backend,
Consumer<Style> {
    private final DrawContext context;
    private final TextRenderer textRenderer;
    private final DrawnTextConsumer drawer;
    private DrawnTextConsumer.Transformation transformation;
    private final int mouseX;
    private final int mouseY;
    private final Vector2f untransformedOffset = new Vector2f();
    private @Nullable Style style;
    private final boolean field_64672;

    public ChatHud.Interactable(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY, boolean bl) {
        this.context = context;
        this.textRenderer = textRenderer;
        this.drawer = context.getTextConsumer(DrawContext.HoverType.TOOLTIP_AND_CURSOR, this);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.field_64672 = bl;
        this.transformation = this.drawer.getTransformation();
        this.calculateUntransformedOffset();
    }

    private void calculateUntransformedOffset() {
        this.context.getMatrices().invert(new Matrix3x2f()).transformPosition((float)this.mouseX, (float)this.mouseY, this.untransformedOffset);
    }

    @Override
    public void updatePose(Consumer<Matrix3x2f> transformer) {
        transformer.accept((Matrix3x2f)this.context.getMatrices());
        this.transformation = this.transformation.withPose((Matrix3x2fc)new Matrix3x2f((Matrix3x2fc)this.context.getMatrices()));
        this.calculateUntransformedOffset();
    }

    @Override
    public void fill(int x1, int y1, int x2, int y2, int color) {
        this.context.fill(x1, y1, x2, y2, color);
    }

    @Override
    public void accept(Style style) {
        this.style = style;
    }

    @Override
    public boolean text(int y, float opacity, OrderedText text) {
        this.style = null;
        this.drawer.text(Alignment.LEFT, 0, y, this.transformation.withOpacity(opacity), text);
        if (this.field_64672 && this.style != null && this.style.getInsertion() != null) {
            this.context.setCursor(StandardCursors.POINTING_HAND);
        }
        return this.style != null;
    }

    private boolean isWithinBounds(int left, int top, int right, int bottom) {
        return DrawnTextConsumer.isWithinBounds(this.untransformedOffset.x, this.untransformedOffset.y, left, top, right, bottom);
    }

    @Override
    public void indicator(int x1, int y1, int x2, int y2, float opacity, MessageIndicator indicator) {
        int i = ColorHelper.withAlpha(opacity, indicator.indicatorColor());
        this.context.fill(x1, y1, x2, y2, i);
        if (this.isWithinBounds(x1, y1, x2, y2)) {
            this.indicatorTooltip(indicator);
        }
    }

    @Override
    public void indicatorIcon(int left, int bottom, boolean forceDraw, MessageIndicator indicator, MessageIndicator.Icon icon) {
        int i = bottom - icon.height - 1;
        int j = left + icon.width;
        boolean bl = this.isWithinBounds(left, i, j, bottom);
        if (bl) {
            this.indicatorTooltip(indicator);
        }
        if (forceDraw || bl) {
            icon.draw(this.context, left, i);
        }
    }

    private void indicatorTooltip(MessageIndicator indicator) {
        if (indicator.text() != null) {
            this.context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(indicator.text(), 210), this.mouseX, this.mouseY);
        }
    }

    @Override
    public /* synthetic */ void accept(Object style) {
        this.accept((Style)style);
    }
}
