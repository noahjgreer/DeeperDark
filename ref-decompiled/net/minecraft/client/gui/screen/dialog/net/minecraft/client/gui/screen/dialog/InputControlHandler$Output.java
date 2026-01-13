/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.dialog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.action.DialogAction;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public static interface InputControlHandler.Output {
    public void accept(Widget var1, DialogAction.ValueGetter var2);
}
