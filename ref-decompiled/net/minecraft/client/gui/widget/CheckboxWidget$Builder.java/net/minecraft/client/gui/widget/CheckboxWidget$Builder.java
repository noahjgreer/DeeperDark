/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class CheckboxWidget.Builder {
    private final Text message;
    private final TextRenderer textRenderer;
    private int maxWidth;
    private int x = 0;
    private int y = 0;
    private CheckboxWidget.Callback callback = CheckboxWidget.Callback.EMPTY;
    private boolean checked = false;
    private @Nullable SimpleOption<Boolean> option = null;
    private @Nullable Tooltip tooltip = null;

    CheckboxWidget.Builder(Text message, TextRenderer textRenderer) {
        this.message = message;
        this.textRenderer = textRenderer;
        this.maxWidth = CheckboxWidget.calculateWidth(message, textRenderer);
    }

    public CheckboxWidget.Builder pos(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public CheckboxWidget.Builder callback(CheckboxWidget.Callback callback) {
        this.callback = callback;
        return this;
    }

    public CheckboxWidget.Builder checked(boolean checked) {
        this.checked = checked;
        this.option = null;
        return this;
    }

    public CheckboxWidget.Builder option(SimpleOption<Boolean> option) {
        this.option = option;
        this.checked = option.getValue();
        return this;
    }

    public CheckboxWidget.Builder tooltip(Tooltip tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public CheckboxWidget.Builder maxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public CheckboxWidget build() {
        CheckboxWidget.Callback callback = this.option == null ? this.callback : (checkbox, checked) -> {
            this.option.setValue(checked);
            this.callback.onValueChange(checkbox, checked);
        };
        CheckboxWidget checkboxWidget = new CheckboxWidget(this.x, this.y, this.maxWidth, this.message, this.textRenderer, this.checked, callback);
        checkboxWidget.setTooltip(this.tooltip);
        return checkboxWidget;
    }
}
