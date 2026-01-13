/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static class NarratedMultilineTextWidget.Builder {
    private final Text text;
    private final TextRenderer textRenderer;
    private final int margin;
    private int customWidth = -1;
    private boolean alwaysShowBorders = true;
    private NarratedMultilineTextWidget.BackgroundRendering backgroundRendering = NarratedMultilineTextWidget.BackgroundRendering.ALWAYS;

    NarratedMultilineTextWidget.Builder(Text text, TextRenderer textRenderer) {
        this(text, textRenderer, 4);
    }

    NarratedMultilineTextWidget.Builder(Text text, TextRenderer textRenderer, int margin) {
        this.text = text;
        this.textRenderer = textRenderer;
        this.margin = margin;
    }

    public NarratedMultilineTextWidget.Builder width(int width) {
        this.customWidth = width;
        return this;
    }

    public NarratedMultilineTextWidget.Builder innerWidth(int width) {
        this.customWidth = width + this.margin * 2;
        return this;
    }

    public NarratedMultilineTextWidget.Builder alwaysShowBorders(boolean alwaysShowBorders) {
        this.alwaysShowBorders = alwaysShowBorders;
        return this;
    }

    public NarratedMultilineTextWidget.Builder backgroundRendering(NarratedMultilineTextWidget.BackgroundRendering backgroundRendering) {
        this.backgroundRendering = backgroundRendering;
        return this;
    }

    public NarratedMultilineTextWidget build() {
        return new NarratedMultilineTextWidget(this.text, this.textRenderer, this.margin, this.customWidth, this.backgroundRendering, this.alwaysShowBorders);
    }
}
