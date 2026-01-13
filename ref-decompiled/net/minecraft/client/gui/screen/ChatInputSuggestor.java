/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.context.CommandContextBuilder
 *  com.mojang.brigadier.context.ParsedArgument
 *  com.mojang.brigadier.context.SuggestionContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.Suggestion
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ChatInputSuggestor
 *  net.minecraft.client.gui.screen.ChatInputSuggestor$SuggestionWindow
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.network.ClientCommandSource
 *  net.minecraft.command.CommandSource
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.server.command.CommandManager
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.text.Texts
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.math.MathHelper
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ChatInputSuggestor {
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
    private static final Style ERROR_STYLE = Style.EMPTY.withColor(Formatting.RED);
    private static final Style INFO_STYLE = Style.EMPTY.withColor(Formatting.GRAY);
    private static final List<Style> HIGHLIGHT_STYLES = (List)Stream.of(Formatting.AQUA, Formatting.YELLOW, Formatting.GREEN, Formatting.LIGHT_PURPLE, Formatting.GOLD).map(arg_0 -> ((Style)Style.EMPTY).withColor(arg_0)).collect(ImmutableList.toImmutableList());
    final MinecraftClient client;
    private final Screen owner;
    final TextFieldWidget textField;
    final TextRenderer textRenderer;
    private final boolean slashOptional;
    private final boolean suggestingWhenEmpty;
    final int inWindowIndexOffset;
    final int maxSuggestionSize;
    final boolean chatScreenSized;
    final int color;
    private final List<OrderedText> messages = Lists.newArrayList();
    private int x;
    private int width;
    private @Nullable ParseResults<ClientCommandSource> parse;
    private @Nullable CompletableFuture<Suggestions> pendingSuggestions;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable SuggestionWindow window;
    private boolean windowActive;
    boolean completingSuggestions;
    private boolean canLeave = true;

    public ChatInputSuggestor(MinecraftClient client, Screen owner, TextFieldWidget textField, TextRenderer textRenderer, boolean slashOptional, boolean suggestingWhenEmpty, int inWindowIndexOffset, int maxSuggestionSize, boolean chatScreenSized, int color) {
        this.client = client;
        this.owner = owner;
        this.textField = textField;
        this.textRenderer = textRenderer;
        this.slashOptional = slashOptional;
        this.suggestingWhenEmpty = suggestingWhenEmpty;
        this.inWindowIndexOffset = inWindowIndexOffset;
        this.maxSuggestionSize = maxSuggestionSize;
        this.chatScreenSized = chatScreenSized;
        this.color = color;
        textField.addFormatter((arg_0, arg_1) -> this.provideRenderText(arg_0, arg_1));
    }

    public void setWindowActive(boolean windowActive) {
        this.windowActive = windowActive;
        if (!windowActive) {
            this.window = null;
        }
    }

    public void setCanLeave(boolean canLeave) {
        this.canLeave = canLeave;
    }

    public boolean keyPressed(KeyInput input) {
        boolean bl;
        boolean bl2 = bl = this.window != null;
        if (bl && this.window.keyPressed(input)) {
            return true;
        }
        if (this.owner.getFocused() == this.textField && input.isTab() && (!this.canLeave || bl)) {
            this.show(true);
            return true;
        }
        return false;
    }

    public boolean mouseScrolled(double amount) {
        return this.window != null && this.window.mouseScrolled(MathHelper.clamp((double)amount, (double)-1.0, (double)1.0));
    }

    public boolean mouseClicked(Click click) {
        return this.window != null && this.window.mouseClicked((int)click.x(), (int)click.y());
    }

    public void show(boolean narrateFirstSuggestion) {
        Suggestions suggestions;
        if (this.pendingSuggestions != null && this.pendingSuggestions.isDone() && !(suggestions = (Suggestions)this.pendingSuggestions.join()).isEmpty()) {
            int i = 0;
            for (Suggestion suggestion : suggestions.getList()) {
                i = Math.max(i, this.textRenderer.getWidth(suggestion.getText()));
            }
            int j = MathHelper.clamp((int)this.textField.getCharacterX(suggestions.getRange().getStart()), (int)0, (int)(this.textField.getCharacterX(0) + this.textField.getInnerWidth() - i));
            int k = this.chatScreenSized ? this.owner.height - 12 : 72;
            this.window = new SuggestionWindow(this, j, k, i, this.sortSuggestions(suggestions), narrateFirstSuggestion);
        }
    }

    public boolean isOpen() {
        return this.window != null;
    }

    public Text getSuggestionUsageNarrationText() {
        if (this.window != null && this.window.completed) {
            if (this.canLeave) {
                return Text.translatable((String)"narration.suggestion.usage.cycle.hidable");
            }
            return Text.translatable((String)"narration.suggestion.usage.cycle.fixed");
        }
        if (this.canLeave) {
            return Text.translatable((String)"narration.suggestion.usage.fill.hidable");
        }
        return Text.translatable((String)"narration.suggestion.usage.fill.fixed");
    }

    public void clearWindow() {
        this.window = null;
    }

    private List<Suggestion> sortSuggestions(Suggestions suggestions) {
        String string = this.textField.getText().substring(0, this.textField.getCursor());
        int i = ChatInputSuggestor.getStartOfCurrentWord((String)string);
        String string2 = string.substring(i).toLowerCase(Locale.ROOT);
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        for (Suggestion suggestion : suggestions.getList()) {
            if (suggestion.getText().startsWith(string2) || suggestion.getText().startsWith("minecraft:" + string2)) {
                list.add(suggestion);
                continue;
            }
            list2.add(suggestion);
        }
        list.addAll(list2);
        return list;
    }

    public void refresh() {
        boolean bl;
        String string = this.textField.getText();
        if (this.parse != null && !this.parse.getReader().getString().equals(string)) {
            this.parse = null;
        }
        if (!this.completingSuggestions) {
            this.textField.setSuggestion(null);
            this.window = null;
        }
        this.messages.clear();
        StringReader stringReader = new StringReader(string);
        boolean bl2 = bl = stringReader.canRead() && stringReader.peek() == '/';
        if (bl) {
            stringReader.skip();
        }
        boolean bl22 = this.slashOptional || bl;
        int i = this.textField.getCursor();
        if (bl22) {
            int j;
            CommandDispatcher commandDispatcher = this.client.player.networkHandler.getCommandDispatcher();
            if (this.parse == null) {
                this.parse = commandDispatcher.parse(stringReader, (Object)this.client.player.networkHandler.getCommandSource());
            }
            int n = j = this.suggestingWhenEmpty ? stringReader.getCursor() : 1;
            if (!(i < j || this.window != null && this.completingSuggestions)) {
                this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.parse, i);
                this.pendingSuggestions.thenRun(() -> {
                    if (!this.pendingSuggestions.isDone()) {
                        return;
                    }
                    this.showCommandSuggestions();
                });
            }
        } else {
            String string2 = string.substring(0, i);
            int j = ChatInputSuggestor.getStartOfCurrentWord((String)string2);
            Collection collection = this.client.player.networkHandler.getCommandSource().getChatSuggestions();
            this.pendingSuggestions = CommandSource.suggestMatching((Iterable)collection, (SuggestionsBuilder)new SuggestionsBuilder(string2, j));
        }
    }

    private static int getStartOfCurrentWord(String input) {
        if (Strings.isNullOrEmpty((String)input)) {
            return 0;
        }
        int i = 0;
        Matcher matcher = WHITESPACE_PATTERN.matcher(input);
        while (matcher.find()) {
            i = matcher.end();
        }
        return i;
    }

    private static OrderedText formatException(CommandSyntaxException exception) {
        Text text = Texts.toText((Message)exception.getRawMessage());
        String string = exception.getContext();
        if (string == null) {
            return text.asOrderedText();
        }
        return Text.translatable((String)"command.context.parse_error", (Object[])new Object[]{text, exception.getCursor(), string}).asOrderedText();
    }

    private void showCommandSuggestions() {
        boolean bl = false;
        if (this.textField.getCursor() == this.textField.getText().length()) {
            if (((Suggestions)this.pendingSuggestions.join()).isEmpty() && !this.parse.getExceptions().isEmpty()) {
                int i = 0;
                for (Map.Entry entry : this.parse.getExceptions().entrySet()) {
                    CommandSyntaxException commandSyntaxException = (CommandSyntaxException)((Object)entry.getValue());
                    if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                        ++i;
                        continue;
                    }
                    this.messages.add(ChatInputSuggestor.formatException((CommandSyntaxException)commandSyntaxException));
                }
                if (i > 0) {
                    this.messages.add(ChatInputSuggestor.formatException((CommandSyntaxException)CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(this.parse.getReader())));
                }
            } else if (this.parse.getReader().canRead()) {
                bl = true;
            }
        }
        this.x = 0;
        this.width = this.owner.width;
        if (this.messages.isEmpty() && !this.showUsages(Formatting.GRAY) && bl) {
            this.messages.add(ChatInputSuggestor.formatException((CommandSyntaxException)CommandManager.getException((ParseResults)this.parse)));
        }
        this.window = null;
        if (this.windowActive && ((Boolean)this.client.options.getAutoSuggestions().getValue()).booleanValue()) {
            this.show(false);
        }
    }

    private boolean showUsages(Formatting formatting) {
        CommandContextBuilder commandContextBuilder = this.parse.getContext();
        SuggestionContext suggestionContext = commandContextBuilder.findSuggestionContext(this.textField.getCursor());
        Map map = this.client.player.networkHandler.getCommandDispatcher().getSmartUsage(suggestionContext.parent, (Object)this.client.player.networkHandler.getCommandSource());
        ArrayList list = Lists.newArrayList();
        int i = 0;
        Style style = Style.EMPTY.withColor(formatting);
        for (Map.Entry entry : map.entrySet()) {
            if (entry.getKey() instanceof LiteralCommandNode) continue;
            list.add(OrderedText.styledForwardsVisitedString((String)((String)entry.getValue()), (Style)style));
            i = Math.max(i, this.textRenderer.getWidth((String)entry.getValue()));
        }
        if (!list.isEmpty()) {
            this.messages.addAll(list);
            this.x = MathHelper.clamp((int)this.textField.getCharacterX(suggestionContext.startPos), (int)0, (int)(this.textField.getCharacterX(0) + this.textField.getInnerWidth() - i));
            this.width = i;
            return true;
        }
        return false;
    }

    private @Nullable OrderedText provideRenderText(String original, int firstCharacterIndex) {
        if (this.parse != null) {
            return ChatInputSuggestor.highlight((ParseResults)this.parse, (String)original, (int)firstCharacterIndex);
        }
        return null;
    }

    static @Nullable String getSuggestionSuffix(String original, String suggestion) {
        if (suggestion.startsWith(original)) {
            return suggestion.substring(original.length());
        }
        return null;
    }

    private static OrderedText highlight(ParseResults<ClientCommandSource> parse, String original, int firstCharacterIndex) {
        int m;
        ArrayList list = Lists.newArrayList();
        int i = 0;
        int j = -1;
        CommandContextBuilder commandContextBuilder = parse.getContext().getLastChild();
        for (ParsedArgument parsedArgument : commandContextBuilder.getArguments().values()) {
            int k;
            if (++j >= HIGHLIGHT_STYLES.size()) {
                j = 0;
            }
            if ((k = Math.max(parsedArgument.getRange().getStart() - firstCharacterIndex, 0)) >= original.length()) break;
            int l = Math.min(parsedArgument.getRange().getEnd() - firstCharacterIndex, original.length());
            if (l <= 0) continue;
            list.add(OrderedText.styledForwardsVisitedString((String)original.substring(i, k), (Style)INFO_STYLE));
            list.add(OrderedText.styledForwardsVisitedString((String)original.substring(k, l), (Style)((Style)HIGHLIGHT_STYLES.get(j))));
            i = l;
        }
        if (parse.getReader().canRead() && (m = Math.max(parse.getReader().getCursor() - firstCharacterIndex, 0)) < original.length()) {
            int n = Math.min(m + parse.getReader().getRemainingLength(), original.length());
            list.add(OrderedText.styledForwardsVisitedString((String)original.substring(i, m), (Style)INFO_STYLE));
            list.add(OrderedText.styledForwardsVisitedString((String)original.substring(m, n), (Style)ERROR_STYLE));
            i = n;
        }
        list.add(OrderedText.styledForwardsVisitedString((String)original.substring(i), (Style)INFO_STYLE));
        return OrderedText.concat((List)list);
    }

    public void render(DrawContext context, int mouseX, int mouseY) {
        if (!this.tryRenderWindow(context, mouseX, mouseY)) {
            this.renderMessages(context);
        }
    }

    public boolean tryRenderWindow(DrawContext context, int mouseX, int mouseY) {
        if (this.window != null) {
            this.window.render(context, mouseX, mouseY);
            return true;
        }
        return false;
    }

    public void renderMessages(DrawContext context) {
        int i = 0;
        for (OrderedText orderedText : this.messages) {
            int j = this.chatScreenSized ? this.owner.height - 14 - 13 - 12 * i : 72 + 12 * i;
            context.fill(this.x - 1, j, this.x + this.width + 1, j + 12, this.color);
            context.drawTextWithShadow(this.textRenderer, orderedText, this.x, j + 2, -1);
            ++i;
        }
    }

    public Text getNarration() {
        if (this.window != null) {
            return ScreenTexts.LINE_BREAK.copy().append(this.window.getNarration());
        }
        return ScreenTexts.EMPTY;
    }
}

