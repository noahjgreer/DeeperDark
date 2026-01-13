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
 *  net.minecraft.client.gui.widget.ScrollableTextFieldWidget
 *  net.minecraft.client.gui.widget.ScrollableTextWidget
 *  net.minecraft.text.Text
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
import net.minecraft.client.gui.widget.ScrollableTextFieldWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ScrollableTextWidget
extends ScrollableTextFieldWidget {
    private final TextRenderer textRenderer;
    private final MultilineTextWidget wrapped;

    public ScrollableTextWidget(int x, int y, int width, int height, Text message, TextRenderer textRenderer) {
        super(x, y, width, height, message);
        this.textRenderer = textRenderer;
        this.wrapped = new MultilineTextWidget(message, textRenderer).setMaxWidth(this.getWidth() - this.getPadding());
    }

    public void setWidth(int width) {
        super.setWidth(width);
        this.wrapped.setMaxWidth(this.getWidth() - this.getPadding());
    }

    protected int getContentsHeight() {
        return this.wrapped.getHeight();
    }

    public void updateHeight() {
        if (!this.textOverflows()) {
            this.setHeight(this.getContentsHeight() + this.getPadding());
        }
    }

    protected double getDeltaYPerScroll() {
        Objects.requireNonNull(this.textRenderer);
        return 9.0;
    }

    protected void drawBox(DrawContext context) {
        super.drawBox(context);
    }

    public boolean textOverflows() {
        return super.overflows();
    }

    protected void renderContents(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float)this.getTextX(), (float)this.getTextY());
        this.wrapped.render(context, mouseX, mouseY, deltaTicks);
        context.getMatrices().popMatrix();
    }

    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getMessage());
    }

    public void setMessage(Text message) {
        super.setMessage(message);
        this.wrapped.setMessage(message);
    }
}

