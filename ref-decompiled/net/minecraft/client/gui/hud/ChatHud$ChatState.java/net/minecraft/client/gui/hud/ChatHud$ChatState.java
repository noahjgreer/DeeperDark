/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;

@Environment(value=EnvType.CLIENT)
public static class ChatHud.ChatState {
    final List<ChatHudLine> messages;
    final List<String> messageHistory;
    final List<ChatHud.RemovalQueuedMessage> removalQueue;

    public ChatHud.ChatState(List<ChatHudLine> messages, List<String> messageHistory, List<ChatHud.RemovalQueuedMessage> removalQueue) {
        this.messages = messages;
        this.messageHistory = messageHistory;
        this.removalQueue = removalQueue;
    }
}
