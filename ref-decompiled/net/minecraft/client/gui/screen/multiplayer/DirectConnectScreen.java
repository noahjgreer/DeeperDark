/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.network.ServerAddress
 *  net.minecraft.client.network.ServerInfo
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen.multiplayer;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class DirectConnectScreen
extends Screen {
    private static final Text ENTER_IP_TEXT = Text.translatable((String)"manageServer.enterIp");
    private ButtonWidget selectServerButton;
    private final ServerInfo serverEntry;
    private TextFieldWidget addressField;
    private final BooleanConsumer callback;
    private final Screen parent;

    public DirectConnectScreen(Screen parent, BooleanConsumer callback, ServerInfo server) {
        super((Text)Text.translatable((String)"selectServer.direct"));
        this.parent = parent;
        this.serverEntry = server;
        this.callback = callback;
    }

    public boolean keyPressed(KeyInput input) {
        if (this.selectServerButton.active && this.getFocused() == this.addressField && input.isEnter()) {
            this.saveAndClose();
            return true;
        }
        return super.keyPressed(input);
    }

    protected void init() {
        this.addressField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 116, 200, 20, ENTER_IP_TEXT);
        this.addressField.setMaxLength(128);
        this.addressField.setText(this.client.options.lastServer);
        this.addressField.setChangedListener(text -> this.onAddressFieldChanged());
        this.addSelectableChild((Element)this.addressField);
        this.selectServerButton = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"selectServer.select"), button -> this.saveAndClose()).dimensions(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20).build());
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.callback.accept(false)).dimensions(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20).build());
        this.onAddressFieldChanged();
    }

    protected void setInitialFocus() {
        this.setInitialFocus((Element)this.addressField);
    }

    public void resize(int width, int height) {
        String string = this.addressField.getText();
        this.init(width, height);
        this.addressField.setText(string);
    }

    private void saveAndClose() {
        this.serverEntry.address = this.addressField.getText();
        this.callback.accept(true);
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    public void removed() {
        this.client.options.lastServer = this.addressField.getText();
        this.client.options.write();
    }

    private void onAddressFieldChanged() {
        this.selectServerButton.active = ServerAddress.isValid((String)this.addressField.getText());
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, -1);
        context.drawTextWithShadow(this.textRenderer, ENTER_IP_TEXT, this.width / 2 - 100 + 1, 100, -6250336);
        this.addressField.render(context, mouseX, mouseY, deltaTicks);
    }
}

