/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.report;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.report.ChatSelectionScreen;

@Environment(value=EnvType.CLIENT)
record ChatSelectionScreen.SelectionListWidget.SenderEntryPair(UUID sender, ChatSelectionScreen.SelectionListWidget.Entry entry) {
    public boolean senderEquals(ChatSelectionScreen.SelectionListWidget.SenderEntryPair pair) {
        return pair.sender.equals(this.sender);
    }
}
