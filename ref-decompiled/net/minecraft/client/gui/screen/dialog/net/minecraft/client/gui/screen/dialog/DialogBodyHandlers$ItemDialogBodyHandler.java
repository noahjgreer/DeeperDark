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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.dialog.DialogBodyHandler;
import net.minecraft.client.gui.screen.dialog.DialogBodyHandlers;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ItemStackWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.body.ItemDialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.screen.ScreenTexts;

@Environment(value=EnvType.CLIENT)
static class DialogBodyHandlers.ItemDialogBodyHandler
implements DialogBodyHandler<ItemDialogBody> {
    DialogBodyHandlers.ItemDialogBodyHandler() {
    }

    @Override
    public Widget createWidget(DialogScreen<?> dialogScreen, ItemDialogBody itemDialogBody) {
        if (itemDialogBody.description().isPresent()) {
            PlainMessageDialogBody plainMessageDialogBody = itemDialogBody.description().get();
            DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(2);
            directionalLayoutWidget.getMainPositioner().alignVerticalCenter();
            ItemStackWidget itemStackWidget = new ItemStackWidget(MinecraftClient.getInstance(), 0, 0, itemDialogBody.width(), itemDialogBody.height(), ScreenTexts.EMPTY, itemDialogBody.item(), itemDialogBody.showDecorations(), itemDialogBody.showTooltip());
            directionalLayoutWidget.add(itemStackWidget);
            directionalLayoutWidget.add(NarratedMultilineTextWidget.builder(plainMessageDialogBody.contents(), dialogScreen.getTextRenderer()).width(plainMessageDialogBody.width()).alwaysShowBorders(false).backgroundRendering(NarratedMultilineTextWidget.BackgroundRendering.NEVER).build().onClick(style -> DialogBodyHandlers.runActionFromStyle(dialogScreen, style)));
            return directionalLayoutWidget;
        }
        return new ItemStackWidget(MinecraftClient.getInstance(), 0, 0, itemDialogBody.width(), itemDialogBody.height(), itemDialogBody.item().getName(), itemDialogBody.item(), itemDialogBody.showDecorations(), itemDialogBody.showTooltip());
    }
}
