/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.message;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public record SentMessage.Chat(SignedMessage message) implements SentMessage
{
    @Override
    public Text content() {
        return this.message.getContent();
    }

    @Override
    public void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params) {
        SignedMessage signedMessage = this.message.withFilterMaskEnabled(filterMaskEnabled);
        if (!signedMessage.isFullyFiltered()) {
            sender.networkHandler.sendChatMessage(signedMessage, params);
        }
    }
}
