package net.minecraft.client.gui.screen.report;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.network.message.MessageTrustStatus;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ChatAbuseReport;
import net.minecraft.client.session.report.MessagesListAdder;
import net.minecraft.client.session.report.log.ReceivedMessage;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.Nullables;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ChatSelectionScreen extends Screen {
   static final Identifier CHECKMARK_ICON_TEXTURE = Identifier.ofVanilla("icon/checkmark");
   private static final Text TITLE_TEXT = Text.translatable("gui.chatSelection.title");
   private static final Text CONTEXT_TEXT = Text.translatable("gui.chatSelection.context");
   @Nullable
   private final Screen parent;
   private final AbuseReportContext reporter;
   private ButtonWidget doneButton;
   private MultilineText contextMessage;
   @Nullable
   private SelectionListWidget selectionList;
   final ChatAbuseReport.Builder report;
   private final Consumer newReportConsumer;
   private MessagesListAdder listAdder;

   public ChatSelectionScreen(@Nullable Screen parent, AbuseReportContext reporter, ChatAbuseReport.Builder report, Consumer newReportConsumer) {
      super(TITLE_TEXT);
      this.parent = parent;
      this.reporter = reporter;
      this.report = report.copy();
      this.newReportConsumer = newReportConsumer;
   }

   protected void init() {
      this.listAdder = new MessagesListAdder(this.reporter, this::isSentByReportedPlayer);
      this.contextMessage = MultilineText.create(this.textRenderer, CONTEXT_TEXT, this.width - 16);
      MinecraftClient var10005 = this.client;
      int var10006 = this.contextMessage.count() + 1;
      Objects.requireNonNull(this.textRenderer);
      this.selectionList = (SelectionListWidget)this.addDrawableChild(new SelectionListWidget(var10005, var10006 * 9));
      this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, (button) -> {
         this.close();
      }).dimensions(this.width / 2 - 155, this.height - 32, 150, 20).build());
      this.doneButton = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
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
      this.listAdder.add(i, this.selectionList);
   }

   void addMoreMessages() {
      this.addMessages();
   }

   void setDoneButtonActivation() {
      this.doneButton.active = !this.report.getSelectedMessages().isEmpty();
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.title, this.width / 2, 10, -1);
      AbuseReportLimits abuseReportLimits = this.reporter.getSender().getLimits();
      int i = this.report.getSelectedMessages().size();
      int j = abuseReportLimits.maxReportedMessageCount();
      Text text = Text.translatable("gui.chatSelection.selected", i, j);
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)text, this.width / 2, 26, -1);
      this.contextMessage.drawCenterWithShadow(context, this.width / 2, this.selectionList.getContextMessageY());
   }

   public void close() {
      this.client.setScreen(this.parent);
   }

   public Text getNarratedTitle() {
      return ScreenTexts.joinSentences(super.getNarratedTitle(), CONTEXT_TEXT);
   }

   @Environment(EnvType.CLIENT)
   public class SelectionListWidget extends AlwaysSelectedEntryListWidget implements MessagesListAdder.MessagesList {
      @Nullable
      private SenderEntryPair lastSenderEntryPair;

      public SelectionListWidget(final MinecraftClient client, final int contextMessagesHeight) {
         super(client, ChatSelectionScreen.this.width, ChatSelectionScreen.this.height - contextMessagesHeight - 80, 40, 16);
      }

      public void setScrollY(double scrollY) {
         double d = this.getScrollY();
         super.setScrollY(scrollY);
         if ((float)this.getMaxScrollY() > 1.0E-5F && scrollY <= 9.999999747378752E-6 && !MathHelper.approximatelyEquals(scrollY, d)) {
            ChatSelectionScreen.this.addMoreMessages();
         }

      }

      public void addMessage(int index, ReceivedMessage.ChatMessage message) {
         boolean bl = message.isSentFrom(ChatSelectionScreen.this.report.getReportedPlayerUuid());
         MessageTrustStatus messageTrustStatus = message.trustStatus();
         MessageIndicator messageIndicator = messageTrustStatus.createIndicator(message.message());
         Entry entry = new MessageEntry(index, message.getContent(), message.getNarration(), messageIndicator, bl, true);
         this.addEntryToTop(entry);
         this.addSenderEntry(message, bl);
      }

      private void addSenderEntry(ReceivedMessage.ChatMessage message, boolean fromReportedPlayer) {
         Entry entry = new SenderEntry(message.profile(), message.getHeadingText(), fromReportedPlayer);
         this.addEntryToTop(entry);
         SenderEntryPair senderEntryPair = new SenderEntryPair(message.getSenderUuid(), entry);
         if (this.lastSenderEntryPair != null && this.lastSenderEntryPair.senderEquals(senderEntryPair)) {
            this.removeEntryWithoutScrolling(this.lastSenderEntryPair.entry());
         }

         this.lastSenderEntryPair = senderEntryPair;
      }

      public void addText(Text text) {
         this.addEntryToTop(new SeparatorEntry());
         this.addEntryToTop(new TextEntry(text));
         this.addEntryToTop(new SeparatorEntry());
         this.lastSenderEntryPair = null;
      }

      public int getRowWidth() {
         return Math.min(350, this.width - 50);
      }

      public int getDisplayedItemCount() {
         return MathHelper.ceilDiv(this.height, this.itemHeight);
      }

      protected void renderEntry(DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
         Entry entry = (Entry)this.getEntry(index);
         if (this.shouldHighlight(entry)) {
            boolean bl = this.getSelectedOrNull() == entry;
            int i = this.isFocused() && bl ? -1 : -8355712;
            this.drawSelectionHighlight(context, y, entryWidth, entryHeight, i, -16777216);
         }

         entry.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, this.getHoveredEntry() == entry, delta);
      }

      private boolean shouldHighlight(Entry entry) {
         if (entry.canSelect()) {
            boolean bl = this.getSelectedOrNull() == entry;
            boolean bl2 = this.getSelectedOrNull() == null;
            boolean bl3 = this.getHoveredEntry() == entry;
            return bl || bl2 && bl3 && entry.isHighlightedOnHover();
         } else {
            return false;
         }
      }

      @Nullable
      protected Entry getNeighboringEntry(NavigationDirection navigationDirection) {
         return (Entry)this.getNeighboringEntry(navigationDirection, Entry::canSelect);
      }

      public void setSelected(@Nullable Entry entry) {
         super.setSelected(entry);
         Entry entry2 = this.getNeighboringEntry(NavigationDirection.UP);
         if (entry2 == null) {
            ChatSelectionScreen.this.addMoreMessages();
         }

      }

      public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
         Entry entry = (Entry)this.getSelectedOrNull();
         return entry != null && entry.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
      }

      public int getContextMessageY() {
         int var10000 = this.getBottom();
         Objects.requireNonNull(ChatSelectionScreen.this.textRenderer);
         return var10000 + 9;
      }

      // $FF: synthetic method
      @Nullable
      protected EntryListWidget.Entry getNeighboringEntry(final NavigationDirection direction) {
         return this.getNeighboringEntry(direction);
      }

      @Environment(EnvType.CLIENT)
      public class MessageEntry extends Entry {
         private static final int CHECKMARK_WIDTH = 9;
         private static final int CHECKMARK_HEIGHT = 8;
         private static final int CHAT_MESSAGE_LEFT_MARGIN = 11;
         private static final int INDICATOR_LEFT_MARGIN = 4;
         private final int index;
         private final StringVisitable truncatedContent;
         private final Text narration;
         @Nullable
         private final List fullContent;
         @Nullable
         private final MessageIndicator.Icon indicatorIcon;
         @Nullable
         private final List originalContent;
         private final boolean fromReportedPlayer;
         private final boolean isChatMessage;

         public MessageEntry(final int index, final Text message, final Text narration, @Nullable final MessageIndicator indicator, final boolean fromReportedPlayer, final boolean isChatMessage) {
            this.index = index;
            this.indicatorIcon = (MessageIndicator.Icon)Nullables.map(indicator, MessageIndicator::icon);
            this.originalContent = indicator != null && indicator.text() != null ? ChatSelectionScreen.this.textRenderer.wrapLines(indicator.text(), SelectionListWidget.this.getRowWidth()) : null;
            this.fromReportedPlayer = fromReportedPlayer;
            this.isChatMessage = isChatMessage;
            StringVisitable stringVisitable = ChatSelectionScreen.this.textRenderer.trimToWidth((StringVisitable)message, this.getTextWidth() - ChatSelectionScreen.this.textRenderer.getWidth((StringVisitable)ScreenTexts.ELLIPSIS));
            if (message != stringVisitable) {
               this.truncatedContent = StringVisitable.concat(stringVisitable, ScreenTexts.ELLIPSIS);
               this.fullContent = ChatSelectionScreen.this.textRenderer.wrapLines(message, SelectionListWidget.this.getRowWidth());
            } else {
               this.truncatedContent = message;
               this.fullContent = null;
            }

            this.narration = narration;
         }

         public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            if (this.isSelected() && this.fromReportedPlayer) {
               this.drawCheckmark(context, y, x, entryHeight);
            }

            int i = x + this.getIndent();
            int var10000 = y + 1;
            Objects.requireNonNull(ChatSelectionScreen.this.textRenderer);
            int j = var10000 + (entryHeight - 9) / 2;
            context.drawTextWithShadow(ChatSelectionScreen.this.textRenderer, Language.getInstance().reorder(this.truncatedContent), i, j, this.fromReportedPlayer ? -1 : -1593835521);
            if (this.fullContent != null && hovered) {
               context.drawTooltip(this.fullContent, mouseX, mouseY);
            }

            int k = ChatSelectionScreen.this.textRenderer.getWidth(this.truncatedContent);
            this.renderIndicator(context, i + k + 4, y, entryHeight, mouseX, mouseY);
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
            int j = y + (entryHeight - 8) / 2;
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ChatSelectionScreen.CHECKMARK_ICON_TEXTURE, x, j, 9, 8);
         }

         private int getTextWidth() {
            int i = this.indicatorIcon != null ? this.indicatorIcon.width + 4 : 0;
            return SelectionListWidget.this.getRowWidth() - this.getIndent() - 4 - i;
         }

         private int getIndent() {
            return this.isChatMessage ? 11 : 0;
         }

         public Text getNarration() {
            return (Text)(this.isSelected() ? Text.translatable("narrator.select", this.narration) : this.narration);
         }

         public boolean mouseClicked(double mouseX, double mouseY, int button) {
            SelectionListWidget.this.setSelected((Entry)null);
            return this.toggle();
         }

         public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return KeyCodes.isToggle(keyCode) ? this.toggle() : false;
         }

         public boolean isSelected() {
            return ChatSelectionScreen.this.report.isMessageSelected(this.index);
         }

         public boolean canSelect() {
            return true;
         }

         public boolean isHighlightedOnHover() {
            return this.fromReportedPlayer;
         }

         private boolean toggle() {
            if (this.fromReportedPlayer) {
               ChatSelectionScreen.this.report.toggleMessageSelection(this.index);
               ChatSelectionScreen.this.setDoneButtonActivation();
               return true;
            } else {
               return false;
            }
         }
      }

      @Environment(EnvType.CLIENT)
      public class SenderEntry extends Entry {
         private static final int PLAYER_SKIN_SIZE = 12;
         private static final int field_49545 = 4;
         private final Text headingText;
         private final Supplier skinTexturesSupplier;
         private final boolean fromReportedPlayer;

         public SenderEntry(final GameProfile gameProfile, final Text headingText, final boolean fromReportedPlayer) {
            this.headingText = headingText;
            this.fromReportedPlayer = fromReportedPlayer;
            this.skinTexturesSupplier = SelectionListWidget.this.client.getSkinProvider().getSkinTexturesSupplier(gameProfile);
         }

         public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            int i = x - 12 + 4;
            int j = y + (entryHeight - 12) / 2;
            PlayerSkinDrawer.draw(context, (SkinTextures)this.skinTexturesSupplier.get(), i, j, 12);
            int var10000 = y + 1;
            Objects.requireNonNull(ChatSelectionScreen.this.textRenderer);
            int k = var10000 + (entryHeight - 9) / 2;
            context.drawTextWithShadow(ChatSelectionScreen.this.textRenderer, this.headingText, i + 12 + 4, k, this.fromReportedPlayer ? -1 : -1593835521);
         }
      }

      @Environment(EnvType.CLIENT)
      private static record SenderEntryPair(UUID sender, Entry entry) {
         SenderEntryPair(UUID uUID, Entry entry) {
            this.sender = uUID;
            this.entry = entry;
         }

         public boolean senderEquals(SenderEntryPair pair) {
            return pair.sender.equals(this.sender);
         }

         public UUID sender() {
            return this.sender;
         }

         public Entry entry() {
            return this.entry;
         }
      }

      @Environment(EnvType.CLIENT)
      public abstract static class Entry extends AlwaysSelectedEntryListWidget.Entry {
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

         public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return this.canSelect();
         }
      }

      @Environment(EnvType.CLIENT)
      public static class SeparatorEntry extends Entry {
         public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         }
      }

      @Environment(EnvType.CLIENT)
      public class TextEntry extends Entry {
         private final Text text;

         public TextEntry(final Text text) {
            this.text = text;
         }

         public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            int i = y + entryHeight / 2;
            int j = x + entryWidth - 8;
            int k = ChatSelectionScreen.this.textRenderer.getWidth((StringVisitable)this.text);
            int l = (x + j - k) / 2;
            Objects.requireNonNull(ChatSelectionScreen.this.textRenderer);
            int m = i - 9 / 2;
            context.drawTextWithShadow(ChatSelectionScreen.this.textRenderer, this.text, l, m, -6250336);
         }

         public Text getNarration() {
            return this.text;
         }
      }
   }
}
