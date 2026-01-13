/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
static class RealmsMainScreen.CrossButton
extends TexturedButtonWidget {
    private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("widget/cross_button"), Identifier.ofVanilla("widget/cross_button_highlighted"));

    protected RealmsMainScreen.CrossButton(ButtonWidget.PressAction onPress, Text tooltip) {
        super(0, 0, 14, 14, TEXTURES, onPress);
        this.setTooltip(Tooltip.of(tooltip));
    }
}
