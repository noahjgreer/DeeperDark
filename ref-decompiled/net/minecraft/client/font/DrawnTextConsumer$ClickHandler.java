/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.render.state.TextGuiElementRenderState;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class DrawnTextConsumer.ClickHandler
implements DrawnTextConsumer {
    private static final DrawnTextConsumer.Transformation DEFAULT_TRANSFORMATION = new DrawnTextConsumer.Transformation((Matrix3x2fc)new Matrix3x2f());
    private final TextRenderer textRenderer;
    private final int clickX;
    private final int clickY;
    private DrawnTextConsumer.Transformation transformation = DEFAULT_TRANSFORMATION;
    private boolean insert;
    private @Nullable Style style;
    private final Consumer<Style> setStyleCallback = style -> {
        if (style.getClickEvent() != null || this.insert && style.getInsertion() != null) {
            this.style = style;
        }
    };

    public DrawnTextConsumer.ClickHandler(TextRenderer textRenderer, int clickX, int clickY) {
        this.textRenderer = textRenderer;
        this.clickX = clickX;
        this.clickY = clickY;
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
    public void text(Alignment alignment, int x, int y, DrawnTextConsumer.Transformation transformation, OrderedText text) {
        int i = alignment.getAdjustedX(x, this.textRenderer, text);
        TextGuiElementRenderState textGuiElementRenderState = new TextGuiElementRenderState(this.textRenderer, text, transformation.pose(), i, y, ColorHelper.getWhite(transformation.opacity()), 0, true, true, transformation.scissor());
        DrawnTextConsumer.handleHover(textGuiElementRenderState, this.clickX, this.clickY, this.setStyleCallback);
    }

    @Override
    public void marqueedText(Text text, int x, int left, int right, int top, int bottom, DrawnTextConsumer.Transformation transformation) {
        int i = this.textRenderer.getWidth(text);
        int j = this.textRenderer.fontHeight;
        this.marqueedText(text, x, left, right, top, bottom, i, j, transformation);
    }

    public DrawnTextConsumer.ClickHandler insert(boolean insert) {
        this.insert = insert;
        return this;
    }

    public @Nullable Style getStyle() {
        return this.style;
    }
}
