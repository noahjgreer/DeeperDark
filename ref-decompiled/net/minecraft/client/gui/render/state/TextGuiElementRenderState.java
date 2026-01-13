/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.font.TextRenderer$GlyphDrawable
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.GuiElementRenderState
 *  net.minecraft.client.gui.render.state.TextGuiElementRenderState
 *  net.minecraft.text.OrderedText
 *  org.joml.Matrix3x2fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.text.OrderedText;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class TextGuiElementRenderState
implements GuiElementRenderState {
    public final TextRenderer textRenderer;
    public final OrderedText orderedText;
    public final Matrix3x2fc matrix;
    public final int x;
    public final int y;
    public final int color;
    public final int backgroundColor;
    public final boolean shadow;
    final boolean trackEmpty;
    public final @Nullable ScreenRect clipBounds;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable TextRenderer.GlyphDrawable preparation;
    private @Nullable ScreenRect bounds;

    public TextGuiElementRenderState(TextRenderer textRenderer, OrderedText orderedText, Matrix3x2fc matrix, int x, int y, int color, int backgroundColor, boolean shadow, boolean trackEmpty, @Nullable ScreenRect clipBounds) {
        this.textRenderer = textRenderer;
        this.orderedText = orderedText;
        this.matrix = matrix;
        this.x = x;
        this.y = y;
        this.color = color;
        this.backgroundColor = backgroundColor;
        this.shadow = shadow;
        this.trackEmpty = trackEmpty;
        this.clipBounds = clipBounds;
    }

    public TextRenderer.GlyphDrawable prepare() {
        if (this.preparation == null) {
            this.preparation = this.textRenderer.prepare(this.orderedText, (float)this.x, (float)this.y, this.color, this.shadow, this.trackEmpty, this.backgroundColor);
            ScreenRect screenRect = this.preparation.getScreenRect();
            if (screenRect != null) {
                screenRect = screenRect.transformEachVertex(this.matrix);
                this.bounds = this.clipBounds != null ? this.clipBounds.intersection(screenRect) : screenRect;
            }
        }
        return this.preparation;
    }

    public @Nullable ScreenRect bounds() {
        this.prepare();
        return this.bounds;
    }
}

