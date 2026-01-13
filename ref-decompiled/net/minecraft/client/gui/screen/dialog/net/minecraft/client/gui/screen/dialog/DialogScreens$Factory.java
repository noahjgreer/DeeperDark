/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.dialog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.dialog.type.Dialog;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public static interface DialogScreens.Factory<T extends Dialog> {
    public DialogScreen<T> create(@Nullable Screen var1, T var2, DialogNetworkAccess var3);
}
