/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.dialog.InputControlHandler
 *  net.minecraft.client.gui.screen.dialog.InputControlHandler$Output
 *  net.minecraft.dialog.input.InputControl
 */
package net.minecraft.client.gui.screen.dialog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.InputControlHandler;
import net.minecraft.dialog.input.InputControl;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface InputControlHandler<T extends InputControl> {
    public void addControl(T var1, Screen var2, Output var3);
}

