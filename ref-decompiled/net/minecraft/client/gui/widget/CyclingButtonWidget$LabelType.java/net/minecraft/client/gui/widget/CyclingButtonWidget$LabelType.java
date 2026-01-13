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
public static final class CyclingButtonWidget.LabelType
extends Enum<CyclingButtonWidget.LabelType> {
    public static final /* enum */ CyclingButtonWidget.LabelType NAME_AND_VALUE = new CyclingButtonWidget.LabelType();
    public static final /* enum */ CyclingButtonWidget.LabelType VALUE = new CyclingButtonWidget.LabelType();
    public static final /* enum */ CyclingButtonWidget.LabelType HIDE = new CyclingButtonWidget.LabelType();
    private static final /* synthetic */ CyclingButtonWidget.LabelType[] field_64542;

    public static CyclingButtonWidget.LabelType[] values() {
        return (CyclingButtonWidget.LabelType[])field_64542.clone();
    }

    public static CyclingButtonWidget.LabelType valueOf(String string) {
        return Enum.valueOf(CyclingButtonWidget.LabelType.class, string);
    }

    private static /* synthetic */ CyclingButtonWidget.LabelType[] method_76617() {
        return new CyclingButtonWidget.LabelType[]{NAME_AND_VALUE, VALUE, HIDE};
    }

    static {
        field_64542 = CyclingButtonWidget.LabelType.method_76617();
    }
}
