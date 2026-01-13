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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class TextIconButtonWidget.WithText
extends TextIconButtonWidget {
    protected TextIconButtonWidget.WithText(int i, int j, Text text, int k, int l, ButtonTextures buttonTextures, ButtonWidget.PressAction pressAction, @Nullable Text text2,  @Nullable ButtonWidget.NarrationSupplier narrationSupplier) {
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
