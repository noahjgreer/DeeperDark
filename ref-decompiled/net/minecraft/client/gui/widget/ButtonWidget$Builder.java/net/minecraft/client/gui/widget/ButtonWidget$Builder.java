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
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class ButtonWidget.Builder {
    private final Text message;
    private final ButtonWidget.PressAction onPress;
    private @Nullable Tooltip tooltip;
    private int x;
    private int y;
    private int width = 150;
    private int height = 20;
    private ButtonWidget.NarrationSupplier narrationSupplier = DEFAULT_NARRATION_SUPPLIER;

    public ButtonWidget.Builder(Text message, ButtonWidget.PressAction onPress) {
        this.message = message;
        this.onPress = onPress;
    }

    public ButtonWidget.Builder position(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public ButtonWidget.Builder width(int width) {
        this.width = width;
        return this;
    }

    public ButtonWidget.Builder size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public ButtonWidget.Builder dimensions(int x, int y, int width, int height) {
        return this.position(x, y).size(width, height);
    }

    public ButtonWidget.Builder tooltip(@Nullable Tooltip tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public ButtonWidget.Builder narrationSupplier(ButtonWidget.NarrationSupplier narrationSupplier) {
        this.narrationSupplier = narrationSupplier;
        return this;
    }

    public ButtonWidget build() {
        ButtonWidget.Text buttonWidget = new ButtonWidget.Text(this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier);
        buttonWidget.setTooltip(this.tooltip);
        return buttonWidget;
    }
}
