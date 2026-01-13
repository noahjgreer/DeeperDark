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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.LoadingWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static class MultiplayerServerListWidget.ScanningEntry
extends MultiplayerServerListWidget.Entry {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final LoadingWidget loadingWidget;

    public MultiplayerServerListWidget.ScanningEntry() {
        this.loadingWidget = new LoadingWidget(this.client.textRenderer, LAN_SCANNING_TEXT);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        this.loadingWidget.setPosition(this.getContentMiddleX() - this.client.textRenderer.getWidth(LAN_SCANNING_TEXT) / 2, this.getContentY());
        this.loadingWidget.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public Text getNarration() {
        return LAN_SCANNING_TEXT;
    }

    @Override
    boolean isOfSameType(MultiplayerServerListWidget.Entry entry) {
        return entry instanceof MultiplayerServerListWidget.ScanningEntry;
    }

    @Override
    public void connect() {
    }
}
