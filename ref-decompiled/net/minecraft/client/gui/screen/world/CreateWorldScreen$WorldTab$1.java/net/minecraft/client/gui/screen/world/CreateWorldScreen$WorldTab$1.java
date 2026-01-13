/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class CreateWorldScreen.WorldTab.1
extends TextFieldWidget {
    final /* synthetic */ CreateWorldScreen field_42193;

    CreateWorldScreen.WorldTab.1(CreateWorldScreen.WorldTab worldTab, TextRenderer textRenderer, int i, int j, Text text, CreateWorldScreen createWorldScreen) {
        this.field_42193 = createWorldScreen;
        super(textRenderer, i, j, text);
    }

    @Override
    protected MutableText getNarrationMessage() {
        return super.getNarrationMessage().append(ScreenTexts.SENTENCE_SEPARATOR).append(SEED_INFO_TEXT);
    }
}
