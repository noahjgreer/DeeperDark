/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.message;

import net.minecraft.network.message.LastSeenMessageList;

public record LastSeenMessagesCollector.LastSeenMessages(LastSeenMessageList lastSeen, LastSeenMessageList.Acknowledgment update) {
}
