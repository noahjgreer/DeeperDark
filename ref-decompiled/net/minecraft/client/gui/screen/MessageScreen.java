/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.MessageScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.NarratedMultilineTextWidget
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MessageScreen
extends Screen {
    private @Nullable NarratedMultilineTextWidget textWidget;

    public MessageScreen(Text text) {
        super(text);
    }

    protected void init() {
        this.textWidget = (NarratedMultilineTextWidget)this.addDrawableChild((Element)NarratedMultilineTextWidget.builder((Text)this.title, (TextRenderer)this.textRenderer, (int)12).innerWidth(this.textRenderer.getWidth((StringVisitable)this.title)).build());
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        if (this.textWidget != null) {
            int n = this.width / 2 - this.textWidget.getWidth() / 2;
            int n2 = this.height / 2;
            Objects.requireNonNull(this.textRenderer);
            this.textWidget.setPosition(n, n2 - 9 / 2);
        }
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    protected boolean hasUsageText() {
        return false;
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.renderPanoramaBackground(context, deltaTicks);
        this.applyBlur(context);
        this.renderDarkening(context);
    }
}

