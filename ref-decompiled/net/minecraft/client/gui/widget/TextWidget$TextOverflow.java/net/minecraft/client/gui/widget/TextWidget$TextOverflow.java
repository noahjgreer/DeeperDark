/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class TextWidget.TextOverflow
extends Enum<TextWidget.TextOverflow> {
    public static final /* enum */ TextWidget.TextOverflow CLAMPED = new TextWidget.TextOverflow();
    public static final /* enum */ TextWidget.TextOverflow SCROLLING = new TextWidget.TextOverflow();
    private static final /* synthetic */ TextWidget.TextOverflow[] field_62128;

    public static TextWidget.TextOverflow[] values() {
        return (TextWidget.TextOverflow[])field_62128.clone();
    }

    public static TextWidget.TextOverflow valueOf(String string) {
        return Enum.valueOf(TextWidget.TextOverflow.class, string);
    }

    private static /* synthetic */ TextWidget.TextOverflow[] method_73397() {
        return new TextWidget.TextOverflow[]{CLAMPED, SCROLLING};
    }

    static {
        field_62128 = TextWidget.TextOverflow.method_73397();
    }
}
