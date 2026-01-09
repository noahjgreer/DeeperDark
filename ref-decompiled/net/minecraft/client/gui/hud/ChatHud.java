package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Nullables;
import net.minecraft.util.collection.ArrayListDeque;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ChatHud {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_MESSAGES = 100;
   private static final int MISSING_MESSAGE_INDEX = -1;
   private static final int field_39772 = 4;
   private static final int field_39773 = 4;
   private static final int OFFSET_FROM_BOTTOM = 40;
   private static final int REMOVAL_QUEUE_TICKS = 60;
   private static final Text DELETED_MARKER_TEXT;
   private final MinecraftClient client;
   private final ArrayListDeque messageHistory = new ArrayListDeque(100);
   private final List messages = Lists.newArrayList();
   private final List visibleMessages = Lists.newArrayList();
   private int scrolledLines;
   private boolean hasUnreadNewMessages;
   private final List removalQueue = new ArrayList();

   public ChatHud(MinecraftClient client) {
      this.client = client;
      this.messageHistory.addAll(client.getCommandHistoryManager().getHistory());
   }

   public void tickRemovalQueueIfExists() {
      if (!this.removalQueue.isEmpty()) {
         this.tickRemovalQueue();
      }

   }

   private int method_71990(int i, int j, boolean bl, int k, class_11511 arg) {
      int l = this.getLineHeight();
      int m = 0;

      for(int n = Math.min(this.visibleMessages.size() - this.scrolledLines, i) - 1; n >= 0; --n) {
         int o = n + this.scrolledLines;
         ChatHudLine.Visible visible = (ChatHudLine.Visible)this.visibleMessages.get(o);
         if (visible != null) {
            int p = j - visible.addedTime();
            float f = bl ? 1.0F : (float)getMessageOpacityMultiplier(p);
            if (f > 1.0E-5F) {
               ++m;
               int q = k - n * l;
               int r = q - l;
               arg.accept(0, r, q, visible, n, f);
            }
         }
      }

      return m;
   }

   public void render(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused) {
      if (!this.isChatHidden()) {
         int i = this.getVisibleLineCount();
         int j = this.visibleMessages.size();
         if (j > 0) {
            Profiler profiler = Profilers.get();
            profiler.push("chat");
            float f = (float)this.getChatScale();
            int k = MathHelper.ceil((float)this.getWidth() / f);
            int l = context.getScaledWindowHeight();
            context.getMatrices().pushMatrix();
            context.getMatrices().scale(f, f);
            context.getMatrices().translate(4.0F, 0.0F);
            int m = MathHelper.floor((float)(l - 40) / f);
            int n = this.getMessageIndex(this.toChatLineX((double)mouseX), this.toChatLineY((double)mouseY));
            float g = ((Double)this.client.options.getChatOpacity().getValue()).floatValue() * 0.9F + 0.1F;
            float h = ((Double)this.client.options.getTextBackgroundOpacity().getValue()).floatValue();
            double d = (Double)this.client.options.getChatLineSpacing().getValue();
            int o = (int)Math.round(-8.0 * (d + 1.0) + 4.0 * d);
            this.method_71990(i, currentTick, focused, m, (lx, mx, nx, visible, ox, hx) -> {
               context.fill(lx - 4, mx, lx + k + 4 + 4, nx, ColorHelper.withAlpha(hx * h, -16777216));
               MessageIndicator messageIndicator = visible.indicator();
               if (messageIndicator != null) {
                  int p = ColorHelper.withAlpha(hx * g, messageIndicator.indicatorColor());
                  context.fill(lx - 4, mx, lx - 2, nx, p);
                  if (ox == n && messageIndicator.icon() != null) {
                     int q = this.getIndicatorX(visible);
                     int var10000 = nx + o;
                     Objects.requireNonNull(this.client.textRenderer);
                     int r = var10000 + 9;
                     this.drawIndicatorIcon(context, q, r, messageIndicator.icon());
                  }
               }

            });
            int p = this.method_71990(i, currentTick, focused, m, (jx, kx, lx, visible, mx, gx) -> {
               int n = lx + o;
               context.drawTextWithShadow(this.client.textRenderer, visible.content(), jx, n, ColorHelper.withAlpha(gx * g, -1));
            });
            long q = this.client.getMessageHandler().getUnprocessedMessageCount();
            int r;
            int s;
            if (q > 0L) {
               r = (int)(128.0F * g);
               s = (int)(255.0F * h);
               context.getMatrices().pushMatrix();
               context.getMatrices().translate(0.0F, (float)m);
               context.fill(-2, 0, k + 4, 9, s << 24);
               context.drawTextWithShadow(this.client.textRenderer, (Text)Text.translatable("chat.queue", q), 0, 1, ColorHelper.withAlpha(r, -1));
               context.getMatrices().popMatrix();
            }

            if (focused) {
               r = this.getLineHeight();
               s = j * r;
               int t = p * r;
               int u = this.scrolledLines * t / j - m;
               int v = t * t / s;
               if (s != t) {
                  int w = u > 0 ? 170 : 96;
                  int x = this.hasUnreadNewMessages ? 13382451 : 3355562;
                  int y = k + 4;
                  context.fill(y, -u, y + 2, -u - v, ColorHelper.withAlpha(w, x));
                  context.fill(y + 2, -u, y + 1, -u - v, ColorHelper.withAlpha(w, 13421772));
               }
            }

            context.getMatrices().popMatrix();
            profiler.pop();
         }
      }
   }

   private void drawIndicatorIcon(DrawContext context, int x, int y, MessageIndicator.Icon icon) {
      int i = y - icon.height - 1;
      icon.draw(context, x, i);
   }

   private int getIndicatorX(ChatHudLine.Visible line) {
      return this.client.textRenderer.getWidth(line.content()) + 4;
   }

   private boolean isChatHidden() {
      return this.client.options.getChatVisibility().getValue() == ChatVisibility.HIDDEN;
   }

   private static double getMessageOpacityMultiplier(int age) {
      double d = (double)age / 200.0;
      d = 1.0 - d;
      d *= 10.0;
      d = MathHelper.clamp(d, 0.0, 1.0);
      d *= d;
      return d;
   }

   public void clear(boolean clearHistory) {
      this.client.getMessageHandler().processAll();
      this.removalQueue.clear();
      this.visibleMessages.clear();
      this.messages.clear();
      if (clearHistory) {
         this.messageHistory.clear();
         this.messageHistory.addAll(this.client.getCommandHistoryManager().getHistory());
      }

   }

   public void addMessage(Text message) {
      this.addMessage(message, (MessageSignatureData)null, this.client.isConnectedToLocalServer() ? MessageIndicator.singlePlayer() : MessageIndicator.system());
   }

   public void addMessage(Text message, @Nullable MessageSignatureData signatureData, @Nullable MessageIndicator indicator) {
      ChatHudLine chatHudLine = new ChatHudLine(this.client.inGameHud.getTicks(), message, signatureData, indicator);
      this.logChatMessage(chatHudLine);
      this.addVisibleMessage(chatHudLine);
      this.addMessage(chatHudLine);
   }

   private void logChatMessage(ChatHudLine message) {
      String string = message.content().getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
      String string2 = (String)Nullables.map(message.indicator(), MessageIndicator::loggedName);
      if (string2 != null) {
         LOGGER.info("[{}] [CHAT] {}", string2, string);
      } else {
         LOGGER.info("[CHAT] {}", string);
      }

   }

   private void addVisibleMessage(ChatHudLine message) {
      int i = MathHelper.floor((double)this.getWidth() / this.getChatScale());
      MessageIndicator.Icon icon = message.getIcon();
      if (icon != null) {
         i -= icon.width + 4 + 2;
      }

      List list = ChatMessages.breakRenderedChatMessageLines(message.content(), i, this.client.textRenderer);
      boolean bl = this.isChatFocused();

      for(int j = 0; j < list.size(); ++j) {
         OrderedText orderedText = (OrderedText)list.get(j);
         if (bl && this.scrolledLines > 0) {
            this.hasUnreadNewMessages = true;
            this.scroll(1);
         }

         boolean bl2 = j == list.size() - 1;
         this.visibleMessages.add(0, new ChatHudLine.Visible(message.creationTick(), orderedText, message.indicator(), bl2));
      }

      while(this.visibleMessages.size() > 100) {
         this.visibleMessages.remove(this.visibleMessages.size() - 1);
      }

   }

   private void addMessage(ChatHudLine message) {
      this.messages.add(0, message);

      while(this.messages.size() > 100) {
         this.messages.remove(this.messages.size() - 1);
      }

   }

   private void tickRemovalQueue() {
      int i = this.client.inGameHud.getTicks();
      this.removalQueue.removeIf((message) -> {
         if (i >= message.deletableAfter()) {
            return this.queueForRemoval(message.signature()) == null;
         } else {
            return false;
         }
      });
   }

   public void removeMessage(MessageSignatureData signature) {
      RemovalQueuedMessage removalQueuedMessage = this.queueForRemoval(signature);
      if (removalQueuedMessage != null) {
         this.removalQueue.add(removalQueuedMessage);
      }

   }

   @Nullable
   private RemovalQueuedMessage queueForRemoval(MessageSignatureData signature) {
      int i = this.client.inGameHud.getTicks();
      ListIterator listIterator = this.messages.listIterator();

      ChatHudLine chatHudLine;
      do {
         if (!listIterator.hasNext()) {
            return null;
         }

         chatHudLine = (ChatHudLine)listIterator.next();
      } while(!signature.equals(chatHudLine.signature()));

      int j = chatHudLine.creationTick() + 60;
      if (i >= j) {
         listIterator.set(this.createRemovalMarker(chatHudLine));
         this.refresh();
         return null;
      } else {
         return new RemovalQueuedMessage(signature, j);
      }
   }

   private ChatHudLine createRemovalMarker(ChatHudLine original) {
      return new ChatHudLine(original.creationTick(), DELETED_MARKER_TEXT, (MessageSignatureData)null, MessageIndicator.system());
   }

   public void reset() {
      this.resetScroll();
      this.refresh();
   }

   private void refresh() {
      this.visibleMessages.clear();
      Iterator var1 = Lists.reverse(this.messages).iterator();

      while(var1.hasNext()) {
         ChatHudLine chatHudLine = (ChatHudLine)var1.next();
         this.addVisibleMessage(chatHudLine);
      }

   }

   public ArrayListDeque getMessageHistory() {
      return this.messageHistory;
   }

   public void addToMessageHistory(String message) {
      if (!message.equals(this.messageHistory.peekLast())) {
         if (this.messageHistory.size() >= 100) {
            this.messageHistory.removeFirst();
         }

         this.messageHistory.addLast(message);
      }

      if (message.startsWith("/")) {
         this.client.getCommandHistoryManager().add(message);
      }

   }

   public void resetScroll() {
      this.scrolledLines = 0;
      this.hasUnreadNewMessages = false;
   }

   public void scroll(int scroll) {
      this.scrolledLines += scroll;
      int i = this.visibleMessages.size();
      if (this.scrolledLines > i - this.getVisibleLineCount()) {
         this.scrolledLines = i - this.getVisibleLineCount();
      }

      if (this.scrolledLines <= 0) {
         this.scrolledLines = 0;
         this.hasUnreadNewMessages = false;
      }

   }

   public boolean mouseClicked(double mouseX, double mouseY) {
      if (this.isChatFocused() && !this.client.options.hudHidden && !this.isChatHidden()) {
         MessageHandler messageHandler = this.client.getMessageHandler();
         if (messageHandler.getUnprocessedMessageCount() == 0L) {
            return false;
         } else {
            double d = mouseX - 2.0;
            double e = (double)this.client.getWindow().getScaledHeight() - mouseY - 40.0;
            if (d <= (double)MathHelper.floor((double)this.getWidth() / this.getChatScale()) && e < 0.0 && e > (double)MathHelper.floor(-9.0 * this.getChatScale())) {
               messageHandler.process();
               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   @Nullable
   public Style getTextStyleAt(double x, double y) {
      double d = this.toChatLineX(x);
      double e = this.toChatLineY(y);
      int i = this.getMessageLineIndex(d, e);
      if (i >= 0 && i < this.visibleMessages.size()) {
         ChatHudLine.Visible visible = (ChatHudLine.Visible)this.visibleMessages.get(i);
         return this.client.textRenderer.getTextHandler().getStyleAt(visible.content(), MathHelper.floor(d));
      } else {
         return null;
      }
   }

   @Nullable
   public MessageIndicator getIndicatorAt(double mouseX, double mouseY) {
      double d = this.toChatLineX(mouseX);
      double e = this.toChatLineY(mouseY);
      int i = this.getMessageIndex(d, e);
      if (i >= 0 && i < this.visibleMessages.size()) {
         ChatHudLine.Visible visible = (ChatHudLine.Visible)this.visibleMessages.get(i);
         MessageIndicator messageIndicator = visible.indicator();
         if (messageIndicator != null && this.isXInsideIndicatorIcon(d, visible, messageIndicator)) {
            return messageIndicator;
         }
      }

      return null;
   }

   private boolean isXInsideIndicatorIcon(double x, ChatHudLine.Visible line, MessageIndicator indicator) {
      if (x < 0.0) {
         return true;
      } else {
         MessageIndicator.Icon icon = indicator.icon();
         if (icon == null) {
            return false;
         } else {
            int i = this.getIndicatorX(line);
            int j = i + icon.width;
            return x >= (double)i && x <= (double)j;
         }
      }
   }

   private double toChatLineX(double x) {
      return x / this.getChatScale() - 4.0;
   }

   private double toChatLineY(double y) {
      double d = (double)this.client.getWindow().getScaledHeight() - y - 40.0;
      return d / (this.getChatScale() * (double)this.getLineHeight());
   }

   private int getMessageIndex(double chatLineX, double chatLineY) {
      int i = this.getMessageLineIndex(chatLineX, chatLineY);
      if (i == -1) {
         return -1;
      } else {
         while(i >= 0) {
            if (((ChatHudLine.Visible)this.visibleMessages.get(i)).endOfEntry()) {
               return i;
            }

            --i;
         }

         return i;
      }
   }

   private int getMessageLineIndex(double chatLineX, double chatLineY) {
      if (this.isChatFocused() && !this.isChatHidden()) {
         if (!(chatLineX < -4.0) && !(chatLineX > (double)MathHelper.floor((double)this.getWidth() / this.getChatScale()))) {
            int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
            if (chatLineY >= 0.0 && chatLineY < (double)i) {
               int j = MathHelper.floor(chatLineY + (double)this.scrolledLines);
               if (j >= 0 && j < this.visibleMessages.size()) {
                  return j;
               }
            }

            return -1;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public boolean isChatFocused() {
      return this.client.currentScreen instanceof ChatScreen;
   }

   public int getWidth() {
      return getWidth((Double)this.client.options.getChatWidth().getValue());
   }

   public int getHeight() {
      return getHeight(this.isChatFocused() ? (Double)this.client.options.getChatHeightFocused().getValue() : (Double)this.client.options.getChatHeightUnfocused().getValue());
   }

   public double getChatScale() {
      return (Double)this.client.options.getChatScale().getValue();
   }

   public static int getWidth(double widthOption) {
      int i = true;
      int j = true;
      return MathHelper.floor(widthOption * 280.0 + 40.0);
   }

   public static int getHeight(double heightOption) {
      int i = true;
      int j = true;
      return MathHelper.floor(heightOption * 160.0 + 20.0);
   }

   public static double getDefaultUnfocusedHeight() {
      int i = true;
      int j = true;
      return 70.0 / (double)(getHeight(1.0) - 20);
   }

   public int getVisibleLineCount() {
      return this.getHeight() / this.getLineHeight();
   }

   private int getLineHeight() {
      Objects.requireNonNull(this.client.textRenderer);
      return (int)(9.0 * ((Double)this.client.options.getChatLineSpacing().getValue() + 1.0));
   }

   public ChatState toChatState() {
      return new ChatState(List.copyOf(this.messages), List.copyOf(this.messageHistory), List.copyOf(this.removalQueue));
   }

   public void restoreChatState(ChatState state) {
      this.messageHistory.clear();
      this.messageHistory.addAll(state.messageHistory);
      this.removalQueue.clear();
      this.removalQueue.addAll(state.removalQueue);
      this.messages.clear();
      this.messages.addAll(state.messages);
      this.refresh();
   }

   static {
      DELETED_MARKER_TEXT = Text.translatable("chat.deleted_marker").formatted(Formatting.GRAY, Formatting.ITALIC);
   }

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   private interface class_11511 {
      void accept(int i, int j, int k, ChatHudLine.Visible visible, int l, float f);
   }

   @Environment(EnvType.CLIENT)
   private static record RemovalQueuedMessage(MessageSignatureData signature, int deletableAfter) {
      RemovalQueuedMessage(MessageSignatureData messageSignatureData, int i) {
         this.signature = messageSignatureData;
         this.deletableAfter = i;
      }

      public MessageSignatureData signature() {
         return this.signature;
      }

      public int deletableAfter() {
         return this.deletableAfter;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class ChatState {
      final List messages;
      final List messageHistory;
      final List removalQueue;

      public ChatState(List messages, List messageHistory, List removalQueue) {
         this.messages = messages;
         this.messageHistory = messageHistory;
         this.removalQueue = removalQueue;
      }
   }
}
