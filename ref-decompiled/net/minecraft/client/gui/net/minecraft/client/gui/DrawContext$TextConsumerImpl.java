/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.TextGuiElementRenderState;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class DrawContext.TextConsumerImpl
implements DrawnTextConsumer,
Consumer<Style> {
    private DrawnTextConsumer.Transformation transformation;
    private final DrawContext.HoverType hoverType;
    private final @Nullable Consumer<Style> styleCallback;

    DrawContext.TextConsumerImpl(DrawnTextConsumer.Transformation transformation,  @Nullable DrawContext.HoverType hoverType, Consumer<Style> styleCallback) {
        this.transformation = transformation;
        this.hoverType = hoverType;
        this.styleCallback = styleCallback;
    }

    @Override
    public DrawnTextConsumer.Transformation getTransformation() {
        return this.transformation;
    }

    @Override
    public void setTransformation(DrawnTextConsumer.Transformation transformation) {
        this.transformation = transformation;
    }

    @Override
    public void accept(Style style) {
        if (this.hoverType.tooltip && style.getHoverEvent() != null) {
            DrawContext.this.hoverStyle = style;
        }
        if (this.hoverType.cursor && style.getClickEvent() != null) {
            DrawContext.this.clickStyle = style;
        }
        if (this.styleCallback != null) {
            this.styleCallback.accept(style);
        }
    }

    @Override
    public void text(Alignment alignment, int x, int y, DrawnTextConsumer.Transformation transformation, OrderedText text) {
        boolean bl = this.hoverType.cursor || this.hoverType.tooltip || this.styleCallback != null;
        int i = alignment.getAdjustedX(x, DrawContext.this.client.textRenderer, text);
        TextGuiElementRenderState textGuiElementRenderState = new TextGuiElementRenderState(DrawContext.this.client.textRenderer, text, transformation.pose(), i, y, ColorHelper.getWhite(transformation.opacity()), 0, true, bl, transformation.scissor());
        if (ColorHelper.channelFromFloat(transformation.opacity()) != 0) {
            DrawContext.this.state.addText(textGuiElementRenderState);
        }
        if (bl) {
            DrawnTextConsumer.handleHover(textGuiElementRenderState, DrawContext.this.mouseX, DrawContext.this.mouseY, this);
        }
    }

    @Override
    public void marqueedText(Text text, int x, int left, int right, int top, int bottom, DrawnTextConsumer.Transformation transformation) {
        int i = DrawContext.this.client.textRenderer.getWidth(text);
        int j = DrawContext.this.client.textRenderer.fontHeight;
        this.marqueedText(text, x, left, right, top, bottom, i, j, transformation);
    }

    @Override
    public /* synthetic */ void accept(Object style) {
        this.accept((Style)style);
    }
}
