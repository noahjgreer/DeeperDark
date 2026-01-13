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
class TextHandler.LineBreakingVisitor
implements CharacterVisitor {
    private final float maxWidth;
    private int endIndex = -1;
    private Style endStyle = Style.EMPTY;
    private boolean nonEmpty;
    private float totalWidth;
    private int lastSpaceBreak = -1;
    private Style lastSpaceStyle = Style.EMPTY;
    private int count;
    private int startOffset;

    public TextHandler.LineBreakingVisitor(float maxWidth) {
        this.maxWidth = Math.max(maxWidth, 1.0f);
    }

    @Override
    public boolean accept(int i, Style style, int j) {
        int k = i + this.startOffset;
        switch (j) {
            case 10: {
                return this.breakLine(k, style);
            }
            case 32: {
                this.lastSpaceBreak = k;
                this.lastSpaceStyle = style;
            }
        }
        float f = TextHandler.this.widthRetriever.getWidth(j, style);
        this.totalWidth += f;
        if (this.nonEmpty && this.totalWidth > this.maxWidth) {
            if (this.lastSpaceBreak != -1) {
                return this.breakLine(this.lastSpaceBreak, this.lastSpaceStyle);
            }
            return this.breakLine(k, style);
        }
        this.nonEmpty |= f != 0.0f;
        this.count = k + Character.charCount(j);
        return true;
    }

    private boolean breakLine(int finishIndex, Style finishStyle) {
        this.endIndex = finishIndex;
        this.endStyle = finishStyle;
        return false;
    }

    private boolean hasLineBreak() {
        return this.endIndex != -1;
    }

    public int getEndingIndex() {
        return this.hasLineBreak() ? this.endIndex : this.count;
    }

    public Style getEndingStyle() {
        return this.endStyle;
    }

    public void offset(int extraOffset) {
        this.startOffset += extraOffset;
    }
}
