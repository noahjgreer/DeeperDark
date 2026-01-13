/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.multiplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static class MultiplayerServerListWidget.LanServerEntry
extends MultiplayerServerListWidget.Entry {
    private static final int field_32386 = 32;
    private static final Text TITLE_TEXT = Text.translatable("lanServer.title");
    private static final Text HIDDEN_ADDRESS_TEXT = Text.translatable("selectServer.hiddenAddress");
    private final MultiplayerScreen screen;
    protected final MinecraftClient client;
    protected final LanServerInfo server;

    protected MultiplayerServerListWidget.LanServerEntry(MultiplayerScreen screen, LanServerInfo server) {
        this.screen = screen;
        this.server = server;
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        context.drawTextWithShadow(this.client.textRenderer, TITLE_TEXT, this.getContentX() + 32 + 3, this.getContentY() + 1, -1);
        context.drawTextWithShadow(this.client.textRenderer, this.server.getMotd(), this.getContentX() + 32 + 3, this.getContentY() + 12, -8355712);
        if (this.client.options.hideServerAddress) {
            context.drawTextWithShadow(this.client.textRenderer, HIDDEN_ADDRESS_TEXT, this.getContentX() + 32 + 3, this.getContentY() + 12 + 11, -8355712);
        } else {
            context.drawTextWithShadow(this.client.textRenderer, this.server.getAddressPort(), this.getContentX() + 32 + 3, this.getContentY() + 12 + 11, -8355712);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (doubled) {
            this.connect();
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.isEnterOrSpace()) {
            this.connect();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void connect() {
        this.screen.connect(new ServerInfo(this.server.getMotd(), this.server.getAddressPort(), ServerInfo.ServerType.LAN));
    }

    @Override
    public Text getNarration() {
        return Text.translatable("narrator.select", this.getMotdNarration());
    }

    public Text getMotdNarration() {
        return Text.empty().append(TITLE_TEXT).append(ScreenTexts.SPACE).append(this.server.getMotd());
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    boolean isOfSameType(MultiplayerServerListWidget.Entry entry) {
        if (!(entry instanceof MultiplayerServerListWidget.LanServerEntry)) return false;
        MultiplayerServerListWidget.LanServerEntry lanServerEntry = (MultiplayerServerListWidget.LanServerEntry)entry;
        if (lanServerEntry.server != this.server) return false;
        return true;
    }
}
