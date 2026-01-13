/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.network.message.MessageTrustStatus
 *  net.minecraft.client.session.report.log.ChatLogEntry
 *  net.minecraft.client.session.report.log.ReceivedMessage
 *  net.minecraft.client.session.report.log.ReceivedMessage$ChatMessage
 *  net.minecraft.client.session.report.log.ReceivedMessage$GameMessage
 *  net.minecraft.network.message.SignedMessage
 *  net.minecraft.text.Text
 */
package net.minecraft.client.session.report.log;

import com.mojang.authlib.GameProfile;
import java.time.Instant;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.message.MessageTrustStatus;
import net.minecraft.client.session.report.log.ChatLogEntry;
import net.minecraft.client.session.report.log.ReceivedMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public interface ReceivedMessage
extends ChatLogEntry {
    public static ChatMessage of(GameProfile gameProfile, SignedMessage message, MessageTrustStatus trustStatus) {
        return new ChatMessage(gameProfile, message, trustStatus);
    }

    public static GameMessage of(Text message, Instant timestamp) {
        return new GameMessage(message, timestamp);
    }

    public Text getContent();

    default public Text getNarration() {
        return this.getContent();
    }

    public boolean isSentFrom(UUID var1);
}

