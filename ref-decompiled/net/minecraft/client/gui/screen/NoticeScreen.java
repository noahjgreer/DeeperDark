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
 *  net.minecraft.client.gui.screen.NoticeScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.math.MathHelper
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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class NoticeScreen
extends Screen {
    private static final int NOTICE_TEXT_Y = 90;
    private final Text notice;
    private MultilineText noticeLines = MultilineText.EMPTY;
    private final Runnable actionHandler;
    private final Text buttonText;
    private final boolean shouldCloseOnEsc;

    public NoticeScreen(Runnable actionHandler, Text title, Text notice) {
        this(actionHandler, title, notice, ScreenTexts.BACK, true);
    }

    public NoticeScreen(Runnable actionHandler, Text title, Text notice, Text buttonText, boolean shouldCloseOnEsc) {
        super(title);
        this.actionHandler = actionHandler;
        this.notice = notice;
        this.buttonText = buttonText;
        this.shouldCloseOnEsc = shouldCloseOnEsc;
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences((Text[])new Text[]{super.getNarratedTitle(), this.notice});
    }

    protected void init() {
        super.init();
        this.noticeLines = MultilineText.create((TextRenderer)this.textRenderer, (Text)this.notice, (int)(this.width - 50));
        int n = this.noticeLines.getLineCount();
        Objects.requireNonNull(this.textRenderer);
        int i = n * 9;
        int j = MathHelper.clamp((int)(90 + i + 12), (int)(this.height / 6 + 96), (int)(this.height - 24));
        int k = 150;
        this.addDrawableChild((Element)ButtonWidget.builder((Text)this.buttonText, button -> this.actionHandler.run()).dimensions((this.width - 150) / 2, j, 150, 20).build());
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        DrawnTextConsumer drawnTextConsumer = context.getTextConsumer();
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 70, -1);
        int n = this.width / 2;
        Objects.requireNonNull(this.textRenderer);
        this.noticeLines.draw(Alignment.CENTER, n, 90, 9, drawnTextConsumer);
    }

    public boolean shouldCloseOnEsc() {
        return this.shouldCloseOnEsc;
    }
}

