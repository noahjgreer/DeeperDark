/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen.tab;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.tab.RealmsPlayerTab;

@Environment(value=EnvType.CLIENT)
class RealmsPlayerTab.InvitedObjectSelectionList
extends ElementListWidget<RealmsPlayerTab.PlayerTabEntry> {
    private static final int field_49472 = 36;

    public RealmsPlayerTab.InvitedObjectSelectionList(int width, int height) {
        super(MinecraftClient.getInstance(), width, height, RealmsPlayerTab.this.screen.getHeaderHeight(), 36);
    }

    void refreshEntries(RealmsServer serverData) {
        this.clearEntries();
        this.addEntries(serverData);
    }

    private void addEntries(RealmsServer serverData) {
        RealmsPlayerTab.HeaderEntry headerEntry = new RealmsPlayerTab.HeaderEntry(RealmsPlayerTab.this);
        this.addEntry(headerEntry, headerEntry.getHeight(RealmsPlayerTab.this.textRenderer.fontHeight));
        for (RealmsPlayerTab.InvitedObjectSelectionListEntry invitedObjectSelectionListEntry : serverData.players.stream().map(playerInfo -> new RealmsPlayerTab.InvitedObjectSelectionListEntry(RealmsPlayerTab.this, (PlayerInfo)playerInfo)).toList()) {
            this.addEntry(invitedObjectSelectionListEntry);
        }
    }

    @Override
    protected void drawMenuListBackground(DrawContext context) {
    }

    @Override
    protected void drawHeaderAndFooterSeparators(DrawContext context) {
    }

    @Override
    public int getRowWidth() {
        return 300;
    }
}
