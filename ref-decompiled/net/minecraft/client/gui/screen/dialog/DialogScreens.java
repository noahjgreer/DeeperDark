/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.dialog.DialogListDialogScreen
 *  net.minecraft.client.gui.screen.dialog.DialogNetworkAccess
 *  net.minecraft.client.gui.screen.dialog.DialogScreen
 *  net.minecraft.client.gui.screen.dialog.DialogScreens
 *  net.minecraft.client.gui.screen.dialog.DialogScreens$Factory
 *  net.minecraft.client.gui.screen.dialog.MultiActionDialogScreen
 *  net.minecraft.client.gui.screen.dialog.ServerLinksDialogScreen
 *  net.minecraft.client.gui.screen.dialog.SimpleDialogScreen
 *  net.minecraft.dialog.type.ConfirmationDialog
 *  net.minecraft.dialog.type.Dialog
 *  net.minecraft.dialog.type.DialogListDialog
 *  net.minecraft.dialog.type.MultiActionDialog
 *  net.minecraft.dialog.type.NoticeDialog
 *  net.minecraft.dialog.type.ServerLinksDialog
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.dialog;

import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.DialogListDialogScreen;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.screen.dialog.DialogScreens;
import net.minecraft.client.gui.screen.dialog.MultiActionDialogScreen;
import net.minecraft.client.gui.screen.dialog.ServerLinksDialogScreen;
import net.minecraft.client.gui.screen.dialog.SimpleDialogScreen;
import net.minecraft.dialog.type.ConfirmationDialog;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.DialogListDialog;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.dialog.type.NoticeDialog;
import net.minecraft.dialog.type.ServerLinksDialog;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class DialogScreens {
    private static final Map<MapCodec<? extends Dialog>, Factory<?>> DIALOG_SCREEN_FACTORIES = new HashMap();

    private static <T extends Dialog> void register(MapCodec<T> dialogCodec, Factory<? super T> factory) {
        DIALOG_SCREEN_FACTORIES.put(dialogCodec, factory);
    }

    public static <T extends Dialog> @Nullable DialogScreen<T> create(T dialog, @Nullable Screen parent, DialogNetworkAccess networkAccess) {
        Factory factory = (Factory)DIALOG_SCREEN_FACTORIES.get(dialog.getCodec());
        if (factory != null) {
            return factory.create(parent, dialog, networkAccess);
        }
        return null;
    }

    public static void bootstrap() {
        DialogScreens.register((MapCodec)ConfirmationDialog.CODEC, SimpleDialogScreen::new);
        DialogScreens.register((MapCodec)NoticeDialog.CODEC, SimpleDialogScreen::new);
        DialogScreens.register((MapCodec)DialogListDialog.CODEC, DialogListDialogScreen::new);
        DialogScreens.register((MapCodec)MultiActionDialog.CODEC, MultiActionDialogScreen::new);
        DialogScreens.register((MapCodec)ServerLinksDialog.CODEC, ServerLinksDialogScreen::new);
    }
}

