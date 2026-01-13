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
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static class EditBoxWidget.Builder {
    private int x;
    private int y;
    private Text placeholder = ScreenTexts.EMPTY;
    private int textColor = -2039584;
    private boolean textShadow = true;
    private int cursorColor = -3092272;
    private boolean hasBackground = true;
    private boolean hasOverlay = true;

    public EditBoxWidget.Builder x(int x) {
        this.x = x;
        return this;
    }

    public EditBoxWidget.Builder y(int y) {
        this.y = y;
        return this;
    }

    public EditBoxWidget.Builder placeholder(Text placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public EditBoxWidget.Builder textColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public EditBoxWidget.Builder textShadow(boolean textShadow) {
        this.textShadow = textShadow;
        return this;
    }

    public EditBoxWidget.Builder cursorColor(int cursorColor) {
        this.cursorColor = cursorColor;
        return this;
    }

    public EditBoxWidget.Builder hasBackground(boolean hasBackground) {
        this.hasBackground = hasBackground;
        return this;
    }

    public EditBoxWidget.Builder hasOverlay(boolean hasOverlay) {
        this.hasOverlay = hasOverlay;
        return this;
    }

    public EditBoxWidget build(TextRenderer textRenderer, int width, int height, Text message) {
        return new EditBoxWidget(textRenderer, this.x, this.y, width, height, this.placeholder, message, this.textColor, this.textShadow, this.cursorColor, this.hasBackground, this.hasOverlay);
    }
}
