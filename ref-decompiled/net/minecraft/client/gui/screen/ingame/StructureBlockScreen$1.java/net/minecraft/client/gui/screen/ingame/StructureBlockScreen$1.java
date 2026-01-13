/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class StructureBlockScreen.1
extends TextFieldWidget {
    StructureBlockScreen.1(TextRenderer textRenderer, int i, int j, int k, int l, Text text) {
        super(textRenderer, i, j, k, l, text);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (!StructureBlockScreen.this.isValidCharacterForName(this.getText(), input.codepoint(), this.getCursor())) {
            return false;
        }
        return super.charTyped(input);
    }
}
