/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.dialog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class DialogScreen.WarningScreen
extends ConfirmScreen {
    private final MutableObject<@Nullable Screen> dialogScreen;

    public static Screen create(MinecraftClient client, DialogNetworkAccess dialogNetworkAccess, Screen dialogScreen) {
        return new DialogScreen.WarningScreen(client, dialogNetworkAccess, (MutableObject<Screen>)new MutableObject((Object)dialogScreen));
    }

    private DialogScreen.WarningScreen(MinecraftClient client, DialogNetworkAccess dialogNetworkAccess, MutableObject<Screen> dialogScreen) {
        super(disconnect -> {
            if (disconnect) {
                dialogNetworkAccess.disconnect(CUSTOM_SCREEN_REJECTED_DISCONNECT_TEXT);
            } else {
                client.setScreen((Screen)dialogScreen.get());
            }
        }, Text.translatable("menu.custom_screen_info.title"), Text.translatable("menu.custom_screen_info.contents"), ScreenTexts.returnToMenuOrDisconnect(client.isInSingleplayer()), ScreenTexts.BACK);
        this.dialogScreen = dialogScreen;
    }

    public @Nullable Screen getDialogScreen() {
        return (Screen)this.dialogScreen.get();
    }

    public void setDialogScreen(@Nullable Screen screen) {
        this.dialogScreen.setValue((Object)screen);
    }
}
