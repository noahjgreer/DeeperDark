/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class RealmsMainScreen.RealmSelectionList
extends AlwaysSelectedEntryListWidget<RealmsMainScreen.Entry> {
    public RealmsMainScreen.RealmSelectionList() {
        super(MinecraftClient.getInstance(), RealmsMainScreen.this.width, RealmsMainScreen.this.height, 0, 36);
    }

    @Override
    public void setSelected(@Nullable RealmsMainScreen.Entry entry) {
        super.setSelected(entry);
        RealmsMainScreen.this.refreshButtons();
    }

    @Override
    public int getRowWidth() {
        return 300;
    }

    void refresh(RealmsMainScreen mainScreen) {
        RealmsMainScreen.Entry entry = (RealmsMainScreen.Entry)this.getSelectedOrNull();
        this.clearEntries();
        for (RealmsNotification realmsNotification : RealmsMainScreen.this.notifications) {
            if (!(realmsNotification instanceof RealmsNotification.VisitUrl)) continue;
            RealmsNotification.VisitUrl visitUrl = (RealmsNotification.VisitUrl)realmsNotification;
            this.addVisitEntries(visitUrl, mainScreen, entry);
            RealmsMainScreen.this.markAsSeen(List.of(realmsNotification));
            break;
        }
        this.addServerEntries(entry);
    }

    private void addVisitEntries(RealmsNotification.VisitUrl url, RealmsMainScreen mainScreen, @Nullable RealmsMainScreen.Entry selectedEntry) {
        RealmsMainScreen.VisitUrlNotification visitUrlNotification2;
        Text text = url.getDefaultMessage();
        int i = RealmsMainScreen.this.textRenderer.getWrappedLinesHeight(text, RealmsMainScreen.VisitUrlNotification.getTextWidth(this.getRowWidth()));
        RealmsMainScreen.VisitUrlNotification visitUrlNotification = new RealmsMainScreen.VisitUrlNotification(RealmsMainScreen.this, mainScreen, i, text, url);
        this.addEntry(visitUrlNotification, 38 + i);
        if (selectedEntry instanceof RealmsMainScreen.VisitUrlNotification && (visitUrlNotification2 = (RealmsMainScreen.VisitUrlNotification)selectedEntry).getMessage().equals(text)) {
            this.setSelected(visitUrlNotification);
        }
    }

    private void addServerEntries(@Nullable RealmsMainScreen.Entry selectedEntry) {
        for (RealmsServer realmsServer : RealmsMainScreen.this.availableSnapshotServers) {
            this.addEntry(new RealmsMainScreen.SnapshotEntry(RealmsMainScreen.this, realmsServer));
        }
        for (RealmsServer realmsServer : RealmsMainScreen.this.serverFilterer) {
            RealmsMainScreen.Entry entry;
            if (RealmsMainScreen.isSnapshotRealmsEligible() && !realmsServer.isPrerelease()) {
                if (realmsServer.state == RealmsServer.State.UNINITIALIZED) continue;
                entry = new RealmsMainScreen.ParentRealmSelectionListEntry(RealmsMainScreen.this, realmsServer);
            } else {
                entry = new RealmsMainScreen.RealmSelectionListEntry(RealmsMainScreen.this, realmsServer);
            }
            this.addEntry(entry);
            if (!(selectedEntry instanceof RealmsMainScreen.RealmSelectionListEntry)) continue;
            RealmsMainScreen.RealmSelectionListEntry realmSelectionListEntry = (RealmsMainScreen.RealmSelectionListEntry)selectedEntry;
            if (realmSelectionListEntry.server.id != realmsServer.id) continue;
            this.setSelected(entry);
        }
    }
}
