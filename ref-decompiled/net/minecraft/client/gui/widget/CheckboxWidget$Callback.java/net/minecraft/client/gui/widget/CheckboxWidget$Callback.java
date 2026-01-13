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
import net.minecraft.client.gui.widget.CheckboxWidget;

@Environment(value=EnvType.CLIENT)
public static interface CheckboxWidget.Callback {
    public static final CheckboxWidget.Callback EMPTY = (checkbox, checked) -> {};

    public void onValueChange(CheckboxWidget var1, boolean var2);
}
