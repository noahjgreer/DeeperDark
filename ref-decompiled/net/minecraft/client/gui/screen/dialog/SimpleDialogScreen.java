/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.dialog.DialogControls
 *  net.minecraft.client.gui.screen.dialog.DialogNetworkAccess
 *  net.minecraft.client.gui.screen.dialog.DialogScreen
 *  net.minecraft.client.gui.screen.dialog.SimpleDialogScreen
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.dialog.DialogActionButtonData
 *  net.minecraft.dialog.type.SimpleDialog
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.dialog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.DialogControls;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.type.SimpleDialog;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SimpleDialogScreen<T extends SimpleDialog>
extends DialogScreen<T> {
    public SimpleDialogScreen(@Nullable Screen parent, T dialog, DialogNetworkAccess networkAccess) {
        super(parent, dialog, networkAccess);
    }

    protected void initHeaderAndFooter(ThreePartsLayoutWidget threePartsLayoutWidget, DialogControls dialogControls, T simpleDialog, DialogNetworkAccess dialogNetworkAccess) {
        super.initHeaderAndFooter(threePartsLayoutWidget, dialogControls, simpleDialog, dialogNetworkAccess);
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(8);
        for (DialogActionButtonData dialogActionButtonData : simpleDialog.getButtons()) {
            directionalLayoutWidget.add((Widget)dialogControls.createButton(dialogActionButtonData).build());
        }
        threePartsLayoutWidget.addFooter((Widget)directionalLayoutWidget);
    }
}

