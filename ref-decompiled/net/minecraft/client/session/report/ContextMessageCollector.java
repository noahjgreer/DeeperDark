/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntRBTreeSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.session.report.ContextMessageCollector
 *  net.minecraft.client.session.report.ContextMessageCollector$ContextMessage
 *  net.minecraft.client.session.report.ContextMessageCollector$IndexedMessageConsumer
 *  net.minecraft.client.session.report.log.ChatLog
 *  net.minecraft.client.session.report.log.ChatLogEntry
 *  net.minecraft.client.session.report.log.ReceivedMessage$ChatMessage
 *  net.minecraft.network.message.SignedMessage
 */
package net.minecraft.client.session.report;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.ContextMessageCollector;
import net.minecraft.client.session.report.log.ChatLog;
import net.minecraft.client.session.report.log.ChatLogEntry;
import net.minecraft.client.session.report.log.ReceivedMessage;
import net.minecraft.network.message.SignedMessage;

@Environment(value=EnvType.CLIENT)
public class ContextMessageCollector {
    final int leadingContextMessageCount;
    private final List<ContextMessage> contextMessages = new ArrayList();

    public ContextMessageCollector(int leadingContextMessageCount) {
        this.leadingContextMessageCount = leadingContextMessageCount;
    }

    public void add(ChatLog log, IntCollection selections, IndexedMessageConsumer consumer) {
        IntRBTreeSet intSortedSet = new IntRBTreeSet(selections);
        for (int i = intSortedSet.lastInt(); i >= log.getMinIndex() && (this.hasContextMessage() || !intSortedSet.isEmpty()); --i) {
            ChatLogEntry chatLogEntry = log.get(i);
            if (!(chatLogEntry instanceof ReceivedMessage.ChatMessage)) continue;
            ReceivedMessage.ChatMessage chatMessage = (ReceivedMessage.ChatMessage)chatLogEntry;
            boolean bl = this.tryLink(chatMessage.message());
            if (intSortedSet.remove(i)) {
                this.add(chatMessage.message());
                consumer.accept(i, chatMessage);
                continue;
            }
            if (!bl) continue;
            consumer.accept(i, chatMessage);
        }
    }

    public void add(SignedMessage message) {
        this.contextMessages.add(new ContextMessage(this, message));
    }

    public boolean tryLink(SignedMessage message) {
        boolean bl = false;
        Iterator iterator = this.contextMessages.iterator();
        while (iterator.hasNext()) {
            ContextMessage contextMessage = (ContextMessage)iterator.next();
            if (!contextMessage.linkTo(message)) continue;
            bl = true;
            if (!contextMessage.isInvalid()) continue;
            iterator.remove();
        }
        return bl;
    }

    public boolean hasContextMessage() {
        return !this.contextMessages.isEmpty();
    }
}

