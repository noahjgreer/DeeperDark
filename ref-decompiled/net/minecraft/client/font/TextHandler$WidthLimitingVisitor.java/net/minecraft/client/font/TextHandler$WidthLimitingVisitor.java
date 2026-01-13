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
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;

@Environment(value=EnvType.CLIENT)
class TextHandler.WidthLimitingVisitor
implements CharacterVisitor {
    private float widthLeft;
    private int length;

    public TextHandler.WidthLimitingVisitor(float maxWidth) {
        this.widthLeft = maxWidth;
    }

    @Override
    public boolean accept(int i, Style style, int j) {
        this.widthLeft -= TextHandler.this.widthRetriever.getWidth(j, style);
        if (this.widthLeft >= 0.0f) {
            this.length = i + Character.charCount(j);
            return true;
        }
        return false;
    }

    public int getLength() {
        return this.length;
    }

    public void resetLength() {
        this.length = 0;
    }
}
