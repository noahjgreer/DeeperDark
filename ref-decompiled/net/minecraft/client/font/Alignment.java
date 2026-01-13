/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.Alignment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.text.OrderedText
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public abstract sealed class Alignment
extends Enum<Alignment> {
    public static final /* enum */ Alignment LEFT = new /* Unavailable Anonymous Inner Class!! */;
    public static final /* enum */ Alignment CENTER = new /* Unavailable Anonymous Inner Class!! */;
    public static final /* enum */ Alignment RIGHT = new /* Unavailable Anonymous Inner Class!! */;
    private static final /* synthetic */ Alignment[] field_62012;

    public static Alignment[] values() {
        return (Alignment[])field_62012.clone();
    }

    public static Alignment valueOf(String string) {
        return Enum.valueOf(Alignment.class, string);
    }

    public abstract int getAdjustedX(int var1, int var2);

    public int getAdjustedX(int x, TextRenderer textRenderer, OrderedText text) {
        return this.getAdjustedX(x, textRenderer.getWidth(text));
    }

    private static /* synthetic */ Alignment[] method_73213() {
        return new Alignment[]{LEFT, CENTER, RIGHT};
    }

    static {
        field_62012 = Alignment.method_73213();
    }
}

