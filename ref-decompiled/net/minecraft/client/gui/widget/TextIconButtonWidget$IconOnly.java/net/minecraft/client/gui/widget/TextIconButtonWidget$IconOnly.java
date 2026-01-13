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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class TextIconButtonWidget.IconOnly
extends TextIconButtonWidget {
    protected TextIconButtonWidget.IconOnly(int i, int j, Text text, int k, int l, ButtonTextures buttonTextures, ButtonWidget.PressAction pressAction, @Nullable Text text2,  @Nullable ButtonWidget.NarrationSupplier narrationSupplier) {
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
