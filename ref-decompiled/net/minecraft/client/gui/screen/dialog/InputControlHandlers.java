/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.dialog.InputControlHandler
 *  net.minecraft.client.gui.screen.dialog.InputControlHandler$Output
 *  net.minecraft.client.gui.screen.dialog.InputControlHandlers
 *  net.minecraft.client.gui.screen.dialog.InputControlHandlers$BooleanInputControlHandler
 *  net.minecraft.client.gui.screen.dialog.InputControlHandlers$NumberRangeInputControlHandler
 *  net.minecraft.client.gui.screen.dialog.InputControlHandlers$SimpleOptionInputControlHandler
 *  net.minecraft.client.gui.screen.dialog.InputControlHandlers$TextInputControlHandler
 *  net.minecraft.dialog.input.BooleanInputControl
 *  net.minecraft.dialog.input.InputControl
 *  net.minecraft.dialog.input.NumberRangeInputControl
 *  net.minecraft.dialog.input.SingleOptionInputControl
 *  net.minecraft.dialog.input.TextInputControl
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.dialog;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.InputControlHandler;
import net.minecraft.client.gui.screen.dialog.InputControlHandlers;
import net.minecraft.dialog.input.BooleanInputControl;
import net.minecraft.dialog.input.InputControl;
import net.minecraft.dialog.input.NumberRangeInputControl;
import net.minecraft.dialog.input.SingleOptionInputControl;
import net.minecraft.dialog.input.TextInputControl;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class InputControlHandlers {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<MapCodec<? extends InputControl>, InputControlHandler<?>> INPUT_CONTROL_HANDLERS = new HashMap();

    private static <T extends InputControl> void register(MapCodec<T> inputControlCodec, InputControlHandler<? super T> inputControlHandler) {
        INPUT_CONTROL_HANDLERS.put(inputControlCodec, inputControlHandler);
    }

    private static <T extends InputControl> @Nullable InputControlHandler<T> getHandler(T inputControl) {
        return (InputControlHandler)INPUT_CONTROL_HANDLERS.get(inputControl.getCodec());
    }

    public static <T extends InputControl> void addControl(T inputControl, Screen screen, InputControlHandler.Output output) {
        InputControlHandler inputControlHandler = InputControlHandlers.getHandler(inputControl);
        if (inputControlHandler == null) {
            LOGGER.warn("Unrecognized input control {}", inputControl);
            return;
        }
        inputControlHandler.addControl(inputControl, screen, output);
    }

    public static void bootstrap() {
        InputControlHandlers.register((MapCodec)TextInputControl.CODEC, (InputControlHandler)new TextInputControlHandler());
        InputControlHandlers.register((MapCodec)SingleOptionInputControl.CODEC, (InputControlHandler)new SimpleOptionInputControlHandler());
        InputControlHandlers.register((MapCodec)BooleanInputControl.CODEC, (InputControlHandler)new BooleanInputControlHandler());
        InputControlHandlers.register((MapCodec)NumberRangeInputControl.CODEC, (InputControlHandler)new NumberRangeInputControlHandler());
    }
}

