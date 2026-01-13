/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.dialog.ColumnsDialogScreen
 *  net.minecraft.client.gui.screen.dialog.DialogListDialogScreen
 *  net.minecraft.client.gui.screen.dialog.DialogNetworkAccess
 *  net.minecraft.dialog.DialogActionButtonData
 *  net.minecraft.dialog.DialogButtonData
 *  net.minecraft.dialog.action.SimpleDialogAction
 *  net.minecraft.dialog.type.ColumnsDialog
 *  net.minecraft.dialog.type.Dialog
 *  net.minecraft.dialog.type.DialogListDialog
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.text.ClickEvent
 *  net.minecraft.text.ClickEvent$ShowDialog
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.dialog;

import java.util.Optional;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.ColumnsDialogScreen;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.dialog.type.ColumnsDialog;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.DialogListDialog;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.ClickEvent;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class DialogListDialogScreen
extends ColumnsDialogScreen<DialogListDialog> {
    public DialogListDialogScreen(@Nullable Screen parent, DialogListDialog dialog, DialogNetworkAccess networkAccess) {
        super(parent, (ColumnsDialog)dialog, networkAccess);
    }

    protected Stream<DialogActionButtonData> streamActionButtonData(DialogListDialog dialogListDialog, DialogNetworkAccess dialogNetworkAccess) {
        return dialogListDialog.dialogs().stream().map(entry -> DialogListDialogScreen.createButton((DialogListDialog)dialogListDialog, (RegistryEntry)entry));
    }

    private static DialogActionButtonData createButton(DialogListDialog dialog, RegistryEntry<Dialog> entry) {
        return new DialogActionButtonData(new DialogButtonData(((Dialog)entry.value()).common().getExternalTitle(), dialog.buttonWidth()), Optional.of(new SimpleDialogAction((ClickEvent)new ClickEvent.ShowDialog(entry))));
    }
}

