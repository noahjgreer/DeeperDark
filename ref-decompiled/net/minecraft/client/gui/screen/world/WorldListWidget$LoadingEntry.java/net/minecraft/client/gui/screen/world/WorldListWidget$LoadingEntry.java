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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public static class WorldListWidget.LoadingEntry
extends WorldListWidget.Entry {
    private static final Text LOADING_LIST_TEXT = Text.translatable("selectWorld.loading_list");
    private final MinecraftClient client;

    public WorldListWidget.LoadingEntry(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        int i = (this.client.currentScreen.width - this.client.textRenderer.getWidth(LOADING_LIST_TEXT)) / 2;
        int j = this.getContentY() + (this.getContentHeight() - this.client.textRenderer.fontHeight) / 2;
        context.drawTextWithShadow(this.client.textRenderer, LOADING_LIST_TEXT, i, j, -1);
        String string = LoadingDisplay.get(Util.getMeasuringTimeMs());
        int k = (this.client.currentScreen.width - this.client.textRenderer.getWidth(string)) / 2;
        int l = j + this.client.textRenderer.fontHeight;
        context.drawTextWithShadow(this.client.textRenderer, string, k, l, -8355712);
    }

    @Override
    public Text getNarration() {
        return LOADING_LIST_TEXT;
    }
}
