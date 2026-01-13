/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.dialog.ColumnsDialogScreen
 *  net.minecraft.client.gui.screen.dialog.DialogControls
 *  net.minecraft.client.gui.screen.dialog.DialogNetworkAccess
 *  net.minecraft.client.gui.screen.dialog.DialogScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.dialog.DialogActionButtonData
 *  net.minecraft.dialog.type.ColumnsDialog
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.dialog;

import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.DialogControls;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.type.ColumnsDialog;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public abstract class ColumnsDialogScreen<T extends ColumnsDialog>
extends DialogScreen<T> {
    public static final int field_61004 = 5;

    public ColumnsDialogScreen(@Nullable Screen parent, T dialog, DialogNetworkAccess networkAccess) {
        super(parent, dialog, networkAccess);
    }

    protected void initBody(DirectionalLayoutWidget directionalLayoutWidget, DialogControls dialogControls, T columnsDialog, DialogNetworkAccess dialogNetworkAccess) {
        super.initBody(directionalLayoutWidget, dialogControls, columnsDialog, dialogNetworkAccess);
        List<ButtonWidget> list = this.streamActionButtonData(columnsDialog, dialogNetworkAccess).map(actionButtonData -> dialogControls.createButton(actionButtonData).build()).toList();
        directionalLayoutWidget.add(ColumnsDialogScreen.createGridWidget(list, (int)columnsDialog.columns()));
    }

    protected abstract Stream<DialogActionButtonData> streamActionButtonData(T var1, DialogNetworkAccess var2);

    protected void initHeaderAndFooter(ThreePartsLayoutWidget threePartsLayoutWidget, DialogControls dialogControls, T columnsDialog, DialogNetworkAccess dialogNetworkAccess) {
        super.initHeaderAndFooter(threePartsLayoutWidget, dialogControls, columnsDialog, dialogNetworkAccess);
        columnsDialog.exitAction().ifPresentOrElse(actionButtonData -> threePartsLayoutWidget.addFooter((Widget)dialogControls.createButton(actionButtonData).build()), () -> threePartsLayoutWidget.setFooterHeight(5));
    }
}

