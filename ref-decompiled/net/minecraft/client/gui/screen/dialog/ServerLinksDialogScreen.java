/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.dialog.ColumnsDialogScreen
 *  net.minecraft.client.gui.screen.dialog.DialogNetworkAccess
 *  net.minecraft.client.gui.screen.dialog.ServerLinksDialogScreen
 *  net.minecraft.dialog.DialogActionButtonData
 *  net.minecraft.dialog.DialogButtonData
 *  net.minecraft.dialog.action.SimpleDialogAction
 *  net.minecraft.dialog.type.ColumnsDialog
 *  net.minecraft.dialog.type.ServerLinksDialog
 *  net.minecraft.server.ServerLinks$Entry
 *  net.minecraft.text.ClickEvent
 *  net.minecraft.text.ClickEvent$OpenUrl
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
import net.minecraft.dialog.type.ServerLinksDialog;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.ClickEvent;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ServerLinksDialogScreen
extends ColumnsDialogScreen<ServerLinksDialog> {
    public ServerLinksDialogScreen(@Nullable Screen parent, ServerLinksDialog dialog, DialogNetworkAccess networkAccess) {
        super(parent, (ColumnsDialog)dialog, networkAccess);
    }

    protected Stream<DialogActionButtonData> streamActionButtonData(ServerLinksDialog serverLinksDialog, DialogNetworkAccess dialogNetworkAccess) {
        return dialogNetworkAccess.getServerLinks().entries().stream().map(entry -> ServerLinksDialogScreen.createButton((ServerLinksDialog)serverLinksDialog, (ServerLinks.Entry)entry));
    }

    private static DialogActionButtonData createButton(ServerLinksDialog dialog, ServerLinks.Entry entry) {
        return new DialogActionButtonData(new DialogButtonData(entry.getText(), dialog.buttonWidth()), Optional.of(new SimpleDialogAction((ClickEvent)new ClickEvent.OpenUrl(entry.link()))));
    }
}

