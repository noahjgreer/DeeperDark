/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.multiplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class SocialInteractionsPlayerListEntry.2
extends TexturedButtonWidget {
    SocialInteractionsPlayerListEntry.2(int i, int j, int k, int l, ButtonTextures buttonTextures, ButtonWidget.PressAction pressAction, Text text) {
        super(i, j, k, l, buttonTextures, pressAction, text);
    }

    @Override
    protected MutableText getNarrationMessage() {
        return SocialInteractionsPlayerListEntry.this.getNarrationMessage(super.getNarrationMessage());
    }
}
