/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.EditBox
 *  net.minecraft.client.gui.EditBox$Substring
 *  net.minecraft.client.gui.cursor.StandardCursors
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.screen.narration.NarrationPart
 *  net.minecraft.client.gui.widget.EditBoxWidget
 *  net.minecraft.client.gui.widget.EditBoxWidget$Builder
 *  net.minecraft.client.gui.widget.ScrollableTextFieldWidget
 *  net.minecraft.client.input.CharInput
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.ColorHelper
 */
package net.minecraft.client.gui.widget;

import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.ScrollableTextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;

@Environment(value=EnvType.CLIENT)
public class EditBoxWidget
extends ScrollableTextFieldWidget {
    private static final int CURSOR_PADDING = 1;
    private static final int CURSOR_COLOR = -3092272;
    private static final String UNDERSCORE = "_";
    private static final int UNFOCUSED_BOX_TEXT_COLOR = ColorHelper.withAlpha((int)204, (int)-2039584);
    private static final int CURSOR_BLINK_INTERVAL = 300;
    private final TextRenderer textRenderer;
    private final Text placeholder;
    private final EditBox editBox;
    private final int textColor;
    private final boolean textShadow;
    private final int cursorColor;
    private long lastSwitchFocusTime = Util.getMeasuringTimeMs();

    EditBoxWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text placeholder, Text message, int textColor, boolean textShadow, int cursorColor, boolean hasBackground, boolean hasOverlay) {
        super(x, y, width, height, message, hasBackground, hasOverlay);
        this.textRenderer = textRenderer;
        this.textShadow = textShadow;
        this.textColor = textColor;
        this.cursorColor = cursorColor;
        this.placeholder = placeholder;
        this.editBox = new EditBox(textRenderer, width - this.getPadding());
        this.editBox.setCursorChangeListener(() -> this.onCursorChange());
    }

    public void setMaxLength(int maxLength) {
        this.editBox.setMaxLength(maxLength);
    }

    public void setMaxLines(int maxLines) {
        this.editBox.setMaxLines(maxLines);
    }

    public void setChangeListener(Consumer<String> changeListener) {
        this.editBox.setChangeListener(changeListener);
    }

    public void setText(String text) {
        this.setText(text, false);
    }

    public void setText(String text, boolean allowOverflow) {
        this.editBox.setText(text, allowOverflow);
    }

    public String getText() {
        return this.editBox.getText();
    }

    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, (Text)Text.translatable((String)"gui.narrate.editBox", (Object[])new Object[]{this.getMessage(), this.getText()}));
    }

    public void onClick(Click click, boolean doubled) {
        if (doubled) {
            this.editBox.selectWord();
        } else {
            this.editBox.setSelecting(click.hasShift());
            this.moveCursor(click.x(), click.y());
        }
    }

    protected void onDrag(Click click, double offsetX, double offsetY) {
        this.editBox.setSelecting(true);
        this.moveCursor(click.x(), click.y());
        this.editBox.setSelecting(click.hasShift());
    }

    public boolean keyPressed(KeyInput input) {
        return this.editBox.handleSpecialKey(input);
    }

    public boolean charTyped(CharInput input) {
        if (!(this.visible && this.isFocused() && input.isValidChar())) {
            return false;
        }
        this.editBox.replaceSelection(input.asString());
        return true;
    }

    protected void renderContents(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        String string = this.editBox.getText();
        if (string.isEmpty() && !this.isFocused()) {
            context.drawWrappedTextWithShadow(this.textRenderer, (StringVisitable)this.placeholder, this.getTextX(), this.getTextY(), this.width - this.getPadding(), UNFOCUSED_BOX_TEXT_COLOR);
            return;
        }
        int i = this.editBox.getCursor();
        boolean bl = this.isFocused() && (Util.getMeasuringTimeMs() - this.lastSwitchFocusTime) / 300L % 2L == 0L;
        boolean bl2 = i < string.length();
        int j = 0;
        int k = 0;
        int l = this.getTextY();
        boolean bl3 = false;
        for (EditBox.Substring substring : this.editBox.getLines()) {
            Objects.requireNonNull(this.textRenderer);
            boolean bl4 = this.isVisible(l, l + 9);
            int m = this.getTextX();
            if (bl && bl2 && i >= substring.beginIndex() && i <= substring.endIndex()) {
                if (bl4) {
                    string2 = string.substring(substring.beginIndex(), i);
                    context.drawText(this.textRenderer, string2, m, l, this.textColor, this.textShadow);
                    j = m + this.textRenderer.getWidth(string2);
                    if (!bl3) {
                        Objects.requireNonNull(this.textRenderer);
                        context.fill(j, l - 1, j + 1, l + 1 + 9, this.cursorColor);
                        bl3 = true;
                    }
                    context.drawText(this.textRenderer, string.substring(i, substring.endIndex()), j, l, this.textColor, this.textShadow);
                }
            } else {
                if (bl4) {
                    string2 = string.substring(substring.beginIndex(), substring.endIndex());
                    context.drawText(this.textRenderer, string2, m, l, this.textColor, this.textShadow);
                    j = m + this.textRenderer.getWidth(string2) - 1;
                }
                k = l;
            }
            Objects.requireNonNull(this.textRenderer);
            l += 9;
        }
        if (bl && !bl2) {
            Objects.requireNonNull(this.textRenderer);
            if (this.isVisible(k, k + 9)) {
                context.drawText(this.textRenderer, UNDERSCORE, j + 1, k, this.cursorColor, this.textShadow);
            }
        }
        if (this.editBox.hasSelection()) {
            EditBox.Substring substring2 = this.editBox.getSelection();
            int n = this.getTextX();
            l = this.getTextY();
            for (EditBox.Substring substring3 : this.editBox.getLines()) {
                if (substring2.beginIndex() > substring3.endIndex()) {
                    Objects.requireNonNull(this.textRenderer);
                    l += 9;
                    continue;
                }
                if (substring3.beginIndex() > substring2.endIndex()) break;
                Objects.requireNonNull(this.textRenderer);
                if (this.isVisible(l, l + 9)) {
                    int o = this.textRenderer.getWidth(string.substring(substring3.beginIndex(), Math.max(substring2.beginIndex(), substring3.beginIndex())));
                    int p = substring2.endIndex() > substring3.endIndex() ? this.width - this.getTextMargin() : this.textRenderer.getWidth(string.substring(substring3.beginIndex(), substring2.endIndex()));
                    Objects.requireNonNull(this.textRenderer);
                    context.drawSelection(n + o, l, n + p, l + 9, true);
                }
                Objects.requireNonNull(this.textRenderer);
                l += 9;
            }
        }
        if (this.isHovered()) {
            context.setCursor(StandardCursors.IBEAM);
        }
    }

    protected void renderOverlay(DrawContext context) {
        super.renderOverlay(context);
        if (this.editBox.hasMaxLength()) {
            int i = this.editBox.getMaxLength();
            MutableText text = Text.translatable((String)"gui.multiLineEditBox.character_limit", (Object[])new Object[]{this.editBox.getText().length(), i});
            context.drawTextWithShadow(this.textRenderer, (Text)text, this.getX() + this.width - this.textRenderer.getWidth((StringVisitable)text), this.getY() + this.height + 4, -6250336);
        }
    }

    public int getContentsHeight() {
        Objects.requireNonNull(this.textRenderer);
        return 9 * this.editBox.getLineCount();
    }

    protected double getDeltaYPerScroll() {
        Objects.requireNonNull(this.textRenderer);
        return 9.0 / 2.0;
    }

    private void onCursorChange() {
        double d = this.getScrollY();
        Objects.requireNonNull(this.textRenderer);
        EditBox.Substring substring = this.editBox.getLine((int)(d / 9.0));
        if (this.editBox.getCursor() <= substring.beginIndex()) {
            int n = this.editBox.getCurrentLineIndex();
            Objects.requireNonNull(this.textRenderer);
            d = n * 9;
        } else {
            double d2 = d + (double)this.height;
            Objects.requireNonNull(this.textRenderer);
            EditBox.Substring substring2 = this.editBox.getLine((int)(d2 / 9.0) - 1);
            if (this.editBox.getCursor() > substring2.endIndex()) {
                int n = this.editBox.getCurrentLineIndex();
                Objects.requireNonNull(this.textRenderer);
                int n2 = n * 9 - this.height;
                Objects.requireNonNull(this.textRenderer);
                d = n2 + 9 + this.getPadding();
            }
        }
        this.setScrollY(d);
    }

    private void moveCursor(double mouseX, double mouseY) {
        double d = mouseX - (double)this.getX() - (double)this.getTextMargin();
        double e = mouseY - (double)this.getY() - (double)this.getTextMargin() + this.getScrollY();
        this.editBox.moveCursor(d, e);
    }

    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (focused) {
            this.lastSwitchFocusTime = Util.getMeasuringTimeMs();
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}

