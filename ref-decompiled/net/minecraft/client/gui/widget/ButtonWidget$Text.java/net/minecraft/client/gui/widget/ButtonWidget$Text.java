/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static class ButtonWidget.Text
extends ButtonWidget {
    protected ButtonWidget.Text(int i, int j, int k, int l, Text text, ButtonWidget.PressAction pressAction, ButtonWidget.NarrationSupplier narrationSupplier) {
        super(i, j, k, l, text, pressAction, narrationSupplier);
    }

    @Override
    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.drawButton(context);
        this.drawLabel(context.getHoverListener(this, DrawContext.HoverType.NONE));
    }
}
