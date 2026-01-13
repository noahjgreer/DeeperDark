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
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class TextIconButtonWidget.Builder {
    private final Text text;
    private final ButtonWidget.PressAction onPress;
    private final boolean hideText;
    private int width = 150;
    private int height = 20;
    private @Nullable ButtonTextures texture;
    private int textureWidth;
    private int textureHeight;
    private @Nullable Text tooltip;
    private  @Nullable ButtonWidget.NarrationSupplier narrationSupplier;

    public TextIconButtonWidget.Builder(Text text, ButtonWidget.PressAction onPress, boolean hideText) {
        this.text = text;
        this.onPress = onPress;
        this.hideText = hideText;
    }

    public TextIconButtonWidget.Builder width(int width) {
        this.width = width;
        return this;
    }

    public TextIconButtonWidget.Builder dimension(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public TextIconButtonWidget.Builder texture(Identifier texture, int width, int height) {
        this.texture = new ButtonTextures(texture);
        this.textureWidth = width;
        this.textureHeight = height;
        return this;
    }

    public TextIconButtonWidget.Builder texture(ButtonTextures texture, int width, int height) {
        this.texture = texture;
        this.textureWidth = width;
        this.textureHeight = height;
        return this;
    }

    public TextIconButtonWidget.Builder useTextAsTooltip() {
        this.tooltip = this.text;
        return this;
    }

    public TextIconButtonWidget.Builder narration(ButtonWidget.NarrationSupplier narrationSupplier) {
        this.narrationSupplier = narrationSupplier;
        return this;
    }

    public TextIconButtonWidget build() {
        if (this.texture == null) {
            throw new IllegalStateException("Sprite not set");
        }
        if (this.hideText) {
            return new TextIconButtonWidget.IconOnly(this.width, this.height, this.text, this.textureWidth, this.textureHeight, this.texture, this.onPress, this.tooltip, this.narrationSupplier);
        }
        return new TextIconButtonWidget.WithText(this.width, this.height, this.text, this.textureWidth, this.textureHeight, this.texture, this.onPress, this.tooltip, this.narrationSupplier);
    }
}
