/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.screen.narration.NarrationPart
 *  net.minecraft.client.gui.widget.MultilineTextWidget
 *  net.minecraft.client.gui.widget.NarratedMultilineTextWidget
 *  net.minecraft.client.gui.widget.NarratedMultilineTextWidget$BackgroundRendering
 *  net.minecraft.client.gui.widget.NarratedMultilineTextWidget$Builder
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.math.ColorHelper
 */
package net.minecraft.client.gui.widget;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

@Environment(value=EnvType.CLIENT)
public class NarratedMultilineTextWidget
extends MultilineTextWidget {
    public static final int DEFAULT_MARGIN = 4;
    private final int margin;
    private final int customWidth;
    private final boolean alwaysShowBorders;
    private final BackgroundRendering backgroundRendering;

    NarratedMultilineTextWidget(Text text, TextRenderer textRenderer, int margin, int customWidth, BackgroundRendering backgroundRendering, boolean alwaysShowBorders) {
        super(text, textRenderer);
        this.active = true;
        this.margin = margin;
        this.customWidth = customWidth;
        this.alwaysShowBorders = alwaysShowBorders;
        this.backgroundRendering = backgroundRendering;
        this.updateWidth();
        this.updateHeight();
        this.setCentered(true);
    }

    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getMessage());
    }

    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int i = this.alwaysShowBorders && !this.isFocused() ? ColorHelper.withAlpha((float)this.alpha, (int)-6250336) : ColorHelper.getWhite((float)this.alpha);
        switch (this.backgroundRendering.ordinal()) {
            case 0: {
                context.fill(this.getX() + 1, this.getY(), this.getRight(), this.getBottom(), ColorHelper.toAlpha((float)this.alpha));
                break;
            }
            case 1: {
                if (!this.isFocused()) break;
                context.fill(this.getX() + 1, this.getY(), this.getRight(), this.getBottom(), ColorHelper.toAlpha((float)this.alpha));
                break;
            }
        }
        if (this.isFocused() || this.alwaysShowBorders) {
            context.drawStrokedRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight(), i);
        }
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
    }

    protected int getTextX() {
        return this.getX() + this.margin;
    }

    protected int getTextY() {
        return super.getTextY() + this.margin;
    }

    public MultilineTextWidget setMaxWidth(int maxWidth) {
        return super.setMaxWidth(maxWidth - this.margin * 2);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getMargin() {
        return this.margin;
    }

    public void updateWidth() {
        if (this.customWidth != -1) {
            this.setWidth(this.customWidth);
            this.setMaxWidth(this.customWidth);
        } else {
            this.setWidth(this.getTextRenderer().getWidth((StringVisitable)this.getMessage()) + this.margin * 2);
        }
    }

    public void updateHeight() {
        Objects.requireNonNull(this.getTextRenderer());
        int i = 9 * this.getTextRenderer().wrapLines((StringVisitable)this.getMessage(), super.getWidth()).size();
        this.setHeight(i + this.margin * 2);
    }

    public void setMessage(Text message) {
        this.message = message;
        int i = this.customWidth != -1 ? this.customWidth : this.getTextRenderer().getWidth((StringVisitable)message) + this.margin * 2;
        this.setWidth(i);
        this.updateHeight();
    }

    public void playDownSound(SoundManager soundManager) {
    }

    public static Builder builder(Text text, TextRenderer textRenderer) {
        return new Builder(text, textRenderer);
    }

    public static Builder builder(Text text, TextRenderer textRenderer, int margin) {
        return new Builder(text, textRenderer, margin);
    }
}

