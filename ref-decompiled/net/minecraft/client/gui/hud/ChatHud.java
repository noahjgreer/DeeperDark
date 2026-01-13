/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.hud.ChatHud
 *  net.minecraft.client.gui.hud.ChatHud$Backend
 *  net.minecraft.client.gui.hud.ChatHud$ChatMethod
 *  net.minecraft.client.gui.hud.ChatHud$ChatState
 *  net.minecraft.client.gui.hud.ChatHud$Draft
 *  net.minecraft.client.gui.hud.ChatHud$Forwarder
 *  net.minecraft.client.gui.hud.ChatHud$Hud
 *  net.minecraft.client.gui.hud.ChatHud$Interactable
 *  net.minecraft.client.gui.hud.ChatHud$LineConsumer
 *  net.minecraft.client.gui.hud.ChatHud$OpacityRule
 *  net.minecraft.client.gui.hud.ChatHud$RemovalQueuedMessage
 *  net.minecraft.client.gui.hud.ChatHudLine
 *  net.minecraft.client.gui.hud.ChatHudLine$Visible
 *  net.minecraft.client.gui.hud.MessageIndicator
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.client.gui.screen.ChatScreen$Factory
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.network.message.ChatVisibility
 *  net.minecraft.network.message.MessageSignatureData
 *  net.minecraft.text.ClickEvent
 *  net.minecraft.text.ClickEvent$Custom
 *  net.minecraft.text.HoverEvent
 *  net.minecraft.text.HoverEvent$ShowText
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Nullables
 *  net.minecraft.util.collection.ArrayListDeque
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.profiler.Profiler
 *  net.minecraft.util.profiler.Profilers
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nullables;
import net.minecraft.util.collection.ArrayListDeque;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ChatHud {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_MESSAGES = 100;
    private static final int field_39772 = 4;
    private static final int OFFSET_FROM_BOTTOM = 40;
    private static final int field_63864 = 210;
    private static final int REMOVAL_QUEUE_TICKS = 60;
    private static final Text DELETED_MARKER_TEXT = Text.translatable((String)"chat.deleted_marker").formatted(new Formatting[]{Formatting.GRAY, Formatting.ITALIC});
    public static final int field_63862 = 8;
    public static final Identifier EXPAND_CHAT_QUEUE_ID = Identifier.ofVanilla((String)"internal/expand_chat_queue");
    private static final Style CHAT_QUEUE_STYLE = Style.EMPTY.withClickEvent((ClickEvent)new ClickEvent.Custom(EXPAND_CHAT_QUEUE_ID, Optional.empty())).withHoverEvent((HoverEvent)new HoverEvent.ShowText((Text)Text.translatable((String)"chat.queue.tooltip")));
    final MinecraftClient client;
    private final ArrayListDeque<String> messageHistory = new ArrayListDeque(100);
    private final List<ChatHudLine> messages = Lists.newArrayList();
    private final List<ChatHudLine.Visible> visibleMessages = Lists.newArrayList();
    private int scrolledLines;
    private boolean hasUnreadNewMessages;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ChatHud.Draft draft;
    private @Nullable ChatScreen screen;
    private final List<RemovalQueuedMessage> removalQueue = new ArrayList();

    public ChatHud(MinecraftClient client) {
        this.client = client;
        this.messageHistory.addAll(client.getCommandHistoryManager().getHistory());
    }

    public void tickRemovalQueueIfExists() {
        if (!this.removalQueue.isEmpty()) {
            this.tickRemovalQueue();
        }
    }

    private int forEachVisibleLine(OpacityRule opacityRule, LineConsumer lineConsumer) {
        int i = this.getVisibleLineCount();
        int j = 0;
        for (int k = Math.min(this.visibleMessages.size() - this.scrolledLines, i) - 1; k >= 0; --k) {
            int l = k + this.scrolledLines;
            ChatHudLine.Visible visible = (ChatHudLine.Visible)this.visibleMessages.get(l);
            float f = opacityRule.calculate(visible);
            if (!(f > 1.0E-5f)) continue;
            ++j;
            lineConsumer.accept(visible, k, f);
        }
        return j;
    }

    public void render(DrawContext context, TextRenderer textRenderer, int currentTick, int mouseX, int mouseY, boolean interactable, boolean bl) {
        context.getMatrices().pushMatrix();
        this.render((Backend)(interactable ? new Interactable(context, textRenderer, mouseX, mouseY, bl) : new Hud(context)), context.getScaledWindowHeight(), currentTick, interactable);
        context.getMatrices().popMatrix();
    }

    public void render(DrawnTextConsumer textConsumer, int windowHeight, int currentTick, boolean expanded) {
        this.render((Backend)new Forwarder(textConsumer), windowHeight, currentTick, expanded);
    }

    private void render(Backend drawer, int windowHeight, int currentTick, boolean expanded) {
        int r;
        if (this.isChatHidden()) {
            return;
        }
        int i = this.visibleMessages.size();
        if (i <= 0) {
            return;
        }
        Profiler profiler = Profilers.get();
        profiler.push("chat");
        float f = (float)this.getChatScale();
        int j = MathHelper.ceil((float)((float)this.getWidth() / f));
        int k = MathHelper.floor((float)((float)(windowHeight - 40) / f));
        float g = ((Double)this.client.options.getChatOpacity().getValue()).floatValue() * 0.9f + 0.1f;
        float h = ((Double)this.client.options.getTextBackgroundOpacity().getValue()).floatValue();
        Objects.requireNonNull(this.client.textRenderer);
        int l = 9;
        int m = 8;
        double d = (Double)this.client.options.getChatLineSpacing().getValue();
        int n = (int)((double)l * (d + 1.0));
        int o = (int)Math.round(8.0 * (d + 1.0) - 4.0 * d);
        long p = this.client.getMessageHandler().getUnprocessedMessageCount();
        OpacityRule opacityRule = expanded ? OpacityRule.CONSTANT : OpacityRule.timeBased((int)currentTick);
        drawer.updatePose(pose -> {
            pose.scale(f, f);
            pose.translate(4.0f, 0.0f);
        });
        this.forEachVisibleLine(opacityRule, (line, y, opacity) -> {
            int l = k - y * n;
            int m = l - n;
            drawer.fill(-4, m, j + 4 + 4, l, ColorHelper.toAlpha((float)(opacity * h)));
        });
        if (p > 0L) {
            drawer.fill(-2, k, j + 4, k + l, ColorHelper.toAlpha((float)h));
        }
        int q = this.forEachVisibleLine(opacityRule, (LineConsumer)new /* Unavailable Anonymous Inner Class!! */);
        if (p > 0L) {
            r = k + l;
            MutableText text = Text.translatable((String)"chat.queue", (Object[])new Object[]{p}).setStyle(CHAT_QUEUE_STYLE);
            drawer.text(r - 8, 0.5f * g, text.asOrderedText());
        }
        if (expanded) {
            r = i * n;
            int s = q * n;
            int t = this.scrolledLines * s / i - k;
            int u = s * s / r;
            if (r != s) {
                int v = t > 0 ? 170 : 96;
                int w = this.hasUnreadNewMessages ? 0xCC3333 : 0x3333AA;
                int x = j + 4;
                drawer.fill(x, -t, x + 2, -t - u, ColorHelper.withAlpha((int)v, (int)w));
                drawer.fill(x + 2, -t, x + 1, -t - u, ColorHelper.withAlpha((int)v, (int)0xCCCCCC));
            }
        }
        profiler.pop();
    }

    private boolean isChatHidden() {
        return this.client.options.getChatVisibility().getValue() == ChatVisibility.HIDDEN;
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
        this.addMessage(message, null, this.client.isConnectedToLocalServer() ? MessageIndicator.singlePlayer() : MessageIndicator.system());
    }

    public void addMessage(Text message, @Nullable MessageSignatureData signatureData, @Nullable MessageIndicator indicator) {
        ChatHudLine chatHudLine = new ChatHudLine(this.client.inGameHud.getTicks(), message, signatureData, indicator);
        this.logChatMessage(chatHudLine);
        this.addVisibleMessage(chatHudLine);
        this.addMessage(chatHudLine);
    }

    private void logChatMessage(ChatHudLine message) {
        String string = message.content().getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
        String string2 = (String)Nullables.map((Object)message.indicator(), MessageIndicator::loggedName);
        if (string2 != null) {
            LOGGER.info("[{}] [CHAT] {}", (Object)string2, (Object)string);
        } else {
            LOGGER.info("[CHAT] {}", (Object)string);
        }
    }

    private void addVisibleMessage(ChatHudLine message) {
        int i = MathHelper.floor((double)((double)this.getWidth() / this.getChatScale()));
        List list = message.breakLines(this.client.textRenderer, i);
        boolean bl = this.isChatFocused();
        for (int j = 0; j < list.size(); ++j) {
            OrderedText orderedText = (OrderedText)list.get(j);
            if (bl && this.scrolledLines > 0) {
                this.hasUnreadNewMessages = true;
                this.scroll(1);
            }
            boolean bl2 = j == list.size() - 1;
            this.visibleMessages.addFirst(new ChatHudLine.Visible(message.creationTick(), orderedText, message.indicator(), bl2));
        }
        while (this.visibleMessages.size() > 100) {
            this.visibleMessages.removeLast();
        }
    }

    private void addMessage(ChatHudLine message) {
        this.messages.addFirst(message);
        while (this.messages.size() > 100) {
            this.messages.removeLast();
        }
    }

    private void tickRemovalQueue() {
        int i = this.client.inGameHud.getTicks();
        this.removalQueue.removeIf(message -> {
            if (i >= message.deletableAfter()) {
                return this.queueForRemoval(message.signature()) == null;
            }
            return false;
        });
    }

    public void removeMessage(MessageSignatureData signature) {
        RemovalQueuedMessage removalQueuedMessage = this.queueForRemoval(signature);
        if (removalQueuedMessage != null) {
            this.removalQueue.add(removalQueuedMessage);
        }
    }

    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ChatHud.RemovalQueuedMessage queueForRemoval(MessageSignatureData signature) {
        int i = this.client.inGameHud.getTicks();
        ListIterator<ChatHudLine> listIterator = this.messages.listIterator();
        while (listIterator.hasNext()) {
            ChatHudLine chatHudLine = (ChatHudLine)listIterator.next();
            if (!signature.equals((Object)chatHudLine.signature())) continue;
            int j = chatHudLine.creationTick() + 60;
            if (i >= j) {
                listIterator.set(this.createRemovalMarker(chatHudLine));
                this.refresh();
                return null;
            }
            return new RemovalQueuedMessage(signature, j);
        }
        return null;
    }

    private ChatHudLine createRemovalMarker(ChatHudLine original) {
        return new ChatHudLine(original.creationTick(), DELETED_MARKER_TEXT, null, MessageIndicator.system());
    }

    public void reset() {
        this.resetScroll();
        this.refresh();
    }

    private void refresh() {
        this.visibleMessages.clear();
        for (ChatHudLine chatHudLine : Lists.reverse((List)this.messages)) {
            this.addVisibleMessage(chatHudLine);
        }
    }

    public ArrayListDeque<String> getMessageHistory() {
        return this.messageHistory;
    }

    public void addToMessageHistory(String message) {
        if (!message.equals(this.messageHistory.peekLast())) {
            if (this.messageHistory.size() >= 100) {
                this.messageHistory.removeFirst();
            }
            this.messageHistory.addLast((Object)message);
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

    public boolean isChatFocused() {
        return this.client.currentScreen instanceof ChatScreen;
    }

    private int getWidth() {
        return ChatHud.getWidth((double)((Double)this.client.options.getChatWidth().getValue()));
    }

    private int getHeight() {
        return ChatHud.getHeight((double)(this.isChatFocused() ? (Double)this.client.options.getChatHeightFocused().getValue() : (Double)this.client.options.getChatHeightUnfocused().getValue()));
    }

    private double getChatScale() {
        return (Double)this.client.options.getChatScale().getValue();
    }

    public static int getWidth(double widthOption) {
        int i = 320;
        int j = 40;
        return MathHelper.floor((double)(widthOption * 280.0 + 40.0));
    }

    public static int getHeight(double heightOption) {
        int i = 180;
        int j = 20;
        return MathHelper.floor((double)(heightOption * 160.0 + 20.0));
    }

    public static double getDefaultUnfocusedHeight() {
        int i = 180;
        int j = 20;
        return 70.0 / (double)(ChatHud.getHeight((double)1.0) - 20);
    }

    public int getVisibleLineCount() {
        return this.getHeight() / this.getLineHeight();
    }

    private int getLineHeight() {
        Objects.requireNonNull(this.client.textRenderer);
        return (int)(9.0 * ((Double)this.client.options.getChatLineSpacing().getValue() + 1.0));
    }

    public void saveDraft(String text) {
        boolean bl = text.startsWith("/");
        this.draft = new Draft(text, bl ? ChatMethod.COMMAND : ChatMethod.MESSAGE);
    }

    public void discardDraft() {
        this.draft = null;
    }

    public <T extends ChatScreen> T createScreen(ChatMethod method, ChatScreen.Factory<T> factory) {
        if (this.draft != null && method.shouldKeepDraft(this.draft)) {
            return (T)factory.create(this.draft.text(), true);
        }
        return (T)factory.create(method.getReplacement(), false);
    }

    public void setClientScreen(ChatMethod method, ChatScreen.Factory<?> factory) {
        this.client.setScreen((Screen)this.createScreen(method, factory));
    }

    public void setScreen() {
        Screen screen = this.client.currentScreen;
        if (screen instanceof ChatScreen) {
            ChatScreen chatScreen;
            this.screen = chatScreen = (ChatScreen)screen;
        }
    }

    public @Nullable ChatScreen removeScreen() {
        ChatScreen chatScreen = this.screen;
        this.screen = null;
        return chatScreen;
    }

    public ChatState toChatState() {
        return new ChatState(List.copyOf(this.messages), List.copyOf(this.messageHistory), List.copyOf(this.removalQueue));
    }

    public void restoreChatState(ChatState state) {
        this.messageHistory.clear();
        this.messageHistory.addAll((Collection)state.messageHistory);
        this.removalQueue.clear();
        this.removalQueue.addAll(state.removalQueue);
        this.messages.clear();
        this.messages.addAll(state.messages);
        this.refresh();
    }
}

