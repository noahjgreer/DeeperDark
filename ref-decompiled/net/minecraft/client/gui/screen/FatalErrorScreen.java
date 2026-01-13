/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.FatalErrorScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class FatalErrorScreen
extends Screen {
    private final Text message;

    public FatalErrorScreen(Text title, Text message) {
        super(title);
        this.message = message;
    }

    protected void init() {
        super.init();
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.client.setScreen(null)).dimensions(this.width / 2 - 100, 140, 200, 20).build());
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 90, -1);
        context.drawCenteredTextWithShadow(this.textRenderer, this.message, this.width / 2, 110, -1);
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }
}

