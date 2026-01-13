/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.pack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class PackListWidget.HeaderEntry
extends PackListWidget.Entry {
    private final TextRenderer textRenderer;
    private final Text text;

    public PackListWidget.HeaderEntry(PackListWidget packListWidget, TextRenderer textRenderer, Text text) {
        super(packListWidget);
        this.textRenderer = textRenderer;
        this.text = text;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        context.drawCenteredTextWithShadow(this.textRenderer, this.text, this.getX() + this.getWidth() / 2, this.getContentMiddleY() - this.textRenderer.fontHeight / 2, -1);
    }

    @Override
    public Text getNarration() {
        return this.text;
    }

    @Override
    public String getName() {
        return "";
    }
}
