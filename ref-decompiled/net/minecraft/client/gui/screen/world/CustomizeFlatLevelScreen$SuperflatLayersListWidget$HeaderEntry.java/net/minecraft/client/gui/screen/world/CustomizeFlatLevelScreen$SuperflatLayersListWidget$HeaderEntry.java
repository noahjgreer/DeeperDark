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
import net.minecraft.client.gui.screen.world.CustomizeFlatLevelScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
static class CustomizeFlatLevelScreen.SuperflatLayersListWidget.HeaderEntry
extends CustomizeFlatLevelScreen.SuperflatLayersListWidget.Entry {
    private final TextRenderer textRenderer;

    public CustomizeFlatLevelScreen.SuperflatLayersListWidget.HeaderEntry(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        context.drawTextWithShadow(this.textRenderer, LAYER_MATERIAL_TEXT, this.getContentX(), this.getContentY(), -1);
        context.drawTextWithShadow(this.textRenderer, HEIGHT_TEXT, this.getContentRightEnd() - this.textRenderer.getWidth(HEIGHT_TEXT), this.getContentY(), -1);
    }

    @Override
    public Text getNarration() {
        return ScreenTexts.joinSentences(LAYER_MATERIAL_TEXT, HEIGHT_TEXT);
    }
}
