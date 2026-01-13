/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.ChatScreen
 *  net.minecraft.client.gui.screen.ChatScreen$CloseReason
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.SleepingChatScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.input.CharInput
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket$Mode
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class SleepingChatScreen
extends ChatScreen {
    private ButtonWidget stopSleepingButton;

    public SleepingChatScreen(String string, boolean bl) {
        super(string, bl);
    }

    protected void init() {
        super.init();
        this.stopSleepingButton = ButtonWidget.builder((Text)Text.translatable((String)"multiplayer.stopSleeping"), button -> this.stopSleeping()).dimensions(this.width / 2 - 100, this.height - 40, 200, 20).build();
        this.addDrawableChild((Element)this.stopSleepingButton);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (!this.client.getChatRestriction().allowsChat(this.client.isInSingleplayer())) {
            this.stopSleepingButton.render(context, mouseX, mouseY, deltaTicks);
            return;
        }
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    public void close() {
        this.stopSleeping();
    }

    public boolean charTyped(CharInput input) {
        if (!this.client.getChatRestriction().allowsChat(this.client.isInSingleplayer())) {
            return true;
        }
        return super.charTyped(input);
    }

    public boolean keyPressed(KeyInput input) {
        if (input.isEscape()) {
            this.stopSleeping();
        }
        if (!this.client.getChatRestriction().allowsChat(this.client.isInSingleplayer())) {
            return true;
        }
        if (input.isEnter()) {
            this.sendMessage(this.chatField.getText(), true);
            this.chatField.setText("");
            this.client.inGameHud.getChatHud().resetScroll();
            return true;
        }
        return super.keyPressed(input);
    }

    private void stopSleeping() {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
        clientPlayNetworkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)this.client.player, ClientCommandC2SPacket.Mode.STOP_SLEEPING));
    }

    public void closeChatIfEmpty() {
        String string = this.chatField.getText();
        if (this.draft || string.isEmpty()) {
            this.closeReason = ChatScreen.CloseReason.INTERRUPTED;
            this.client.setScreen(null);
        } else {
            this.closeReason = ChatScreen.CloseReason.DONE;
            this.client.setScreen((Screen)new ChatScreen(string, false));
        }
    }
}

