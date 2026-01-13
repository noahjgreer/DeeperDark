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
import net.minecraft.client.realms.gui.screen.RealmsBackupScreen;

@Environment(value=EnvType.CLIENT)
class RealmsBackupScreen.BackupObjectSelectionList
extends ElementListWidget<RealmsBackupScreen.BackupObjectSelectionListEntry> {
    private static final int field_49450 = 36;

    public RealmsBackupScreen.BackupObjectSelectionList(RealmsBackupScreen realmsBackupScreen) {
        super(MinecraftClient.getInstance(), realmsBackupScreen.width, realmsBackupScreen.layout.getContentHeight(), realmsBackupScreen.layout.getHeaderHeight(), 36);
    }

    @Override
    public int getRowWidth() {
        return 300;
    }
}
