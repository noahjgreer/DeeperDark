/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.SignedMessage;

@Environment(value=EnvType.CLIENT)
class ContextMessageCollector.ContextMessage {
    private final Set<MessageSignatureData> lastSeenEntries;
    private SignedMessage message;
    private boolean linkSuccessful = true;
    private int count;

    ContextMessageCollector.ContextMessage(SignedMessage message) {
        this.lastSeenEntries = new ObjectOpenHashSet(message.signedBody().lastSeenMessages().entries());
        this.message = message;
    }

    boolean linkTo(SignedMessage message) {
        if (message.equals(this.message)) {
            return false;
        }
        boolean bl = this.lastSeenEntries.remove(message.signature());
        if (this.linkSuccessful && this.message.getSender().equals(message.getSender())) {
            if (this.message.link().linksTo(message.link())) {
                bl = true;
                this.message = message;
            } else {
                this.linkSuccessful = false;
            }
        }
        if (bl) {
            ++this.count;
        }
        return bl;
    }

    boolean isInvalid() {
        return this.count >= ContextMessageCollector.this.leadingContextMessageCount || !this.linkSuccessful && this.lastSeenEntries.isEmpty();
    }
}
