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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class SocialInteractionsScreen.1
extends TextFieldWidget {
    SocialInteractionsScreen.1(TextRenderer textRenderer, int i, int j, int k, int l, Text text) {
        super(textRenderer, i, j, k, l, text);
    }

    @Override
    protected MutableText getNarrationMessage() {
        if (!SocialInteractionsScreen.this.searchBox.getText().isEmpty() && SocialInteractionsScreen.this.playerList.isEmpty()) {
            return super.getNarrationMessage().append(", ").append(EMPTY_SEARCH_TEXT);
        }
        return super.getNarrationMessage();
    }
}
