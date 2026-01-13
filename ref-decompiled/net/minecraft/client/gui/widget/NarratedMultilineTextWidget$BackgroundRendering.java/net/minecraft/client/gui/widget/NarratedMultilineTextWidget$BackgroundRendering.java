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
public static final class NarratedMultilineTextWidget.BackgroundRendering
extends Enum<NarratedMultilineTextWidget.BackgroundRendering> {
    public static final /* enum */ NarratedMultilineTextWidget.BackgroundRendering ALWAYS = new NarratedMultilineTextWidget.BackgroundRendering();
    public static final /* enum */ NarratedMultilineTextWidget.BackgroundRendering ON_FOCUS = new NarratedMultilineTextWidget.BackgroundRendering();
    public static final /* enum */ NarratedMultilineTextWidget.BackgroundRendering NEVER = new NarratedMultilineTextWidget.BackgroundRendering();
    private static final /* synthetic */ NarratedMultilineTextWidget.BackgroundRendering[] field_62120;

    public static NarratedMultilineTextWidget.BackgroundRendering[] values() {
        return (NarratedMultilineTextWidget.BackgroundRendering[])field_62120.clone();
    }

    public static NarratedMultilineTextWidget.BackgroundRendering valueOf(String string) {
        return Enum.valueOf(NarratedMultilineTextWidget.BackgroundRendering.class, string);
    }

    private static /* synthetic */ NarratedMultilineTextWidget.BackgroundRendering[] method_73391() {
        return new NarratedMultilineTextWidget.BackgroundRendering[]{ALWAYS, ON_FOCUS, NEVER};
    }

    static {
        field_62120 = NarratedMultilineTextWidget.BackgroundRendering.method_73391();
    }
}
