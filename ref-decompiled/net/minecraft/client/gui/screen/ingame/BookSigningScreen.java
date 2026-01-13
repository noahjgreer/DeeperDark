/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.BookEditScreen
 *  net.minecraft.client.gui.screen.ingame.BookScreen
 *  net.minecraft.client.gui.screen.ingame.BookSigningScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Hand
 *  net.minecraft.util.StringHelper
 */
package net.minecraft.client.gui.screen.ingame;

import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.StringHelper;

@Environment(value=EnvType.CLIENT)
public class BookSigningScreen
extends Screen {
    private static final Text EDIT_TITLE_TEXT = Text.translatable((String)"book.editTitle");
    private static final Text FINALIZE_WARNING_TEXT = Text.translatable((String)"book.finalizeWarning");
    private static final Text TITLE_TEXT = Text.translatable((String)"book.sign.title");
    private static final Text TITLE_BOX_TEXT = Text.translatable((String)"book.sign.titlebox");
    private final BookEditScreen editScreen;
    private final PlayerEntity player;
    private final List<String> pages;
    private final Hand hand;
    private final Text bylineText;
    private TextFieldWidget bookTitleTextField;
    private String bookTitle = "";

    public BookSigningScreen(BookEditScreen editScreen, PlayerEntity player, Hand hand, List<String> pages) {
        super(TITLE_TEXT);
        this.editScreen = editScreen;
        this.player = player;
        this.hand = hand;
        this.pages = pages;
        this.bylineText = Text.translatable((String)"book.byAuthor", (Object[])new Object[]{player.getName()}).formatted(Formatting.DARK_GRAY);
    }

    protected void init() {
        ButtonWidget buttonWidget = ButtonWidget.builder((Text)Text.translatable((String)"book.finalizeButton"), button -> {
            this.onFinalize();
            this.client.setScreen(null);
        }).dimensions(this.width / 2 - 100, 196, 98, 20).build();
        buttonWidget.active = false;
        this.bookTitleTextField = (TextFieldWidget)this.addDrawableChild((Element)new TextFieldWidget(this.client.textRenderer, (this.width - 114) / 2 - 3, 50, 114, 20, TITLE_BOX_TEXT));
        this.bookTitleTextField.setMaxLength(15);
        this.bookTitleTextField.setDrawsBackground(false);
        this.bookTitleTextField.setCentered(true);
        this.bookTitleTextField.setEditableColor(-16777216);
        this.bookTitleTextField.setTextShadow(false);
        this.bookTitleTextField.setChangedListener(bookTitle -> {
            buttonWidget.active = !StringHelper.isBlank((String)bookTitle);
        });
        this.bookTitleTextField.setText(this.bookTitle);
        this.addDrawableChild((Element)buttonWidget);
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> {
            this.bookTitle = this.bookTitleTextField.getText();
            this.client.setScreen((Screen)this.editScreen);
        }).dimensions(this.width / 2 + 2, 196, 98, 20).build());
    }

    protected void setInitialFocus() {
        this.setInitialFocus((Element)this.bookTitleTextField);
    }

    private void onFinalize() {
        int i = this.hand == Hand.MAIN_HAND ? this.player.getInventory().getSelectedSlot() : 40;
        this.client.getNetworkHandler().sendPacket((Packet)new BookUpdateC2SPacket(i, this.pages, Optional.of(this.bookTitleTextField.getText().trim())));
    }

    public boolean deferSubtitles() {
        return true;
    }

    public boolean keyPressed(KeyInput input) {
        if (this.bookTitleTextField.isFocused() && !this.bookTitleTextField.getText().isEmpty() && input.isEnter()) {
            this.onFinalize();
            this.client.setScreen(null);
            return true;
        }
        return super.keyPressed(input);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        int i = (this.width - 192) / 2;
        int j = 2;
        int k = this.textRenderer.getWidth((StringVisitable)EDIT_TITLE_TEXT);
        context.drawText(this.textRenderer, EDIT_TITLE_TEXT, i + 36 + (114 - k) / 2, 34, -16777216, false);
        int l = this.textRenderer.getWidth((StringVisitable)this.bylineText);
        context.drawText(this.textRenderer, this.bylineText, i + 36 + (114 - l) / 2, 60, -16777216, false);
        context.drawWrappedText(this.textRenderer, (StringVisitable)FINALIZE_WARNING_TEXT, i + 36, 82, 114, -16777216, false);
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderBackground(context, mouseX, mouseY, deltaTicks);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, BookScreen.BOOK_TEXTURE, (this.width - 192) / 2, 2, 0.0f, 0.0f, 192, 192, 256, 256);
    }
}

