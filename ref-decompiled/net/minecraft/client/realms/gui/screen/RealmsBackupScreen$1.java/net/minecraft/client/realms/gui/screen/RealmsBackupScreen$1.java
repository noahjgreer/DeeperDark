/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsBackupScreen;

@Environment(value=EnvType.CLIENT)
class RealmsBackupScreen.1
extends Thread {
    RealmsBackupScreen.1(String string) {
        super(string);
    }

    @Override
    public void run() {
        RealmsClient realmsClient = RealmsClient.create();
        try {
            List<Backup> list = realmsClient.backupsFor(RealmsBackupScreen.this.serverData.id).backups();
            RealmsBackupScreen.this.client.execute(() -> {
                RealmsBackupScreen.this.backups = list;
                RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
                if (!RealmsBackupScreen.this.noBackups && RealmsBackupScreen.this.downloadButton != null) {
                    RealmsBackupScreen.this.downloadButton.active = true;
                }
                if (RealmsBackupScreen.this.selectionList != null) {
                    RealmsBackupScreen.this.selectionList.replaceEntries(RealmsBackupScreen.this.backups.stream().map(backup -> new RealmsBackupScreen.BackupObjectSelectionListEntry(RealmsBackupScreen.this, (Backup)backup)).toList());
                }
            });
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't request backups", (Throwable)realmsServiceException);
        }
    }
}
