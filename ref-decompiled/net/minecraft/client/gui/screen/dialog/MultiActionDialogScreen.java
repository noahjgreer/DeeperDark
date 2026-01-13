/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.dialog.ColumnsDialogScreen
 *  net.minecraft.client.gui.screen.dialog.DialogNetworkAccess
 *  net.minecraft.client.gui.screen.dialog.MultiActionDialogScreen
 *  net.minecraft.dialog.DialogActionButtonData
 *  net.minecraft.dialog.type.ColumnsDialog
 *  net.minecraft.dialog.type.MultiActionDialog
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.dialog;

import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.ColumnsDialogScreen;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.type.ColumnsDialog;
import net.minecraft.dialog.type.MultiActionDialog;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MultiActionDialogScreen
extends ColumnsDialogScreen<MultiActionDialog> {
    public MultiActionDialogScreen(@Nullable Screen parent, MultiActionDialog dialog, DialogNetworkAccess networkAccess) {
        super(parent, (ColumnsDialog)dialog, networkAccess);
    }

    protected Stream<DialogActionButtonData> streamActionButtonData(MultiActionDialog multiActionDialog, DialogNetworkAccess dialogNetworkAccess) {
        return multiActionDialog.actions().stream();
    }
}

