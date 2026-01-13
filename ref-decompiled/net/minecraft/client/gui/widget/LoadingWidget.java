/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.navigation.GuiNavigation
 *  net.minecraft.client.gui.navigation.GuiNavigationPath
 *  net.minecraft.client.gui.screen.LoadingDisplay
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.LoadingWidget
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LoadingWidget
extends ClickableWidget {
    private final TextRenderer textRenderer;

    public LoadingWidget(TextRenderer textRenderer, Text message) {
        int n = textRenderer.getWidth((StringVisitable)message);
        Objects.requireNonNull(textRenderer);
        super(0, 0, n, 9 * 3, message);
        this.textRenderer = textRenderer;
    }

    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int i = this.getX() + this.getWidth() / 2;
        int j = this.getY() + this.getHeight() / 2;
        Text text = this.getMessage();
        int n = i - this.textRenderer.getWidth((StringVisitable)text) / 2;
        Objects.requireNonNull(this.textRenderer);
        context.drawTextWithShadow(this.textRenderer, text, n, j - 9, -1);
        String string = LoadingDisplay.get((long)Util.getMeasuringTimeMs());
        int n2 = i - this.textRenderer.getWidth(string) / 2;
        Objects.requireNonNull(this.textRenderer);
        context.drawTextWithShadow(this.textRenderer, string, n2, j + 9, -8355712);
    }

    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    public void playDownSound(SoundManager soundManager) {
    }

    public boolean isInteractable() {
        return false;
    }

    public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        return null;
    }
}

