/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.suggestion.Suggestion
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestion;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

@Environment(value=EnvType.CLIENT)
public class ChatInputSuggestor.SuggestionWindow {
    private final Rect2i area;
    private final String typedText;
    private final List<Suggestion> suggestions;
    private int inWindowIndex;
    private int selection;
    private Vec2f mouse = Vec2f.ZERO;
    boolean completed;
    private int lastNarrationIndex;

    ChatInputSuggestor.SuggestionWindow(int x, int y, int width, List<Suggestion> suggestions, boolean narrateFirstSuggestion) {
        int i = x - (ChatInputSuggestor.this.textField.drawsBackground() ? 0 : 1);
        int j = ChatInputSuggestor.this.chatScreenSized ? y - 3 - Math.min(suggestions.size(), ChatInputSuggestor.this.maxSuggestionSize) * 12 : y - (ChatInputSuggestor.this.textField.drawsBackground() ? 1 : 0);
        this.area = new Rect2i(i, j, width + 1, Math.min(suggestions.size(), ChatInputSuggestor.this.maxSuggestionSize) * 12);
        this.typedText = ChatInputSuggestor.this.textField.getText();
        this.lastNarrationIndex = narrateFirstSuggestion ? -1 : 0;
        this.suggestions = suggestions;
        this.select(0);
    }

    public void render(DrawContext context, int mouseX, int mouseY) {
        Message message;
        boolean bl4;
        int i = Math.min(this.suggestions.size(), ChatInputSuggestor.this.maxSuggestionSize);
        int j = -5592406;
        boolean bl = this.inWindowIndex > 0;
        boolean bl2 = this.suggestions.size() > this.inWindowIndex + i;
        boolean bl3 = bl || bl2;
        boolean bl5 = bl4 = this.mouse.x != (float)mouseX || this.mouse.y != (float)mouseY;
        if (bl4) {
            this.mouse = new Vec2f(mouseX, mouseY);
        }
        if (bl3) {
            int k;
            context.fill(this.area.getX(), this.area.getY() - 1, this.area.getX() + this.area.getWidth(), this.area.getY(), ChatInputSuggestor.this.color);
            context.fill(this.area.getX(), this.area.getY() + this.area.getHeight(), this.area.getX() + this.area.getWidth(), this.area.getY() + this.area.getHeight() + 1, ChatInputSuggestor.this.color);
            if (bl) {
                for (k = 0; k < this.area.getWidth(); ++k) {
                    if (k % 2 != 0) continue;
                    context.fill(this.area.getX() + k, this.area.getY() - 1, this.area.getX() + k + 1, this.area.getY(), -1);
                }
            }
            if (bl2) {
                for (k = 0; k < this.area.getWidth(); ++k) {
                    if (k % 2 != 0) continue;
                    context.fill(this.area.getX() + k, this.area.getY() + this.area.getHeight(), this.area.getX() + k + 1, this.area.getY() + this.area.getHeight() + 1, -1);
                }
            }
        }
        boolean bl52 = false;
        for (int l = 0; l < i; ++l) {
            Suggestion suggestion = this.suggestions.get(l + this.inWindowIndex);
            context.fill(this.area.getX(), this.area.getY() + 12 * l, this.area.getX() + this.area.getWidth(), this.area.getY() + 12 * l + 12, ChatInputSuggestor.this.color);
            if (mouseX > this.area.getX() && mouseX < this.area.getX() + this.area.getWidth() && mouseY > this.area.getY() + 12 * l && mouseY < this.area.getY() + 12 * l + 12) {
                if (bl4) {
                    this.select(l + this.inWindowIndex);
                }
                bl52 = true;
            }
            context.drawTextWithShadow(ChatInputSuggestor.this.textRenderer, suggestion.getText(), this.area.getX() + 1, this.area.getY() + 2 + 12 * l, l + this.inWindowIndex == this.selection ? -256 : -5592406);
        }
        if (bl52 && (message = this.suggestions.get(this.selection).getTooltip()) != null) {
            context.drawTooltip(ChatInputSuggestor.this.textRenderer, Texts.toText(message), mouseX, mouseY);
        }
        if (this.area.contains(mouseX, mouseY)) {
            context.setCursor(StandardCursors.POINTING_HAND);
        }
    }

    public boolean mouseClicked(int x, int y) {
        if (!this.area.contains(x, y)) {
            return false;
        }
        int i = (y - this.area.getY()) / 12 + this.inWindowIndex;
        if (i >= 0 && i < this.suggestions.size()) {
            this.select(i);
            this.complete();
        }
        return true;
    }

    public boolean mouseScrolled(double amount) {
        int j;
        int i = (int)ChatInputSuggestor.this.client.mouse.getScaledX(ChatInputSuggestor.this.client.getWindow());
        if (this.area.contains(i, j = (int)ChatInputSuggestor.this.client.mouse.getScaledY(ChatInputSuggestor.this.client.getWindow()))) {
            this.inWindowIndex = MathHelper.clamp((int)((double)this.inWindowIndex - amount), 0, Math.max(this.suggestions.size() - ChatInputSuggestor.this.maxSuggestionSize, 0));
            return true;
        }
        return false;
    }

    public boolean keyPressed(KeyInput input) {
        if (input.isUp()) {
            this.scroll(-1);
            this.completed = false;
            return true;
        }
        if (input.isDown()) {
            this.scroll(1);
            this.completed = false;
            return true;
        }
        if (input.isTab()) {
            if (this.completed) {
                this.scroll(input.hasShift() ? -1 : 1);
            }
            this.complete();
            return true;
        }
        if (input.isEscape()) {
            ChatInputSuggestor.this.clearWindow();
            ChatInputSuggestor.this.textField.setSuggestion(null);
            return true;
        }
        return false;
    }

    public void scroll(int offset) {
        this.select(this.selection + offset);
        int i = this.inWindowIndex;
        int j = this.inWindowIndex + ChatInputSuggestor.this.maxSuggestionSize - 1;
        if (this.selection < i) {
            this.inWindowIndex = MathHelper.clamp(this.selection, 0, Math.max(this.suggestions.size() - ChatInputSuggestor.this.maxSuggestionSize, 0));
        } else if (this.selection > j) {
            this.inWindowIndex = MathHelper.clamp(this.selection + ChatInputSuggestor.this.inWindowIndexOffset - ChatInputSuggestor.this.maxSuggestionSize, 0, Math.max(this.suggestions.size() - ChatInputSuggestor.this.maxSuggestionSize, 0));
        }
    }

    public void select(int index) {
        this.selection = index;
        if (this.selection < 0) {
            this.selection += this.suggestions.size();
        }
        if (this.selection >= this.suggestions.size()) {
            this.selection -= this.suggestions.size();
        }
        Suggestion suggestion = this.suggestions.get(this.selection);
        ChatInputSuggestor.this.textField.setSuggestion(ChatInputSuggestor.getSuggestionSuffix(ChatInputSuggestor.this.textField.getText(), suggestion.apply(this.typedText)));
        if (this.lastNarrationIndex != this.selection) {
            ChatInputSuggestor.this.client.getNarratorManager().narrateSystemImmediately(this.getNarration());
        }
    }

    public void complete() {
        Suggestion suggestion = this.suggestions.get(this.selection);
        ChatInputSuggestor.this.completingSuggestions = true;
        ChatInputSuggestor.this.textField.setText(suggestion.apply(this.typedText));
        int i = suggestion.getRange().getStart() + suggestion.getText().length();
        ChatInputSuggestor.this.textField.setSelectionStart(i);
        ChatInputSuggestor.this.textField.setSelectionEnd(i);
        this.select(this.selection);
        ChatInputSuggestor.this.completingSuggestions = false;
        this.completed = true;
    }

    Text getNarration() {
        this.lastNarrationIndex = this.selection;
        Suggestion suggestion = this.suggestions.get(this.selection);
        Message message = suggestion.getTooltip();
        if (message != null) {
            return Text.translatable("narration.suggestion.tooltip", this.selection + 1, this.suggestions.size(), suggestion.getText(), Text.of(message));
        }
        return Text.translatable("narration.suggestion", this.selection + 1, this.suggestions.size(), suggestion.getText());
    }
}
