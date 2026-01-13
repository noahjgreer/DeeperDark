/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.Alignment
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.font.MultilineText
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.report.ChatSelectionScreen
 *  net.minecraft.client.gui.screen.report.ChatSelectionScreen$SelectionListWidget
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.session.report.AbuseReportContext
 *  net.minecraft.client.session.report.ChatAbuseReport$Builder
 *  net.minecraft.client.session.report.MessagesListAdder
 *  net.minecraft.client.session.report.MessagesListAdder$MessagesList
 *  net.minecraft.client.session.report.log.ReceivedMessage
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.report;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.ChatSelectionScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ChatAbuseReport;
import net.minecraft.client.session.report.MessagesListAdder;
import net.minecraft.client.session.report.log.ReceivedMessage;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChatSelectionScreen
extends Screen {
    static final Identifier CHECKMARK_ICON_TEXTURE = Identifier.ofVanilla((String)"icon/checkmark");
    private static final Text TITLE_TEXT = Text.translatable((String)"gui.chatSelection.title");
    private static final Text CONTEXT_TEXT = Text.translatable((String)"gui.chatSelection.context");
    private final @Nullable Screen parent;
    private final AbuseReportContext reporter;
    private ButtonWidget doneButton;
    private MultilineText contextMessage;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable SelectionListWidget selectionList;
    final ChatAbuseReport.Builder report;
    private final Consumer<ChatAbuseReport.Builder> newReportConsumer;
    private MessagesListAdder listAdder;

    public ChatSelectionScreen(@Nullable Screen parent, AbuseReportContext reporter, ChatAbuseReport.Builder report, Consumer<ChatAbuseReport.Builder> newReportConsumer) {
        super(TITLE_TEXT);
        this.parent = parent;
        this.reporter = reporter;
        this.report = report.copy();
        this.newReportConsumer = newReportConsumer;
    }

    protected void init() {
        this.listAdder = new MessagesListAdder(this.reporter, arg_0 -> this.isSentByReportedPlayer(arg_0));
        this.contextMessage = MultilineText.create((TextRenderer)this.textRenderer, (Text)CONTEXT_TEXT, (int)(this.width - 16));
        int n = this.contextMessage.getLineCount() + 1;
        Objects.requireNonNull(this.textRenderer);
        this.selectionList = (SelectionListWidget)this.addDrawableChild((Element)new SelectionListWidget(this, this.client, n * 9));
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.close()).dimensions(this.width / 2 - 155, this.height - 32, 150, 20).build());
        this.doneButton = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> {
            this.newReportConsumer.accept(this.report);
            this.close();
        }).dimensions(this.width / 2 - 155 + 160, this.height - 32, 150, 20).build());
        this.setDoneButtonActivation();
        this.addMessages();
        this.selectionList.setScrollY((double)this.selectionList.getMaxScrollY());
    }

    private boolean isSentByReportedPlayer(ReceivedMessage message) {
        return message.isSentFrom(this.report.getReportedPlayerUuid());
    }

    private void addMessages() {
        int i = this.selectionList.getDisplayedItemCount();
        this.listAdder.add(i, (MessagesListAdder.MessagesList)this.selectionList);
    }

    void addMoreMessages() {
        this.addMessages();
    }

    void setDoneButtonActivation() {
        this.doneButton.active = !this.report.getSelectedMessages().isEmpty();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        DrawnTextConsumer drawnTextConsumer = context.getTextConsumer();
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, -1);
        AbuseReportLimits abuseReportLimits = this.reporter.getSender().getLimits();
        int i = this.report.getSelectedMessages().size();
        int j = abuseReportLimits.maxReportedMessageCount();
        MutableText text = Text.translatable((String)"gui.chatSelection.selected", (Object[])new Object[]{i, j});
        context.drawCenteredTextWithShadow(this.textRenderer, (Text)text, this.width / 2, 26, -1);
        int k = this.selectionList.getContextMessageY();
        int n = this.width / 2;
        Objects.requireNonNull(this.textRenderer);
        this.contextMessage.draw(Alignment.CENTER, n, k, 9, drawnTextConsumer);
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences((Text[])new Text[]{super.getNarratedTitle(), CONTEXT_TEXT});
    }

    static /* synthetic */ TextRenderer method_44494(ChatSelectionScreen chatSelectionScreen) {
        return chatSelectionScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_44497(ChatSelectionScreen chatSelectionScreen) {
        return chatSelectionScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_44499(ChatSelectionScreen chatSelectionScreen) {
        return chatSelectionScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_44500(ChatSelectionScreen chatSelectionScreen) {
        return chatSelectionScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_44501(ChatSelectionScreen chatSelectionScreen) {
        return chatSelectionScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_44502(ChatSelectionScreen chatSelectionScreen) {
        return chatSelectionScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_44726(ChatSelectionScreen chatSelectionScreen) {
        return chatSelectionScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_44664(ChatSelectionScreen chatSelectionScreen) {
        return chatSelectionScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_44506(ChatSelectionScreen chatSelectionScreen) {
        return chatSelectionScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_44508(ChatSelectionScreen chatSelectionScreen) {
        return chatSelectionScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_44505(ChatSelectionScreen chatSelectionScreen) {
        return chatSelectionScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_44727(ChatSelectionScreen chatSelectionScreen) {
        return chatSelectionScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_44728(ChatSelectionScreen chatSelectionScreen) {
        return chatSelectionScreen.textRenderer;
    }
}

