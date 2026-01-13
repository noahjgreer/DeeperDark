/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fc
 *  org.joml.Vector2f
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.StandardCursors;
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
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ChatHud {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_MESSAGES = 100;
    private static final int field_39772 = 4;
    private static final int OFFSET_FROM_BOTTOM = 40;
    private static final int field_63864 = 210;
    private static final int REMOVAL_QUEUE_TICKS = 60;
    private static final Text DELETED_MARKER_TEXT = Text.translatable("chat.deleted_marker").formatted(Formatting.GRAY, Formatting.ITALIC);
    public static final int field_63862 = 8;
    public static final Identifier EXPAND_CHAT_QUEUE_ID = Identifier.ofVanilla("internal/expand_chat_queue");
    private static final Style CHAT_QUEUE_STYLE = Style.EMPTY.withClickEvent(new ClickEvent.Custom(EXPAND_CHAT_QUEUE_ID, Optional.empty())).withHoverEvent(new HoverEvent.ShowText(Text.translatable("chat.queue.tooltip")));
    final MinecraftClient client;
    private final ArrayListDeque<String> messageHistory = new ArrayListDeque(100);
    private final List<ChatHudLine> messages = Lists.newArrayList();
    private final List<ChatHudLine.Visible> visibleMessages = Lists.newArrayList();
    private int scrolledLines;
    private boolean hasUnreadNewMessages;
    private @Nullable Draft draft;
    private @Nullable ChatScreen screen;
    private final List<RemovalQueuedMessage> removalQueue = new ArrayList<RemovalQueuedMessage>();

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
            ChatHudLine.Visible visible = this.visibleMessages.get(l);
            float f = opacityRule.calculate(visible);
            if (!(f > 1.0E-5f)) continue;
            ++j;
            lineConsumer.accept(visible, k, f);
        }
        return j;
    }

    public void render(DrawContext context, TextRenderer textRenderer, int currentTick, int mouseX, int mouseY, boolean interactable, boolean bl) {
        context.getMatrices().pushMatrix();
        this.render(interactable ? new Interactable(context, textRenderer, mouseX, mouseY, bl) : new Hud(context), context.getScaledWindowHeight(), currentTick, interactable);
        context.getMatrices().popMatrix();
    }

    public void render(DrawnTextConsumer textConsumer, int windowHeight, int currentTick, boolean expanded) {
        this.render(new Forwarder(textConsumer), windowHeight, currentTick, expanded);
    }

    private void render(final Backend drawer, int windowHeight, int currentTick, boolean expanded) {
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
        int j = MathHelper.ceil((float)this.getWidth() / f);
        final int k = MathHelper.floor((float)(windowHeight - 40) / f);
        final float g = this.client.options.getChatOpacity().getValue().floatValue() * 0.9f + 0.1f;
        float h = this.client.options.getTextBackgroundOpacity().getValue().floatValue();
        final int l = this.client.textRenderer.fontHeight;
        int m = 8;
        double d = this.client.options.getChatLineSpacing().getValue();
        final int n = (int)((double)l * (d + 1.0));
        final int o = (int)Math.round(8.0 * (d + 1.0) - 4.0 * d);
        long p = this.client.getMessageHandler().getUnprocessedMessageCount();
        OpacityRule opacityRule = expanded ? OpacityRule.CONSTANT : OpacityRule.timeBased(currentTick);
        drawer.updatePose(pose -> {
            pose.scale(f, f);
            pose.translate(4.0f, 0.0f);
        });
        this.forEachVisibleLine(opacityRule, (line, y, opacity) -> {
            int l = k - y * n;
            int m = l - n;
            drawer.fill(-4, m, j + 4 + 4, l, ColorHelper.toAlpha(opacity * h));
        });
        if (p > 0L) {
            drawer.fill(-2, k, j + 4, k + l, ColorHelper.toAlpha(h));
        }
        int q = this.forEachVisibleLine(opacityRule, new LineConsumer(){
            boolean styledCurrentLine;

            @Override
            public void accept(ChatHudLine.Visible visible, int i, float f) {
                boolean bl2;
                int j = k - i * n;
                int k2 = j - n;
                int l2 = j - o;
                boolean bl = drawer.text(l2, f * g, visible.content());
                this.styledCurrentLine |= bl;
                if (visible.endOfEntry()) {
                    bl2 = this.styledCurrentLine;
                    this.styledCurrentLine = false;
                } else {
                    bl2 = false;
                }
                MessageIndicator messageIndicator = visible.indicator();
                if (messageIndicator != null) {
                    drawer.indicator(-4, k2, -2, j, f * g, messageIndicator);
                    if (messageIndicator.icon() != null) {
                        int m = visible.getWidth(ChatHud.this.client.textRenderer);
                        int n2 = l2 + l;
                        drawer.indicatorIcon(m, n2, bl2, messageIndicator, messageIndicator.icon());
                    }
                }
            }
        });
        if (p > 0L) {
            r = k + l;
            MutableText text = Text.translatable("chat.queue", p).setStyle(CHAT_QUEUE_STYLE);
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
                drawer.fill(x, -t, x + 2, -t - u, ColorHelper.withAlpha(v, w));
                drawer.fill(x + 2, -t, x + 1, -t - u, ColorHelper.withAlpha(v, 0xCCCCCC));
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
        String string2 = Nullables.map(message.indicator(), MessageIndicator::loggedName);
        if (string2 != null) {
            LOGGER.info("[{}] [CHAT] {}", (Object)string2, (Object)string);
        } else {
            LOGGER.info("[CHAT] {}", (Object)string);
        }
    }

    private void addVisibleMessage(ChatHudLine message) {
        int i = MathHelper.floor((double)this.getWidth() / this.getChatScale());
        List<OrderedText> list = message.breakLines(this.client.textRenderer, i);
        boolean bl = this.isChatFocused();
        for (int j = 0; j < list.size(); ++j) {
            OrderedText orderedText = list.get(j);
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

    private @Nullable RemovalQueuedMessage queueForRemoval(MessageSignatureData signature) {
        int i = this.client.inGameHud.getTicks();
        ListIterator<ChatHudLine> listIterator = this.messages.listIterator();
        while (listIterator.hasNext()) {
            ChatHudLine chatHudLine = listIterator.next();
            if (!signature.equals(chatHudLine.signature())) continue;
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
        for (ChatHudLine chatHudLine : Lists.reverse(this.messages)) {
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

    public boolean isChatFocused() {
        return this.client.currentScreen instanceof ChatScreen;
    }

    private int getWidth() {
        return ChatHud.getWidth(this.client.options.getChatWidth().getValue());
    }

    private int getHeight() {
        return ChatHud.getHeight(this.isChatFocused() ? this.client.options.getChatHeightFocused().getValue() : this.client.options.getChatHeightUnfocused().getValue());
    }

    private double getChatScale() {
        return this.client.options.getChatScale().getValue();
    }

    public static int getWidth(double widthOption) {
        int i = 320;
        int j = 40;
        return MathHelper.floor(widthOption * 280.0 + 40.0);
    }

    public static int getHeight(double heightOption) {
        int i = 180;
        int j = 20;
        return MathHelper.floor(heightOption * 160.0 + 20.0);
    }

    public static double getDefaultUnfocusedHeight() {
        int i = 180;
        int j = 20;
        return 70.0 / (double)(ChatHud.getHeight(1.0) - 20);
    }

    public int getVisibleLineCount() {
        return this.getHeight() / this.getLineHeight();
    }

    private int getLineHeight() {
        return (int)((double)this.client.textRenderer.fontHeight * (this.client.options.getChatLineSpacing().getValue() + 1.0));
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
            return factory.create(this.draft.text(), true);
        }
        return factory.create(method.getReplacement(), false);
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
        this.messageHistory.addAll(state.messageHistory);
        this.removalQueue.clear();
        this.removalQueue.addAll(state.removalQueue);
        this.messages.clear();
        this.messages.addAll(state.messages);
        this.refresh();
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface OpacityRule {
        public static final OpacityRule CONSTANT = line -> 1.0f;

        public static OpacityRule timeBased(int currentTick) {
            return line -> {
                int j = currentTick - line.addedTime();
                double d = (double)j / 200.0;
                d = 1.0 - d;
                d *= 10.0;
                d = MathHelper.clamp(d, 0.0, 1.0);
                d *= d;
                return (float)d;
            };
        }

        public float calculate(ChatHudLine.Visible var1);
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface LineConsumer {
        public void accept(ChatHudLine.Visible var1, int var2, float var3);
    }

    @Environment(value=EnvType.CLIENT)
    static class Interactable
    implements Backend,
    Consumer<Style> {
        private final DrawContext context;
        private final TextRenderer textRenderer;
        private final DrawnTextConsumer drawer;
        private DrawnTextConsumer.Transformation transformation;
        private final int mouseX;
        private final int mouseY;
        private final Vector2f untransformedOffset = new Vector2f();
        private @Nullable Style style;
        private final boolean field_64672;

        public Interactable(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY, boolean bl) {
            this.context = context;
            this.textRenderer = textRenderer;
            this.drawer = context.getTextConsumer(DrawContext.HoverType.TOOLTIP_AND_CURSOR, this);
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.field_64672 = bl;
            this.transformation = this.drawer.getTransformation();
            this.calculateUntransformedOffset();
        }

        private void calculateUntransformedOffset() {
            this.context.getMatrices().invert(new Matrix3x2f()).transformPosition((float)this.mouseX, (float)this.mouseY, this.untransformedOffset);
        }

        @Override
        public void updatePose(Consumer<Matrix3x2f> transformer) {
            transformer.accept((Matrix3x2f)this.context.getMatrices());
            this.transformation = this.transformation.withPose((Matrix3x2fc)new Matrix3x2f((Matrix3x2fc)this.context.getMatrices()));
            this.calculateUntransformedOffset();
        }

        @Override
        public void fill(int x1, int y1, int x2, int y2, int color) {
            this.context.fill(x1, y1, x2, y2, color);
        }

        @Override
        public void accept(Style style) {
            this.style = style;
        }

        @Override
        public boolean text(int y, float opacity, OrderedText text) {
            this.style = null;
            this.drawer.text(Alignment.LEFT, 0, y, this.transformation.withOpacity(opacity), text);
            if (this.field_64672 && this.style != null && this.style.getInsertion() != null) {
                this.context.setCursor(StandardCursors.POINTING_HAND);
            }
            return this.style != null;
        }

        private boolean isWithinBounds(int left, int top, int right, int bottom) {
            return DrawnTextConsumer.isWithinBounds(this.untransformedOffset.x, this.untransformedOffset.y, left, top, right, bottom);
        }

        @Override
        public void indicator(int x1, int y1, int x2, int y2, float opacity, MessageIndicator indicator) {
            int i = ColorHelper.withAlpha(opacity, indicator.indicatorColor());
            this.context.fill(x1, y1, x2, y2, i);
            if (this.isWithinBounds(x1, y1, x2, y2)) {
                this.indicatorTooltip(indicator);
            }
        }

        @Override
        public void indicatorIcon(int left, int bottom, boolean forceDraw, MessageIndicator indicator, MessageIndicator.Icon icon) {
            int i = bottom - icon.height - 1;
            int j = left + icon.width;
            boolean bl = this.isWithinBounds(left, i, j, bottom);
            if (bl) {
                this.indicatorTooltip(indicator);
            }
            if (forceDraw || bl) {
                icon.draw(this.context, left, i);
            }
        }

        private void indicatorTooltip(MessageIndicator indicator) {
            if (indicator.text() != null) {
                this.context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(indicator.text(), 210), this.mouseX, this.mouseY);
            }
        }

        @Override
        public /* synthetic */ void accept(Object style) {
            this.accept((Style)style);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Hud
    implements Backend {
        private final DrawContext context;
        private final DrawnTextConsumer textConsumer;
        private DrawnTextConsumer.Transformation transformation;

        public Hud(DrawContext context) {
            this.context = context;
            this.textConsumer = context.getTextConsumer(DrawContext.HoverType.NONE, null);
            this.transformation = this.textConsumer.getTransformation();
        }

        @Override
        public void updatePose(Consumer<Matrix3x2f> transformer) {
            transformer.accept((Matrix3x2f)this.context.getMatrices());
            this.transformation = this.transformation.withPose((Matrix3x2fc)new Matrix3x2f((Matrix3x2fc)this.context.getMatrices()));
        }

        @Override
        public void fill(int x1, int y1, int x2, int y2, int color) {
            this.context.fill(x1, y1, x2, y2, color);
        }

        @Override
        public boolean text(int y, float opacity, OrderedText text) {
            this.textConsumer.text(Alignment.LEFT, 0, y, this.transformation.withOpacity(opacity), text);
            return false;
        }

        @Override
        public void indicator(int x1, int y1, int x2, int y2, float opacity, MessageIndicator indicator) {
            int i = ColorHelper.withAlpha(opacity, indicator.indicatorColor());
            this.context.fill(x1, y1, x2, y2, i);
        }

        @Override
        public void indicatorIcon(int left, int bottom, boolean forceDraw, MessageIndicator indicator, MessageIndicator.Icon icon) {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Backend {
        public void updatePose(Consumer<Matrix3x2f> var1);

        public void fill(int var1, int var2, int var3, int var4, int var5);

        public boolean text(int var1, float var2, OrderedText var3);

        public void indicator(int var1, int var2, int var3, int var4, float var5, MessageIndicator var6);

        public void indicatorIcon(int var1, int var2, boolean var3, MessageIndicator var4, MessageIndicator.Icon var5);
    }

    @Environment(value=EnvType.CLIENT)
    static class Forwarder
    implements Backend {
        private final DrawnTextConsumer drawer;

        public Forwarder(DrawnTextConsumer drawer) {
            this.drawer = drawer;
        }

        @Override
        public void updatePose(Consumer<Matrix3x2f> transformer) {
            DrawnTextConsumer.Transformation transformation = this.drawer.getTransformation();
            Matrix3x2f matrix3x2f = new Matrix3x2f(transformation.pose());
            transformer.accept(matrix3x2f);
            this.drawer.setTransformation(transformation.withPose((Matrix3x2fc)matrix3x2f));
        }

        @Override
        public void fill(int x1, int y1, int x2, int y2, int color) {
        }

        @Override
        public boolean text(int y, float opacity, OrderedText text) {
            this.drawer.text(Alignment.LEFT, 0, y, text);
            return false;
        }

        @Override
        public void indicator(int x1, int y1, int x2, int y2, float opacity, MessageIndicator indicator) {
        }

        @Override
        public void indicatorIcon(int left, int bottom, boolean forceDraw, MessageIndicator indicator, MessageIndicator.Icon icon) {
        }
    }

    @Environment(value=EnvType.CLIENT)
    record RemovalQueuedMessage(MessageSignatureData signature, int deletableAfter) {
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Draft
    extends Record {
        private final String text;
        final ChatMethod chatMethod;

        public Draft(String text, ChatMethod chatMethod) {
            this.text = text;
            this.chatMethod = chatMethod;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Draft.class, "text;chatMethod", "text", "chatMethod"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Draft.class, "text;chatMethod", "text", "chatMethod"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Draft.class, "text;chatMethod", "text", "chatMethod"}, this, object);
        }

        public String text() {
            return this.text;
        }

        public ChatMethod chatMethod() {
            return this.chatMethod;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract sealed class ChatMethod
    extends Enum<ChatMethod> {
        public static final /* enum */ ChatMethod MESSAGE = new ChatMethod(""){

            @Override
            public boolean shouldKeepDraft(Draft draft) {
                return true;
            }
        };
        public static final /* enum */ ChatMethod COMMAND = new ChatMethod("/"){

            @Override
            public boolean shouldKeepDraft(Draft draft) {
                return this == draft.chatMethod;
            }
        };
        private final String replacement;
        private static final /* synthetic */ ChatMethod[] field_62007;

        public static ChatMethod[] values() {
            return (ChatMethod[])field_62007.clone();
        }

        public static ChatMethod valueOf(String string) {
            return Enum.valueOf(ChatMethod.class, string);
        }

        ChatMethod(String replacement) {
            this.replacement = replacement;
        }

        public String getReplacement() {
            return this.replacement;
        }

        public abstract boolean shouldKeepDraft(Draft var1);

        private static /* synthetic */ ChatMethod[] method_73209() {
            return new ChatMethod[]{MESSAGE, COMMAND};
        }

        static {
            field_62007 = ChatMethod.method_73209();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class ChatState {
        final List<ChatHudLine> messages;
        final List<String> messageHistory;
        final List<RemovalQueuedMessage> removalQueue;

        public ChatState(List<ChatHudLine> messages, List<String> messageHistory, List<RemovalQueuedMessage> removalQueue) {
            this.messages = messages;
            this.messageHistory = messageHistory;
            this.removalQueue = removalQueue;
        }
    }
}
