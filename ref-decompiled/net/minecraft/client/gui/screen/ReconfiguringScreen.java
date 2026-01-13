/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.screen.ReconfiguringScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.multiplayer.ConnectScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.network.ClientConnection
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.network.ClientConnection;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ReconfiguringScreen
extends Screen {
    private static final int DISCONNECT_BUTTON_ACTIVATION_TICK = 600;
    private final ClientConnection connection;
    private ButtonWidget disconnectButton;
    private int tick;
    private final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical();

    public ReconfiguringScreen(Text title, ClientConnection connection) {
        super(title);
        this.connection = connection;
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    protected void init() {
        this.layout.getMainPositioner().alignHorizontalCenter().margin(10);
        this.layout.add((Widget)new TextWidget(this.title, this.textRenderer));
        this.disconnectButton = (ButtonWidget)this.layout.add((Widget)ButtonWidget.builder((Text)ScreenTexts.DISCONNECT, button -> this.connection.disconnect(ConnectScreen.ABORTED_TEXT)).build());
        this.disconnectButton.active = false;
        this.layout.refreshPositions();
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        SimplePositioningWidget.setPos((Widget)this.layout, (ScreenRect)this.getNavigationFocus());
    }

    public void tick() {
        super.tick();
        ++this.tick;
        if (this.tick == 600) {
            this.disconnectButton.active = true;
        }
        if (this.connection.isOpen()) {
            this.connection.tick();
        } else {
            this.connection.handleDisconnection();
        }
    }
}

