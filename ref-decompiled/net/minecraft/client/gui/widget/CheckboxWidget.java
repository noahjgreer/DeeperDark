/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.DrawContext$HoverType
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.screen.narration.NarrationPart
 *  net.minecraft.client.gui.widget.CheckboxWidget
 *  net.minecraft.client.gui.widget.CheckboxWidget$Builder
 *  net.minecraft.client.gui.widget.CheckboxWidget$Callback
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.MultilineTextWidget
 *  net.minecraft.client.gui.widget.PressableWidget
 *  net.minecraft.client.input.AbstractInput
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.ColorHelper
 */
package net.minecraft.client.gui.widget;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class CheckboxWidget
extends PressableWidget {
    private static final Identifier SELECTED_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"widget/checkbox_selected_highlighted");
    private static final Identifier SELECTED_TEXTURE = Identifier.ofVanilla((String)"widget/checkbox_selected");
    private static final Identifier HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"widget/checkbox_highlighted");
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"widget/checkbox");
    private static final int field_47105 = 4;
    private static final int field_47106 = 8;
    private boolean checked;
    private final Callback callback;
    private final MultilineTextWidget textWidget;

    CheckboxWidget(int x, int y, int maxWidth, Text message, TextRenderer textRenderer, boolean checked, Callback callback) {
        super(x, y, 0, 0, message);
        this.textWidget = new MultilineTextWidget(message, textRenderer);
        this.textWidget.setMaxRows(2);
        this.width = this.setMaxWidth(maxWidth, textRenderer);
        this.height = this.calculateHeight(textRenderer);
        this.checked = checked;
        this.callback = callback;
    }

    public int setMaxWidth(int max, TextRenderer textRenderer) {
        this.width = this.calculateWidth(max, this.getMessage(), textRenderer);
        this.textWidget.setMaxWidth(this.width);
        return this.width;
    }

    private int calculateWidth(int max, Text text, TextRenderer textRenderer) {
        return Math.min(CheckboxWidget.calculateWidth((Text)text, (TextRenderer)textRenderer), max);
    }

    private int calculateHeight(TextRenderer textRenderer) {
        return Math.max(CheckboxWidget.getCheckboxSize((TextRenderer)textRenderer), this.textWidget.getHeight());
    }

    static int calculateWidth(Text text, TextRenderer textRenderer) {
        return CheckboxWidget.getCheckboxSize((TextRenderer)textRenderer) + 4 + textRenderer.getWidth((StringVisitable)text);
    }

    public static Builder builder(Text text, TextRenderer textRenderer) {
        return new Builder(text, textRenderer);
    }

    public static int getCheckboxSize(TextRenderer textRenderer) {
        Objects.requireNonNull(textRenderer);
        return 9 + 8;
    }

    public void onPress(AbstractInput input) {
        this.checked = !this.checked;
        this.callback.onValueChange(this, this.checked);
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, (Text)this.getNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                builder.put(NarrationPart.USAGE, (Text)Text.translatable((String)(this.checked ? "narration.checkbox.usage.focused.uncheck" : "narration.checkbox.usage.focused.check")));
            } else {
                builder.put(NarrationPart.USAGE, (Text)Text.translatable((String)(this.checked ? "narration.checkbox.usage.hovered.uncheck" : "narration.checkbox.usage.hovered.check")));
            }
        }
    }

    public void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        Identifier identifier = this.checked ? (this.isFocused() ? SELECTED_HIGHLIGHTED_TEXTURE : SELECTED_TEXTURE) : (this.isFocused() ? HIGHLIGHTED_TEXTURE : TEXTURE);
        int i = CheckboxWidget.getCheckboxSize((TextRenderer)textRenderer);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), i, i, ColorHelper.getWhite((float)this.alpha));
        int j = this.getX() + i + 4;
        int k = this.getY() + i / 2 - this.textWidget.getHeight() / 2;
        this.textWidget.setPosition(j, k);
        this.textWidget.draw(context.getHoverListener((ClickableWidget)this, DrawContext.HoverType.fromTooltip((boolean)this.isHovered())));
    }
}

