/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static final class WorldListWidget.EmptyListEntry
extends WorldListWidget.Entry {
    private final TextWidget widget;

    public WorldListWidget.EmptyListEntry(Text text, TextRenderer textRenderer) {
        this.widget = new TextWidget(text, textRenderer);
    }

    @Override
    public Text getNarration() {
        return this.widget.getMessage();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        this.widget.setPosition(this.getContentMiddleX() - this.widget.getWidth() / 2, this.getContentMiddleY() - this.widget.getHeight() / 2);
        this.widget.render(context, mouseX, mouseY, deltaTicks);
    }
}
