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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.sound.SoundManager;
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

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getMessage());
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int i = this.alwaysShowBorders && !this.isFocused() ? ColorHelper.withAlpha(this.alpha, -6250336) : ColorHelper.getWhite(this.alpha);
        switch (this.backgroundRendering.ordinal()) {
            case 0: {
                context.fill(this.getX() + 1, this.getY(), this.getRight(), this.getBottom(), ColorHelper.toAlpha(this.alpha));
                break;
            }
            case 1: {
                if (!this.isFocused()) break;
                context.fill(this.getX() + 1, this.getY(), this.getRight(), this.getBottom(), ColorHelper.toAlpha(this.alpha));
                break;
            }
        }
        if (this.isFocused() || this.alwaysShowBorders) {
            context.drawStrokedRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight(), i);
        }
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    protected int getTextX() {
        return this.getX() + this.margin;
    }

    @Override
    protected int getTextY() {
        return super.getTextY() + this.margin;
    }

    @Override
    public MultilineTextWidget setMaxWidth(int maxWidth) {
        return super.setMaxWidth(maxWidth - this.margin * 2);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
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
            this.setWidth(this.getTextRenderer().getWidth(this.getMessage()) + this.margin * 2);
        }
    }

    public void updateHeight() {
        int i = this.getTextRenderer().fontHeight * this.getTextRenderer().wrapLines(this.getMessage(), super.getWidth()).size();
        this.setHeight(i + this.margin * 2);
    }

    @Override
    public void setMessage(Text message) {
        this.message = message;
        int i = this.customWidth != -1 ? this.customWidth : this.getTextRenderer().getWidth(message) + this.margin * 2;
        this.setWidth(i);
        this.updateHeight();
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    public static Builder builder(Text text, TextRenderer textRenderer) {
        return new Builder(text, textRenderer);
    }

    public static Builder builder(Text text, TextRenderer textRenderer, int margin) {
        return new Builder(text, textRenderer, margin);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class BackgroundRendering
    extends Enum<BackgroundRendering> {
        public static final /* enum */ BackgroundRendering ALWAYS = new BackgroundRendering();
        public static final /* enum */ BackgroundRendering ON_FOCUS = new BackgroundRendering();
        public static final /* enum */ BackgroundRendering NEVER = new BackgroundRendering();
        private static final /* synthetic */ BackgroundRendering[] field_62120;

        public static BackgroundRendering[] values() {
            return (BackgroundRendering[])field_62120.clone();
        }

        public static BackgroundRendering valueOf(String string) {
            return Enum.valueOf(BackgroundRendering.class, string);
        }

        private static /* synthetic */ BackgroundRendering[] method_73391() {
            return new BackgroundRendering[]{ALWAYS, ON_FOCUS, NEVER};
        }

        static {
            field_62120 = BackgroundRendering.method_73391();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private final Text text;
        private final TextRenderer textRenderer;
        private final int margin;
        private int customWidth = -1;
        private boolean alwaysShowBorders = true;
        private BackgroundRendering backgroundRendering = BackgroundRendering.ALWAYS;

        Builder(Text text, TextRenderer textRenderer) {
            this(text, textRenderer, 4);
        }

        Builder(Text text, TextRenderer textRenderer, int margin) {
            this.text = text;
            this.textRenderer = textRenderer;
            this.margin = margin;
        }

        public Builder width(int width) {
            this.customWidth = width;
            return this;
        }

        public Builder innerWidth(int width) {
            this.customWidth = width + this.margin * 2;
            return this;
        }

        public Builder alwaysShowBorders(boolean alwaysShowBorders) {
            this.alwaysShowBorders = alwaysShowBorders;
            return this;
        }

        public Builder backgroundRendering(BackgroundRendering backgroundRendering) {
            this.backgroundRendering = backgroundRendering;
            return this;
        }

        public NarratedMultilineTextWidget build() {
            return new NarratedMultilineTextWidget(this.text, this.textRenderer, this.margin, this.customWidth, this.backgroundRendering, this.alwaysShowBorders);
        }
    }
}
