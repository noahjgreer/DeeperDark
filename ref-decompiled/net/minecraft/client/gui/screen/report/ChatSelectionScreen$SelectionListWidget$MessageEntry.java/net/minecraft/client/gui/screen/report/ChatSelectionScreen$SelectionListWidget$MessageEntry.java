/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.report;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.report.ChatSelectionScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import net.minecraft.util.Nullables;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChatSelectionScreen.SelectionListWidget.MessageEntry
extends ChatSelectionScreen.SelectionListWidget.Entry {
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

    public ChatSelectionScreen.SelectionListWidget.MessageEntry(int index, Text message, @Nullable Text narration, MessageIndicator indicator, boolean fromReportedPlayer, boolean isChatMessage) {
        this.index = index;
        this.indicatorIcon = Nullables.map(indicator, MessageIndicator::icon);
        this.originalContent = indicator != null && indicator.text() != null ? SelectionListWidget.this.field_39592.textRenderer.wrapLines(indicator.text(), SelectionListWidget.this.getRowWidth()) : null;
        this.fromReportedPlayer = fromReportedPlayer;
        this.isChatMessage = isChatMessage;
        StringVisitable stringVisitable = SelectionListWidget.this.field_39592.textRenderer.trimToWidth(message, this.getTextWidth() - SelectionListWidget.this.field_39592.textRenderer.getWidth(ScreenTexts.ELLIPSIS));
        if (message != stringVisitable) {
            this.truncatedContent = StringVisitable.concat(stringVisitable, ScreenTexts.ELLIPSIS);
            this.fullContent = SelectionListWidget.this.field_39592.textRenderer.wrapLines(message, SelectionListWidget.this.getRowWidth());
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
        int j = this.getContentY() + 1 + (this.getContentHeight() - ((ChatSelectionScreen)SelectionListWidget.this.field_39592).textRenderer.fontHeight) / 2;
        context.drawTextWithShadow(SelectionListWidget.this.field_39592.textRenderer, Language.getInstance().reorder(this.truncatedContent), i, j, this.fromReportedPlayer ? -1 : -1593835521);
        if (this.fullContent != null && hovered) {
            context.drawTooltip(this.fullContent, mouseX, mouseY);
        }
        int k = SelectionListWidget.this.field_39592.textRenderer.getWidth(this.truncatedContent);
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
        SelectionListWidget.this.setSelected((ChatSelectionScreen.SelectionListWidget.Entry)null);
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
        return SelectionListWidget.this.field_39592.report.isMessageSelected(this.index);
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
            SelectionListWidget.this.field_39592.report.toggleMessageSelection(this.index);
            SelectionListWidget.this.field_39592.setDoneButtonActivation();
            return true;
        }
        return false;
    }
}
