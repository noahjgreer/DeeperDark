/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent
 *  net.minecraft.client.gui.tooltip.TooltipComponent
 *  net.minecraft.text.OrderedText
 */
package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.OrderedText;

@Environment(value=EnvType.CLIENT)
public class OrderedTextTooltipComponent
implements TooltipComponent {
    private final OrderedText text;

    public OrderedTextTooltipComponent(OrderedText text) {
        this.text = text;
    }

    public int getWidth(TextRenderer textRenderer) {
        return textRenderer.getWidth(this.text);
    }

    public int getHeight(TextRenderer textRenderer) {
        return 10;
    }

    public void drawText(DrawContext context, TextRenderer textRenderer, int x, int y) {
        context.drawText(textRenderer, this.text, x, y, -1, true);
    }
}

