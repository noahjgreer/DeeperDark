package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.noahsarch.deeperdark.client.chat.ChatInputLayoutState;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {

    private static final int MAX_CHAT_LENGTH = 32767;

    @Shadow protected EditBox input;

    @Inject(method = "init", at = @At("RETURN"))
    private void deeperdark$expandChatLimit(CallbackInfo ci) {
        if (this.input != null) {
            this.input.setMaxLength(MAX_CHAT_LENGTH);
        }
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void deeperdark$resetLayout(CallbackInfo ci) {
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

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void deeperdark$renderWrappedPreview(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        Screen screen = (Screen)(Object)this;
        Minecraft mc = Minecraft.getInstance();
        if (this.input == null || mc.font == null) return;

        String text = this.input.getValue();
        if (text.isEmpty()) {
            ChatInputLayoutState.reset();
            return;
        }

        int maxWidth = screen.width - 8;
        List<FormattedCharSequence> lines = mc.font.split(FormattedText.of("> " + text), maxWidth);

        if (lines.size() <= 1) {
            ChatInputLayoutState.reset();
            return;
        }

        // Lines 0..N-2 are rendered above the EditBox; line N-1 stays in the EditBox.
        int lineH = mc.font.lineHeight + 1;
        int extraLines = lines.size() - 1;
        int extraHeight = extraLines * lineH;

        int editBoxY = screen.height - 14;
        int topY = editBoxY - extraHeight;

        // Background covering the extra lines area
        graphics.fill(0, topY - 2, screen.width, editBoxY, Integer.MIN_VALUE);

        // Draw previous wrapped lines (fully opaque white — 0xFFFFFFFF)
        for (int i = 0; i < extraLines; i++) {
            graphics.text(mc.font, lines.get(i), 4, topY + i * lineH, 0xFFFFFFFF);
        }

        ChatInputLayoutState.setChatOffset(extraHeight);
    }
}
