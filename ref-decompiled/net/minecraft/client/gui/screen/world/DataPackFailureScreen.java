/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.Alignment
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.font.MultilineText
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.world.DataPackFailureScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen.world;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class DataPackFailureScreen
extends Screen {
    private MultilineText wrappedText = MultilineText.EMPTY;
    private final Runnable goBack;
    private final Runnable runServerInSafeMode;

    public DataPackFailureScreen(Runnable goBack, Runnable runServerInSafeMode) {
        super((Text)Text.translatable((String)"datapackFailure.title"));
        this.goBack = goBack;
        this.runServerInSafeMode = runServerInSafeMode;
    }

    protected void init() {
        super.init();
        this.wrappedText = MultilineText.create((TextRenderer)this.textRenderer, (Text)this.getTitle(), (int)(this.width - 50));
        this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"datapackFailure.safeMode"), button -> this.runServerInSafeMode.run()).dimensions(this.width / 2 - 155, this.height / 6 + 96, 150, 20).build());
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.goBack.run()).dimensions(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20).build());
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        DrawnTextConsumer drawnTextConsumer = context.getTextConsumer();
        int n = this.width / 2;
        Objects.requireNonNull(this.textRenderer);
        this.wrappedText.draw(Alignment.CENTER, n, 70, 9, drawnTextConsumer);
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }
}

