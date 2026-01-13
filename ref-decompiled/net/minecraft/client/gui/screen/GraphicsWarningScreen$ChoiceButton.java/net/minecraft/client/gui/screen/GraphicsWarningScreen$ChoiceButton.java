/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static final class GraphicsWarningScreen.ChoiceButton {
    final Text message;
    final ButtonWidget.PressAction pressAction;

    public GraphicsWarningScreen.ChoiceButton(Text message, ButtonWidget.PressAction pressAction) {
        this.message = message;
        this.pressAction = pressAction;
    }
}
