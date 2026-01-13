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
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

@Environment(value=EnvType.CLIENT)
public static abstract class ClickableWidget.InactivityIndicatingWidget
extends ClickableWidget {
    private Text inactiveMessage;

    public static Text makeInactive(Text text) {
        return Texts.withStyle(text, Style.EMPTY.withColor(-6250336));
    }

    public ClickableWidget.InactivityIndicatingWidget(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
        this.inactiveMessage = ClickableWidget.InactivityIndicatingWidget.makeInactive(text);
    }

    @Override
    public Text getMessage() {
        return this.active ? super.getMessage() : this.inactiveMessage;
    }

    @Override
    public void setMessage(Text message) {
        super.setMessage(message);
        this.inactiveMessage = ClickableWidget.InactivityIndicatingWidget.makeInactive(message);
    }
}
