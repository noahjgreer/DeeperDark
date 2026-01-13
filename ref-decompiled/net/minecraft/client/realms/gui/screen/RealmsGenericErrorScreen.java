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
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.realms.exception.RealmsServiceException
 *  net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen
 *  net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen$ErrorMessages
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.text.Texts
 */
package net.minecraft.client.realms.gui.screen;

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
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsGenericErrorScreen
extends RealmsScreen {
    private static final Text GENERIC_ERROR_TEXT = Text.translatable((String)"mco.errorMessage.generic");
    private final Screen parent;
    private final Text detail;
    private MultilineText text = MultilineText.EMPTY;

    public RealmsGenericErrorScreen(RealmsServiceException realmsServiceException, Screen parent) {
        this(ErrorMessages.of((RealmsServiceException)realmsServiceException), parent);
    }

    public RealmsGenericErrorScreen(Text description, Screen parent) {
        this(new ErrorMessages(GENERIC_ERROR_TEXT, description), parent);
    }

    public RealmsGenericErrorScreen(Text title, Text description, Screen parent) {
        this(new ErrorMessages(title, description), parent);
    }

    private RealmsGenericErrorScreen(ErrorMessages messages, Screen parent) {
        super(messages.title);
        this.parent = parent;
        this.detail = Texts.withStyle((Text)messages.detail, (Style)Style.EMPTY.withColor(-2142128));
    }

    public void init() {
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.OK, button -> this.close()).dimensions(this.width / 2 - 100, this.height - 52, 200, 20).build());
        this.text = MultilineText.create((TextRenderer)this.textRenderer, (Text)this.detail, (int)(this.width * 3 / 4));
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences((Text[])new Text[]{super.getNarratedTitle(), this.detail});
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 80, -1);
        DrawnTextConsumer drawnTextConsumer = context.getTextConsumer();
        int n = this.width / 2;
        Objects.requireNonNull(this.client.textRenderer);
        this.text.draw(Alignment.CENTER, n, 100, 9, drawnTextConsumer);
    }
}

