/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.option.ControlsListWidget
 *  net.minecraft.client.gui.screen.option.GameOptionsScreen
 *  net.minecraft.client.gui.screen.option.KeybindsScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.client.util.InputUtil$Type
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class KeybindsScreen
extends GameOptionsScreen {
    private static final Text TITLE_TEXT = Text.translatable((String)"controls.keybinds.title");
    public @Nullable KeyBinding selectedKeyBinding;
    public long lastKeyCodeUpdateTime;
    private ControlsListWidget controlsList;
    private ButtonWidget resetAllButton;

    public KeybindsScreen(Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, TITLE_TEXT);
    }

    protected void initBody() {
        this.controlsList = (ControlsListWidget)this.layout.addBody((Widget)new ControlsListWidget(this, this.client));
    }

    protected void addOptions() {
    }

    protected void initFooter() {
        this.resetAllButton = ButtonWidget.builder((Text)Text.translatable((String)"controls.resetAll"), button -> {
            for (KeyBinding keyBinding : this.gameOptions.allKeys) {
                keyBinding.setBoundKey(keyBinding.getDefaultKey());
            }
            this.controlsList.update();
        }).build();
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget.add((Widget)this.resetAllButton);
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.close()).build());
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        this.controlsList.position(this.width, this.layout);
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.selectedKeyBinding != null) {
            this.selectedKeyBinding.setBoundKey(InputUtil.Type.MOUSE.createFromCode(click.button()));
            this.selectedKeyBinding = null;
            this.controlsList.update();
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    public boolean keyPressed(KeyInput input) {
        if (this.selectedKeyBinding != null) {
            if (input.isEscape()) {
                this.selectedKeyBinding.setBoundKey(InputUtil.UNKNOWN_KEY);
            } else {
                this.selectedKeyBinding.setBoundKey(InputUtil.fromKeyCode((KeyInput)input));
            }
            this.selectedKeyBinding = null;
            this.lastKeyCodeUpdateTime = Util.getMeasuringTimeMs();
            this.controlsList.update();
            return true;
        }
        return super.keyPressed(input);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        boolean bl = false;
        for (KeyBinding keyBinding : this.gameOptions.allKeys) {
            if (keyBinding.isDefault()) continue;
            bl = true;
            break;
        }
        this.resetAllButton.active = bl;
    }
}

