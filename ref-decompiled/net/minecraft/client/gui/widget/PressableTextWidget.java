/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.gui.widget.PressableTextWidget
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.text.Texts
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PressableTextWidget
extends ButtonWidget {
    private final TextRenderer textRenderer;
    private final Text text;
    private final Text hoverText;

    public PressableTextWidget(int x, int y, int width, int height, Text text, ButtonWidget.PressAction onPress, TextRenderer textRenderer) {
        super(x, y, width, height, text, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.textRenderer = textRenderer;
        this.text = text;
        this.hoverText = Texts.withStyle((Text)text, (Style)Style.EMPTY.withUnderline(Boolean.valueOf(true)));
    }

    public void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        Text text = this.isSelected() ? this.hoverText : this.text;
        context.drawTextWithShadow(this.textRenderer, text, this.getX(), this.getY(), 0xFFFFFF | MathHelper.ceil((float)(this.alpha * 255.0f)) << 24);
    }
}

