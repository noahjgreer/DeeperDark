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
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class TextIconButtonWidget
extends ButtonWidget {
    protected final ButtonTextures texture;
    protected final int textureWidth;
    protected final int textureHeight;

    TextIconButtonWidget(int width, int height, Text message, int textureWidth, int textureHeight, ButtonTextures textures, ButtonWidget.PressAction onPress, @Nullable Text tooltip, @Nullable ButtonWidget.NarrationSupplier narrationSupplier) {
        super(0, 0, width, height, message, onPress, narrationSupplier == null ? DEFAULT_NARRATION_SUPPLIER : narrationSupplier);
        if (tooltip != null) {
            this.setTooltip(Tooltip.of(tooltip));
        }
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.texture = textures;
    }

    protected void drawIcon(DrawContext context, int x, int y) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture.get(this.isInteractable(), this.isSelected()), x, y, this.textureWidth, this.textureHeight, this.alpha);
    }

    public static Builder builder(Text text, ButtonWidget.PressAction onPress, boolean hideLabel) {
        return new Builder(text, onPress, hideLabel);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private final Text text;
        private final ButtonWidget.PressAction onPress;
        private final boolean hideText;
        private int width = 150;
        private int height = 20;
        private @Nullable ButtonTextures texture;
        private int textureWidth;
        private int textureHeight;
        private @Nullable Text tooltip;
        private @Nullable ButtonWidget.NarrationSupplier narrationSupplier;

        public Builder(Text text, ButtonWidget.PressAction onPress, boolean hideText) {
            this.text = text;
            this.onPress = onPress;
            this.hideText = hideText;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder dimension(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder texture(Identifier texture, int width, int height) {
            this.texture = new ButtonTextures(texture);
            this.textureWidth = width;
            this.textureHeight = height;
            return this;
        }

        public Builder texture(ButtonTextures texture, int width, int height) {
            this.texture = texture;
            this.textureWidth = width;
            this.textureHeight = height;
            return this;
        }

        public Builder useTextAsTooltip() {
            this.tooltip = this.text;
            return this;
        }

        public Builder narration(ButtonWidget.NarrationSupplier narrationSupplier) {
            this.narrationSupplier = narrationSupplier;
            return this;
        }

        public TextIconButtonWidget build() {
            if (this.texture == null) {
                throw new IllegalStateException("Sprite not set");
            }
            if (this.hideText) {
                return new IconOnly(this.width, this.height, this.text, this.textureWidth, this.textureHeight, this.texture, this.onPress, this.tooltip, this.narrationSupplier);
            }
            return new WithText(this.width, this.height, this.text, this.textureWidth, this.textureHeight, this.texture, this.onPress, this.tooltip, this.narrationSupplier);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class WithText
    extends TextIconButtonWidget {
        protected WithText(int i, int j, Text text, int k, int l, ButtonTextures buttonTextures, ButtonWidget.PressAction pressAction, @Nullable Text text2, @Nullable ButtonWidget.NarrationSupplier narrationSupplier) {
            super(i, j, text, k, l, buttonTextures, pressAction, text2, narrationSupplier);
        }

        @Override
        public void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            this.drawButton(context);
            int i = this.getX() + 2;
            int j = this.getX() + this.getWidth() - this.textureWidth - 4;
            int k = this.getX() + this.getWidth() / 2;
            DrawnTextConsumer drawnTextConsumer = context.getHoverListener(this, DrawContext.HoverType.NONE);
            drawnTextConsumer.marqueedText(this.getMessage(), k, i, j, this.getY(), this.getY() + this.getHeight());
            int l = this.getX() + this.getWidth() - this.textureWidth - 2;
            int m = this.getY() + this.getHeight() / 2 - this.textureHeight / 2;
            this.drawIcon(context, l, m);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class IconOnly
    extends TextIconButtonWidget {
        protected IconOnly(int i, int j, Text text, int k, int l, ButtonTextures buttonTextures, ButtonWidget.PressAction pressAction, @Nullable Text text2, @Nullable ButtonWidget.NarrationSupplier narrationSupplier) {
            super(i, j, text, k, l, buttonTextures, pressAction, text2, narrationSupplier);
        }

        @Override
        public void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            this.drawButton(context);
            int i = this.getX() + this.getWidth() / 2 - this.textureWidth / 2;
            int j = this.getY() + this.getHeight() / 2 - this.textureHeight / 2;
            this.drawIcon(context, i, j);
        }
    }
}
