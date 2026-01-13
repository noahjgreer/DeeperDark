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
import net.minecraft.client.gui.widget.CyclingButtonWidget;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public static interface CyclingButtonWidget.UpdateCallback<T> {
    public void onValueChange(CyclingButtonWidget<T> var1, T var2);
}
