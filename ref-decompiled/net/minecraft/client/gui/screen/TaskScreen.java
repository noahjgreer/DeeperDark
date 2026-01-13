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
 *  net.minecraft.client.gui.screen.LoadingDisplay
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.TaskScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TaskScreen
extends Screen {
    private static final int TITLE_TEXT_Y = 80;
    private static final int DESCRIPTION_TEXT_Y = 120;
    private static final int DESCRIPTION_TEXT_WIDTH = 360;
    private final @Nullable Text descriptionText;
    private final Text closeButtonText;
    private final Runnable closeCallback;
    private @Nullable MultilineText description;
    private ButtonWidget button;
    private int buttonCooldown;

    public static TaskScreen createRunningScreen(Text title, Text closeButtonText, Runnable closeCallback) {
        return new TaskScreen(title, null, closeButtonText, closeCallback, 0);
    }

    public static TaskScreen createResultScreen(Text title, Text descriptionText, Text closeButtonText, Runnable closeCallback) {
        return new TaskScreen(title, descriptionText, closeButtonText, closeCallback, 20);
    }

    protected TaskScreen(Text title, @Nullable Text descriptionText, Text closeButtonText, Runnable closeCallback, int buttonCooldown) {
        super(title);
        this.descriptionText = descriptionText;
        this.closeButtonText = closeButtonText;
        this.closeCallback = closeCallback;
        this.buttonCooldown = buttonCooldown;
    }

    protected void init() {
        super.init();
        if (this.descriptionText != null) {
            this.description = MultilineText.create((TextRenderer)this.textRenderer, (Text)this.descriptionText, (int)360);
        }
        int i = 150;
        int j = 20;
        int k = this.description != null ? this.description.getLineCount() : 1;
        int n = Math.max(k, 5);
        Objects.requireNonNull(this.textRenderer);
        int l = n * 9;
        int m = Math.min(120 + l, this.height - 40);
        this.button = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)this.closeButtonText, button -> this.close()).dimensions((this.width - 150) / 2, m, 150, 20).build());
    }

    public void tick() {
        if (this.buttonCooldown > 0) {
            --this.buttonCooldown;
        }
        this.button.active = this.buttonCooldown == 0;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        DrawnTextConsumer drawnTextConsumer = context.getTextConsumer();
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 80, -1);
        if (this.description == null) {
            String string = LoadingDisplay.get((long)Util.getMeasuringTimeMs());
            context.drawCenteredTextWithShadow(this.textRenderer, string, this.width / 2, 120, -6250336);
        } else {
            int n = this.width / 2;
            Objects.requireNonNull(this.textRenderer);
            this.description.draw(Alignment.CENTER, n, 120, 9, drawnTextConsumer);
        }
    }

    public boolean shouldCloseOnEsc() {
        return this.description != null && this.button.active;
    }

    public void close() {
        this.closeCallback.run();
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences((Text[])new Text[]{this.title, this.descriptionText != null ? this.descriptionText : ScreenTexts.EMPTY});
    }
}

