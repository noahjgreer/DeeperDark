/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.MultilineText;

@Environment(value=EnvType.CLIENT)
class MultilineText.1
implements MultilineText {
    MultilineText.1() {
    }

    @Override
    public int draw(Alignment alignment, int x, int y, int lineHeight, DrawnTextConsumer consumer) {
        return y;
    }

    @Override
    public int getLineCount() {
        return 0;
    }

    @Override
    public int getMaxWidth() {
        return 0;
    }
}
