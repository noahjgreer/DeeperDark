/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.Alignment
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.font.MultilineText
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.GraphicsWarningScreen
 *  net.minecraft.client.gui.screen.GraphicsWarningScreen$ChoiceButton
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.text.Texts
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.GraphicsWarningScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

@Environment(value=EnvType.CLIENT)
public class GraphicsWarningScreen
extends Screen {
    private static final int BUTTON_PADDING = 20;
    private static final int BUTTON_MARGIN = 5;
    private static final int BUTTON_HEIGHT = 20;
    private final Text narrationMessage;
    private final List<Text> message;
    private final ImmutableList<ChoiceButton> choiceButtons;
    private MultilineText lines = MultilineText.EMPTY;
    private int linesY;
    private int buttonWidth;

    protected GraphicsWarningScreen(Text title, List<Text> messages, ImmutableList<ChoiceButton> choiceButtons) {
        super(title);
        this.message = messages;
        this.narrationMessage = ScreenTexts.joinSentences((Text[])new Text[]{title, Texts.join(messages, (Text)ScreenTexts.EMPTY)});
        this.choiceButtons = choiceButtons;
    }

    public Text getNarratedTitle() {
        return this.narrationMessage;
    }

    public void init() {
        for (ChoiceButton choiceButton : this.choiceButtons) {
            this.buttonWidth = Math.max(this.buttonWidth, 20 + this.textRenderer.getWidth((StringVisitable)choiceButton.message) + 20);
        }
        int i = 5 + this.buttonWidth + 5;
        int j = i * this.choiceButtons.size();
        this.lines = MultilineText.create((TextRenderer)this.textRenderer, (int)j, (Text[])this.message.toArray(new Text[0]));
        int n = this.lines.getLineCount();
        Objects.requireNonNull(this.textRenderer);
        int k = n * 9;
        this.linesY = (int)((double)this.height / 2.0 - (double)k / 2.0);
        Objects.requireNonNull(this.textRenderer);
        int l = this.linesY + k + 9 * 2;
        int m = (int)((double)this.width / 2.0 - (double)j / 2.0);
        for (ChoiceButton choiceButton2 : this.choiceButtons) {
            this.addDrawableChild((Element)ButtonWidget.builder((Text)choiceButton2.message, (ButtonWidget.PressAction)choiceButton2.pressAction).dimensions(m, l, this.buttonWidth, 20).build());
            m += i;
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        DrawnTextConsumer drawnTextConsumer = context.getTextConsumer();
        int n = this.width / 2;
        Objects.requireNonNull(this.textRenderer);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, n, this.linesY - 9 * 2, -1);
        int n2 = this.width / 2;
        Objects.requireNonNull(this.textRenderer);
        this.lines.draw(Alignment.CENTER, n2, this.linesY, 9, drawnTextConsumer);
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }
}

