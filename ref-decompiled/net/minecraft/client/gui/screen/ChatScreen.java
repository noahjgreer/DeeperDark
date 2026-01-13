/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.font.DrawnTextConsumer$ClickHandler
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.hud.ChatHud
 *  net.minecraft.client.gui.screen.ChatInputSuggestor
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.client.gui.screen.ChatScreen$CloseReason
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.screen.narration.NarrationPart
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.network.message.MessageHandler
 *  net.minecraft.text.ClickEvent
 *  net.minecraft.text.ClickEvent$Custom
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.StringHelper
 *  net.minecraft.util.math.MathHelper
 *  org.apache.commons.lang3.StringUtils
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ChatScreen
extends Screen {
    public static final double SHIFT_SCROLL_AMOUNT = 7.0;
    private static final Text USAGE_TEXT = Text.translatable((String)"chat_screen.usage");
    private String chatLastMessage = "";
    private int messageHistoryIndex = -1;
    protected TextFieldWidget chatField;
    protected String originalChatText;
    protected boolean draft;
    protected CloseReason closeReason = CloseReason.INTERRUPTED;
    ChatInputSuggestor chatInputSuggestor;

    public ChatScreen(String text, boolean draft) {
        super((Text)Text.translatable((String)"chat_screen.title"));
        this.originalChatText = text;
        this.draft = draft;
    }

    protected void init() {
        this.messageHistoryIndex = this.client.inGameHud.getChatHud().getMessageHistory().size();
        this.chatField = new /* Unavailable Anonymous Inner Class!! */;
        this.chatField.setMaxLength(256);
        this.chatField.setDrawsBackground(false);
        this.chatField.setText(this.originalChatText);
        this.chatField.setChangedListener(arg_0 -> this.onChatFieldUpdate(arg_0));
        this.chatField.addFormatter((arg_0, arg_1) -> this.format(arg_0, arg_1));
        this.chatField.setFocusUnlocked(false);
        this.addDrawableChild((Element)this.chatField);
        this.chatInputSuggestor = new ChatInputSuggestor(this.client, (Screen)this, this.chatField, this.textRenderer, false, false, 1, 10, true, -805306368);
        this.chatInputSuggestor.setCanLeave(false);
        this.chatInputSuggestor.setWindowActive(false);
        this.chatInputSuggestor.refresh();
    }

    protected void setInitialFocus() {
        this.setInitialFocus((Element)this.chatField);
    }

    public void resize(int width, int height) {
        this.originalChatText = this.chatField.getText();
        this.init(width, height);
    }

    public void close() {
        this.closeReason = CloseReason.INTENTIONAL;
        super.close();
    }

    public void removed() {
        this.client.inGameHud.getChatHud().resetScroll();
        this.originalChatText = this.chatField.getText();
        if (this.shouldNotSaveDraft() || StringUtils.isBlank((CharSequence)this.originalChatText)) {
            this.client.inGameHud.getChatHud().discardDraft();
        } else if (!this.draft) {
            this.client.inGameHud.getChatHud().saveDraft(this.originalChatText);
        }
    }

    protected boolean shouldNotSaveDraft() {
        return this.closeReason != CloseReason.INTERRUPTED && (this.closeReason != CloseReason.INTENTIONAL || (Boolean)this.client.options.getChatDrafts().getValue() == false);
    }

    private void onChatFieldUpdate(String chatText) {
        this.chatInputSuggestor.setWindowActive(true);
        this.chatInputSuggestor.refresh();
        this.draft = false;
    }

    public boolean keyPressed(KeyInput input) {
        if (this.chatInputSuggestor.keyPressed(input)) {
            return true;
        }
        if (this.draft && input.key() == 259) {
            this.chatField.setText("");
            this.draft = false;
            return true;
        }
        if (super.keyPressed(input)) {
            return true;
        }
        if (input.isEnter()) {
            this.sendMessage(this.chatField.getText(), true);
            this.closeReason = CloseReason.DONE;
            this.client.setScreen(null);
            return true;
        }
        switch (input.key()) {
            case 265: {
                this.setChatFromHistory(-1);
                break;
            }
            case 264: {
                this.setChatFromHistory(1);
                break;
            }
            case 266: {
                this.client.inGameHud.getChatHud().scroll(this.client.inGameHud.getChatHud().getVisibleLineCount() - 1);
                break;
            }
            case 267: {
                this.client.inGameHud.getChatHud().scroll(-this.client.inGameHud.getChatHud().getVisibleLineCount() + 1);
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.chatInputSuggestor.mouseScrolled(verticalAmount = MathHelper.clamp((double)verticalAmount, (double)-1.0, (double)1.0))) {
            return true;
        }
        if (!this.client.isShiftPressed()) {
            verticalAmount *= 7.0;
        }
        this.client.inGameHud.getChatHud().scroll((int)verticalAmount);
        return true;
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.chatInputSuggestor.mouseClicked(click)) {
            return true;
        }
        if (click.button() == 0) {
            int i = this.client.getWindow().getScaledHeight();
            DrawnTextConsumer.ClickHandler clickHandler = new DrawnTextConsumer.ClickHandler(this.getTextRenderer(), (int)click.x(), (int)click.y()).insert(this.shouldInsert());
            this.client.inGameHud.getChatHud().render((DrawnTextConsumer)clickHandler, i, this.client.inGameHud.getTicks(), true);
            Style style = clickHandler.getStyle();
            if (style != null && this.handleClickEvent(style, this.shouldInsert())) {
                this.originalChatText = this.chatField.getText();
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    private boolean shouldInsert() {
        return this.client.isShiftPressed();
    }

    private boolean handleClickEvent(Style style, boolean insert) {
        ClickEvent clickEvent = style.getClickEvent();
        if (insert) {
            if (style.getInsertion() != null) {
                this.insertText(style.getInsertion(), false);
            }
        } else if (clickEvent != null) {
            ClickEvent.Custom custom;
            if (clickEvent instanceof ClickEvent.Custom && (custom = (ClickEvent.Custom)clickEvent).id().equals((Object)ChatHud.EXPAND_CHAT_QUEUE_ID)) {
                MessageHandler messageHandler = this.client.getMessageHandler();
                if (messageHandler.getUnprocessedMessageCount() != 0L) {
                    messageHandler.process();
                }
            } else {
                ChatScreen.handleClickEvent((ClickEvent)clickEvent, (MinecraftClient)this.client, (Screen)this);
            }
            return true;
        }
        return false;
    }

    public void insertText(String text, boolean override) {
        if (override) {
            this.chatField.setText(text);
        } else {
            this.chatField.write(text);
        }
    }

    public void setChatFromHistory(int offset) {
        int i = this.messageHistoryIndex + offset;
        int j = this.client.inGameHud.getChatHud().getMessageHistory().size();
        if ((i = MathHelper.clamp((int)i, (int)0, (int)j)) == this.messageHistoryIndex) {
            return;
        }
        if (i == j) {
            this.messageHistoryIndex = j;
            this.chatField.setText(this.chatLastMessage);
            return;
        }
        if (this.messageHistoryIndex == j) {
            this.chatLastMessage = this.chatField.getText();
        }
        this.chatField.setText((String)this.client.inGameHud.getChatHud().getMessageHistory().get(i));
        this.chatInputSuggestor.setWindowActive(false);
        this.messageHistoryIndex = i;
    }

    private @Nullable OrderedText format(String string, int firstCharacterIndex) {
        if (this.draft) {
            return OrderedText.styledForwardsVisitedString((String)string, (Style)Style.EMPTY.withColor(Formatting.GRAY).withItalic(Boolean.valueOf(true)));
        }
        return null;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.fill(2, this.height - 14, this.width - 2, this.height - 2, this.client.options.getTextBackgroundColor(Integer.MIN_VALUE));
        this.client.inGameHud.getChatHud().render(context, this.textRenderer, this.client.inGameHud.getTicks(), mouseX, mouseY, true, this.shouldInsert());
        super.render(context, mouseX, mouseY, deltaTicks);
        this.chatInputSuggestor.render(context, mouseX, mouseY);
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
    }

    public boolean shouldPause() {
        return false;
    }

    public boolean keepOpenThroughPortal() {
        return true;
    }

    protected void addScreenNarrations(NarrationMessageBuilder messageBuilder) {
        messageBuilder.put(NarrationPart.TITLE, this.getTitle());
        messageBuilder.put(NarrationPart.USAGE, USAGE_TEXT);
        String string = this.chatField.getText();
        if (!string.isEmpty()) {
            messageBuilder.nextMessage().put(NarrationPart.TITLE, (Text)Text.translatable((String)"chat_screen.message", (Object[])new Object[]{string}));
        }
    }

    public void sendMessage(String chatText, boolean addToHistory) {
        if ((chatText = this.normalize(chatText)).isEmpty()) {
            return;
        }
        if (addToHistory) {
            this.client.inGameHud.getChatHud().addToMessageHistory(chatText);
        }
        if (chatText.startsWith("/")) {
            this.client.player.networkHandler.sendChatCommand(chatText.substring(1));
        } else {
            this.client.player.networkHandler.sendChatMessage(chatText);
        }
    }

    public String normalize(String chatText) {
        return StringHelper.truncateChat((String)StringUtils.normalizeSpace((String)chatText.trim()));
    }
}

