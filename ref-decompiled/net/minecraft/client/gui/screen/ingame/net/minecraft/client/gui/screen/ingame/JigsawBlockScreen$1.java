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
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
class JigsawBlockScreen.1
extends SliderWidget {
    JigsawBlockScreen.1(int i, int j, int k, int l, Text text, double d) {
        super(i, j, k, l, text, d);
        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Text.translatable("jigsaw_block.levels", JigsawBlockScreen.this.generationDepth));
    }

    @Override
    protected void applyValue() {
        JigsawBlockScreen.this.generationDepth = MathHelper.floor(MathHelper.clampedLerp(this.value, 0.0, 20.0));
    }
}
