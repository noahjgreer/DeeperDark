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
import net.minecraft.client.gui.screen.dialog.DialogBodyHandler;
import net.minecraft.client.gui.screen.dialog.DialogBodyHandlers;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.body.PlainMessageDialogBody;

@Environment(value=EnvType.CLIENT)
static class DialogBodyHandlers.PlainMessageDialogBodyHandler
implements DialogBodyHandler<PlainMessageDialogBody> {
    DialogBodyHandlers.PlainMessageDialogBodyHandler() {
    }

    @Override
    public Widget createWidget(DialogScreen<?> dialogScreen, PlainMessageDialogBody plainMessageDialogBody) {
        return NarratedMultilineTextWidget.builder(plainMessageDialogBody.contents(), dialogScreen.getTextRenderer()).width(plainMessageDialogBody.width()).alwaysShowBorders(false).backgroundRendering(NarratedMultilineTextWidget.BackgroundRendering.NEVER).build().setCentered(true).onClick(style -> DialogBodyHandlers.runActionFromStyle(dialogScreen, style));
    }
}
