package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ChatScreen extends Screen {
   public static final double SHIFT_SCROLL_AMOUNT = 7.0;
   private static final Text USAGE_TEXT = Text.translatable("chat_screen.usage");
   private static final int MAX_INDICATOR_TOOLTIP_WIDTH = 210;
   private String chatLastMessage = "";
   private int messageHistoryIndex = -1;
   protected TextFieldWidget chatField;
   private String originalChatText;
   ChatInputSuggestor chatInputSuggestor;

   public ChatScreen(String originalChatText) {
      super(Text.translatable("chat_screen.title"));
      this.originalChatText = originalChatText;
   }

   protected void init() {
      this.messageHistoryIndex = this.client.inGameHud.getChatHud().getMessageHistory().size();
      this.chatField = new TextFieldWidget(this.client.advanceValidatingTextRenderer, 4, this.height - 12, this.width - 4, 12, Text.translatable("chat.editBox")) {
         protected MutableText getNarrationMessage() {
            return super.getNarrationMessage().append(ChatScreen.this.chatInputSuggestor.getNarration());
         }
      };
      this.chatField.setMaxLength(256);
      this.chatField.setDrawsBackground(false);
      this.chatField.setText(this.originalChatText);
      this.chatField.setChangedListener(this::onChatFieldUpdate);
      this.chatField.setFocusUnlocked(false);
      this.addDrawableChild(this.chatField);
      this.chatInputSuggestor = new ChatInputSuggestor(this.client, this, this.chatField, this.textRenderer, false, false, 1, 10, true, -805306368);
      this.chatInputSuggestor.setCanLeave(false);
      this.chatInputSuggestor.refresh();
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.chatField);
   }

   public void resize(MinecraftClient client, int width, int height) {
      String string = this.chatField.getText();
      this.init(client, width, height);
      this.setText(string);
      this.chatInputSuggestor.refresh();
   }

   public void removed() {
      this.client.inGameHud.getChatHud().resetScroll();
   }

   private void onChatFieldUpdate(String chatText) {
      String string = this.chatField.getText();
      this.chatInputSuggestor.setWindowActive(!string.equals(this.originalChatText));
      this.chatInputSuggestor.refresh();
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.chatInputSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (keyCode == 256) {
         this.client.setScreen((Screen)null);
         return true;
      } else if (keyCode != 257 && keyCode != 335) {
         if (keyCode == 265) {
            this.setChatFromHistory(-1);
            return true;
         } else if (keyCode == 264) {
            this.setChatFromHistory(1);
            return true;
         } else if (keyCode == 266) {
            this.client.inGameHud.getChatHud().scroll(this.client.inGameHud.getChatHud().getVisibleLineCount() - 1);
            return true;
         } else if (keyCode == 267) {
            this.client.inGameHud.getChatHud().scroll(-this.client.inGameHud.getChatHud().getVisibleLineCount() + 1);
            return true;
         } else {
            return false;
         }
      } else {
         this.sendMessage(this.chatField.getText(), true);
         this.client.setScreen((Screen)null);
         return true;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      verticalAmount = MathHelper.clamp(verticalAmount, -1.0, 1.0);
      if (this.chatInputSuggestor.mouseScrolled(verticalAmount)) {
         return true;
      } else {
         if (!hasShiftDown()) {
            verticalAmount *= 7.0;
         }

         this.client.inGameHud.getChatHud().scroll((int)verticalAmount);
         return true;
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.chatInputSuggestor.mouseClicked((double)((int)mouseX), (double)((int)mouseY), button)) {
         return true;
      } else {
         if (button == 0) {
            ChatHud chatHud = this.client.inGameHud.getChatHud();
            if (chatHud.mouseClicked(mouseX, mouseY)) {
               return true;
            }

            Style style = this.getTextStyleAt(mouseX, mouseY);
            if (style != null && this.handleTextClick(style)) {
               this.originalChatText = this.chatField.getText();
               return true;
            }
         }

         return this.chatField.mouseClicked(mouseX, mouseY, button) ? true : super.mouseClicked(mouseX, mouseY, button);
      }
   }

   protected void insertText(String text, boolean override) {
      if (override) {
         this.chatField.setText(text);
      } else {
         this.chatField.write(text);
      }

   }

   public void setChatFromHistory(int offset) {
      int i = this.messageHistoryIndex + offset;
      int j = this.client.inGameHud.getChatHud().getMessageHistory().size();
      i = MathHelper.clamp(i, 0, j);
      if (i != this.messageHistoryIndex) {
         if (i == j) {
            this.messageHistoryIndex = j;
            this.chatField.setText(this.chatLastMessage);
         } else {
            if (this.messageHistoryIndex == j) {
               this.chatLastMessage = this.chatField.getText();
            }

            this.chatField.setText((String)this.client.inGameHud.getChatHud().getMessageHistory().get(i));
            this.chatInputSuggestor.setWindowActive(false);
            this.messageHistoryIndex = i;
         }
      }
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      context.fill(2, this.height - 14, this.width - 2, this.height - 2, this.client.options.getTextBackgroundColor(Integer.MIN_VALUE));
      this.client.inGameHud.getChatHud().render(context, this.client.inGameHud.getTicks(), mouseX, mouseY, true);
      super.render(context, mouseX, mouseY, deltaTicks);
      this.chatInputSuggestor.render(context, mouseX, mouseY);
      MessageIndicator messageIndicator = this.client.inGameHud.getChatHud().getIndicatorAt((double)mouseX, (double)mouseY);
      if (messageIndicator != null && messageIndicator.text() != null) {
         context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(messageIndicator.text(), 210), mouseX, mouseY);
      } else {
         Style style = this.getTextStyleAt((double)mouseX, (double)mouseY);
         if (style != null && style.getHoverEvent() != null) {
            context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
         }
      }

   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
   }

   public boolean shouldPause() {
      return false;
   }

   private void setText(String text) {
      this.chatField.setText(text);
   }

   protected void addScreenNarrations(NarrationMessageBuilder messageBuilder) {
      messageBuilder.put(NarrationPart.TITLE, this.getTitle());
      messageBuilder.put(NarrationPart.USAGE, USAGE_TEXT);
      String string = this.chatField.getText();
      if (!string.isEmpty()) {
         messageBuilder.nextMessage().put(NarrationPart.TITLE, (Text)Text.translatable("chat_screen.message", string));
      }

   }

   @Nullable
   private Style getTextStyleAt(double x, double y) {
      return this.client.inGameHud.getChatHud().getTextStyleAt(x, y);
   }

   public void sendMessage(String chatText, boolean addToHistory) {
      chatText = this.normalize(chatText);
      if (!chatText.isEmpty()) {
         if (addToHistory) {
            this.client.inGameHud.getChatHud().addToMessageHistory(chatText);
         }

         if (chatText.startsWith("/")) {
            this.client.player.networkHandler.sendChatCommand(chatText.substring(1));
         } else {
            this.client.player.networkHandler.sendChatMessage(chatText);
         }

      }
   }

   public String normalize(String chatText) {
      return StringHelper.truncateChat(StringUtils.normalizeSpace(chatText.trim()));
   }
}
