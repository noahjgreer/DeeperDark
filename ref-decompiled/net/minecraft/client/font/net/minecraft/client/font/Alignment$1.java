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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;

@Environment(value=EnvType.CLIENT)
final class Alignment.1
extends Alignment {
    @Override
    public int getAdjustedX(int x, int width) {
        return x;
    }

    @Override
    public int getAdjustedX(int x, TextRenderer textRenderer, OrderedText text) {
        return x;
    }
}
