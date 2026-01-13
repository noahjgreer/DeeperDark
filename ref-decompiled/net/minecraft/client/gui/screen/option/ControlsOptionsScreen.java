/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.option.ControlsOptionsScreen
 *  net.minecraft.client.gui.screen.option.GameOptionsScreen
 *  net.minecraft.client.gui.screen.option.KeybindsScreen
 *  net.minecraft.client.gui.screen.option.MouseOptionsScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.option.SimpleOption
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.screen.option.MouseOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ControlsOptionsScreen
extends GameOptionsScreen {
    private static final Text TITLE_TEXT = Text.translatable((String)"controls.title");

    private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
        return new SimpleOption[]{gameOptions.getSneakToggled(), gameOptions.getSprintToggled(), gameOptions.getAttackToggled(), gameOptions.getUseToggled(), gameOptions.getAutoJump(), gameOptions.getSprintWindow(), gameOptions.getOperatorItemsTab()};
    }

    public ControlsOptionsScreen(Screen parent, GameOptions options) {
        super(parent, options, TITLE_TEXT);
    }

    protected void addOptions() {
        this.body.addWidgetEntry((ClickableWidget)ButtonWidget.builder((Text)Text.translatable((String)"options.mouse_settings"), button -> this.client.setScreen((Screen)new MouseOptionsScreen((Screen)this, this.gameOptions))).build(), (ClickableWidget)ButtonWidget.builder((Text)Text.translatable((String)"controls.keybinds"), button -> this.client.setScreen((Screen)new KeybindsScreen((Screen)this, this.gameOptions))).build());
        this.body.addAll(ControlsOptionsScreen.getOptions((GameOptions)this.gameOptions));
    }
}

