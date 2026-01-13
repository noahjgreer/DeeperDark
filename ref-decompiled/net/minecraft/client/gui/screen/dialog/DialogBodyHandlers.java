/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.dialog.DialogBodyHandler
 *  net.minecraft.client.gui.screen.dialog.DialogBodyHandlers
 *  net.minecraft.client.gui.screen.dialog.DialogBodyHandlers$ItemDialogBodyHandler
 *  net.minecraft.client.gui.screen.dialog.DialogBodyHandlers$PlainMessageDialogBodyHandler
 *  net.minecraft.client.gui.screen.dialog.DialogScreen
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.dialog.body.DialogBody
 *  net.minecraft.dialog.body.ItemDialogBody
 *  net.minecraft.dialog.body.PlainMessageDialogBody
 *  net.minecraft.text.ClickEvent
 *  net.minecraft.text.Style
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.dialog;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.dialog.DialogBodyHandler;
import net.minecraft.client.gui.screen.dialog.DialogBodyHandlers;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.body.ItemDialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class DialogBodyHandlers {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<MapCodec<? extends DialogBody>, DialogBodyHandler<?>> DIALOG_BODY_HANDLERS = new HashMap();

    private static <B extends DialogBody> void register(MapCodec<B> dialogBodyCodec, DialogBodyHandler<? super B> dialogBodyHandler) {
        DIALOG_BODY_HANDLERS.put(dialogBodyCodec, dialogBodyHandler);
    }

    private static <B extends DialogBody> @Nullable DialogBodyHandler<B> getHandler(B dialogBody) {
        return (DialogBodyHandler)DIALOG_BODY_HANDLERS.get(dialogBody.getTypeCodec());
    }

    public static <B extends DialogBody> @Nullable Widget createWidget(DialogScreen<?> dialogScreen, B dialogBody) {
        DialogBodyHandler dialogBodyHandler = DialogBodyHandlers.getHandler(dialogBody);
        if (dialogBodyHandler == null) {
            LOGGER.warn("Unrecognized dialog body {}", dialogBody);
            return null;
        }
        return dialogBodyHandler.createWidget(dialogScreen, dialogBody);
    }

    public static void bootstrap() {
        DialogBodyHandlers.register((MapCodec)PlainMessageDialogBody.CODEC, (DialogBodyHandler)new PlainMessageDialogBodyHandler());
        DialogBodyHandlers.register((MapCodec)ItemDialogBody.CODEC, (DialogBodyHandler)new ItemDialogBodyHandler());
    }

    static void runActionFromStyle(DialogScreen<?> dialogScreen, @Nullable Style style) {
        ClickEvent clickEvent;
        if (style != null && (clickEvent = style.getClickEvent()) != null) {
            dialogScreen.runAction(Optional.of(clickEvent));
        }
    }
}

