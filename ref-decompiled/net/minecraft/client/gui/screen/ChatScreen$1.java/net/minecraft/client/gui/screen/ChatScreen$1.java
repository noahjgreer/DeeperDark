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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class ChatScreen.1
extends TextFieldWidget {
    ChatScreen.1(TextRenderer textRenderer, int i, int j, int k, int l, Text text) {
        super(textRenderer, i, j, k, l, text);
    }

    @Override
    protected MutableText getNarrationMessage() {
        return super.getNarrationMessage().append(ChatScreen.this.chatInputSuggestor.getNarration());
    }
}
