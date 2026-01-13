/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

@Environment(value=EnvType.CLIENT)
public class RealmsGenericErrorScreen
extends RealmsScreen {
    private static final Text GENERIC_ERROR_TEXT = Text.translatable("mco.errorMessage.generic");
    private final Screen parent;
    private final Text detail;
    private MultilineText text = MultilineText.EMPTY;

    public RealmsGenericErrorScreen(RealmsServiceException realmsServiceException, Screen parent) {
        this(ErrorMessages.of(realmsServiceException), parent);
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
        this.detail = Texts.withStyle(messages.detail, Style.EMPTY.withColor(-2142128));
    }

    @Override
    public void init() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.OK, button -> this.close()).dimensions(this.width / 2 - 100, this.height - 52, 200, 20).build());
        this.text = MultilineText.create(this.textRenderer, this.detail, this.width * 3 / 4);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(super.getNarratedTitle(), this.detail);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 80, -1);
        DrawnTextConsumer drawnTextConsumer = context.getTextConsumer();
        this.text.draw(Alignment.CENTER, this.width / 2, 100, this.client.textRenderer.fontHeight, drawnTextConsumer);
    }

    @Environment(value=EnvType.CLIENT)
    static final class ErrorMessages
    extends Record {
        final Text title;
        final Text detail;

        ErrorMessages(Text title, Text detail) {
            this.title = title;
            this.detail = detail;
        }

        static ErrorMessages of(RealmsServiceException exception) {
            RealmsError realmsError = exception.error;
            return new ErrorMessages(Text.translatable("mco.errorMessage.realmsService.realmsError", realmsError.getErrorCode()), realmsError.getText());
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ErrorMessages.class, "title;detail", "title", "detail"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ErrorMessages.class, "title;detail", "title", "detail"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ErrorMessages.class, "title;detail", "title", "detail"}, this, object);
        }

        public Text title() {
            return this.title;
        }

        public Text detail() {
            return this.detail;
        }
    }
}
