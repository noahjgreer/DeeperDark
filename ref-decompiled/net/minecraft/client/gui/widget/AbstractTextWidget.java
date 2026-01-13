/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.font.DrawnTextConsumer$ClickHandler
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.DrawContext$HoverType
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.widget.AbstractTextWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractTextWidget
extends ClickableWidget {
    private @Nullable Consumer<Style> clickedStyleConsumer = null;
    private final TextRenderer textRenderer;

    public AbstractTextWidget(int x, int y, int width, int height, Text message, TextRenderer textRenderer) {
        super(x, y, width, height, message);
        this.textRenderer = textRenderer;
    }

    public abstract void draw(DrawnTextConsumer var1);

    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        DrawContext.HoverType hoverType = this.isHovered() ? (this.clickedStyleConsumer != null ? DrawContext.HoverType.TOOLTIP_AND_CURSOR : DrawContext.HoverType.TOOLTIP_ONLY) : DrawContext.HoverType.NONE;
        this.draw(context.getHoverListener((ClickableWidget)this, hoverType));
    }

    public void onClick(Click click, boolean doubled) {
        if (this.clickedStyleConsumer != null) {
            DrawnTextConsumer.ClickHandler clickHandler = new DrawnTextConsumer.ClickHandler(this.getTextRenderer(), (int)click.x(), (int)click.y());
            this.draw((DrawnTextConsumer)clickHandler);
            Style style = clickHandler.getStyle();
            if (style != null) {
                this.clickedStyleConsumer.accept(style);
                return;
            }
        }
        super.onClick(click, doubled);
    }

    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    protected final TextRenderer getTextRenderer() {
        return this.textRenderer;
    }

    public void setMessage(Text message) {
        super.setMessage(message);
        this.setWidth(this.getTextRenderer().getWidth(message.asOrderedText()));
    }

    public AbstractTextWidget onClick(@Nullable Consumer<Style> clickedStyleConsumer) {
        this.clickedStyleConsumer = clickedStyleConsumer;
        return this;
    }
}

