package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;
import net.noahsarch.deeperdark.client.chat.ChatInputLayoutState;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {

    private static final int MAX_CHAT_LENGTH = 32767;
    private static final int TEXT_COLOR = 0xFFE0E0E0;
    private static final int TEXT_X = 4;
    private static final int LINE_SPACING = 2;
    private static final int V_PADDING = 3;

    @Shadow protected EditBox input;
    @Shadow private CommandSuggestions commandSuggestions;
    @Shadow private ChatComponent.DisplayMode displayMode;

    @Inject(method = "init", at = @At("RETURN"))
    private void deeperdark$initMultiline(CallbackInfo ci) {
        if (this.input != null) {
            this.input.setMaxLength(MAX_CHAT_LENGTH);
            this.input.setVisible(false);
        }
        ChatInputLayoutState.reset();
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void deeperdark$resetLayoutOnClose(CallbackInfo ci) {
        ChatInputLayoutState.reset();
    }

    @Overwrite
    public String normalizeChatMessage(String message) {
        return StringUtils.normalizeSpace(message.trim());
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void deeperdark$renderMultiline(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Screen self = (Screen)(Object)this;
        Minecraft mc = Minecraft.getInstance();
        if (this.input == null || mc.font == null) {
            return;
        }

        String text = this.input.getValue();
        Font font = mc.font;
        int lineH = font.lineHeight + LINE_SPACING;
        int maxWidth = self.width - TEXT_X * 2;

        List<int[]> lineRanges = deeperdark$computeLineRanges(font, text, maxWidth);
        int numLines = lineRanges.size();
        int totalInputH = numLines * lineH + V_PADDING * 2;

        int bgBottom = self.height - 2;
        int bgTop = bgBottom - totalInputH;
        int bgColor = mc.options.getBackgroundColor(Integer.MIN_VALUE);

        // Background for the expanding input area
        graphics.fill(2, bgTop, self.width - 2, bgBottom, bgColor);

        // Locate which visual line the cursor is on
        int cursorCharPos = this.input.getCursorPosition();
        int cursorLine = numLines - 1;
        int cursorInLine = text.length() - lineRanges.get(numLines - 1)[0];
        for (int i = 0; i < lineRanges.size(); i++) {
            int start = lineRanges.get(i)[0];
            int end   = lineRanges.get(i)[1];
            boolean isLastLine = (i == lineRanges.size() - 1);
            if (cursorCharPos >= start && (isLastLine || cursorCharPos < end)) {
                cursorLine = i;
                cursorInLine = cursorCharPos - start;
                break;
            }
        }

        boolean showCursor = this.input.isFocused() && (Util.getMillis() / 300 % 2 == 0);

        // Draw each wrapped line and the cursor
        for (int i = 0; i < lineRanges.size(); i++) {
            int start    = lineRanges.get(i)[0];
            int end      = lineRanges.get(i)[1];
            String lineText = text.substring(start, end);
            int textY = bgTop + V_PADDING + i * lineH;

            graphics.text(font, FormattedCharSequence.forward(lineText, Style.EMPTY), TEXT_X, textY, TEXT_COLOR);

            if (showCursor && i == cursorLine) {
                int posInLine = Math.min(cursorInLine, lineText.length());
                int cx = TEXT_X + font.width(lineText.substring(0, posInLine));
                graphics.fill(cx, textY - 1, cx + 1, textY + font.lineHeight + 1, 0xFFFFFFFF);
            }
        }

        // Expose expanded height for any systems that need to offset around the input box
        ChatInputLayoutState.setChatOffset((numLines - 1) * lineH);

        // Chat history (renders above the input area)
        mc.gui.getChat().extractRenderState(
            graphics, font, mc.gui.getGuiTicks(),
            mouseX, mouseY, this.displayMode, mc.hasShiftDown()
        );

        // Command suggestions (not a renderable widget, rendered explicitly)
        this.commandSuggestions.extractRenderState(graphics, mouseX, mouseY);

        ci.cancel();
    }

    @Unique
    private static List<int[]> deeperdark$computeLineRanges(Font font, String text, int maxWidth) {
        List<int[]> ranges = new ArrayList<>();
        if (text.isEmpty()) {
            ranges.add(new int[]{0, 0});
            return ranges;
        }
        int pos = 0;
        while (pos < text.length()) {
            String remaining = text.substring(pos);
            String fits = font.plainSubstrByWidth(remaining, maxWidth);
            int end = pos + fits.length();
            // Prefer breaking at the last space rather than mid-word
            if (end < text.length() && fits.length() > 0) {
                int lastSpace = fits.lastIndexOf(' ');
                if (lastSpace > 0) {
                    end = pos + lastSpace + 1;
                }
            }
            if (end <= pos) end = pos + 1; // guarantee forward progress on very-wide chars
            ranges.add(new int[]{pos, Math.min(end, text.length())});
            pos = end;
        }
        return ranges;
    }
}
