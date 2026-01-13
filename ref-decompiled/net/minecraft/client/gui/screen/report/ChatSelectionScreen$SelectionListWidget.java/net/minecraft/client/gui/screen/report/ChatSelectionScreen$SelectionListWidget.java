/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.report;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.report.ChatSelectionScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.message.MessageTrustStatus;
import net.minecraft.client.session.report.MessagesListAdder;
import net.minecraft.client.session.report.log.ReceivedMessage;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import net.minecraft.util.Nullables;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChatSelectionScreen.SelectionListWidget
extends AlwaysSelectedEntryListWidget<Entry>
implements MessagesListAdder.MessagesList {
    public static final int field_62185 = 16;
    private @Nullable SenderEntryPair lastSenderEntryPair;

    public ChatSelectionScreen.SelectionListWidget(MinecraftClient client, int contextMessagesHeight) {
        super(client, ChatSelectionScreen.this.width, ChatSelectionScreen.this.height - contextMessagesHeight - 80, 40, 16);
    }

    @Override
    public void setScrollY(double scrollY) {
        double d = this.getScrollY();
        super.setScrollY(scrollY);
        if ((float)this.getMaxScrollY() > 1.0E-5f && scrollY <= (double)1.0E-5f && !MathHelper.approximatelyEquals(scrollY, d)) {
            ChatSelectionScreen.this.addMoreMessages();
        }
    }

    @Override
    public void addMessage(int index, ReceivedMessage.ChatMessage message) {
        boolean bl = message.isSentFrom(ChatSelectionScreen.this.report.getReportedPlayerUuid());
        MessageTrustStatus messageTrustStatus = message.trustStatus();
        MessageIndicator messageIndicator = messageTrustStatus.createIndicator(message.message());
        MessageEntry entry = new MessageEntry(index, message.getContent(), message.getNarration(), messageIndicator, bl, true);
        this.addEntryToTop(entry);
        this.addSenderEntry(message, bl);
    }

    private void addSenderEntry(ReceivedMessage.ChatMessage message, boolean fromReportedPlayer) {
        SenderEntry entry = new SenderEntry(message.profile(), message.getHeadingText(), fromReportedPlayer);
        this.addEntryToTop(entry);
        SenderEntryPair senderEntryPair = new SenderEntryPair(message.getSenderUuid(), entry);
        if (this.lastSenderEntryPair != null && this.lastSenderEntryPair.senderEquals(senderEntryPair)) {
            this.removeEntryWithoutScrolling(this.lastSenderEntryPair.entry());
        }
        this.lastSenderEntryPair = senderEntryPair;
    }

    @Override
    public void addText(Text text) {
        this.addEntryToTop(new SeparatorEntry());
        this.addEntryToTop(new TextEntry(text));
        this.addEntryToTop(new SeparatorEntry());
        this.lastSenderEntryPair = null;
    }

    @Override
    public int getRowWidth() {
        return Math.min(350, this.width - 50);
    }

    public int getDisplayedItemCount() {
        return MathHelper.ceilDiv(this.height, 16);
    }

    @Override
    protected void renderEntry(DrawContext drawContext, int i, int j, float f, Entry entry) {
        if (this.shouldHighlight(entry)) {
            boolean bl = this.getSelectedOrNull() == entry;
            int k = this.isFocused() && bl ? -1 : -8355712;
            this.drawSelectionHighlight(drawContext, entry, k);
        }
        entry.render(drawContext, i, j, this.getHoveredEntry() == entry, f);
    }

    private boolean shouldHighlight(Entry entry) {
        if (entry.canSelect()) {
            boolean bl = this.getSelectedOrNull() == entry;
            boolean bl2 = this.getSelectedOrNull() == null;
            boolean bl3 = this.getHoveredEntry() == entry;
            return bl || bl2 && bl3 && entry.isHighlightedOnHover();
        }
        return false;
    }

    @Override
    protected @Nullable Entry getNeighboringEntry(NavigationDirection navigationDirection) {
        return this.getNeighboringEntry(navigationDirection, Entry::canSelect);
    }

    @Override
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        Entry entry2 = this.getNeighboringEntry(NavigationDirection.UP);
        if (entry2 == null) {
            ChatSelectionScreen.this.addMoreMessages();
        }
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        Entry entry = (Entry)this.getSelectedOrNull();
        if (entry != null && entry.keyPressed(input)) {
            return true;
        }
        return super.keyPressed(input);
    }

    public int getContextMessageY() {
        return this.getBottom() + ((ChatSelectionScreen)ChatSelectionScreen.this).textRenderer.fontHeight;
    }

    @Override
    protected /* synthetic */  @Nullable EntryListWidget.Entry getNeighboringEntry(NavigationDirection direction) {
        return this.getNeighboringEntry(direction);
    }

    @Environment(value=EnvType.CLIENT)
    public class MessageEntry
    extends Entry {
        private static final int CHECKMARK_WIDTH = 9;
        private static final int CHECKMARK_HEIGHT = 8;
        private static final int CHAT_MESSAGE_LEFT_MARGIN = 11;
        private static final int INDICATOR_LEFT_MARGIN = 4;
        private final int index;
        private final StringVisitable truncatedContent;
        private final Text narration;
        private final @Nullable List<OrderedText> fullContent;
        private final @Nullable MessageIndicator.Icon indicatorIcon;
        private final @Nullable List<OrderedText> originalContent;
        private final boolean fromReportedPlayer;
        private final boolean isChatMessage;

        public MessageEntry(int index, Text message, @Nullable Text narration, MessageIndicator indicator, boolean fromReportedPlayer, boolean isChatMessage) {
            this.index = index;
            this.indicatorIcon = Nullables.map(indicator, MessageIndicator::icon);
            this.originalContent = indicator != null && indicator.text() != null ? ChatSelectionScreen.this.textRenderer.wrapLines(indicator.text(), SelectionListWidget.this.getRowWidth()) : null;
            this.fromReportedPlayer = fromReportedPlayer;
            this.isChatMessage = isChatMessage;
            StringVisitable stringVisitable = ChatSelectionScreen.this.textRenderer.trimToWidth(message, this.getTextWidth() - ChatSelectionScreen.this.textRenderer.getWidth(ScreenTexts.ELLIPSIS));
            if (message != stringVisitable) {
                this.truncatedContent = StringVisitable.concat(stringVisitable, ScreenTexts.ELLIPSIS);
                this.fullContent = ChatSelectionScreen.this.textRenderer.wrapLines(message, SelectionListWidget.this.getRowWidth());
            } else {
                this.truncatedContent = message;
                this.fullContent = null;
            }
            this.narration = narration;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            if (this.isSelected() && this.fromReportedPlayer) {
                this.drawCheckmark(context, this.getContentY(), this.getContentX(), this.getContentHeight());
            }
            int i = this.getContentX() + this.getIndent();
            int j = this.getContentY() + 1 + (this.getContentHeight() - ((ChatSelectionScreen)ChatSelectionScreen.this).textRenderer.fontHeight) / 2;
            context.drawTextWithShadow(ChatSelectionScreen.this.textRenderer, Language.getInstance().reorder(this.truncatedContent), i, j, this.fromReportedPlayer ? -1 : -1593835521);
            if (this.fullContent != null && hovered) {
                context.drawTooltip(this.fullContent, mouseX, mouseY);
            }
            int k = ChatSelectionScreen.this.textRenderer.getWidth(this.truncatedContent);
            this.renderIndicator(context, i + k + 4, this.getContentY(), this.getContentHeight(), mouseX, mouseY);
        }

        private void renderIndicator(DrawContext context, int x, int y, int entryHeight, int mouseX, int mouseY) {
            if (this.indicatorIcon != null) {
                int i = y + (entryHeight - this.indicatorIcon.height) / 2;
                this.indicatorIcon.draw(context, x, i);
                if (this.originalContent != null && mouseX >= x && mouseX <= x + this.indicatorIcon.width && mouseY >= i && mouseY <= i + this.indicatorIcon.height) {
                    context.drawTooltip(this.originalContent, mouseX, mouseY);
                }
            }
        }

        private void drawCheckmark(DrawContext context, int y, int x, int entryHeight) {
            int i = x;
            int j = y + (entryHeight - 8) / 2;
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, CHECKMARK_ICON_TEXTURE, i, j, 9, 8);
        }

        private int getTextWidth() {
            int i = this.indicatorIcon != null ? this.indicatorIcon.width + 4 : 0;
            return SelectionListWidget.this.getRowWidth() - this.getIndent() - 4 - i;
        }

        private int getIndent() {
            return this.isChatMessage ? 11 : 0;
        }

        @Override
        public Text getNarration() {
            return this.isSelected() ? Text.translatable("narrator.select", this.narration) : this.narration;
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            SelectionListWidget.this.setSelected((Entry)null);
            return this.toggle();
        }

        @Override
        public boolean keyPressed(KeyInput input) {
            if (input.isEnterOrSpace()) {
                return this.toggle();
            }
            return false;
        }

        @Override
        public boolean isSelected() {
            return ChatSelectionScreen.this.report.isMessageSelected(this.index);
        }

        @Override
        public boolean canSelect() {
            return true;
        }

        @Override
        public boolean isHighlightedOnHover() {
            return this.fromReportedPlayer;
        }

        private boolean toggle() {
            if (this.fromReportedPlayer) {
                ChatSelectionScreen.this.report.toggleMessageSelection(this.index);
                ChatSelectionScreen.this.setDoneButtonActivation();
                return true;
            }
            return false;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class SenderEntry
    extends Entry {
        private static final int PLAYER_SKIN_SIZE = 12;
        private static final int field_49545 = 4;
        private final Text headingText;
        private final Supplier<SkinTextures> skinTexturesSupplier;
        private final boolean fromReportedPlayer;

        public SenderEntry(GameProfile gameProfile, Text headingText, boolean fromReportedPlayer) {
            this.headingText = headingText;
            this.fromReportedPlayer = fromReportedPlayer;
            this.skinTexturesSupplier = SelectionListWidget.this.client.getSkinProvider().supplySkinTextures(gameProfile, true);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            int i = this.getContentX() - 12 + 4;
            int j = this.getContentY() + (this.getContentHeight() - 12) / 2;
            PlayerSkinDrawer.draw(context, this.skinTexturesSupplier.get(), i, j, 12);
            int k = this.getContentY() + 1 + (this.getContentHeight() - ((ChatSelectionScreen)ChatSelectionScreen.this).textRenderer.fontHeight) / 2;
            context.drawTextWithShadow(ChatSelectionScreen.this.textRenderer, this.headingText, i + 12 + 4, k, this.fromReportedPlayer ? -1 : -1593835521);
        }
    }

    @Environment(value=EnvType.CLIENT)
    record SenderEntryPair(UUID sender, Entry entry) {
        public boolean senderEquals(SenderEntryPair pair) {
            return pair.sender.equals(this.sender);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        @Override
        public Text getNarration() {
            return ScreenTexts.EMPTY;
        }

        public boolean isSelected() {
            return false;
        }

        public boolean canSelect() {
            return false;
        }

        public boolean isHighlightedOnHover() {
            return this.canSelect();
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            return this.canSelect();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class SeparatorEntry
    extends Entry {
        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class TextEntry
    extends Entry {
        private final Text text;

        public TextEntry(Text text) {
            this.text = text;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            int i = this.getContentMiddleY();
            int j = this.getContentRightEnd() - 8;
            int k = ChatSelectionScreen.this.textRenderer.getWidth(this.text);
            int l = (this.getContentX() + j - k) / 2;
            int m = i - ((ChatSelectionScreen)ChatSelectionScreen.this).textRenderer.fontHeight / 2;
            context.drawTextWithShadow(ChatSelectionScreen.this.textRenderer, this.text, l, m, -6250336);
        }

        @Override
        public Text getNarration() {
            return this.text;
        }
    }
}
