/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.realms.gui.screen.RealmsPendingInvitesScreen;

@Environment(value=EnvType.CLIENT)
class RealmsPendingInvitesScreen.PendingInvitationSelectionList
extends ElementListWidget<RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry> {
    public static final int field_62098 = 36;

    public RealmsPendingInvitesScreen.PendingInvitationSelectionList(RealmsPendingInvitesScreen realmsPendingInvitesScreen, MinecraftClient client) {
        super(client, realmsPendingInvitesScreen.width, realmsPendingInvitesScreen.layout.getContentHeight(), realmsPendingInvitesScreen.layout.getHeaderHeight(), 36);
    }

    @Override
    public int getRowWidth() {
        return 280;
    }

    public boolean isEmpty() {
        return this.getEntryCount() == 0;
    }

    public void remove(RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry invitation) {
        this.removeEntry(invitation);
    }
}
