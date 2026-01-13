/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ButtonTextures
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.gui.widget.TextIconButtonWidget
 *  net.minecraft.client.gui.widget.TextIconButtonWidget$Builder
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class TextIconButtonWidget
extends ButtonWidget {
    protected final ButtonTextures texture;
    protected final int textureWidth;
    protected final int textureHeight;

    TextIconButtonWidget(int width, int height, Text message, int textureWidth, int textureHeight, ButtonTextures textures, ButtonWidget.PressAction onPress, @Nullable Text tooltip, // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ButtonWidget.NarrationSupplier narrationSupplier) {
        super(0, 0, width, height, message, onPress, narrationSupplier == null ? DEFAULT_NARRATION_SUPPLIER : narrationSupplier);
        if (tooltip != null) {
            this.setTooltip(Tooltip.of((Text)tooltip));
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
}

