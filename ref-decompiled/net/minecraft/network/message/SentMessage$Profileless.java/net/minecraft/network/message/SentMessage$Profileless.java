/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.message;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public record SentMessage.Profileless(Text content) implements SentMessage
{
    @Override
    public void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params) {
        sender.networkHandler.sendProfilelessChatMessage(this.content, params);
    }
}
