/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.Alignment
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.font.MultilineText
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.text.Text
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public interface MultilineText {
    public static final MultilineText EMPTY = new /* Unavailable Anonymous Inner Class!! */;

    public static MultilineText create(TextRenderer renderer, Text ... texts) {
        return MultilineText.create((TextRenderer)renderer, (int)Integer.MAX_VALUE, (int)Integer.MAX_VALUE, (Text[])texts);
    }

    public static MultilineText create(TextRenderer renderer, int maxWidth, Text ... texts) {
        return MultilineText.create((TextRenderer)renderer, (int)maxWidth, (int)Integer.MAX_VALUE, (Text[])texts);
    }

    public static MultilineText create(TextRenderer renderer, Text text, int maxWidth) {
        return MultilineText.create((TextRenderer)renderer, (int)maxWidth, (int)Integer.MAX_VALUE, (Text[])new Text[]{text});
    }

    public static MultilineText create(TextRenderer textRenderer, int maxWidth, int maxLines, Text ... texts) {
        if (texts.length == 0) {
            return EMPTY;
        }
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    public int draw(Alignment var1, int var2, int var3, int var4, DrawnTextConsumer var5);

    public int getLineCount();

    public int getMaxWidth();
}

