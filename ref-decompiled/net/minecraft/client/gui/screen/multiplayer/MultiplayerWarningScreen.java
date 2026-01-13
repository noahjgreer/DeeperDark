/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.WarningScreen
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.LayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 */
package net.minecraft.client.gui.screen.multiplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class MultiplayerWarningScreen
extends WarningScreen {
    private static final Text HEADER = Text.translatable((String)"multiplayerWarning.header").formatted(Formatting.BOLD);
    private static final Text MESSAGE = Text.translatable((String)"multiplayerWarning.message");
    private static final Text CHECK_MESSAGE = Text.translatable((String)"multiplayerWarning.check").withColor(-2039584);
    private static final Text NARRATED_TEXT = HEADER.copy().append("\n").append(MESSAGE);
    private final Screen parent;

    public MultiplayerWarningScreen(Screen parent) {
        super(HEADER, MESSAGE, CHECK_MESSAGE, NARRATED_TEXT);
        this.parent = parent;
    }

    protected LayoutWidget getLayout() {
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(8);
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)ScreenTexts.PROCEED, button -> {
            if (this.checkbox.isChecked()) {
                this.client.options.skipMultiplayerWarning = true;
                this.client.options.write();
            }
            this.client.setScreen((Screen)new MultiplayerScreen(this.parent));
        }).build());
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.close()).build());
        return directionalLayoutWidget;
    }

    public void close() {
        this.client.setScreen(this.parent);
    }
}

