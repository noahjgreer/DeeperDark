/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.gui.RealmsWorldSlotButton;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class RealmsWorldSlotButton.State {
    final String slotName;
    final String version;
    final RealmsServer.Compatibility compatibility;
    final long imageId;
    final @Nullable String image;
    public final boolean empty;
    public final boolean minigame;
    public final RealmsWorldSlotButton.Action action;
    public final boolean hardcore;
    public final boolean active;

    public RealmsWorldSlotButton.State(RealmsServer server, int slot) {
        boolean bl = this.minigame = slot == 4;
        if (this.minigame) {
            this.slotName = MINIGAME_SLOT_NAME.getString();
            this.imageId = server.minigameId;
            this.image = server.minigameImage;
            this.empty = server.minigameId == -1;
            this.version = "";
            this.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
            this.hardcore = false;
            this.active = server.isMinigame();
        } else {
            RealmsSlot realmsSlot = server.slots.get(slot);
            this.slotName = realmsSlot.options.getSlotName(slot);
            this.imageId = realmsSlot.options.templateId;
            this.image = realmsSlot.options.templateImage;
            this.empty = realmsSlot.options.empty;
            this.version = realmsSlot.options.version;
            this.compatibility = realmsSlot.options.compatibility;
            this.hardcore = realmsSlot.isHardcore();
            this.active = server.activeSlot == slot && !server.isMinigame();
        }
        this.action = RealmsWorldSlotButton.getAction(this.active, this.empty, server.expired);
    }
}
