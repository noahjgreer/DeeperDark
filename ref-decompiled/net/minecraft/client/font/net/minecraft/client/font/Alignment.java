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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;

@Environment(value=EnvType.CLIENT)
public abstract sealed class Alignment
extends Enum<Alignment> {
    public static final /* enum */ Alignment LEFT = new Alignment(){

        @Override
        public int getAdjustedX(int x, int width) {
            return x;
        }

        @Override
        public int getAdjustedX(int x, TextRenderer textRenderer, OrderedText text) {
            return x;
        }
    };
    public static final /* enum */ Alignment CENTER = new Alignment(){

        @Override
        public int getAdjustedX(int x, int width) {
            return x - width / 2;
        }
    };
    public static final /* enum */ Alignment RIGHT = new Alignment(){

        @Override
        public int getAdjustedX(int x, int width) {
            return x - width;
        }
    };
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
