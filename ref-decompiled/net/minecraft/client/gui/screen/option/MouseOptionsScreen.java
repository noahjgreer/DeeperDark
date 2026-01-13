/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.option.GameOptionsScreen
 *  net.minecraft.client.gui.screen.option.MouseOptionsScreen
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.option.SimpleOption
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen.option;

import java.util.Arrays;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class MouseOptionsScreen
extends GameOptionsScreen {
    private static final Text TITLE = Text.translatable((String)"options.mouse_settings.title");

    private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
        return new SimpleOption[]{gameOptions.getMouseSensitivity(), gameOptions.getTouchscreen(), gameOptions.getMouseWheelSensitivity(), gameOptions.getDiscreteMouseScroll(), gameOptions.getInvertMouseX(), gameOptions.getInvertMouseY(), gameOptions.getAllowCursorChanges()};
    }

    public MouseOptionsScreen(Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, TITLE);
    }

    protected void addOptions() {
        if (InputUtil.isRawMouseMotionSupported()) {
            this.body.addAll((SimpleOption[])Stream.concat(Arrays.stream(MouseOptionsScreen.getOptions((GameOptions)this.gameOptions)), Stream.of(this.gameOptions.getRawMouseInput())).toArray(SimpleOption[]::new));
        } else {
            this.body.addAll(MouseOptionsScreen.getOptions((GameOptions)this.gameOptions));
        }
    }
}

