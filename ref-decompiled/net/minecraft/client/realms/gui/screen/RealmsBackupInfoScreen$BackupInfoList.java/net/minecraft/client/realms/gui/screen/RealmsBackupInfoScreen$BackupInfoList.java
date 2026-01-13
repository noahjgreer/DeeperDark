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
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.realms.gui.screen.RealmsBackupInfoScreen;

@Environment(value=EnvType.CLIENT)
class RealmsBackupInfoScreen.BackupInfoList
extends AlwaysSelectedEntryListWidget<RealmsBackupInfoScreen.BackupInfoListEntry> {
    public RealmsBackupInfoScreen.BackupInfoList(MinecraftClient client) {
        super(client, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.layout.getContentHeight(), RealmsBackupInfoScreen.this.layout.getHeaderHeight(), 36);
        if (RealmsBackupInfoScreen.this.backup.changeList != null) {
            RealmsBackupInfoScreen.this.backup.changeList.forEach((key, value) -> this.addEntry(new RealmsBackupInfoScreen.BackupInfoListEntry(RealmsBackupInfoScreen.this, (String)key, (String)value)));
        }
    }
}
